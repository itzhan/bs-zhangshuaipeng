package com.rescue.petrescue.util;

import com.rescue.petrescue.common.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Component
public class FileUtil {

    @Value("${upload.path}")
    private String uploadPath;

    public String upload(MultipartFile file, String subDir) {
        if (file == null || file.isEmpty()) throw new BusinessException(400, "文件不能为空");
        String originalFilename = file.getOriginalFilename();
        String ext = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
        String filename = UUID.randomUUID().toString().replace("-", "") + ext;
        String dirPath = uploadPath + subDir + "/";
        File dir = new File(dirPath);
        if (!dir.exists()) dir.mkdirs();
        try {
            file.transferTo(new File(dirPath + filename));
        } catch (IOException e) {
            throw new BusinessException(500, "文件上传失败");
        }
        return "/upload/" + subDir + "/" + filename;
    }
}
