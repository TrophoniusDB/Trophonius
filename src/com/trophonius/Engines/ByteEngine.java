package com.trophonius.Engines;

import com.trophonius.dbo.Row;

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