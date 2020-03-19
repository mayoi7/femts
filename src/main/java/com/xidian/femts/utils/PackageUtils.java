package com.xidian.femts.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 用于对字符串或对象的再包装工具类
 *
 * @author LiuHaonan
 * @date 19:50 2020/3/18
 * @email acerola.orion@foxmail.com
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PackageUtils {

    /**
     * 将html串包装为完整的页面
     * @param rawHtml 无<html></html>标签的字符串
     * @return 返回完整的html页面字符串
     */
    public static String packageHtml(String rawHtml) {
        return "<!doctype html>" +
                "<html lang=\"en\">" +
                "<head>" +
                "    <meta charset=\"UTF-8\">" +
                "    <meta name=\"viewport\"" +
                "          content=\"width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0\">" +
                "    <meta http-equiv=\"X-UA-Compatible\" content=\"ie=edge\">" +
                "    <title>Document</title>" +
                "</head>" +
                "<body>" +
                rawHtml +
                "</body>" +
                "</html>";
    }

    /**
     * 从完整的html页面字符串中提取html元素标签
     * @deprecated 请尽量不要使用，如果一定要使用，请确认只需要body内的元素，因为其他元素会被忽略
     * @param html 完整页面元素的字符串
     * @return 返回body标签内的字符串
     */
    public static String unpackageHtml(String html) {
        int begin = html.indexOf("<body>");
        int over = html.lastIndexOf("</body>");
        if (begin == -1 || over == -1 || over < begin) {
            return null;
        } else {
            return html.substring(begin + 6, over);
        }
    }
}
