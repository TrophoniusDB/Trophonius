package com.trophonius.dbo;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Objects;

public class Row<E> implements Serializable {

    private static final long serialVersionUID = -544016776593193705L;
    // One row consists of a LinkedHashMap with the fieldName as the key and the field's value as the value
    private LinkedHashMap<String, E> row = new LinkedHashMap<>();

    public Row() {

    }

    public <E> E getValue(String key)  {
        return (E) row.get(key);
    }

    public LinkedHashMap<String, E> getRow() {
        return row;
    }

    public int getMaxValueLength() {
       return this.row.values().stream().filter(Objects::nonNull).mapToInt(e -> e.toString().length()).max().getAsInt();
    }

    public int getMaxKeyLength() {
        return this.row.keySet().stream().filter(Objects::nonNull).mapToInt(e -> e.length()).max().getAsInt();
    }

    public void setRow(LinkedHashMap<String, E> row) {
        this.row = row;
    }

    public void addToRow (String fieldName, E value ) {
        this.row.put(fieldName, value);
    }
    
    @Override
    public String toString() {
        return "Row=" + row ;
    }
}
