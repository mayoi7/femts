package com.xidian.femts.utils;

import com.xidian.femts.constants.FileType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xwpf.converter.core.FileImageExtractor;
import org.apache.poi.xwpf.converter.core.FileURIResolver;
import org.apache.poi.xwpf.converter.xhtml.XHTMLConverter;
import org.apache.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.fit.pdfdom.PDFDomTree;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * 文件与html的转换器工具类
 * @author LiuHaonan
 * @date 16:02 2020/1/18
 * @email acerola.orion@foxmail.com
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileHtmlConverter {

    public static final String FILE_LOCAL_URL = "file/temp/" ;

    /**
     * 将文件字节数组转换为html格式字符串<br/>
     * 目前只支持docx/doc/pdf/txt
     * @param bytes 文件字符数组
     * @param fileType 文件类型
     * @return 返回html标签/div标签；如果返回null则说明转换出现异常
     */
    public static String convertFileBytesToHTML(byte[] bytes, FileType fileType) {
        switch (fileType) {
            case WORD2007:
                File docx = MulFileUtils.changeBytesToFile(bytes, true);
                return convertWord2007ToHTML(docx);
            case WORD2003:
                File doc = MulFileUtils.changeBytesToFile(bytes, true);
                return convertWord2003ToHTML(doc);
            case PDF:
                return convertPdfToHTML(bytes);
            case TXT:
            case CUSTOM:
                return convertTxtToHTML(bytes);
            case OFD:
            case RTF:
                // 后续添加这两种文件的支持
                return null;
            default:
                // 暂时不支持其他文件格式
                return null;
        }
    }

    /**
     * 将word2007文件转换为html代码
     * @param file docx文件
     * @return 返回一个div标签，内部是word文件转换后的代码；如果发生异常则返回null
     */
    private static String convertWord2007ToHTML(File file) {
        // 加载word文档生成 XWPFDocument对象
        try(InputStream in = new FileInputStream(file);
            ByteArrayOutputStream out = new ByteArrayOutputStream()
        ) {
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
            log.error("[FILE] docx file not found <file_path: {}>", file.getPath(), fe);
            return null;
        } catch (IOException ioe) {
            log.error("[FILE] docx create converter failed <file_path: {}>", file.getPath(), ioe);
            return null;
        }
    }

    /**
     * 将word2003文件转换为html代码
     * @param file doc文件
     * @return 返回一个html标签，内部是word文件转换后的代码；如果发生异常则返回null
     */
    private static String convertWord2003ToHTML(File file) {
        try (InputStream input = new FileInputStream(file);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()
        ) {
            HWPFDocument wordDocument = new HWPFDocument(input);
            WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
            //解析word文档
            wordToHtmlConverter.processDocument(wordDocument);
            Document htmlDocument = wordToHtmlConverter.getDocument();

            DOMSource domSource = new DOMSource(htmlDocument);
            StreamResult streamResult = new StreamResult(baos);

            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer serializer = factory.newTransformer();
            serializer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            serializer.setOutputProperty(OutputKeys.METHOD, "html");

            serializer.transform(domSource, streamResult);
            return baos.toString();
        } catch (FileNotFoundException fne) {
            log.error("[FILE] doc file not found <file_path: {}>", file.getPath(), fne);
            return null;
        } catch (Exception e) {
            log.error("[FILE] doc file convert failed <file_path: {}>", file.getPath(), e);
            return null;
        }
    }

    /**
     * 将pdf文件转换为html代码
     * @param bytes 文件字节数组
     * @return 返回一个div，内部是word文件转换后的代码；如果发生异常则返回null
     */
    private static String convertPdfToHTML(byte[] bytes)  {
        try (
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(baos, StandardCharsets.UTF_8))){
            //加载PDF文档
            PDDocument document = PDDocument.load(bytes);
            PDFDomTree pdfDomTree = new PDFDomTree();
            pdfDomTree.writeText(document,out);

            return baos.toString();
        } catch (ParserConfigurationException pce) {
            log.error("[FILE] pdf file converter config wrong", pce);
            return null;
        } catch (IOException ioe) {
            log.error("[FILE] pdf file convert failed", ioe);
            return null;
        }
    }

    /**
     * 将txt文件转换为html代码
     * @param bytes 文件字节数组
     * @return 返回一个字符串，表示文件内容；如果发生异常则返回null
     */
    private static String convertTxtToHTML(byte[] bytes) {
        return new String(bytes);
    }

    public static byte[] convertHTMLToWord2007(String html) {
        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
        try (ByteArrayInputStream os = new ByteArrayInputStream(bytes);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            POIFSFileSystem fs = new POIFSFileSystem();
            DirectoryEntry directory = fs.getRoot();
            DocumentEntry documentEntry = directory.createDocument("WordDocument", os);
            fs.writeFilesystem(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("[FILE] convert html to doc failed");
            e.printStackTrace();
        }
        return null;
    }
}
