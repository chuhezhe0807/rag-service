package com.chuhezhe.ragknowledgeservice.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.chuhezhe.ragknowledgeservice.dto.KnowledgeBaseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("`knowledge_bases`")
public class KnowledgeBase {

    @TableField("`id`")
    private Integer id;

    @TableField("`name`")
    private String name;

    @TableField("`description`")
    private String description;

    @TableField("`user_id`")
    private Integer userId;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    public static KnowledgeBase fromDTO(KnowledgeBaseDTO knowledgeBaseDTO) {

        return KnowledgeBase.builder()
                .name(knowledgeBaseDTO.getName())
                .description(knowledgeBaseDTO.getDescription())
                .build();
    }
}
