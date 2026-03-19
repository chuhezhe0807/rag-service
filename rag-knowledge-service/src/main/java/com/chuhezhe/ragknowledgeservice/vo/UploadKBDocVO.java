package com.chuhezhe.ragknowledgeservice.vo;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UploadKBDocVO {

    // document_upload 表中的上传记录的ID
    private Integer uploadId;

    // document 表中的文档ID
    private Integer documentId;

    // 文档名称
    private String fileName;

    // 文档临时路径
    private String tempPath;

    // 文档上传状态
    private Status status;

    // message
    private String message;

    // 是否跳过文档处理
    private Boolean skipProcessing;

    public enum Status {
        EXISTS("exists"),
        PENDING("pending");

        @Getter
        private final String value;

        Status(String value) {
            this.value = value;
        }

        // 根据字符串查找枚举
        public static Status fromValue(String value) {
            for (Status s : Status.values()) {
                if (s.value.equals(value)) {
                    return s;
                }
            }
            return null;
        }
    }
}
