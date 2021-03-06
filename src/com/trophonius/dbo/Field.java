package com.trophonius.dbo;

import java.io.Serializable;

public class Field implements Serializable {

    private static final long serialVersionUID = -5851856856760605828L;
    private String name;
    private DataType dataType;
    private boolean primaryKey;
    private boolean notNull;
    private boolean autoIncrement;
    private boolean unique;


    public Field() {
    }

    public Field(String name, DataType dataType) {
        this.name = name;
        this.dataType = dataType;
    }

    public Field(String name, DataType dataType, boolean primaryKey, boolean notNull, boolean autoIncrement) {
        this.name = name;
        this.dataType = dataType;
        this.primaryKey = primaryKey;
        this.notNull = notNull;
        this.autoIncrement = autoIncrement;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
        this.notNull = true;
    }

    public boolean isNotNull() {
        return notNull;
    }

    public void setNotNull(boolean notNull) {
        this.notNull = notNull;
    }

    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    public void setFieldProperties(Field f1, String field) {

        // strip extra blanks
        field = field.strip();
        // Split into array of each word in field statement
        String[] fieldElement = field.split(" ");

        f1.setName(fieldElement[0]);

        // Data Type Conversion

        String dataTypeString = fieldElement[1].toLowerCase();

        DataType dataType = new DataType();
        dataType.setName(dataTypeString);
        dataType.setClassAndComplex(dataType, dataTypeString);

        f1.setDataType(dataType);

        if (field.contains("primary key")) {
            f1.setPrimaryKey(true);
            f1.setNotNull(true);
        }

        if (field.contains("auto_increment") || field.contains("identity")) {
            f1.setAutoIncrement(true);
        }

        if (field.contains("not null")) {
            f1.setNotNull(true);
        }

        if (field.contains("unique")) {
            f1.setUnique(true);
        }

    }

}
