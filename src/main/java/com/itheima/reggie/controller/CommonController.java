package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {
    @Value("${upload.path}")
    public String basePath;

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        log.info("开始文件上传");
//        log.info(file.toString());
        String fileName = UUID.randomUUID().toString();
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        File dir = new File(basePath);
        if (!dir.exists())
            dir.mkdirs();
        try {
            //接收并存储上传文件
            file.transferTo(new File(basePath + fileName + suffix));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(fileName + suffix);
    }

    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        int temp = 0;
        byte[] bytes = new byte[1024];
        try {
            FileInputStream fis = new FileInputStream(new File(basePath + name));
            ServletOutputStream os = response.getOutputStream();
            response.setContentType("image/jpeg");

            while ((temp = fis.read(bytes)) != -1){
                os.write(bytes,0, temp);
                os.flush();
            }
            os.close();
            fis.close();

/*            BufferedInputStream bis = new BufferedInputStream(fis);
            BufferedOutputStream bos = new BufferedOutputStream(os);
            while ((temp = bis.read()) != -1){
                bos.write(temp);
                bos.flush();
            }
            bis.close();
            fis.close();
            bos.close();
            os.close();*/

//方法三
//            IOUtils.copy(fis, os);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
