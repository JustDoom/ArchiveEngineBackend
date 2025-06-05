package com.imjustdoom.service;

import com.imjustdoom.model.Url;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Validated
@Service
public class UrlService {
    private static final int BATCH_SIZE = 5000;

    private final ExecutorService dbExecutor = Executors.newSingleThreadExecutor();

    private final JdbcTemplate jdbcTemplate;

    public UrlService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addAllUrlsTransaction(List<Url> urls) {
        this.dbExecutor.submit(() -> {
            for (int i = 0; i < urls.size(); i += BATCH_SIZE) {
                int endIndex = Math.min(i + BATCH_SIZE, urls.size());
                List<Url> batch = urls.subList(i, endIndex);
                insertBatchWithDubHandling(batch);
            }
        });
    }

    protected void insertBatchWithDubHandling(List<Url> batch) {
        try {
            String sql = "INSERT INTO url (end_timestamp, timestamp, url, url_hash, domain_id) VALUES (?, ?, ?, ?, ?)";
            this.jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
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
            System.out.println("Successfully inserted batch of " + batch.size() + " URLs");
        } catch (DataIntegrityViolationException e) {
            System.out.println("Batch insert failed, inserting individually to handle duplicates");
            insertSingleWithDupHandling(batch);
        }
    }

    private void insertSingleWithDupHandling(List<Url> urls) {
        int successCount = 0;
        int duplicateCount = 0;

        for (Url url : urls) {
            try {
                String sql = "INSERT INTO url (end_timestamp, timestamp, url, url_hash, domain_id) VALUES (?, ?, ?, ?, ?)";
                this.jdbcTemplate.update(sql, ps -> {
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

        System.out.println("Individual insert completed: " + successCount + " inserted, " + duplicateCount + " duplicates skipped");
    }
}
