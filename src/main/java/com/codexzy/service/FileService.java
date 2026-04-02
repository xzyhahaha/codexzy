package com.codexzy.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    String save(MultipartFile file, String subDirectory);
}
