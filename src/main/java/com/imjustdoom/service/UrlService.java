package com.imjustdoom.service;

import com.imjustdoom.model.Url;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.*;

@Validated
@Service
public class UrlService {
    private static final Logger LOG = LoggerFactory.getLogger(UrlService.class);
    
    private static final String SQL = "INSERT INTO url (end_timestamp, timestamp, url, url_hash, domain_id) VALUES (?, ?, ?, ?, ?)";
    private static final int BATCH_SIZE = 5000;

    private final BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(50);
    private final ExecutorService dbExecutor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
            this.queue,
            Executors.defaultThreadFactory(),
            (r, executor) -> {
                try {
                    executor.getQueue().put(r);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RejectedExecutionException("Interrupted while waiting for queue space", e);
                }
            }
    );

    private final JdbcTemplate jdbcTemplate;

    public UrlService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addAllUrlsTransaction(List<Url> urls) {
        this.dbExecutor.submit(() -> {
            for (int i = 0; i < urls.size(); i += BATCH_SIZE) {
                int endIndex = Math.min(i + BATCH_SIZE, urls.size());
                insertBatchWithDubHandling(urls.subList(i, endIndex));
            }
        });
    }

    protected void insertBatchWithDubHandling(List<Url> batch) {
        try {
            this.jdbcTemplate.batchUpdate(SQL, new BatchPreparedStatementSetter() {
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    Url url = batch.get(i);
                    ps.setLong(1, url.getEndTimestamp());
                    ps.setLong(2, url.getTimestamp());
                    ps.setString(3, url.getUrl());
                    ps.setString(4, url.getUrlHash());
                    ps.setObject(5, url.getDomain().getId());
                }

                public int getBatchSize() {
                    return batch.size();
                }
            });
            LOG.info("Successfully inserted batch of {} URLs", batch.size());
        } catch (DataIntegrityViolationException e) {
            LOG.info("Batch insert failed, inserting individually to handle duplicates");
            insertSingleWithDupHandling(batch);
        }
    }

    private void insertSingleWithDupHandling(List<Url> urls) {
        int successCount = 0;
        int duplicateCount = 0;

        for (Url url : urls) {
            try {
                this.jdbcTemplate.update(SQL, ps -> {
                    ps.setLong(1, url.getEndTimestamp());
                    ps.setLong(2, url.getTimestamp());
                    ps.setString(3, url.getUrl());
                    ps.setString(4, url.getUrlHash());
                    ps.setObject(5, url.getDomain().getId());
                });
                successCount++;
            } catch (DataIntegrityViolationException e) {
                duplicateCount++;
            }
        }

        LOG.info("Individual insert completed: {} inserted, {} duplicates skipped", successCount, duplicateCount);
    }
}
