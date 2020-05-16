package com.trophonius.dbo;

import com.trophonius.utils.AppendableObjectOutputStream;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Paths;
import java.util.TreeMap;

public class Row<E> implements Serializable {

    // One row consists of a TreeMap with the fieldname as the key and the field as the value
    private TreeMap<String, E> row = new TreeMap<>();

    public Row() {

    }

    public TreeMap<String, E> getRow() {
        return row;
    }

    public void setRow(TreeMap<String, E> row) {
        this.row = row;
    }

    public void addToRow (String fieldName, E value ) {
        this.row.put(fieldName, value);
    }

    // Append a row to en existing table file
    public void writeRowsToDisk(E primaryKey, Row row, String dbName, String tableName) {
        // a row consists of a primary key followed by the corresponding row
/*
        TreeMap<E, Row> rows = new TreeMap<>();
        rows.put(primaryKey, row);
*/
        try {
            // check that table file exists in data directory
            if (java.nio.file.Files.isRegularFile(Paths.get("data/" + dbName + "/" + tableName + ".tbl"))) {

                // Open table file for writing, and append map of primary key + row to the file

                FileOutputStream dbFile = new FileOutputStream("data/" + dbName + "/" + tableName + ".tbl",true);
                AppendableObjectOutputStream os = new AppendableObjectOutputStream(new BufferedOutputStream(dbFile));
                // Write the primary key
                os.writeObject(primaryKey);
                // write the row
                os.writeObject(row);
                os.flush();
                os.close();
                dbFile.flush();
                dbFile.close();
                System.out.println("Success: 1 row written to table; "+tableName);
            } else  {
                // Table file does not exists
                System.out.println("Table file does not exist");
                return;
            }

        } catch (IOException e) {
            System.out.println("Row could not we written to file for table: "+tableName);
            e.printStackTrace();

        }
    }

    @Override
    public String toString() {
        return "Row=" + row ;
    }
}
