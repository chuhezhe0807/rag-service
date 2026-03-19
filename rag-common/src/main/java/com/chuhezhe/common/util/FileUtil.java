package com.chuhezhe.common.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 文件SHA-256哈希计算工具类
 */
public class FileUtil {

    /**
     * 计算文件的SHA-256哈希值
     * @param file 目标文件（不能为空且必须存在）
     * @return 文件的SHA-256哈希字符串（小写十六进制）
     * @throws IOException 文件读取异常
     * @throws NoSuchAlgorithmException 不支持SHA-256算法（极少出现）
     */
    public static String calculateFileSHA256(File file) throws IOException, NoSuchAlgorithmException {
        // 前置校验：避免空指针和文件不存在
        if (file == null) {
            throw new IllegalArgumentException("文件对象不能为空");
        }
        if (!file.exists()) {
            throw new IOException("文件不存在：" + file.getAbsolutePath());
        }
        if (file.isDirectory()) {
            throw new IOException("目标路径是目录，不是文件：" + file.getAbsolutePath());
        }

        // 初始化SHA-256消息摘要
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        // 缓冲区：8KB（平衡内存占用和读取效率）
        byte[] buffer = new byte[8192];
        int bytesRead;

        // 流式读取文件（避免大文件占用过多内存）
        try (FileInputStream fis = new FileInputStream(file)) {
            while ((bytesRead = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
        }

        // 计算最终哈希值并转为十六进制字符串
        byte[] hashBytes = digest.digest();
        return bytesToHex(hashBytes);
    }

    /**
     * 重载方法：通过文件路径计算SHA-256
     * @param filePath 文件绝对/相对路径
     * @return SHA-256哈希字符串
     * @throws IOException 文件读取异常
     * @throws NoSuchAlgorithmException 算法不支持
     */
    public static String calculateFileSHA256(String filePath) throws IOException, NoSuchAlgorithmException {
        return calculateFileSHA256(new File(filePath));
    }

    /**
     * 计算文件的SHA-256哈希值
     * @param file 目标文件（不能为空且必须存在）
     * @return 文件的SHA-256哈希字符串（小写十六进制）
     * @throws Exception 文件读取异常或算法不支持
     */
    public static String calculateFileSHA256(MultipartFile file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256"); // 可替换为 "MD5"

        try (InputStream inputStream = file.getInputStream()) {
            byte[] byteArray = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(byteArray)) != -1) {
                digest.update(byteArray, 0, bytesRead);
            }
        }

        byte[] hashBytes = digest.digest();
        StringBuilder hexString = new StringBuilder();

        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();
    }

    /**
     * 字节数组转十六进制字符串（小写）
     * @param bytes 哈希字节数组
     * @return 十六进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            // 转为十六进制，补前导0（保证每个字节占2位）
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}