package com.chuhezhe.ragcommonservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QueryDocDTO {
    private Integer knowledgeBaseId;

    private String fileHash;

    private String fileName;
}
