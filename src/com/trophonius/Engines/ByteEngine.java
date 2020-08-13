package com.trophonius.Engines;

import com.trophonius.dbo.Row;

import java.util.ArrayList;
import java.util.List;

// BYTE ENGINE
public class ByteEngine implements Engine {

    public ByteEngine() {

        setName("byteEngine");
        setBinaryFormat(true);
        setTableSuffix("dat");
        setComment("Fast and binary, but not feature rich");

    }

    @Override
    public void createTableFile(String dbName, String tableName) {

    }

    @Override
    public void writeRowToDisk(String dbName, String tableName, Row row) {

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
        return null;
    }

    @Override
    public void setName(String name) {

    }

    @Override
    public String getTableSuffix() {
        return null;
    }

    @Override
    public void setTableSuffix(String tableSuffix) {

    }

    @Override
    public boolean isBinaryFormat() {
        return false;
    }

    @Override
    public void setBinaryFormat(boolean binaryFormat) {

    }

    @Override
    public String getComment() {
        return null;
    }

    @Override
    public void setComment(String comment) {

    }
}
