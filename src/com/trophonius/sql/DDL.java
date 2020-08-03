package com.trophonius.sql;

import com.trophonius.dbo.DataType;
import com.trophonius.dbo.Database;
import com.trophonius.dbo.Field;
import com.trophonius.dbo.Table;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;

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
                    db1.saveDatabase(db1);
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
            if (java.nio.file.Files.isRegularFile(Paths.get("data/"+currentDB.getDbName()+"/"+tableName+".tbl"))) {
                System.out.println("Table "+ tableName +" already exists in database "+ currentDB.getDbName());
                return;
            } else {
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

                    // Data Type Conversion

                    String dataTypeString = fieldElement[1].toLowerCase();

                    DataType dataType = new DataType();
                    dataType.setName(dataTypeString);

                    setClassAndComplex(dataType, dataTypeString);

                    f1.setDataType(dataType);

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

                    // System.out.println(f1.getDataType().getClassName()+" "+ f1.getName());

                    // Add field to table
                    t1.addField(f1);

                } // end create and add fields to table

                // Add newly created table to currentDB
                currentDB.addTable(currentDB, t1);
                t1.writeTableToDisk(currentDB.getDbName());
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

            // Add column
            if (tableAction.equals("add")) {
                String fieldProps = sql.toLowerCase().substring(sql.toLowerCase().indexOf(words[4]));
                System.out.println(fieldProps);
                // configure datatype
                String[] props = fieldProps.split(" ");
                DataType newType = new DataType();
                newType.setName(props[1]);
                setClassAndComplex(newType,props[1]);
                // configure field
                Field newField = new Field();
                newField.setName(props[0]);
                newField.setDataType(newType);

                Table alteredTable = new Table();
                alteredTable.setTableName(tableName);
                // TODO fix denne
                currentDB.getTables().get(alteredTable.getTableName()).addField(newField);
                try {
                    Database.saveDatabase(currentDB);
                } catch (IOException e) {
                    System.out.println("Databasen ikke lagret fordi: "+e.getMessage());
                    e.printStackTrace();
                }

            }

        }

    }

    private void setClassAndComplex(DataType dataType, String dataTypeString) {

        // Map SQL data types to Java classes

        if (dataTypeString.equals("text") || dataTypeString.equals("string")) {
            dataType.setClassName("String");
        }

        if (dataTypeString.equals("date")) {
            dataType.setClassName("LocalDate");
        }

        if (dataTypeString.equals("datetime")) {
            dataType.setClassName("LocalDateTime");
        }

        if (dataTypeString.equals("int")) {
            dataType.setClassName("Integer");
        }

        if (dataTypeString.equals("decimal") || dataTypeString.equals("double")) {
            dataType.setClassName("Double");
        }

        if (dataTypeString.equals("float")) {
            dataType.setClassName("Float");
        }

        if (dataTypeString.equals("object")) {
            dataType.setClassName("Object");
            dataType.setComplex(true);
        }

        if (dataTypeString.equals("array(int)")) {
            dataType.setClassName("ArrayList<Integer>");
            dataType.setComplex(true);
        }

        if (dataTypeString.equals("array(text)")) {
            dataType.setClassName("ArrayList<String>");
            dataType.setComplex(true);
        }

        if (dataTypeString.equals("array(decimal)")) {
            dataType.setClassName("ArrayList<Double>");
            dataType.setComplex(true);
        }

    }


}
