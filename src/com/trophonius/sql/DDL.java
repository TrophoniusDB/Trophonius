package com.trophonius.sql;

import com.trophonius.dbo.DataType;
import com.trophonius.dbo.Database;
import com.trophonius.dbo.Field;
import com.trophonius.dbo.Table;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class DDL {

    public String prompt = "/";
    public Database currentDB;
    private String sql = "";

    public DDL(String prompt, Database currentDB, String sql) {
        this.sql = sql;
        this.prompt = prompt;
        this.currentDB = currentDB;
        parseSql(currentDB, sql);
    }

    private void parseSql(Database currentDB, String sql) {


        // Prepare SQL - Create Array of words and remove =
        String[] words = sql.split("[= ]");
        String charset = "", collation = "";

        // DDL SQL METHODS

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
                    Database.saveDatabase(db1);
                    System.out.println("Database " + db1.getDbName() + " saved.");
                    currentDB = db1;
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } // end else
        } // end create database


        // SQL: CREATE TABLE <tablename> <default charset> <default collation>
        if (sql.toLowerCase().startsWith("create table")) {
            // Determine table name
            String tableName = words[2];

            // Check if table already exists
            if (java.nio.file.Files.isRegularFile(Paths.get("data/" + currentDB.getDbName() + "/" + tableName + ".tbl"))) {
                System.out.println("Table " + tableName + " already exists in database " + currentDB.getDbName());
                return;
            } else {
                // Create new table in memory
                Table t1 = new Table();
                t1.setTableName(tableName);
                // Set database engine
                if(sql.toLowerCase().contains("engine")) {
                    List<String> wordList  = Arrays.asList(words);
                    int i = wordList.indexOf("engine")+1;
                    String engineName = words[i];
                    t1.setEngineName(engineName);
                } else {

                    //TODO set engine to database default
                }

                // Extract table properties from sql
                String propertiesString = sql.substring(sql.lastIndexOf(")") + 1);

                // Set Character Set
                if (propertiesString.contains("charset")) {
                    String charSetString = propertiesString.substring(propertiesString.indexOf("charset"));
                    String[] charSetCode = charSetString.split("[= ]");
                    t1.setCharSet(charSetCode[1]);
                } else {
                    // set character set to database default
                    t1.setCharSet(currentDB.getCharSet());
                }

                // Set Collation Language Code
                if (propertiesString.contains("collation")) {
                    String collationString = propertiesString.substring(propertiesString.indexOf("collation"));
                    String[] langCode = collationString.split("[= ]");
                    t1.setCollation(langCode[1]);
                } else {
                    // set collation to database default
                    t1.setCollation(currentDB.getCollation());
                }

                // Extract fields from sql
                String fieldString = sql.substring(sql.indexOf("(") + 1, sql.lastIndexOf(")"));

                // Split fieldString into fields array
                String[] fields = fieldString.split(",");

                // loop trough fields array
                for (String field : fields) {
                    // strip leading and trailing spaces

                    // Create field
                    Field f1 = new Field();
                    // Set field properties
                    f1.setFieldProperties(f1, field);

                    // Add field to table
                    t1.addField(f1);

                } // end create and add fields to table

                // Add newly created table to currentDB
                currentDB.addTable(currentDB, t1);
                t1.createTableOnDisk(currentDB.getDbName());
            } // end else
        } // end create table


        // SQL: DROP DATABASE
        if (sql.toLowerCase().startsWith("drop database")) {
            String dbName = words[2];
            Database.deleteDatabase(dbName);
        }

        // SQL: DROP TABLE
        if (sql.toLowerCase().startsWith("drop table")) {
            String tableName = words[2];
            Database.deleteTable(currentDB, tableName);
        }

        // SQL: ALTER TABLE
        if (sql.toLowerCase().startsWith("alter table")) {
            String tableName = words[2];
            String tableAction = words[3];
            // find field name, datatype and other field properties
            String fieldProps = sql.toLowerCase().substring(sql.toLowerCase().indexOf(words[4]));

            // Add field to table
            if (tableAction.equals("add")) {
                // Add field to table
                currentDB.getTables().get(tableName).addField(tableName, fieldProps);
            }

            // Drop field from table
            if (tableAction.equals("drop")) {
                // TODO
            }
        } // END ALTER TABLE

    } // end parseSql


}
