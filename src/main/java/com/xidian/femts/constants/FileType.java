package com.xidian.femts.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件格式类型
 *
 * @author LiuHaonan
 * @date 12:14 2020/1/19
 * @email acerola.orion@foxmail.com
 */
@Getter
@AllArgsConstructor
public enum FileType implements CodeEnum {
    WORD2003("doc", 0),
    WORD2007("docx", 1),
    TXT("txt", 2),
    PDF("pdf", 3),
    OFD("ofd", 4),
    RTF("rtf", 5),
    CUSTOM("", 6);

    private String name;

    private int code;

    public static FileType getFileType(MultipartFile file) {
        String name = file.getOriginalFilename();
        if (name == null) {
            return null;
        }
        int index = name.lastIndexOf(".");
        String format = name.substring(index + 1);
        for(FileType type: FileType.values()) {
            if (type.name.equals(format)) {
                return type;
            }
        }
        return CUSTOM;
    }
}
