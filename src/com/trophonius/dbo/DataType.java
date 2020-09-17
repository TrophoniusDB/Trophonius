package com.trophonius.dbo;

import java.io.Serializable;

public class DataType implements Serializable {

    private static final long serialVersionUID = 3002806822164145186L;
    private String name;
    private String className;
    private boolean complex;


    public DataType() {

    }

    public DataType(String name) {
        this.name = name;
    }

    public DataType(String name, String className, boolean complex) {
        this.name = name;
        this.className = className;
        this.complex = complex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public boolean isComplex() {
        return complex;
    }

    public void setComplex(boolean complex) {
        this.complex = complex;
    }

    public void setClassAndComplex(DataType dataType, String dataTypeString) {

        // Map Trophonius/SQL data types to Java classes
        // Set className and complex (if array, list, map or set)

        if (dataTypeString.equals("text") || dataTypeString.equals("string")) {
            dataType.setClassName("String");
        }

        if (dataTypeString.equals("date")) {
            dataType.setClassName("LocalDate");
        }

        if (dataTypeString.equals("datetime")) {
            dataType.setClassName("LocalDateTime");
        }

        if (dataTypeString.equals("int")) {
            dataType.setClassName("Integer");
        }

        if (dataTypeString.equals("decimal") || dataTypeString.equals("double")) {
            dataType.setClassName("Double");
        }

        if (dataTypeString.equals("float")) {
            dataType.setClassName("Float");
        }

        if (dataTypeString.equals("object")) {
            dataType.setClassName("Object");
            dataType.setComplex(true);
        }

        if (dataTypeString.equals("array(int)")) {
            dataType.setClassName("ArrayList<Integer>");
            dataType.setComplex(true);
        }

        if (dataTypeString.equals("array(text)")) {
            dataType.setClassName("ArrayList<String>");
            dataType.setComplex(true);
        }

        if (dataTypeString.equals("array(decimal)")) {
            dataType.setClassName("ArrayList<Double>");
            dataType.setComplex(true);
        }

        if (dataTypeString.equals("map(int,int)")) {
            dataType.setClassName("LinkedHashMap<Integer,Integer>");
            dataType.setComplex(true);
        }

        if (dataTypeString.equals("map(int,text)")) {
            dataType.setClassName("LinkedHashMap<Integer,String>");
            dataType.setComplex(true);
        }

        if (dataTypeString.equals("map(text,text)")) {
            dataType.setClassName("LinkedHashMap<String,String>");
            dataType.setComplex(true);
        }

        if (dataTypeString.equals("map(text,int)")) {
            dataType.setClassName("LinkedHashMap<String, Integer>");
            dataType.setComplex(true);
        }

        if (dataTypeString.equals("map(int,decimal)")) {
            dataType.setClassName("LinkedHashMap<Integer,Double>");
            dataType.setComplex(true);
        }

        if (dataTypeString.equals("map(decimal,decimal)")) {
            dataType.setClassName("LinkedHashMap<Double,Double>");
            dataType.setComplex(true);
        }

        if (dataTypeString.equals("map(decimal,int)")) {
            dataType.setClassName("LinkedHashMap<Double,Integer>");
            dataType.setComplex(true);
        }

        if (dataTypeString.equals("map(text,decimal)")) {
            dataType.setClassName("LinkedHashMap<String,Double>");
            dataType.setComplex(true);
        }

        if (dataTypeString.equals("map(decimal,text)")) {
            dataType.setClassName("LinkedHashMap<Double,String>");
            dataType.setComplex(true);
        }

        if (dataTypeString.equals("map(object,object)")) {
            dataType.setClassName("LinkedHashMap<Object,Object>");
            dataType.setComplex(true);
        }

    }

}
