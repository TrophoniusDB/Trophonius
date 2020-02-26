package com.trophonius.utils;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;

public class HelperMethods {

    public static void recursiveDelete(File file) {
        //to end the recursive loop
        if (!file.exists())
            return;

        //if directory, go inside and call recursively
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                //call recursively
                recursiveDelete(f);
            }
        }
        //call delete to delete files and empty directory
        file.delete();
        System.out.println("Deleted file/folder: " + file.getAbsolutePath());
    }

    public static ArrayList<String> findDatabases() {

        ArrayList<String> dbNames = new ArrayList<>();

        if (java.nio.file.Files.isDirectory(Paths.get("data"))) {
            File rootDir = new File("data");

            for (File dir : rootDir.listFiles()) {
                if (dir.isDirectory()) {

                    dbNames.add(dir.getName());

                }
            }
        }

        return dbNames;
    }

} // END CLASS


