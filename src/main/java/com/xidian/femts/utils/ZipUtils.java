package com.xidian.femts.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;

import java.io.File;
import java.util.List;
import java.util.function.Predicate;

/**
 * 压缩文件处理
 *
 * @author LiuHaonan
 * @date 15:12 2020/2/5
 * @email acerola.orion@foxmail.com
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ZipUtils {

    /**
     * 在压缩文件根目录下添加单个文件
     * @param zipPath 压缩文件目录
     * @param toAdd 待添加的文件
     */
    public static void addFile(String zipPath, File toAdd) {
        try {
            ZipFile zipFile = new ZipFile(zipPath);
            ZipParameters parameters = new ZipParameters();
//            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
//            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
            // 目标路径
//            parameters.setRootFolderInZip("/");
            zipFile.addFile(toAdd, parameters);
        } catch (ZipException e) {
            log.error("");
            e.printStackTrace();
        }
    }

    /**
     * 从压缩文件所有目录获取指定格式的目录名
     * @param path 压缩文件路径
     * @param predicate 当目录名为指定格式时返回true
     * @return 返回需要的目录名
     */
    public static String getTargetDirectory(String path, Predicate<String> predicate) {
        ZipFile file = new ZipFile(path);
        List<FileHeader> files = null;
        try {
            files = file.getFileHeaders();
        } catch (ZipException e) {
            log.error("[FILE] open file failed <file_path: {}>", path, e);
            e.printStackTrace();
            return null;
        }
        for (FileHeader header :files) {
            if (predicate.test(header.getFileName())) {
                return header.getFileName();
            }
        }
        return null;
    }

}
