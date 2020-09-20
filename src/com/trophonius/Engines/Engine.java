package com.trophonius.Engines;

import com.trophonius.dbo.Row;

import java.io.Serializable;
import java.util.List;

public interface Engine extends Serializable {
    String getName();

    void setName(String name);

    String getTableSuffix();

    void setTableSuffix(String tableSuffix);

    boolean isBinaryFormat();

    void setBinaryFormat(boolean binaryFormat);

    String getComment();

    void setComment(String comment);

    void createTableFile(String dbName, String tableName);

    void writeRowToDisk(String dbName, String tableName, Row row, boolean verbose);

    List<Row> fetchRows(String tableName, List<String> fieldList, String relTerms, int limit, int offset);

    long getRowCount(String dbName, String tableName);
}
