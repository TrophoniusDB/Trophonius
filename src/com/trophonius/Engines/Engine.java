package com.trophonius.Engines;

public interface Engine {
    String getName();

    void setName(String name);

    String getTableSuffix();

    void setTableSuffix(String tableSuffix);

    boolean isBinaryFormat();

    void setBinaryFormat(boolean binaryFormat);

    String getComment();

    void setComment(String comment);

    void createTableFile(String dbName, String tableName);

}
