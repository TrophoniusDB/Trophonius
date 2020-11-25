package com.trophonius.dbo;

import com.trophonius.Engines.Engine;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicReference;

import static com.trophonius.Main.currentDB;

public class Table implements Serializable {
    private static final long serialVersionUID = 9134175559420903358L;
    private String tableName, charSet, collation;
    private LinkedHashMap<String, Field> tableStructure = new LinkedHashMap<>();
    private ArrayList<String> fieldNames;
    private Object primaryKeyValue;
    private int rowCount = 0;
    private Engine engine;


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

    public Table(String tableName, String charSet, String collation, Engine engine) {
        this.tableName = tableName;
        this.charSet = charSet;
        this.collation = collation;
        this.engine = engine;
    }


    public Engine getEngine() {
        return engine;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public <T> Object getPrimaryKey(T value) {
        T primaryKeyValue = value;
        return primaryKeyValue;
    }

    public <T> void setPrimaryKey(T value) {
        T primaryKeyValue = value;
    }

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    // Add Fields to the tableStructure HashMap
    public void addField(Field field) {
        this.tableStructure.put(field.getName(), field);
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

        // Add Field to Table Structure
        currentDB.getTables().get(tableName).addField(newField);
        try {
            // And save to Data Base File
            Database.saveDatabase(currentDB);
            System.out.println("Column " + newField.getName() + " added to table " + tableName);
        } catch (IOException e) {
            System.out.println("Database file not saved, because: " + e.getMessage());
            e.printStackTrace();
        }

    } // END addField

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
        tableStructure.forEach((k, v) -> {
            names.add(k);
        });
        return names;
    }

    public int getMaxFieldNameSize() {
        return tableStructure.entrySet().stream().map(a -> a.getKey().length()).mapToInt(a -> a).max().getAsInt();
    }

    public void printTableStructure() {
        AtomicReference<Integer> i = new AtomicReference<>(1);
        System.out.println("Table Name: " + tableName+" | Number of Rows: "+rowCount);
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
        System.out.println("Storage Engine: "+engine.getName()+" | Character Set: "+charSet+" | Collation: "+collation);
    }

    public LinkedHashMap<String, Field> getTableStructure() {
        return tableStructure;
    }

}
