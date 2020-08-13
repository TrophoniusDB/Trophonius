package com.trophonius.Engines;

import com.trophonius.dbo.Row;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// CSV ENGINE
public class CsvEngine implements Engine {

    private String name;
    private String tableSuffix;
    private boolean binaryFormat = false;
    private String comment;

    public CsvEngine() {

        setName("csvEngine");
        setBinaryFormat(false);
        setTableSuffix("csv");
        setComment("Fast, but not feature rich");
    }

    @Override
    public void createTableFile(String dbName, String tableName) {

        Path filePath = Paths.get("data/" + dbName + "/" + tableName + "."+ this.getTableSuffix());
        if (!Files.isRegularFile(filePath)) {

            try {
                Files.createFile(filePath);
            } catch (IOException e) {
                System.out.println("Table file could not be created, because: "+e.getMessage());
                e.printStackTrace();
            }

        } else {
            // Table file exists
            System.out.println("Table \""+tableName+"\" already exists");
        }

    } // END  createTableFile

    @Override
    // Append a row to an existing table file
    public void writeRowToDisk(String dbName, String tableName,Row row) {

        try {
            // check that table file exists in data directory
            if (java.nio.file.Files.isRegularFile(Paths.get("data/" + dbName + "/" + tableName + "."+getTableSuffix()))) {

                // Open table file for writing, and append map of primary key + row to the file

                FileWriter fileOut = new FileWriter("data/" + dbName + "/" + tableName + ".tbl",true);

                System.out.println(Stream.of(row.getRow()).map(String::valueOf).collect(Collectors.joining(",")));

                try {
                fileOut.write(Stream.of(row.getRow()).map(String::valueOf).collect(Collectors.joining(",")));

                fileOut.flush();
                fileOut.close();
                System.out.println("Success: 1 row written to table: "+tableName);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }

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
    public void fetcHRows(String tableName, String sql) {
        // Split sql into separate words
        String[] words = sql.split("[= ]");

        // make a list to hold field names from sql
        List<String> fieldList = new ArrayList<>();

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getTableSuffix() {
        return tableSuffix;
    }

    @Override
    public void setTableSuffix(String tableSuffix) {
        this.tableSuffix = tableSuffix;
    }

    @Override
    public boolean isBinaryFormat() {
        return binaryFormat;
    }

    @Override
    public void setBinaryFormat(boolean binaryFormat) {
        this.binaryFormat = binaryFormat;
    }

    @Override
    public String getComment() {
        return comment;
    }

    @Override
    public void setComment(String comment) {
        this.comment = comment;
    }
}
