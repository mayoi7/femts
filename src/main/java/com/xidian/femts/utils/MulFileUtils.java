package com.xidian.femts.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

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

    /**
     * 将从客户端接收的文件转换为java的文件格式
     * @param mulFile {@link MultipartFile}格式数据，不可为空
     * @return {@link File}格式数据
     */
    public static File changeToFile(MultipartFile mulFile) {
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
     * @return 对应的字节数组
     */
    public static byte[] changeToBytes(File file) throws IOException {
        // 文件大小不得超过1.9G
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream((int)file.length());
             BufferedInputStream in = new BufferedInputStream(new FileInputStream(file))) {

            int buf_size = 1024;
            byte[] buffer = new byte[buf_size];
            int len = 0;
            while((len = in.read(buffer,0, buf_size)) != -1){
                bos.write(buffer,0,len);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            log.error("[FILE] file convert to bytes failed <file_path: {}>", file.getPath());
            throw e;
        }
    }

    public static File changeBytesToFile(byte[] bytes) {
        File file = new File("file/temp/007.docx");
        try (OutputStream output = new FileOutputStream(file);
             BufferedOutputStream bufferedOutput = new BufferedOutputStream(output);
        ) {
            bufferedOutput.write(bytes);
            return file;
        } catch (FileNotFoundException fne) {
            log.error("[FILE] file not found");
            return null;
        } catch (IOException ioe) {
            log.error("[FILE] file stream write error");
            return null;
        }

    }
}
