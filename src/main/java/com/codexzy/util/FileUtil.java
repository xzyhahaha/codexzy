package com.codexzy.util;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public final class FileUtil {

    private FileUtil() {
    }

    public static String save(MultipartFile file, String rootDir, String subDirectory) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }

        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String newFileName = UUID.randomUUID() + (extension == null ? "" : "." + extension);
        Path directory = Paths.get(rootDir, subDirectory).toAbsolutePath().normalize();

        try {
            Files.createDirectories(directory);
            Path target = directory.resolve(newFileName);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return Paths.get(subDirectory, newFileName).toString().replace("\\", "/");
        } catch (IOException ex) {
            throw new UncheckedIOException("保存文件失败", ex);
        }
    }
}
