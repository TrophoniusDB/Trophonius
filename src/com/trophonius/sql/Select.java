package com.trophonius.sql;

import com.trophonius.Engines.Engine;
import com.trophonius.dbo.Field;
import com.trophonius.dbo.Row;
import com.trophonius.dbo.TableStats;
import com.trophonius.utils.AppendableObjectInputStream;
import com.trophonius.utils.HelperMethods;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.trophonius.Main.currentDB;

public class Select {

    public Select() {
    }

    public Select (String tableName, String sql) {

        // Find table engine
        Engine engine = currentDB.getTables().get(tableName).getEngine();
        // Find table file suffix
        String tableSuffix = engine.getTableSuffix();

        // Check if table file is present and dispatch to engine for fetching rows
        if(Files.exists(Paths.get("data/" + currentDB.getDbName() + "/" + tableName + "."+tableSuffix))) {
            engine.fetchRows(tableName,sql);
        } else {
            System.out.println("Table file not found...");
            return;
        }


    } // END SELECT

} // END CLASS
