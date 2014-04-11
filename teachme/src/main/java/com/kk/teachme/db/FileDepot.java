package com.kk.teachme.db;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Required;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class FileDepot {

    private String path;

    public InputStream get(String id) {
        try {
            return new BufferedInputStream(new FileInputStream(new File(path + id)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String add(byte[] file) {
        try {

            String id = hash(file);
            File path = new File(this.path + id);

            FileOutputStream output = new FileOutputStream(path);
            IOUtils.write(file, output);

            return id;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private static String hash(byte[] file) {
        try {
            return new String(Hex.encodeHex(MessageDigest.getInstance("MD5").digest(file)));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Required
    public void setPath(String path) {
        this.path = path;
    }

}
