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
            // print common commands
            System.out.println("-".repeat(115));
            System.out.printf("%-50s %-10s","Command  (shortcut)","Description\n");
            System.out.println("-".repeat(115));
            System.out.printf("%-50s %-10s","show databases (\\l)","List all databases\n");
            System.out.printf("%-50s %-10s","use <dbname>","Select a database\n");
            System.out.printf("%-50s %-10s","show tables (\\d)","List all tables in selected database\n");
            System.out.printf("%-50s %-10s","describe (\\dt) <table name>","Show table structure of <table name>\n");
            System.out.printf("%-50s %-10s","describe database <dbname> (\\db <dbname>)","Show database and table info from <table name>\n");
            System.out.printf("%-50s %-10s","describe full database <dbname> (\\db+ <dbname>)","Show database and table structures from <table name>\n");
            System.out.printf("%-50s %-10s","import (\\i) <filename.sql>","import sql insert statements from file into table in current db\n");
            System.out.printf("%-50s %-10s","\\timing","toggles timing on or off\n");
            System.out.println("-".repeat(115));
        }

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
            } else {
                dbName = words[1];
                showDB.setDbName(dbName);
            }

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
            String outFile = words[2];
            HelperMethods.populate(numberofRows, outFile);
        }

    }

}
