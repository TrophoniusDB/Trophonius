package com.trophonius.dbo;

import com.trophonius.utils.AppendableObjectOutputStream;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class Table implements Serializable {
    private String tableName, charSet, collation;
    private HashMap<Integer, Field> tableStructure = new HashMap<>();
    private ArrayList<String> fieldNames;

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

    // Add Fields to the tableStructure HashMap
    public void addField (Field field) {
        this.tableStructure.put(tableStructure.size()+1,field);
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
            names.add(v.getName());
        });
        return names;
    }


    public void printTableStructure() {

        System.out.println("Table Name: " + tableName);
        System.out.println("+" + "-".repeat(85) + "+");
        System.out.printf("| %-3s | %-20s | %-15s | %-10s | %-12s | %-8s |\n", "#", "Field", "Data Type", "Not Null", "Primary Key", "Identity");
        System.out.println("+" + "-".repeat(85) + "+");
        tableStructure.forEach((k, v) -> System.out.printf("| %-3d | %-20s | %-15s | %-10s | %-12s | %-8s |\n", k, v.getName(), v.getDataType().getName(), v.isNotNull(), v.isPrimaryKey(), v.isAutoIncrement()));
        System.out.println("+" + "-".repeat(85) + "+");
    }

    public HashMap<Integer, Field> getTableStructure() {
        return tableStructure;
    }



    public void saveTableStructure() {

    }

    // create the physical table file
    public void writeTableToDisk(String dbName) {

        try {

            // check that table file not  exists in data directory
            if (!java.nio.file.Files.isRegularFile(Paths.get("data/" + dbName + "/" + tableName + ".tbl"))) {

                // create table file and write table structure to the file
                FileOutputStream dbFile = new FileOutputStream("data/" + dbName + "/" + tableName + ".tbl");
                ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(dbFile));
                os.writeObject(tableStructure);
                os.flush();
                os.close();
                dbFile.flush();
                dbFile.close();

            } else  {
                // Table file exists - do update of table structure
                // TO DO
            }

        } catch (IOException e) {
            System.out.println("Table could not we written to disk: ");
            e.printStackTrace();

        }
    }




}
