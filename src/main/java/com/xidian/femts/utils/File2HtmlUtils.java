package com.xidian.femts.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.xwpf.converter.core.FileImageExtractor;
import org.apache.poi.xwpf.converter.core.FileURIResolver;
import org.apache.poi.xwpf.converter.xhtml.XHTMLConverter;
import org.apache.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

/**
 * @author LiuHaonan
 * @date 16:02 2020/1/18
 * @email acerola.orion@foxmail.com
 */
@Slf4j
public class File2HtmlUtils {

    /**
     * 将word文件转换为html代码
     * @param file 文件
     * @return 返回一个div，内部是word文件转换后的代码；如果发生异常则返回null
     */
    public static String convertWord2007ToHTML(File file) {
        // 加载word文档生成 XWPFDocument对象
        try(InputStream in = new FileInputStream(file);
            ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            XWPFDocument document = new XWPFDocument(in);

            // 解析 XHTML配置 (这里设置IURIResolver来设置图片存放的目录)
            File imageFolderFile = new File("file/image");
            XHTMLOptions options = XHTMLOptions.create().URIResolver(new FileURIResolver(imageFolderFile));
            options.setExtractor(new FileImageExtractor(imageFolderFile));
            options.setIgnoreStylesIfUnused(false);
            options.setFragment(true);

            // 将XWPFDocument转换成XHTML
            XHTMLConverter.getInstance().convert(document, out, options);
            return out.toString();
        } catch (FileNotFoundException fe) {
            log.error("[FILE] file not found <file_path: {}>", file.getPath(), fe);
            return null;
        } catch (IOException ioe) {
            log.error("[FILE] create converter failed <file_path: {}>", file.getPath(), ioe);
            return null;
        }

    }

    public static String convertWord2003ToHTML(File file) throws Exception {
        InputStream input = new FileInputStream(file);
        HWPFDocument wordDocument = new HWPFDocument(input);
        WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
        //解析word文档
        wordToHtmlConverter.processDocument(wordDocument);
        Document htmlDocument = wordToHtmlConverter.getDocument();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        DOMSource domSource = new DOMSource(htmlDocument);
        StreamResult streamResult = new StreamResult(baos);

        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer serializer = factory.newTransformer();
        serializer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
        serializer.setOutputProperty(OutputKeys.INDENT, "yes");
        serializer.setOutputProperty(OutputKeys.METHOD, "html");

        serializer.transform(domSource, streamResult);
        baos.close();
        return baos.toString();
    }
}
