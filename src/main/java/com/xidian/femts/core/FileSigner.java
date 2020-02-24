package com.xidian.femts.core;

import com.xidian.femts.utils.ZipUtils;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;

import static com.xidian.femts.constants.HashAlgorithm.MD5;
import static com.xidian.femts.utils.HashUtils.hashBytes;

/**
 * 给文件种标识符的工具类
 *
 * @author LiuHaonan
 * @date 9:42 2020/2/6
 * @email acerola.orion@foxmail.com
 */
@Slf4j
@Component
public class FileSigner {

    /** 存放hash文件的根目录 */
    private static final String HASH_FILE_DIR = "file/code/";

    /** 存放临时文件目录 */
    private static final String TEMP_FILE_DIR = "file/temp/";

    @AllArgsConstructor
    @NoArgsConstructor
    public static class FileData {

        public byte[] bytes;

        public String hash;
    }

    /**
     * 初始化方法，创建文件夹
     */
    @PostConstruct
    public void init() {
        File file = new File(HASH_FILE_DIR);
        if (!file.exists()) {
            boolean res1 = file.mkdirs();
            if (!res1) {
                log.warn("[FILE] initialize file directory fail, try again soon");
                // 如果执行失败就重试一次
                boolean res2 = file.mkdirs();
                if (!res2) {
                    log.error("[FILE] initialize file directory still failed");
                }
            }
        }
    }

    /**
     * 在压缩格式文件（如doc、docx、ofd）中种标识符<br/>
     * 方法：在文件中添加一个随机串（文件hash值）文件夹
     * @param path 文件目录（需要文件提前保存到本地）
     * @param bytes 文件数据的字节码
     * @return hash码，需要在调用完本方法后自行保存到数据库中（注意，本hash码不代表最终文件的hash码）
     */
    public String signZipFile(String path, byte[] bytes) {
        // 计算文件的hash码
        String hash = hashBytes(MD5, bytes);
        File signFile = new File(HASH_FILE_DIR + "$" + hash);
        // 失败重试一次
        if (!signFile.mkdir()) {
            if (!signFile.mkdir()) {
                log.error("[FILE] create hash file failed <try: 1>");
            }
        }
        ZipUtils.addFile(path, signFile);
        return hash;
    }

    /**
     * 在单独格式的文件（如pdf、rtf）中种标识符<br/>
     * 方法：在文件尾追加"%$%<FILE_HASH>%"<br/>
     * 不需要文件提前保存到本地
     * @param bytes 文件数据的字节码
     * @return hash码和新字节文件数据，其中hash码需要在调用完本方法后自行保存到数据库中（
     *         注意，本hash码不代表最终文件的hash码）
     */
    public FileData signSingleFile(byte[] bytes) {
        FileData fileData = new FileData();

        fileData.hash = hashBytes(MD5, bytes);
        String sign = "\n%$" + fileData.hash;
        byte[] byteSign = sign.getBytes();

        byte[] newByteFile = new byte[byteSign.length + bytes.length];
        // 拼接两个数组
        System.arraycopy(bytes, 0, newByteFile, 0, bytes.length);
        System.arraycopy(byteSign, 0, newByteFile, bytes.length, byteSign.length);

        fileData.bytes = newByteFile;
        return fileData;
    }

    /**
     * 读取文件中埋的hash码
     * @param path 文件路径
     * @return 从文件中读取的hash码
     */
    public String extractHashCodeFromZipFile(String path) {
        String fileHash = ZipUtils.getTargetDirectory(path, name -> name.startsWith("$"));
        if (fileHash == null) {
            log.info("[FILE] this file have not set signer <file_path: {}>", path);
            return null;
        } else {
            return fileHash.substring(1, fileHash.length() - 1);
        }
    }

    /**
     * 读取文件中埋的hash码
     * @param file 文件数据
     * @return 从文件中读取的hash码
     */
    public String extractHashCodeFromSingleFile(File file) {
        if (!file.exists() || file.isDirectory() || !file.canRead()) {
            return null;
        }
        String rawString = null;
        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {

            long len = raf.length();
            if (len == 0L) {
                return "";
            } else {
                long pos = len - 1;
                while (pos > 0) {
                    pos--;
                    raf.seek(pos);
                    if (raf.readByte() == '\n') {
                        break;
                    }
                }
                if (pos == 0) {
                    raf.seek(0);
                }
                byte[] bytes = new byte[(int) (len - pos)];
                raf.read(bytes);
                rawString = new String(bytes);
            }
        } catch (IOException ex) {
            log.warn("[FILE] read file failed <file_path: {}>", file.getPath(), ex);
            return null;
        }
        if (rawString.startsWith("%$")) {
            return rawString.substring(2);
        } else {
            return null;
        }
    }

    /**
     * 将byte数组转换成文件
     * @param bytes 字节数组
     * @param path 文件保存路径
     */
    public File makeFileByBytes(byte[] bytes, String path) {
        File file = new File(path);
        try(FileOutputStream fos = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            bos.write(bytes);
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
