package com.trophonius.Engines;

import com.trophonius.Main;
import com.trophonius.dbo.Row;
import com.trophonius.sql.FilterTerm;
import com.trophonius.utils.AppendableObjectInputStream;
import com.trophonius.utils.AppendableObjectOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * OBJECT ENGINE - a storage engine that stores serialized Java Objects.
 */
public class ObjectEngine implements Engine {

    private static final long serialVersionUID = -2645684320509863174L;
    private String name;
    private String tableSuffix;
    private boolean binaryFormat = false;
    private String comment;
    Logger logger = LoggerFactory.getLogger(ObjectEngine.class);


    public ObjectEngine() {

        this.setName("objectEngine");
        this.setBinaryFormat(true);
        this.setTableSuffix("tbl");
        this.setComment("Not suitable for large tables, but good for storing serializable java objects");

    } // END CONSTRUCTOR


    /**
     * Counts table rows
     *
     * @param dbName    Name of the Database
     * @param tableName name of the Table
     * @return returns rowcount as long
     */
    @Override
    public long getRowCount(String dbName, String tableName) {
        long rowCount = 0;
        try {
            FileInputStream dbFileIn = new FileInputStream("data/" + Main.currentDB.getDbName() + "/" + tableName + "." + tableSuffix);
            AppendableObjectInputStream is = new AppendableObjectInputStream(new BufferedInputStream(dbFileIn));

            while (true) {
                try {
                    Row theRow = (Row) is.readObject();
                    rowCount++;
                } catch (EOFException e) {
                    break;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } // end while

            is.close();
            dbFileIn.close();

        } catch (IOException e) {
            System.out.println("Error! Could not open table file...");
            e.printStackTrace();
            logger.error("Could not open table file : " + e.getMessage());
        }
        return rowCount;
    } // end getRowCount

    /**
     * Creates the physical table file
     *
     * @param dbName    Name of the Database to which the table belongs
     * @param tableName Name of the Table for which to create a file
     */
    public void createTableFile(String dbName, String tableName) {

        // check that table file not  exists in data directory
        if (!Files.isRegularFile(Paths.get("data/" + dbName + "/" + tableName + "." + this.getTableSuffix()))) {

            try {
                // create table file and write table stats
                FileOutputStream dbFileOut = new FileOutputStream("data/" + dbName + "/" + tableName + "." + this.getTableSuffix(), true);
                AppendableObjectOutputStream oStr = new AppendableObjectOutputStream(new BufferedOutputStream(dbFileOut));
                oStr.flush();
                oStr.close();
                dbFileOut.flush();
                dbFileOut.close();
            } catch (
                    IOException e) {
                System.out.println("Table could not we written to disk: ");
                e.printStackTrace();
                logger.error("Table could not we written to disk: " + e.getMessage());
            }

        } else {
            // Table file exists
            System.out.println("Table \"" + tableName + "\" already exists");
        }

    } // END  createTableFile

    /**
     * Serializes and writes rows to disk
     *
     * @param dbName    Name of Database
     * @param tableName Name of Table
     * @param row       one row object
     * @param verbose   whether to give feedback for each stored row or not
     */
    @Override
    public void writeRowToDisk(String dbName, String tableName, Row row, boolean verbose) {

        try {
            // check that table file exists in data directory
            if (java.nio.file.Files.isRegularFile(Paths.get("data/" + dbName + "/" + tableName + ".tbl"))) {

                // Open table file for writing, and append map of primary key + row to the file

                FileOutputStream dbFileOut = new FileOutputStream("data/" + dbName + "/" + tableName + ".tbl", true);
                AppendableObjectOutputStream oStr = new AppendableObjectOutputStream(new BufferedOutputStream(dbFileOut));
                oStr.writeObject(row);
                oStr.flush();
                oStr.close();
                dbFileOut.flush();
                dbFileOut.close();
                if (verbose) {
                    System.out.println("Success: 1 row written to table: " + tableName);
                }
            } else {
                // Table file does not exists
                System.out.println("Table file does not exist");
            }

        } catch (IOException e) {
            System.out.println("Row could not we written to file for table: " + tableName);
            e.printStackTrace();
            logger.error("Row could not we written to file for table: " + tableName + " - " + e.getMessage());
        }

    }

    /**
     * Fetches rows from table according to SQL terms and returns them as a List
     *
     * @param tableName   Name of the Table
     * @param fieldList   List of table fields to be teched
     * @param filterTerms List of FilterTerm objects
     * @param limit       Number of rows to fecth
     * @param offset      Number of rows to skip
     * @return Returns a List of row - objects
     */
    @Override
    public List<Row> fetchRows(String tableName, List<String> fieldList, List<FilterTerm> filterTerms, int limit, int offset) {

        List<Row> rows = new ArrayList<>();

        // Open Table file to find fields and check with SQL that all fields are present in table
        try {
            FileInputStream dbFileIn = new FileInputStream("data/" + Main.currentDB.getDbName() + "/" + tableName + "." + tableSuffix);
            AppendableObjectInputStream is = new AppendableObjectInputStream(new BufferedInputStream(dbFileIn));

            long rowCount = 0;
            while (rowCount < limit + offset) {
                try {
                    Row theRow = (Row) is.readObject();
                    if (rowCount >= offset & filterRow(theRow, filterTerms)) {
                        rows.add(theRow);
                    }
                    rowCount++;
                } catch (EOFException e) {
                    break;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } // end while

            is.close();
            dbFileIn.close();

        } catch (IOException e) {
            System.out.println("Error! Could not open table file...");
            e.printStackTrace();
        }
        return rows;
    } // end

    private <E> boolean filterRow(Row row, List<FilterTerm> filterTerms) {
        boolean retrieve = false;

        if (filterTerms.isEmpty()) {
            retrieve = true;
        } else {

            for (FilterTerm term : filterTerms) {
                // Get the stored value and compare it to the filter term value
                String fieldValue = row.getRow().get(term.getFieldName()).toString().toLowerCase();
                String fieldType = term.getFieldType();
                String termValue = term.getValue().toLowerCase();
                String operand = term.getOperand();

                switch (operand) {
                    case "=":
                        if (fieldValue.equals(termValue)) {
                            retrieve = true;
                        }
                        break;
                    case ">":
                        switch (fieldType) {
                            case "Integer":
                            try {
                                if (Integer.parseInt(fieldValue) > Integer.parseInt(termValue)) {
                                    retrieve = true;
                                }
                            } catch (Exception e) {
                                System.out.println("Not an integer " + e.getMessage());
                            }
                            break;
                            case "Decimal":
                                try {
                                    if (Double.parseDouble(fieldValue) > Double.parseDouble(termValue)) {
                                        retrieve = true;
                                    }
                                } catch (Exception e) {
                                    System.out.println("Not a Decimal " + e.getMessage());
                                }
                            break;
                            case "Float":
                                try {
                                    if Float.parseFloat(fieldValue) > Float.parseFloat(termValue)) {
                                        retrieve = true;
                                    }
                                } catch (Exception e) {
                                    System.out.println("Not a Float " + e.getMessage());
                                }
                                break;
                        }

                        break;
                    case "<":
                        switch (fieldType) {
                            case "Integer":
                                try {
                                    if (Integer.parseInt(fieldValue) < Integer.parseInt(termValue)) {
                                        retrieve = true;
                                    }
                                } catch (Exception e) {
                                    System.out.println("Not an integer " + e.getMessage());
                                }
                                break;
                            case "Decimal":
                                try {
                                    if (Double.parseDouble(fieldValue) < Double.parseDouble(termValue)) {
                                        retrieve = true;
                                    }
                                } catch (Exception e) {
                                    System.out.println("Not a Decimal " + e.getMessage());
                                }
                                break;
                            case "Float":
                                try {
                                    if Float.parseFloat(fieldValue) < Float.parseFloat(termValue)) {
                                        retrieve = true;
                                    }
                                } catch (Exception e) {
                                    System.out.println("Not a Float " + e.getMessage());
                                }
                                break;
                        }

                        break;
                    case "!=":
                    case "<>":
                        if (!fieldValue.equals(termValue)) {
                            retrieve = true;
                        }
                        break;
                }
            }
        }
        return retrieve;
    } // END filterRow


    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getTableSuffix() {
        return tableSuffix;
    }

    @Override
    public void setTableSuffix(String tableSuffix) {
        this.tableSuffix = tableSuffix;
    }

    @Override
    public boolean isBinaryFormat() {
        return binaryFormat;
    }

    @Override
    public void setBinaryFormat(boolean binaryFormat) {
        this.binaryFormat = binaryFormat;
    }

    @Override
    public String getComment() {
        return comment;
    }

    @Override
    public void setComment(String comment) {
        this.comment = comment;
    }


} // END CLASS
