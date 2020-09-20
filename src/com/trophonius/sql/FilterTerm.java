package com.trophonius.sql;

public class FilterTerm {

    private String fieldName;
    private String operand;
    private String value;

    public FilterTerm() {
    }

    public FilterTerm(String fieldName, String operand, String value) {
        this.fieldName = fieldName;
        this.operand = operand;
        this.value = value;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
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
                ", operand='" + operand + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
