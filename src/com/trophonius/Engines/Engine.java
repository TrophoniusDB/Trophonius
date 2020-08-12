package com.trophonius.Engines;

import com.trophonius.dbo.Row;

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

    void writeRowToDisk(String dbName, String tableName, Row row);

}
