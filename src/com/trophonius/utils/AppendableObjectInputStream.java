package com.trophonius.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;

public class AppendableObjectInputStream extends ObjectInputStream  {

    public AppendableObjectInputStream(InputStream in) throws IOException {
        super(in);
    }

    @Override
    public void readStreamHeader() throws IOException {
        // do not write a header
    }


}
