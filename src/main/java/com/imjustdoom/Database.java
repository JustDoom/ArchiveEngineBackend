package com.imjustdoom;

import java.sql.*;

public class Database {

    private Statement stmt;
    private Connection conn;
    public PreparedStatement preparedStatement;

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
            this.preparedStatement = this.conn.prepareStatement("INSERT INTO url (original, mimetype, timestamp, endtimestamp, statuscode) VALUES (?, ?, ?, ?, ?)");

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
        preparedStatement.setString(1, link);
        preparedStatement.setString(2, mimetype);
        preparedStatement.setString(3, timestamp);
        preparedStatement.setString(4, endtimestamp);
        preparedStatement.setString(5, statuscode);
        preparedStatement.executeUpdate();
    }
}
