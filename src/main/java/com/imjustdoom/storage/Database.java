package com.imjustdoom.storage;

import java.sql.*;

public class Database {

    private Statement stmt;
    private Connection conn;
    private PreparedStatement addLinkStmt, doesLinkExistStmt;

    public Database() {
        String user = "admin";
        String pass = "password";
        String server = "localhost";
        String port = "3306";
        String database = "archiveengine";

        // Connect and setup database
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.conn = DriverManager.getConnection(
                    "jdbc:mysql://" + server + ":" + port + "/" + database + "?autoReconnect=true", user, pass);
            this.stmt = this.conn.createStatement();
            this.addLinkStmt = this.conn.prepareStatement("INSERT INTO url (original, mimetype, timestamp, endtimestamp, statuscode) VALUES (?, ?, ?, ?, ?)");
            this.doesLinkExistStmt = this.conn.prepareStatement("SELECT * FROM url WHERE original = ? AND mimetype = ? AND timestamp = ? AND endtimestamp = ? AND statuscode = ?");

            createSQLTables();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createSQLTables() throws SQLException {
        this.stmt.executeUpdate("CREATE TABLE IF NOT EXISTS url(\n" +
                "    `original` TEXT NOT NULL,\n" +
                "    `mimetype` TINYTEXT NOT NULL,\n" +
                "    `timestamp` TINYTEXT NOT NULL,\n" +
                "    `endtimestamp` TINYTEXT NOT NULL,\n" +
                "    `statuscode` TINYTEXT NOT NULL\n" +
                ")");
    }

    public void addLink(String link, String mimetype, String timestamp, String endtimestamp, String statuscode) throws SQLException {
        this.addLinkStmt.setString(1, link);
        this.addLinkStmt.setString(2, mimetype);
        this.addLinkStmt.setString(3, timestamp);
        this.addLinkStmt.setString(4, endtimestamp);
        this.addLinkStmt.setString(5, statuscode);
        this.addLinkStmt.executeUpdate();
    }

    public boolean doesLinkExist(String link, String mimetype, String timestamp, String endtimestamp, String statuscode) throws SQLException {
        this.doesLinkExistStmt.setString(1, link);
        this.doesLinkExistStmt.setString(2, mimetype);
        this.doesLinkExistStmt.setString(3, timestamp);
        this.doesLinkExistStmt.setString(4, endtimestamp);
        this.doesLinkExistStmt.setString(5, statuscode);
        return this.doesLinkExistStmt.executeQuery().next();
    }

    public void addLinkIfNotExists(String link, String mimetype, String timestamp, String endtimestamp, String statuscode) throws SQLException {
        if (doesLinkExist(link, mimetype, timestamp, endtimestamp, statuscode)) {
            return;
        }
        addLink(link, mimetype, timestamp, endtimestamp, statuscode);
    }
}