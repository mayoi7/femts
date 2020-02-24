package com.xidian.femts.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.xwpf.converter.xhtml.XHTMLConverter;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * @author LiuHaonan
 * @date 16:02 2020/1/18
 * @email acerola.orion@foxmail.com
 */
@Slf4j
public class Word2HtmlUtils {

    public static String Word2007ToHtml(MultipartFile file) throws IOException {

        if (file.isEmpty() || file.getSize() <= 0) {
            log.error("Sorry File does not Exists!");
            return null;
        } else {
            if (Objects.requireNonNull(file.getOriginalFilename()).endsWith(".docx") || file.getOriginalFilename().endsWith(".DOCX")) {

                // 1) 加载word文档生成 XWPFDocument对象
                InputStream in = file.getInputStream();
                XWPFDocument document = new XWPFDocument(in);

                // 也可以使用字符数组流获取解析的内容
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                XHTMLConverter.getInstance().convert(document, baos, null);
                String content = baos.toString();
                baos.close();
                return content;
            } else {
                log.error("Enter only MS Office 2007+ files");
                return null;
            }
        }
    }

    public static String Word2003ToHtml(MultipartFile file)
            throws IOException, ParserConfigurationException, TransformerException {

        if (file.isEmpty() || file.getSize() <= 0) {
            log.error("Sorry File does not Exists!");
            return null;
        } else {
            if (Objects.requireNonNull(file.getOriginalFilename()).endsWith(".doc") || file.getOriginalFilename().endsWith(".DOC")) {
                InputStream input = file.getInputStream();
                HWPFDocument wordDocument = new HWPFDocument(input);
                WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(
                        DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());

                // 解析word文档
                wordToHtmlConverter.processDocument(wordDocument);
                Document htmlDocument = wordToHtmlConverter.getDocument();

                // 也可以使用字符数组流获取解析的内容
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DOMSource domSource = new DOMSource(htmlDocument);
                StreamResult streamResult = new StreamResult(baos);

                TransformerFactory factory = TransformerFactory.newInstance();
                Transformer serializer = factory.newTransformer();
                serializer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
                serializer.setOutputProperty(OutputKeys.INDENT, "yes");
                serializer.setOutputProperty(OutputKeys.METHOD, "html");
                serializer.transform(domSource, streamResult);

                // 也可以使用字符数组流获取解析的内容
                String content = new String(baos.toByteArray());
                baos.close();
                return content;
            } else {
                log.error("Enter only MS Office 2003 files");
                return null;
            }
        }
    }
}
