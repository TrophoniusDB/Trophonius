package com.trophonius.sql;

import com.trophonius.dbo.Database;
import com.trophonius.dbo.Field;
import com.trophonius.dbo.Row;
import com.trophonius.dbo.Table;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.trophonius.Main.startTime;
import static com.trophonius.Main.timing;

/**
 * Class for SQL DML Statements - select, insert, update delete
 * @param <E> generic type for handling fields
 */
public class DML<E> {

    public String prompt = "/";
    public Database currentDB;
    private String sql = "";
    private Table currentTable;
    private Field tableField;
    private Row<Serializable> row = new Row<>();

    /***
     * Constructor that calls method to parse SQL
     * @param prompt prompt that will be changed when a database is selected
     * @param currentDB DBO selected by user
     * @param sql supplied SQL Statement
     */
    public DML(String prompt, Database currentDB, String sql) {
        this.sql = sql;
        this.prompt = prompt;
        this.currentDB = currentDB;
        parseSql(currentDB, sql);
    }

    private void parseSql(Database currentDB, String sql) {

        // Prepare SQL - Create Array of words and remove =
        String[] words = sql.split("[ ]");
        String charset = "", collation = "";

        // DML SQL METHODS

        // SQL INSERT <INTO> <TABLENAME>
        if (sql.toLowerCase().startsWith("insert") || sql.toLowerCase().startsWith("insert into")) {
            // Method to insert new row into existing table
          Insert insert = new Insert(sql,true);

        } // END INSERT INTO

        //  \i or import - sql from <filename>
        if (sql.toLowerCase().startsWith("\\i") || sql.toLowerCase().startsWith("import")) {
            String fileName = words[1];
            importSql(fileName);

        } // end import from file


        // SQL SELECT <fields...> FROM <TABLENAME>
        if (sql.toLowerCase().startsWith("select") && words.length>3) {

            // Determine tablename
            String tableName = null;
            for (int i = 0; i < words.length; i++) {
                if (words[i].toLowerCase().equals("from")) {
                    tableName = words[i + 1];
                }
            }

            try {
                Table currentTable = currentDB.getTables().get(tableName);
                // Get table suffix to find the table
                String tableSuffix = currentTable.getEngine().getTableSuffix();

                // Check if table not found
                if (!java.nio.file.Files.isRegularFile(Paths.get("data/" + currentDB.getDbName() + "/" + tableName + "."+tableSuffix))) {
                    // Table file not found. Return to sender
                    System.out.println("Table not found.");
                    return;
                } else {
                    // Table exists - open it, fetch row and return fields.
                    // If timing is on - set start time
                    if(timing) startTime = System.currentTimeMillis();
                    // dispatch to Select
                    Select select = new Select(tableName, sql);
                    // Add timing info
                    if(timing) System.out.println(" in "+(System.currentTimeMillis()-startTime)+ " millis");
                }

            } catch (Exception e) {
                System.out.println(currentDB.getDbName() == null ? "No Database selected. Select one first..." :
                "Could not find table \""+tableName+"\" in database \""+currentDB.getDbName()+"\"");
                return;
            }


        } // end Select

    } // end parseSQL

    /**
     * Method for importing a SQL file with only insert statements
     * @param filename Path to file with SQL statements
     */
    public void importSql(String filename) {

        // TODO check for other statements than insert
        if(timing) { startTime = System.currentTimeMillis(); }
        int number;
        try {
            // insert into tableName
            number = (int) Files.lines(Path.of(filename)).map(line -> new Insert(line.toString(), false)).count();
            String line = Files.lines(Path.of(filename)).findFirst().toString();
            String tableName = line.substring(line.indexOf("into")+5,line.indexOf("(")-1);
            Table table = currentDB.getTables().get(tableName);
            table.setRowCount(table.getRowCount() + number);
            try {
                Database.saveDatabase(currentDB);
            } catch (IOException e) {
                System.out.println("New row count not saved...");
                e.printStackTrace();
            }
            System.out.print(number);
            System.out.print(number > 1 ? " rows " : " row ");
            System.out.print("from file inserted");

            // Insert as List<String>
           // Insert in = new Insert(Files.lines(Path.of(filename)).collect(Collectors.toList()));

        } catch (IOException e) {
            System.out.println("SQL File not found");
            e.printStackTrace();
        }
        System.out.println(timing ? " in "+(System.currentTimeMillis()-startTime)+ " millis" : "");
    } // end importSQL

    }  // end class
