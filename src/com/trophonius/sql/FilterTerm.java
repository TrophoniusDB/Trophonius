package com.trophonius.sql;

public class FilterTerm {

    private String fieldName;
    private String fieldType;
    private String operand;
    private String value;

    public FilterTerm() {
    }

    public FilterTerm(String fieldName, String fieldType, String operand, String value) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.operand = operand;
        this.value = value;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getOperand() {
        return operand;
    }

    public void setOperand(String operand) {
        this.operand = operand;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "FilterTerm{" +
                "fieldName='" + fieldName + '\'' +
                "fieldType='" + fieldType + '\'' +
                ", operand='" + operand + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
