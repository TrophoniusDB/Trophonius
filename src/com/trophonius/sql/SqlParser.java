package com.trophonius.sql;

import com.trophonius.dbo.Database;
import com.trophonius.utils.HelperMethods;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.trophonius.Main.timing;

/**
 * Main SQL Parser Class. Contains non-SQL methods, like <i>show databases</i>,
 * and dispatches SQL to parsers for DML, DDL or DCL
 * @author Terje Berg-Hansen
 * @version 0.0.1
 */
public class SqlParser {
    public String prompt = "/";
    public Database currentDB;
    private String sql = "";

    /**
     * Sets prompt, currentDB and calls method parseSql(currentDB, sql).
     * @param prompt Sets the command line prompt
     * @param currentDB A Database Object that SQL will be applied to
     * @param sql Current SQL statement supplied by user
     */
    public SqlParser(String prompt, Database currentDB, String sql) {
        this.sql = sql;
        this.prompt = prompt;
        this.currentDB = currentDB;
        parseSql(currentDB, sql);
    }

    /**
     * First responder command parser. Contains helper methods and dispatches SQL to parsers for DDL, DML and DCL
     * @param currentDB the database the sql will be sent to
     * @param sql the sql to send to the database
     */
    private void parseSql(Database currentDB, String sql) {
         // trim extra spaces
        sql = sql.trim();
        // Determine end of SQL statement
        if(sql.contains(";")) {
            // remove ;
            sql = sql.substring(0,sql.length()-1);
        }

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
                        sql.toLowerCase().startsWith("delete") ||
                        sql.toLowerCase().startsWith("import") ||
                        sql.toLowerCase().startsWith("\\i")

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

        // TOGGLE TIMING ON/OFF
        if (sql.toLowerCase().equals("\\timing")) {
            if(timing) {
                System.out.println("Timing is off");
                timing = false;
            } else {
                System.out.println("Timing is on");
                timing = true;
            }
        } // END TIMING

        // SQL: help or \h
        if (sql.toLowerCase().equals("help") || sql.toLowerCase().equals("\\h")) {
            HelperMethods.printHelpText();
        }

        // SQL: SHOW DATABASES
        if (sql.toLowerCase().equals("show databases") || sql.toLowerCase().equals("\\l")) {

            ArrayList<String> dbNames = HelperMethods.findDatabases();

            if (dbNames.isEmpty()) {
                System.out.println("No databases found, Create one?");
            } else {
               // print box with Names of Databases
               HelperMethods.printDatabaseList(dbNames);
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

        // SQL: DESCRIBE <FULL> DATABASE <dbname> ( or \db or \db+ )
        if (sql.toLowerCase().startsWith("describe database")
                || sql.toLowerCase().startsWith("describe full database")
                || sql.toLowerCase().startsWith("\\db")
        ) {
            String dbName;
            boolean full;
            Database showDB = new Database();

            // if sql = describe database <dbname>
            if (words.length == 3) {
                dbName = words[2];
                // if sql = \db <dbname> or \db+ <dbname>
            } else if(words.length == 2) {
                dbName = words[1];
            } else {
                // if sql = \db or \db+ - use currentDB
                dbName = currentDB.getDbName();
            }
            // call method to show database description
            showDB.setDbName(dbName);

            if (sql.toLowerCase().contains("full")  || sql.toLowerCase().startsWith("\\db+")) {
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
        if ((sql.toLowerCase().startsWith("describe") || sql.toLowerCase().startsWith("\\dt")) && this.prompt.length() > 2) {
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
        if (sql.toLowerCase().startsWith("use") && words.length>1) {

            String dbName = words[1];

            if ((currentDB = currentDB.openDatabase(dbName)).getDbName() != null) {
                this.currentDB = currentDB;
                prompt = currentDB.getDbName() + "/";
                System.out.println("Database changed to " + currentDB.getDbName());
            }
        } // end use

        // SQL: show log or \log
        if (sql.toLowerCase().startsWith("show log") || sql.toLowerCase().startsWith("\\log")) {
            HelperMethods.showLog();
        }

        // populate testbase
        if (sql.toLowerCase().startsWith("populate")) {
            try {
                int numberofRows = Integer.valueOf(words[1]);
                String tableName = words[2];
                String outFile = words[3];
                HelperMethods.populate(numberofRows, tableName,outFile);
            } catch (Exception e) {
                System.out.println("\""+words[1] + "\" is not a number.");
            }
        }

    }

}
