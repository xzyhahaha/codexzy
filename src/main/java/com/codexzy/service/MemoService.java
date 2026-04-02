package com.codexzy.service;

import com.codexzy.dto.MemoCategoryFormDTO;
import com.codexzy.dto.MemoUploadDTO;
import com.codexzy.entity.MemoCategory;
import com.codexzy.entity.MemoFile;

import java.util.List;
import java.util.Map;

public interface MemoService {

    long countByUserId(Long userId);

    List<MemoCategory> listCategories(Long userId);

    Map<Long, List<MemoFile>> listFilesGroupedByCategory(Long userId);

    void createCategory(Long userId, MemoCategoryFormDTO formDTO);

    void deleteCategory(Long userId, Long categoryId);

    void uploadFile(Long userId, MemoUploadDTO uploadDTO);

    MemoFile getFile(Long userId, Long fileId);

    void deleteFile(Long userId, Long fileId);
}
