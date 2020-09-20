package com.trophonius.Engines;


import com.trophonius.Main;
import com.trophonius.dbo.Row;
import com.trophonius.sql.FilterTerm;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

// CSV ENGINE
public class CsvEngine implements Engine {

    private static final long serialVersionUID = 7555115851235997327L;
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

        Path filePath = Paths.get("data/" + dbName + "/" + tableName + "." + this.getTableSuffix());
        if (!Files.isRegularFile(filePath)) {

            try {
                Files.createFile(filePath);
            } catch (IOException e) {
                System.out.println("Table file could not be created, because: " + e.getMessage());
                e.printStackTrace();
            }

        } else {
            // Table file exists
            System.out.println("Table \"" + tableName + "\" already exists");
        }

    } // END  createTableFile

    @Override
    // Append a row to an existing table file
    public void writeRowToDisk(String dbName, String tableName, Row row, boolean verbose) {


        try (FileWriter outFile = new FileWriter("data/" + dbName + "/" + tableName + "." + getTableSuffix(), true)) {
            // Open table file for writing, and append map of primary key + row to the file

            var rowString = "";
            for (Object field : row.getRow().values()) {
                rowString += field.toString() + ",";
            }

            rowString = rowString.substring(0, rowString.length() - 1) + '\n';
            outFile.append(rowString);
            if (verbose) {
                System.out.println("Success: 1 row written to table: " + tableName);
            }

        } catch (IOException e) {
            System.out.println("Row could not we written to file for table: " + tableName);
            e.printStackTrace();

        }
    }

    @Override
    public List<Row> fetchRows(String tableName, List<String> fieldList, List<FilterTerm> relTerms, int limit, int offset) {
        List<Row> rows = new ArrayList<>();

        // Open database file
        AtomicInteger numberOfRows = new AtomicInteger();

        try {
            List valueList = Files.lines(Paths.get("data/" + Main.currentDB.getDbName() + "/" + tableName + "." + getTableSuffix())).collect(Collectors.toList());
            if (limit == Integer.MAX_VALUE) {
                numberOfRows.set(valueList.size());
            }

            valueList.stream().limit(limit).forEach(a -> {

                String[] fieldValues = a.toString().split(",");

                Row row = new Row();
                for (int j = 0; j < fieldList.size(); j++) {
                    row.addToRow(fieldList.get(j), fieldValues[j]);
                }

                //           System.out.println(row.toString());
                rows.add(row);
                numberOfRows.getAndIncrement();
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
        return rows;
    }

    @Override
    public long getRowCount(String dbName, String tableName) {
        return 0;
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
