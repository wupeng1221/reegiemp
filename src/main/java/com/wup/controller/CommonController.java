package com.wup.controller;

import cn.hutool.core.util.IdUtil;
import com.wup.common.Result;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

// TODO 这里的全部功能可以改造成通过阿里云oos进行实现
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {
    @Value("${upload.path}")
    private String basePath;
    @Autowired
    private HttpServletResponse httpServletResponse;
    @PostMapping("/upload")
    //@RequestParam支持’application/json’，也同样支持multipart/form-data请求
    //@RequestPart这个注解用在multipart/form-data表单提交请求的方法上。
    //支持的请求方法的方式MultipartFile，属于Spring的MultipartResolver类。这个请求是通过http协议传输的
    //当请求头中指定Content-Type:multipart/form-data时，传递的json参数，@RequestPart注解可以用对象来接收，@RequestParam只能用字符串接收
    public Result<String> upload(@RequestPart("file") MultipartFile f) throws IOException {
        //此时f是服务器上的临时文件，需要转存到指定位置，本次请求完成后临时文件会删除
        //生成随机的文件名
        String originalFilename = f.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String targetFileName = IdUtil.fastUUID() + suffix;
        //判断目录是否存在
        File dir = new File(basePath);
        if (!dir.exists()) {
            //不存在此目录，创建
            dir.mkdirs();
        }

        try {
            f.transferTo(new File(basePath + targetFileName));
        } catch (IOException e) {
            log.info(e.getMessage());
        }
        return Result.success(targetFileName);
    }

    @GetMapping("/download")
    public void download(String name) {
        try {
            //输入流读取文件
            FileInputStream fis = new FileInputStream(new File(basePath + name));
            //http输出流，写会浏览器，在浏览器中展示
            ServletOutputStream fos = httpServletResponse.getOutputStream();
            httpServletResponse.setContentType("image/jpeg");
            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fis.read(bytes)) != -1) {
                fos.write(bytes, 0, len);
                fos.flush();
            }
            fos.close();
            fis.close();
        } catch (Exception e) {
            log.info(e.getMessage());
        }

    }
}
