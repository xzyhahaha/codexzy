package com.codexzy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.codexzy.dto.MemoCategoryFormDTO;
import com.codexzy.dto.MemoUploadDTO;
import com.codexzy.entity.MemoCategory;
import com.codexzy.entity.MemoFile;
import com.codexzy.mapper.MemoCategoryMapper;
import com.codexzy.mapper.MemoFileMapper;
import com.codexzy.service.FileService;
import com.codexzy.service.MemoService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class MemoServiceImpl implements MemoService {

    @Resource
    private MemoCategoryMapper memoCategoryMapper;

    @Resource
    private MemoFileMapper memoFileMapper;

    @Resource
    private FileService fileService;

    @Override
    public long countByUserId(Long userId) {
        return memoFileMapper.selectCount(new LambdaQueryWrapper<MemoFile>()
                .eq(MemoFile::getUserId, userId));
    }

    @Override
    public List<MemoCategory> listCategories(Long userId) {
        return memoCategoryMapper.selectList(new LambdaQueryWrapper<MemoCategory>()
                .eq(MemoCategory::getUserId, userId)
                .orderByAsc(MemoCategory::getSortOrder)
                .orderByAsc(MemoCategory::getId));
    }

    @Override
    public Map<Long, List<MemoFile>> listFilesGroupedByCategory(Long userId) {
        List<MemoFile> files = memoFileMapper.selectList(new LambdaQueryWrapper<MemoFile>()
                .eq(MemoFile::getUserId, userId)
                .orderByDesc(MemoFile::getCreateTime));
        Map<Long, List<MemoFile>> result = new LinkedHashMap<>();
        for (MemoFile file : files) {
            result.computeIfAbsent(file.getCategoryId(), key -> new ArrayList<>()).add(file);
        }
        return result;
    }

    @Override
    @Transactional
    public void createCategory(Long userId, MemoCategoryFormDTO formDTO) {
        String categoryName = formDTO.getCategoryName() == null ? "" : formDTO.getCategoryName().trim();
        MemoCategory existingCategory = memoCategoryMapper.selectOne(new LambdaQueryWrapper<MemoCategory>()
                .eq(MemoCategory::getUserId, userId)
                .eq(MemoCategory::getCategoryName, categoryName)
                .last("LIMIT 1"));
        if (existingCategory != null) {
            throw new IllegalArgumentException("该分类已存在");
        }

        MemoCategory category = new MemoCategory();
        category.setUserId(userId);
        category.setCategoryName(categoryName);
        category.setSortOrder(listCategories(userId).size());
        memoCategoryMapper.insert(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long userId, Long categoryId) {
        MemoCategory category = memoCategoryMapper.selectById(categoryId);
        if (category == null || !userId.equals(category.getUserId())) {
            throw new IllegalArgumentException("分类不存在");
        }

        long fileCount = memoFileMapper.selectCount(new LambdaQueryWrapper<MemoFile>()
                .eq(MemoFile::getCategoryId, categoryId));
        if (fileCount > 0) {
            throw new IllegalArgumentException("分类下还有文件，不能直接删除");
        }

        memoCategoryMapper.deleteById(categoryId);
    }

    @Override
    @Transactional
    public void uploadFile(Long userId, MemoUploadDTO uploadDTO) {
        if (uploadDTO.getFile() == null || uploadDTO.getFile().isEmpty()) {
            throw new IllegalArgumentException("请选择要上传的文件");
        }

        MemoCategory category = memoCategoryMapper.selectById(uploadDTO.getCategoryId());
        if (category == null || !userId.equals(category.getUserId())) {
            throw new IllegalArgumentException("分类不存在");
        }

        String relativePath = fileService.save(uploadDTO.getFile(), "memo");
        MemoFile memoFile = new MemoFile();
        memoFile.setUserId(userId);
        memoFile.setCategoryId(uploadDTO.getCategoryId());
        memoFile.setFileName(uploadDTO.getFile().getOriginalFilename());
        memoFile.setFilePath(relativePath);
        memoFile.setFileSize(uploadDTO.getFile().getSize());
        memoFile.setFileType(uploadDTO.getFile().getContentType());
        memoFile.setRemark(StringUtils.hasText(uploadDTO.getRemark()) ? uploadDTO.getRemark().trim() : null);
        memoFileMapper.insert(memoFile);
    }

    @Override
    public MemoFile getFile(Long userId, Long fileId) {
        MemoFile memoFile = memoFileMapper.selectById(fileId);
        if (memoFile == null || !userId.equals(memoFile.getUserId())) {
            throw new IllegalArgumentException("文件不存在");
        }
        return memoFile;
    }

    @Override
    @Transactional
    public void deleteFile(Long userId, Long fileId) {
        MemoFile memoFile = getFile(userId, fileId);
        memoFileMapper.deleteById(memoFile.getId());
        fileService.delete(memoFile.getFilePath());
    }
}
