package com.trophonius.sql;

import com.trophonius.dbo.Database;
import com.trophonius.utils.HelperMethods;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class SqlParser {
    public String prompt = "/";
    public Database currentDB;
    private String sql = "";


    public SqlParser(String prompt, Database currentDB, String sql) {
        this.sql = sql;
        this.prompt = prompt;
        this.currentDB = currentDB;
        parseSql(currentDB, sql);
    }


    private void parseSql(Database currentDB, String sql) {

        // DDL Statements
        if (
                sql.toLowerCase().startsWith("create") ||
                        sql.toLowerCase().startsWith("drop") ||
                        sql.toLowerCase().startsWith("truncate") ||
                        sql.toLowerCase().startsWith("alter") ||
                        sql.toLowerCase().startsWith("rename")
        ) {
            // Dispatch to DDL-parser
            DDL parser = new DDL(prompt, currentDB, sql);
        }

        // DML Statements
        if (
                sql.toLowerCase().startsWith("select") ||
                        sql.toLowerCase().startsWith("insert") ||
                        sql.toLowerCase().startsWith("update") ||
                        sql.toLowerCase().startsWith("delete")
        ) {
            // Dispatch to DML-parser
            DML parser = new DML(prompt, currentDB, sql);
        }


        // DCL Statements
        if (
                sql.toLowerCase().startsWith("grant") ||
                        sql.toLowerCase().startsWith("revoke")
        ) {
            // Dispatch to DCL-parser
            DCL parser = new DCL(prompt, currentDB, sql);
        }


        // SQL Client Helper methods

        // Prepare SQL - Create Array of words and remove =
        String[] words = sql.split("[= ]");
        String charset = "", collation = "";

        // SQL: SHOW DATABASES
        if (sql.toLowerCase().equals("show databases") || sql.toLowerCase().equals("\\l")) {

            ArrayList<String> dbNames = HelperMethods.findDatabases();

            if (dbNames.isEmpty()) {
                System.out.println("No databases found, Create one?");
            } else {
                System.out.println("+" + "-".repeat(30) + "+");
                System.out.printf("| %-28s |\n", "Database");
                System.out.println("+" + "-".repeat(30) + "+");
                dbNames.forEach(name -> System.out.printf("| %-28s |\n", name));
                System.out.println("+" + "-".repeat(30) + "+");
            }

        } // END SHOW DATABASES

        // SQL: SHOW TABLES
        if (sql.toLowerCase().equals("show tables") || sql.toLowerCase().equals("\\d")) {

            if (prompt.length() > 2) {
                currentDB.printTables();
            } else {
                System.out.println("No database selected. Type: use <dbname> to select one");
            }

        }


        // SQL: DESCRIBE <FULL> DATABASE <dbname>
        if (sql.toLowerCase().startsWith("describe database") || sql.toLowerCase().startsWith("describe full database")) {
            String dbName;
            boolean full;
            Database showDB = new Database();

            // if sql = describe <dbname>
            if (words.length == 3) {
                dbName = words[2];
                // if sql = describe database <dbname>
            } else {
                dbName = words[3];
                showDB.setDbName(dbName);
            }

            if (sql.toLowerCase().contains("full")) {
                full = true;
            } else {
                full = false;
            }

            try {
                showDB.describeDatabase(dbName, full);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

        } // end describe database

        // SQL: DESCRIBE <table name>
        if (sql.toLowerCase().startsWith("describe") && this.prompt.length() > 2) {

            String tableName = words[1];
            AtomicBoolean tableExists = new AtomicBoolean(false);
            currentDB.getTables().forEach((k, v) -> {
                if (v.getTableName().equals(tableName)) {
                    v.printTableStructure();
                    tableExists.set(true);
                }
            });

            if (!tableExists.getAcquire()) {
                System.out.println("Table does not exist");
            }
        }


        // SQL: USE <dbname>
        if (sql.toLowerCase().startsWith("use")) {
            String dbName = words[1];

            if ((currentDB = currentDB.openDatabase(dbName)).getDbName() != null) {
                this.currentDB = currentDB;
                prompt = currentDB.getDbName() + "/";
                // this.currentDBName = currentDB.getDbName();
                System.out.println("Database changed to " + currentDB.getDbName());
            }

        } // end use

        // populate testbase
        if (sql.toLowerCase().startsWith("populate")) {
            int numberofRows = Integer.valueOf(words[1]);
            HelperMethods.populate(numberofRows);
        }
    }
}
