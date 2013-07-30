package com.kk.teachme.servlet;

import com.kk.teachme.db.FileDepot;
import com.kk.teachme.db.ProblemDepot;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Controller
@RequestMapping("/files")
public class FileController {

    @Autowired
    FileDepot fileDepot;

    @RequestMapping(value = "/{file_id:\\w+}")
    public void get(@PathVariable String file_id, HttpServletResponse response) {
        try {
            InputStream is = fileDepot.getById(file_id);
            response.addHeader("fileId", file_id);
            IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/upload")
    public @ResponseBody String upload(@RequestParam(required = false) MultipartFile file) {
        if (file == null) {
            return null;
        }
        try {
            //return "<img src='http://localhost:8080/files/" + fileDepot.addNewFile(file.getBytes()) + "' />";
            return fileDepot.addNewFile(file.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
