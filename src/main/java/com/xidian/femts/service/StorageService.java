package com.xidian.femts.service;

/**
 * 文件存储系统服务
 *
 * @author LiuHaonan
 * @date 11:45 2020/1/21
 * @email acerola.orion@foxmail.com
 */
public interface StorageService {

    /**
     * 上传文件
     * @param data 文件二进制内容
     * @param extName 文件扩展名
     * @return 上传成功后返回文件id；失败则返回null
     */
    String upload(byte[] data, String extName);

    /**
     * 下载文件
     * @param fileId 文件id
     * @return 返回文件二进制内容
     */
    byte[] download(String fileId);

    /**
     * 修改文件（先上传再删除原文件）
     * @param oldFileId 旧文件id
     * @param fileData 新文件数据
     * @param extName 新文件扩展名
     * @return 返回修改后的文件id，如果为空说明修改失败
     */
    String modify(String oldFileId, byte[] fileData, String extName);

    /**
     * 删除文件<br/>
     * 出于收集数据以及备份的需要，删除方法不会真正执行，而是返回一个虚假成功结果
     * @param fileId 文件id
     * @return 删除成功后返回0，否则返回错误代码
     */
    int delete(String fileId);

    /**
     * 真正删除文件的方法（只有当系统存储空间不够的时候才考虑执行该方法）
     * @param fileId 文件id
     * @return 删除成功后返回0，否则返回错误代码
     */
    int realDelete(String fileId);
}
