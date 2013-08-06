package com.kk.teachme.db;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Required;

import java.io.*;
import java.security.MessageDigest;


public class FileDepot {

    private String filesDirectory;

    public InputStream getById(String id) {
        try {
            return new BufferedInputStream(new FileInputStream(new File(filesDirectory + id)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String addNewFile(byte[] file) {
        try {

            String id = getHash(file);
            File path = new File(filesDirectory + id);
            path.mkdir();

            FileOutputStream output = new FileOutputStream(path);
            IOUtils.write(file, output);

            return id;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private String getHash(byte[] file) {
        try {
            return new String(Hex.encodeHex(MessageDigest.getInstance("MD5").digest(file)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Required
    public void setFilesDirectory(String filesDirectory) {
        this.filesDirectory = filesDirectory;
    }

}
