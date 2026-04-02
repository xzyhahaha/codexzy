package com.codexzy.service.impl;

import com.codexzy.service.FileService;
import com.codexzy.util.FileUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileServiceImpl implements FileService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public String save(MultipartFile file, String subDirectory) {
        return FileUtil.save(file, uploadDir, subDirectory);
    }
}
