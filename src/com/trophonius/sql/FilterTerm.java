package com.trophonius.sql;

public class FilterTerm {

    private String fieldName;
    private String fieldType;
    private String operand;
    private String value;
    private String functionName;
    private String functionParameters;

    public FilterTerm() {
    }

    public FilterTerm(String fieldName, String fieldType, String operand, String value, String functionName, String functionParameters) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.operand = operand;
        this.value = value;
        this.functionName = functionName;
        this.functionParameters = functionParameters;
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

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getFunctionParameters() {
        return functionParameters;
    }

    public void setFunctionParameters(String functionParameters) {
        this.functionParameters = functionParameters;
    }

    @Override
    public String toString() {
        return "FilterTerm{" +
                "fieldName='" + fieldName + '\'' +
                ", fieldType='" + fieldType + '\'' +
                ", operand='" + operand + '\'' +
                ", value='" + value + '\'' +
                ", functionName='" + functionName + '\'' +
                ", functionParameters='" + functionParameters + '\'' +
                '}';
    }
}
