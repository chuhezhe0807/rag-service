package com.chuhezhe.ragdocumentservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProcessDocDTO {
    /**
     * 知识库id
     */
     private Integer knowledgeId;

    /**
     * 文档相关
     */
    private List<ProcessDocItemDTO> processDocItemDTOList;

    @Data
    public static class ProcessDocItemDTO {
        /**
         * 文档上传记录id
         */
        private Integer documentUploadId;

        /**
         * 是否跳过处理
         */
        private Boolean skipProcess;
    }
}
