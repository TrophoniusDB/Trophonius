package com.trophonius.dbo;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class Table implements Serializable {
    private static final long serialVersionUID = 9134175559420903358L;
    private String tableName, charSet, collation;
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


    // Save altered table structure to db - file
    public void saveTableStructure() {
      // TODO
    }

    // create the physical table file
    public <E> void createTableOnDisk(String dbName) {

        try {

            // check that table file not  exists in data directory
            if (!Files.isRegularFile(Paths.get("data/" + dbName + "/" + tableName + ".tbl"))) {

                // create table file and write table structure to the file
                FileOutputStream dbFile = new FileOutputStream("data/" + dbName + "/" + tableName + ".tbl");
                ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(dbFile));
                os.writeObject(tableStructure);
                os.flush();
                os.close();
                dbFile.flush();
                dbFile.close();

            } else  {
                // Table file exists
                System.out.println("Table already exists");
            }

        } catch (IOException e) {
            System.out.println("Table could not we written to disk: ");
            e.printStackTrace();

        }
    }



}
