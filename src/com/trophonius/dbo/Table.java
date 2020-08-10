package com.trophonius.dbo;

import com.trophonius.Engines.ByteEngine;
import com.trophonius.Engines.CsvEngine;
import com.trophonius.Engines.Engine;
import com.trophonius.Engines.ObjectEngine;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicReference;

import static com.trophonius.Main.currentDB;

public class Table implements Serializable {
    private static final long serialVersionUID = 9134175559420903358L;
    private String tableName, charSet, collation, engineName;
    private LinkedHashMap<String, Field> tableStructure = new LinkedHashMap<>();
    private ArrayList<String> fieldNames;
    private Object primaryKeyValue;

    public Table() {
    }

    public Table(String tableName) {
        this.tableName = tableName;
    }

    public Table(String tableName, String charSet) {
        this.tableName = tableName;
        this.charSet = charSet;
    }

    public Table(String tableName, String charSet, String collation) {
        this.tableName = tableName;
        this.charSet = charSet;
        this.collation = collation;
    }

    public Table(String tableName, String charSet, String collation, String engine) {
        this.tableName = tableName;
        this.charSet = charSet;
        this.collation = collation;
        this.engineName = engine;
    }

    public String getEngineName() {
        return engineName;
    }

    public void setEngineName(String engineName) {
        this.engineName = engineName;
    }

    public <T> Object getPrimaryKey(T value) {
        T primaryKeyValue = value;
        return primaryKeyValue;
    }

    public <T> void setPrimaryKey(T value) {
        T primaryKeyValue = value;
    }

    // Add Fields to the tableStructure HashMap
    public void addField (Field field) {
        this.tableStructure.put(field.getName(),field);
    }

    public void addField(String tableName, String fieldProps) {

        // configure datatype
        String[] props = fieldProps.split(" ");
        DataType newType = new DataType();
        newType.setName(props[1]);
        newType.setClassAndComplex(newType, props[1]);

        // configure field with obligatory name and data type
        Field newField = new Field();
        newField.setFieldProperties(newField, fieldProps);

        /*  TODO move to test
        System.out.println("Database: " + currentDB.getDbName());
        System.out.println("Table: " + tableName);
        System.out.println("Field Name: " + newField.getName());
        System.out.println("Field Data Type: " + newField.getDataType().getName());
        System.out.println("Field Class: " + newField.getDataType().getClassName());
        System.out.println("Complex Data Type: " + newField.getDataType().isComplex());
        System.out.println("Primary Key: " + newField.isPrimaryKey());
        System.out.println("Identity/Auto_increment: " + newField.isAutoIncrement());
        System.out.println("Unique: " + newField.isUnique());
        System.out.println("Not Null: " + newField.isNotNull());
        */

        // Add Field to Table Structure
        currentDB.getTables().get(tableName).addField(newField);
        try {
            // And save to Data Base File
            Database.saveDatabase(currentDB);
            System.out.println("Column "+newField.getName()+" added to table "+tableName);
        } catch (IOException e) {
            System.out.println("Database file not saved, because: " + e.getMessage());
            e.printStackTrace();
        }

    }


    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getCharSet() {
        return charSet;
    }

    public void setCharSet(String charSet) {
        this.charSet = charSet;
    }

    public String getCollation() {
        return collation;
    }

    public void setCollation(String collation) {
        this.collation = collation;
    }

    public ArrayList<String> getFieldNames() {
        ArrayList<String> names = new ArrayList<>();
        tableStructure.forEach((k,v) -> {
            names.add(k);
        });
        return names;
    }

    public int getMaxFieldNameSize () {
        return tableStructure.entrySet().stream().map(a -> a.getKey().length()).mapToInt(a->a).max().getAsInt();
    }

    public void printTableStructure() {
        AtomicReference<Integer> i = new AtomicReference<>(1);
        System.out.println("Table Name: " + tableName);
        System.out.println("+" + "-".repeat(94) + "+");
        System.out.printf("| %-3s | %-20s | %-15s | %-8s | %-8s | %-12s | %-8s |\n", "#", "Field", "Data Type", "Not Null", "Unique", "Primary Key", "Identity");
        System.out.println("+" + "-".repeat(94) + "+");
        tableStructure.forEach((k, v) -> System.out.printf("| %-3d | %-20s | %-15s | %-8s | %-8s | %-12s | %-8s |\n",
                i.getAndSet(i.get() + 1),
                v.getName(),
                v.getDataType().getName(),
                v.isNotNull(),
                v.isUnique(),
                v.isPrimaryKey(),
                v.isAutoIncrement()));
        System.out.println("+" + "-".repeat(94) + "+");
    }

    public LinkedHashMap<String, Field> getTableStructure() {
        return tableStructure;
    }


    // Create new table with ObjectEngine
    // create the physical table file
    public <E> void createTableOnDisk(String dbName) {

        //Create initial Table Stats
        TableStats stats = new TableStats();
        stats.setNumberOfRows(0);
        stats.setPreviousFileSize(0);

        Engine engine;

        switch(this.engineName) {
            case "ObjectEngine":
                engine = new ObjectEngine();
                ((ObjectEngine) engine).createTableFile(dbName, tableName);
                break;
            case "ByteEngine":
                engine = new ByteEngine();
                ((ByteEngine) engine).createTableFile(dbName, tableName);
                break;
            default:
                engine = new CsvEngine();
                ((CsvEngine) engine).createTableFile(dbName, tableName);
        }



        } // END createTableOnDisk


}
