package com.chuhezhe.ragknowledgeservice.feign;

import com.chuhezhe.common.entity.Result;
import com.chuhezhe.ragcommonservice.dto.QueryDocDTO;
import com.chuhezhe.ragcommonservice.dto.UploadDocRecordDTO;
import com.chuhezhe.ragcommonservice.dto.UploadDocDTO;
import com.chuhezhe.ragcommonservice.vo.DocumentVO;
import com.chuhezhe.ragcommonservice.vo.UploadDocResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("rag-document-service")
public interface DocumentServiceClient {

    /**
     * 查询文档
     */
    @GetMapping("/api/ai/documents/query")
     Result<List<DocumentVO>> queryDocument(@RequestBody QueryDocDTO queryDocDTO);

    /**
     * 创建文档上传记录
     */
    @PostMapping("/api/ai/documents/upload/record")
    Result<Integer> addUploadDocumentRecord(@RequestBody UploadDocRecordDTO uploadDocRecordDTO);

    @PostMapping("/api/ai/documents/upload")
    Result<UploadDocResult> uploadDocument(@RequestBody UploadDocDTO uploadDocDTO);
}
