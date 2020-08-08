package com.trophonius.dbo;

public class DBEngine {

    private String name;
    private String tableSuffix;
    private boolean binaryFormat;
    private String comment;

    public DBEngine() {
    }

    public DBEngine(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTableSuffix() {
        return tableSuffix;
    }

    public void setTableSuffix(String tableSuffix) {
        this.tableSuffix = tableSuffix;
    }

    public boolean isBinaryFormat() {
        return binaryFormat;
    }

    public void setBinaryFormat(boolean binaryFormat) {
        this.binaryFormat = binaryFormat;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
