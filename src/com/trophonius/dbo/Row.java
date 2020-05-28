package com.trophonius.dbo;

import com.trophonius.utils.AppendableObjectOutputStream;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Objects;

public class Row<E> implements Serializable {

    private static final long serialVersionUID = -544016776593193705L;
    // One row consists of a LinkedHashMap with the fieldName as the key and the field's value as the value
    private LinkedHashMap<String, E> row = new LinkedHashMap<>();

    public Row() {

    }

    public <E> E getValue(String key)  {
        return (E) row.get(key);
    }

    public LinkedHashMap<String, E> getRow() {
        return row;
    }

    public int getMaxValueLength() {
       return this.row.values().stream().filter(Objects::nonNull).mapToInt(e -> e.toString().length()).max().getAsInt();
    }

    public int getMaxKeyLength() {
        return this.row.keySet().stream().filter(Objects::nonNull).mapToInt(e -> e.length()).max().getAsInt();
    }

    public void setRow(LinkedHashMap<String, E> row) {
        this.row = row;
    }

    public void addToRow (String fieldName, E value ) {
        this.row.put(fieldName, value);
    }

    // Append a row to an existing table file
    public void writeRowToDisk(Row row, String dbName, String tableName) {

        try {
            // check that table file exists in data directory
            if (java.nio.file.Files.isRegularFile(Paths.get("data/" + dbName + "/" + tableName + ".tbl"))) {

                // Open table file for writing, and append map of primary key + row to the file

                FileOutputStream dbFile = new FileOutputStream("data/" + dbName + "/" + tableName + ".tbl",true);
                AppendableObjectOutputStream oStr = new AppendableObjectOutputStream(new BufferedOutputStream(dbFile));
                // Write the primary key
                // os.writeObject(primaryKey);
                // write the row
                //System.out.println(getRow());
                oStr.writeObject(row);
                oStr.flush();
                oStr.close();
                dbFile.flush();
                dbFile.close();
                System.out.println("Success: 1 row written to table: "+tableName);
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
