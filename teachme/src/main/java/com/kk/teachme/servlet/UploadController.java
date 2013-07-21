package com.kk.teachme.servlet;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * @author Nadya Zabrodina
 */
@Controller
@RequestMapping("/upload")
public class UploadController {
    @RequestMapping(value = "/page")
    public String page() {
        return "upload";
    }

    //TODO refactor and rewrite it
    @ResponseBody
    @RequestMapping(value = "/save")
    public String handleUpload(@RequestParam(value = "file", required = false) org.springframework.web.multipart.MultipartFile multipartFile, HttpServletResponse httpServletResponse) {
        String orgName = multipartFile.getOriginalFilename();
        //file was not created! bug
        String filePath = "/my_uploads/" + orgName;
        File dest = new File(filePath);
        try {
            multipartFile.transferTo(dest);
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return "File uploaded failed:" + orgName;

        } catch (IOException e) {
            e.printStackTrace();
            return "File uploaded failed:" + orgName;
        }
        return "File uploaded:" + orgName;
    }
}