package com.trophonius.dbo;

import com.trophonius.utils.AppendableObjectOutputStream;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.TreeMap;

public class Row<E> implements Serializable {

    private TreeMap<String, E> row = new TreeMap<>();
    private TreeMap<String, E> primaryKey = new TreeMap<>();
    private TreeMap<TreeMap<String, E>, TreeMap<String, E>> rowWithPrimaryKey  = new TreeMap<>();


    public Row() {

    }

    public TreeMap<String, E> getRow() {
        return row;
    }

    public void setRow(TreeMap<String, E> row) {
        this.row = row;
    }

    public ArrayList<E> getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(ArrayList<E> primaryKey) {
        this.primaryKey = primaryKey;
    }

    public TreeMap<ArrayList<E>, TreeMap<String, E>> getRowWithPrimaryKey() {
        return rowWithPrimaryKey;
    }

    public void setRowWithPrimaryKey(TreeMap<ArrayList<E>, TreeMap<String, E>> rowWithPrimaryKey) {
        this.rowWithPrimaryKey = rowWithPrimaryKey;
    }

    public void add (String fieldName, E value ) {
        this.row.put(fieldName, value);
    }

    public void addToPrimaryKey (String fieldName, E value ) {
        this.primaryKey.add(fieldName, value);
    }


    // Append a row to en existing table file
    public void writeRowToDisk(String dbName, String tableName) {

        try {

            // check that table file exists in data directory
            if (java.nio.file.Files.isRegularFile(Paths.get("data/" + dbName + "/" + tableName + ".tbl"))) {

                // Open table file for writing, and append map of primary key + row to the file
                FileOutputStream dbFile = new FileOutputStream("data/" + dbName + "/" + tableName + ".tbl",true);
                AppendableObjectOutputStream os = new AppendableObjectOutputStream(new BufferedOutputStream(dbFile));



                os.writeObject(row);;
                os.flush();
                os.close();
                dbFile.flush();
                dbFile.close();
                System.out.println("Success: 1 row written to table "+tableName);
            } else  {
                // Table file does not exists
                System.out.println("Table file does not exist");
                return;
            }

        } catch (IOException e) {
            System.out.println("Row could not we written to disk: ");
            e.printStackTrace();

        }
    }



    @Override
    public String toString() {
        return "Row=" + row ;
    }
}
