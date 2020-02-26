package com.trophonius.sql;

import com.trophonius.dbo.Database;

public class DML {


    public String prompt = "/";
    public Database currentDB;
    private String sql = "";

    public DML(String prompt, Database currentDB, String sql) {
        this.sql = sql;
        this.prompt = prompt;
        this.currentDB = currentDB;
        parseSql(currentDB, sql);
    }

    private void parseSql(Database currentDB, String sql) {

        // Prepare SQL - Create Array of words and remove =
        String[] words = sql.split("[= ]");
        String charset = "", collation = "";

        // DML SQL METHODS




    }


}
