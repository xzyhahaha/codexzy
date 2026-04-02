package com.codexzy.service;

import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface FileService {

    String save(MultipartFile file, String subDirectory);

    Path resolve(String relativePath);

    void delete(String relativePath);
}
