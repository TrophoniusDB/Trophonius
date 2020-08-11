package com.trophonius.Engines;

// CSV ENGINE
public class CsvEngine implements Engine {

    public CsvEngine() {

        setName("csvEngine");
        setBinaryFormat(false);
        setTableSuffix("csv");
        setComment("Fast, but not feature rich");


    }

    @Override
    public void createTableFile(String dbName, String tableName) {

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
