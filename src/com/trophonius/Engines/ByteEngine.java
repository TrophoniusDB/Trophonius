package com.trophonius.Engines;

import com.trophonius.dbo.Row;

import java.util.List;

// BYTE ENGINE
public class ByteEngine implements Engine {

    private static final long serialVersionUID = -8292584544996693690L;

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
    public void writeRowToDisk(String dbName, String tableName, Row row, boolean verbose) {

    }

    @Override
    public List<Row> fetchRows(String tableName, List<String> fieldList, int limit, int offset) {

        return null;
    }

    @Override
    public long getRowCount(String dbName, String tableName) {
        return 0;
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
