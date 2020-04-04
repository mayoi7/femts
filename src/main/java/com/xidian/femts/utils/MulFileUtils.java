package com.xidian.femts.utils;

import com.xidian.femts.constants.FileType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

import static com.xidian.femts.utils.TokenUtils.generateSingleMark;
import static org.apache.commons.io.FileUtils.copyInputStreamToFile;

/**
 * 文件工具类
 *
 * @author LiuHaonan
 * @date 21:36 2020/2/9
 * @email acerola.orion@foxmail.com
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MulFileUtils {

    public static File convertMulFileToFile(MultipartFile mulFile, FileType fileType) {
        switch (fileType) {
            case WORD2003:
            case WORD2007:
            case OFD:
                return saveFile2Local(mulFile);
            case PDF:
            case TXT:
            case CUSTOM:
            default:
                return changeMulFileToFile(mulFile);
        }
    }

    /**
     * 将文件保存到本地
     * @param mulFile 浏览器上传的文件数据
     * @return 保存后的文件数据
     */
    private static File saveFile2Local(MultipartFile mulFile) {
        String basePath = "file/temp/";
        // 上传文件重命名
        String newName = generateSingleMark() + "-" + mulFile.getOriginalFilename();
        try {
            FileUtils.copyInputStreamToFile(mulFile.getInputStream(), new File(basePath, newName));
        } catch (IOException ex) {
            log.error("[FileSystem] file save to local failed <name: {}>" + mulFile.getOriginalFilename(), ex);
            return null;
        }
        return new File(basePath + newName);
    }

    /**
     * 将从客户端接收的文件转换为java的文件格式
     * @param mulFile {@link MultipartFile}格式数据，不可为空
     * @return {@link File}格式数据
     */
    public static File changeMulFileToFile(MultipartFile mulFile) {
        String name = mulFile.getOriginalFilename();
        String path = "file/temp/" + name;
        File file = new File(path);
        try {
            copyInputStreamToFile(mulFile.getInputStream(), file);
            return file;
        } catch (IOException e) {
            log.error("[FILE] change mulFile to file failed <name: {}>", mulFile.getOriginalFilename());
            return null;
        }
    }

    /**
     * 将文件数据转换为字节数组
     * @param file 文件数据
     * @return 对应的字节数组，如果转换失败会返回空
     */
    public static byte[] changeFileToBytes(File file)  {
        try {
            return FileUtils.readFileToByteArray(file);
        } catch (IOException ioe) {
            log.error("[FILE] file convert to bytes failed <file_path: {}>", file.getPath(), ioe);
        }
        return null;
//        // 文件大小不得超过1.9G
//        try (ByteArrayOutputStream bos = new ByteArrayOutputStream((int)file.length());
//             BufferedInputStream in = new BufferedInputStream(new FileInputStream(file))) {
//
//            int buf_size = 1024;
//            byte[] buffer = new byte[buf_size];
//            int len = 0;
//            while((len = in.read(buffer,0, buf_size)) != -1){
//                bos.write(buffer,0, len);
//            }
//            return bos.toByteArray();
//        } catch (IOException e) {
//            log.error("[FILE] file convert to bytes failed <file_path: {}>", file.getPath());
//            return null;
//        }
    }

    /**
     * 将字节数组转换成文件
     * @param bytes 文件字节数组
     * @param needCreate 是否需要创建文件（true：是；false：否）
     * @return 返回创建后的文件对象，如果为空说明创建失败（已经进行了一次重试）
     */
    public static File changeBytesToFile(byte[] bytes, boolean needCreate) {
        // TODO: 2020/3/17 上线改为随机文件名，用户需要手动传入文件类型
        File file = new File("file/temp/test-test.docx");
        try (OutputStream output = new FileOutputStream(file);
             BufferedOutputStream bufferedOutput = new BufferedOutputStream(output);
        ) {
            bufferedOutput.write(bytes);
            if (needCreate) {
                if (!file.createNewFile()) {
                    file.createNewFile();
                }
            }
            return file;
        } catch (FileNotFoundException fne) {
            log.error("[FILE] file not found");
            return null;
        } catch (IOException ioe) {
            log.error("[FILE] file stream write error or create file error");
            return null;
        }

    }
}
