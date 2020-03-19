package com.xidian.femts.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author LiuHaonan
 * @date 20:23 2020/3/18
 * @email acerola.orion@foxmail.com
 */
class PackageUtilsTest {

    @Test
    void packageHtml() {
        String html = "<div style=\"width:595.0pt;margin-bottom:56.0pt;margin-top:85.0pt;margin-left:85.0pt;margin-right:56.0pt;\"><p style=\"text-align:center;\"><span style=\"font-family:'楷体';font-size:16.0pt;font-weight:bold;\">&#26159;&#30701;&#21457;&#25151;&#39030;&#19978;&#21453;&#20498;&#26159;&#26041;&#24335;</span></p><p>&#20799;&#21834;<span style=\"font-family:'宋体';font-size:16.0pt;\">&#25746;&#26086;</span><span style=\"font-family:'宋体';font-size:16.0pt;font-style:italic;\">&#38463;</span><span style=\"font-style:italic;\">&#26031;&#39039;a</span>F&#21380;s<span style=\"text-decoration:underline;\">d&#38750;</span>dss</p><p>&#25746;<span style=\"font-weight:bold;\">&#26086;&#38463;&#26031;</span>&#39039;adsaa<span id=\"_GoBack\"/></p><p>   &#21714;&#21714;&#21714;</p><p>&#21457;&#29983;&#22823;&#24133;</p></div>";
        System.out.println(PackageUtils.packageHtml(html));
    }

    @Test
    void unpackageHtml() {
    }
}