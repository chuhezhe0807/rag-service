package com.chuhezhe.ragknowledgeservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class KnowledgeBaseDTO {

    /**
     * 知识库名称
     */
    @NotBlank(message = "{validation.knowledgeBase.name.notBlank}")
    private String name;

    /**
     * 知识库描述
     */
    @NotBlank(message = "{validation.knowledgeBase.desc.notBlank}")
    private String description;
}
