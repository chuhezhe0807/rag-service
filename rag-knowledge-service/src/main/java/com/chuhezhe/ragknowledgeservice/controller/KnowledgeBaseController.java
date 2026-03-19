package com.chuhezhe.ragknowledgeservice.controller;

import com.chuhezhe.common.entity.Result;
import com.chuhezhe.ragcommonservice.holder.UserInfoContextHolder;
import com.chuhezhe.ragcommonservice.vo.KnowledgeBaseVO;
import com.chuhezhe.ragcommonservice.vo.UserVO;
import com.chuhezhe.ragknowledgeservice.dto.KnowledgeBaseDTO;
import com.chuhezhe.ragknowledgeservice.service.KnowledgeBaseService;
import com.chuhezhe.ragknowledgeservice.vo.UploadKBDocVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Validated
@RestController
@RequestMapping("/knowledge-base")
@RequiredArgsConstructor
public class KnowledgeBaseController {

    private final KnowledgeBaseService knowledgeBaseService;

    /**
     * 获取所有知识库
     */
    @GetMapping("")
    public Result<List<KnowledgeBaseVO>> getAllKnowledgeBases() {
        UserVO userVO = UserInfoContextHolder.getUserInfo();
        return knowledgeBaseService.getKnowledgeBases(userVO, null);
    }

    /**
     * 获取指定知识库
     */
    @GetMapping("/{kb_id}")
    public Result<List<KnowledgeBaseVO>> getKnowledgeBase(@PathVariable("kb_id") Integer kbId) {
        UserVO userVO = UserInfoContextHolder.getUserInfo();
        return knowledgeBaseService.getKnowledgeBases(userVO, kbId);
    }

    /**
     * 创建知识库
     */
    @PostMapping("")
    public Result<KnowledgeBaseVO> createKnowledgeBase(@Valid @RequestBody KnowledgeBaseDTO knowledgeBaseDTO) {
        UserVO userVO = UserInfoContextHolder.getUserInfo();
        return knowledgeBaseService.createKnowledgeBase(userVO, knowledgeBaseDTO);
    }

    /**
     * 删除知识库
     */
    @DeleteMapping("/{kb_id}")
    public Result<Void> deleteKnowledgeBase(@PathVariable("kb_id") Long kbId) {
        UserVO userVO = UserInfoContextHolder.getUserInfo();
        return knowledgeBaseService.deleteKnowledgeBase(userVO, kbId);
    }

    /**
     * 更新知识库
     */
    @PutMapping("/{kb_id}")
    public Result<Void> updateKnowledgeBase(@PathVariable("kb_id") Integer kbId, @Valid @RequestBody KnowledgeBaseDTO knowledgeBaseDTO) {
        UserVO userVO = UserInfoContextHolder.getUserInfo();
        return knowledgeBaseService.updateKnowledgeBase(userVO, kbId, knowledgeBaseDTO);
    }

    /**
     * 上传知识库文档
     */
    @PutMapping("/{kb_id}/documents/upload")
    public Result<List<UploadKBDocVO>> uploadKnowledgeBaseDocuments(@PathVariable("kb_id") Integer kbId, @RequestParam("file") List<MultipartFile> files) {
        UserVO userVO = UserInfoContextHolder.getUserInfo();
        return knowledgeBaseService.uploadKnowledgeBaseDocuments(userVO, kbId, files);
    }

}
