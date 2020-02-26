package com.trophonius.sql;

import com.trophonius.dbo.Database;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;

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

        // SQL INSERT <INTO> <TABLENAME>
        if (sql.toLowerCase().startsWith("insert") || sql.toLowerCase().startsWith("insert into")  ) {

            // Determine tablename
            String tableName;
            if(sql.toLowerCase().startsWith("insert into")) {
                tableName = words[2];
            } else {
                tableName = words[1];
            }

            // Find field names
            String[] fieldNames = sql.substring(sql.indexOf("(")+1,sql.indexOf(")")).split(", ");
            // Arrays.stream(fieldNames).forEach(System.out::println);

            // Find field values
            String[] fieldValues = sql.substring(sql.indexOf("(")+1,sql.indexOf(")")).split(", ");
            Arrays.stream(fieldValues).forEach(System.out::println);


            // Check if table exists
            if (!java.nio.file.Files.isRegularFile(Paths.get("data/"+currentDB.getDbName()+"/"+tableName+".tbl"))) {
                // Table file not found. Return to sender
                System.out.println("Table not found.");
                return;
            } else {
                // Table file found - open it, construct row and append row to table.
                // System.out.println("Table exists");
                LocalDateTime created = LocalDateTime.now();



            }

        } // END INSERT INTO



            }


}
