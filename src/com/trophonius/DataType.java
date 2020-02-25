package com.trophonius;

import java.io.Serializable;

public class DataType implements Serializable {

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
}
