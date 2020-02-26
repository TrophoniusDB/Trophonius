package com.trophonius.sql;

import com.trophonius.dbo.Database;

public class DCL {

    public String prompt = "/";
    public Database currentDB;
    private String sql = "";

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
