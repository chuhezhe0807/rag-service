package com.chuhezhe.ragcommonservice.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KnowledgeBaseVO {

    /**
     * 知识库名称
     */
    private String name;

     /**
      * 知识库描述
      */
    private String description;
}
