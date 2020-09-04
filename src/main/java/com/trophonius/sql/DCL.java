package com.trophonius.sql;

import com.trophonius.dbo.Database;

/**
 * Handles SQL statements for users, roles and access rights
 */
public class DCL {

    public String prompt = "/";
    public Database currentDB;
    private String sql = "";

    /***
     * Constructor that calls method to parse SQL
     * @param prompt prompt that will be changed when a database is selected
     * @param currentDB DBO selected by user
     * @param sql supplied SQL Statement
     */
    public DCL(String prompt, Database currentDB, String sql) {
        this.sql = sql;
        this.prompt = prompt;
        this.currentDB = currentDB;
        parseSql(currentDB, sql);
    }

    private void parseSql(Database currentDB, String sql) {

        // Prepare SQL - Create Array of words and remove =
        String[] words = sql.split("[= ]");
        String charset = "", collation = "";

        // DCL SQL METHODS

    }

}
