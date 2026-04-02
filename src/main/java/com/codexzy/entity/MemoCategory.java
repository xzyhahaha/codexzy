package com.codexzy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("memo_category")
public class MemoCategory {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String categoryName;

    private Integer sortOrder;

    private LocalDateTime createTime;
}
