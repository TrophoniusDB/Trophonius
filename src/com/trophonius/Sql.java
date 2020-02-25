package com.trophonius;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class Sql {
    public String prompt = "/";
    public Database currentDB;
    private String sql = "";


    public Sql(String prompt, Database currentDB, String sql) {
        this.sql = sql;
        this.prompt = prompt;
        this.currentDB = currentDB;
        parseSql(currentDB, sql);
    }


    private void parseSql(Database currentDB, String sql) {


        // Prepare SQL - Create Array of words and remove =
        String[] words = sql.split("[= ]");
        String charset = "", collation = "";

        // SQL: CREATE DATABASE <dbname>
        if (sql.toLowerCase().startsWith("create database")) {

            String dbName = words[2];
            LocalDateTime created = LocalDateTime.now();

            if (java.nio.file.Files.isDirectory(Paths.get(dbName))) {
                System.out.println("Database already exists");
                return;
            } else {

                for (int i = 0; i < words.length; i++) {
                    // Find Character set
                    if (words[i].equals("charset")) {
                        charset = words[i + 1];
                    }
                    // Find Collation
                    if (words[i].equals("collation")) {
                        collation = words[i + 1];
                    }

                }

                Database db1 = new Database(dbName);
                if (charset != "") db1.setCharSet(charset);
                if (collation != "") db1.setCollation(collation);

                db1.setCreated(created);

                try {
                    db1.saveDatabase(db1);
                    System.out.println("Database " + db1.getDbName() + " saved.");
                    currentDB = db1;
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } // end else
        } // end create database

        // SQL: SHOW DATABASES
        if (sql.toLowerCase().equals("show databases")) {

            ArrayList<String> dbNames = HelperMethods.findDatabases();

            if (dbNames.isEmpty()) {
                System.out.println("No databases found, Create one?");
            } else {
                System.out.println("+"+"-".repeat(30)+"+");
                System.out.printf("| %-28s |\n", "Database");
                System.out.println("+"+"-".repeat(30)+"+");
                dbNames.forEach(name -> System.out.printf("| %-28s |\n",name));
                System.out.println("+"+"-".repeat(30)+"+");
            }

        } // END SHOW DATABASES

        // SQL: SHOW TABLES
        if (sql.toLowerCase().equals("show tables")) {

            if (prompt.length() > 2) {
                currentDB.printTables();
            } else {
                System.out.println("No database selected. Type: use <dbname> to select one");
            }

        }


        // SQL: DESCRIBE <FULL> DATABASE <dbname>
        if (sql.toLowerCase().startsWith("describe database")
                || sql.toLowerCase().startsWith("describe full database")
                || (sql.toLowerCase().startsWith("describe full") && this.prompt.length() > 2)
        ) {
            String dbName;
            boolean full;
            Database showDB = new Database();

            // if sql = describe <dbname>
            if (words.length == 2) {
                dbName = words[1];
                // if sql = describe database <dbname>
            } else {
                dbName = words[2];
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
            currentDB = currentDB.openDatabase(dbName);
            this.currentDB = currentDB;
            prompt = currentDB.getDbName() + "/";
            // this.currentDBName = currentDB.getDbName();
            System.out.println("Database changed to " + currentDB.getDbName());

        } // end use

        // SQL: CREATE TABLE <tablename> <default charset> <default collation>
        if (sql.toLowerCase().startsWith("create table")) {
            // Determine table name
            String tableName = words[2];

            // Create new table in memory
            Table t1 = new Table();
            t1.setTableName(tableName);

            // Extract fields from sql
            String fieldString = sql.substring(sql.indexOf("(") + 1, sql.lastIndexOf(")"));

            // Split fieldString into fields array
            String[] fields = fieldString.split(",");

            // loop trough fields array
            for (String field : fields) {
                // strip leading and trailing spaces
                field = field.strip();
                // Split array into new array of each word in field statement
                String[] fieldElement = field.split(" ");


                // Create fields and set attributes
                Field f1 = new Field();
                f1.setName(fieldElement[0]);
                f1.setDataType(fieldElement[1]);

                //    System.out.println("DataType = " + fieldElement[1]);
                if (field.contains("primary key")) {
                    f1.setPrimaryKey(true);
                    f1.setNotNull(true);
                }

                if (field.contains("auto_increment") || field.contains("identity")) {
                    f1.setAutoIncrement(true);
                }

                if (field.contains("not null")) {
                    f1.setNotNull(true);
                }

                // Add field to table
                t1.addField(f1);

            } // end create and add fields to table

            // Add newly created table to currentDB
            currentDB.addTable(currentDB, t1);
            t1.writeTableToDisk(currentDB.getDbName());

        } // end create table


        // SQL: DROP DATABASE
        if (sql.toLowerCase().startsWith("drop database")) {
            String dbName = words[2];
            Database.deleteDatabase(dbName);

        }
    }
}
