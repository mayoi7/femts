package com.xidian.femts.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 做一些列表和字符串的转换工作
 *
 * @author LiuHaonan
 * @date 20:01 2020/1/16
 * @email acerola.orion@foxmail.com
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ListUtils {

    public static final String DEFAULT_DELIMITER = ",";

    /**
     * 将列表转换为按分隔符分割的字符串
     * @param list 字符串列表
     * @return 压缩后的字符串
     */
    public static String list2String(List<String> list) {
        return list2String(list, DEFAULT_DELIMITER);
    }

    /**
     * 将列表转换为按分隔符分割的字符串
     * @param list 字符串列表
     * @param delimiter 分隔符，如果为空，则使用逗号作为分隔
     * @return 压缩后的字符串
     */
    public static String list2String(List<String> list, String delimiter) {
        if(list.isEmpty()) {
            return "";
        }
        if (delimiter == null) {
            // 默认按逗号分隔
            delimiter = DEFAULT_DELIMITER;
        }
        StringBuilder string = new StringBuilder();
        for (int i = 0; i < list.size() - 1; i++) {
            string.append(list.get(i));
            string.append(delimiter);
        }
        string.append(list.get(list.size() - 1).toString());
        return string.toString();
    }

    /**
     * 将字符串转换成按分隔符分割的列表
     * @param string 初始字符串
     * @return 字符串列表
     */
    public static List<String> string2List(String string) {
        return string2List(string, DEFAULT_DELIMITER);
    }

    /**
     * 将字符串转换成按分隔符分割的列表
     * @param string 初始字符串
     * @param delimiter 分隔符
     * @return 字符串列表
     */
    public static List<String> string2List(String string, String delimiter) {
        if (StringUtils.isEmpty(string)) {
            return new ArrayList<>();
        }
        String[] result = string.split(delimiter);
        List<String> list = new ArrayList<>(result.length);
        list.addAll(Arrays.asList(result));
        return list;
    }

}
