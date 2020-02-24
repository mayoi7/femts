package com.xidian.femts.service.impl;

import com.xidian.femts.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import java.io.*;

/**
 * fast dfs客户端服务实现
 * @author LiuHaonan
 * @date 11:50 2020/1/21
 * @email acerola.orion@foxmail.com
 */
@Slf4j
public class FastDFSStorageService implements StorageService, InitializingBean {

    private TrackerClient trackerClient;

    @Value("${storage.fastdfs.tracker_server}")
    private String trackerServerAddr;

    @Override
    public String upload(byte[] data, String extName) {
        TrackerServer trackerServer = null;
        StorageServer storageServer = null;
        StorageClient1 storageClient1 = null;

        try {
            NameValuePair[] metaList = new NameValuePair[0];

            trackerServer = trackerClient.getTrackerServer();
            if (trackerServer == null) {
                log.error("[FastDFS] fastdfs connection failed");
            }

            storageServer = trackerClient.getStoreStorage(trackerServer);
            storageClient1 = new StorageClient1(trackerServer, storageServer);
            String field = storageClient1.upload_file1(data, extName, metaList);
            log.info("[FastDFS] upload file <{}>", field);
            return field;
        } catch (IOException | MyException e) {
            log.error("[FastDFS] upload fail", e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public byte[] download(String fileId) {
        TrackerServer trackerServer = null;
        StorageServer storageServer = null;
        StorageClient1 storageClient1 = null;

        String groupName = getGroupNameByFileId(fileId);

        try {
            trackerServer = trackerClient.getTrackerServer();
            if (trackerServer == null) {
                log.error("[FastDFS] fastdfs connection failed");
            }
            storageServer = trackerClient.getStoreStorage(trackerServer, groupName);
            storageClient1 = new StorageClient1(trackerServer, storageServer);
            return storageClient1.download_file1(fileId);
        } catch (Exception ex) {
            log.error("[FastDFS] download fail", ex);
        }
        return null;
    }

    @Override
    public String modify(String oldFileId, byte[] fileData, String extName) {
        String fileId = null;
        try {
            // 先上传修改后的新文件
            fileId = upload(fileData, extName);
            if (fileId == null) {
                log.error("[FastDFS] file modify fail in stage upload");
                return null;
            }
            // 再删除原文件
            int delResult = delete(oldFileId);
            if (delResult != 0) {
                return null;
            }
        } catch (Exception ex) {
            log.error("[FastDFS] file modify fail", ex);
            return null;
        }
        return fileId;
    }

    @Override
    public int delete(String fileId) {
        return 0;
    }

    @Override
    public int realDelete(String fileId) {
        TrackerServer trackerServer = null;
        StorageServer storageServer = null;
        StorageClient1 storageClient1 = null;

        String groupName = getGroupNameByFileId(fileId);

        try {
            trackerServer = trackerClient.getTrackerServer();
            if (trackerServer == null) {
                log.error("[FastDFS] fastdfs connection failed");
            }
            storageServer = trackerClient.getStoreStorage(trackerServer, groupName);
            storageClient1 = new StorageClient1(trackerServer, storageServer);
            return storageClient1.delete_file1(fileId);
        } catch (IOException | MyException e) {
            log.error("[FastDFS] delete fail", e);
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        File confFile = File.createTempFile("fastdfs", ".conf");
        PrintWriter confWriter = new PrintWriter(new FileWriter(confFile));
        confWriter.println("tracker_server=" + trackerServerAddr);
        confWriter.close();

        ClientGlobal.init(confFile.getAbsolutePath());
        confFile.delete();
        TrackerGroup trackerGroup = ClientGlobal.g_tracker_group;
        trackerClient = new TrackerClient(trackerGroup);

        log.info("[FastDFS] init fastdfs with track_server: {}", trackerServerAddr);

    }

    /**
     * 根据文件id计算组名
     * @param fileId 文件id
     * @return 返回组名，如果为空说明文件id不合法
     */
    private String getGroupNameByFileId(String fileId) {
        if (StringUtils.isEmpty(fileId)) {
            return null;
        }
        int index = fileId.indexOf("/");
        return fileId.substring(0, index);
    }
}
