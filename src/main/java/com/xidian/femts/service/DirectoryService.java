package com.xidian.femts.service;

import com.xidian.femts.entity.Directory;
import com.xidian.femts.vo.DirectoryElement;

import java.util.List;

/**
 * 文档目录树服务接口
 *
 * @author LiuHaonan
 * @date 20:06 2020/2/2
 * @email acerola.orion@foxmail.com
 */
public interface DirectoryService {

    /**
     * 查询某目录下的公开（指当前登陆用户可见）目录及文档（只包含当前目录下的直接文档及子目录，不包括子目录的子目录）
     * @param id 目录id（directory表主键），禁止为空
     * @param userId 登陆用户id（虽然不限制必须是登陆用户，但是调用方必须传入当前登陆用户的id）
     * @return 目录数据结构，如果不存在目录则会返回空（非null）
     */
    List<DirectoryElement> listPublicDirectories(Long id, Long userId);

    /**
     * 根据id查找
     * @param id 主键
     * @return 数据表中对象
     */
    Directory findById(Long id);

    /**
     * 添加空目录
     * @param name 目录名
     * @param parentId 父目录id
     * @param userId 创建人id
     * @param visible 是否公开，true：是
     * @return 创建后的目录
     */
    Directory createEmptyDirectory(Long parentId, String name, Long userId, boolean visible);

    /**
     * 添加空目录并追加到父目录表中
     * @param parentId 父目录id
     * @param name 目录名
     * @param userId 创建人id
     * @param visible 是否公开，true：是
     * @return 创建后的目录
     */
    Directory createAndAppendDirectory(Long parentId, String name, Long userId, boolean visible);

    /**
     * 在目录下追加文档id
     * @param parentId 所在目录id
     * @param docId 文档id
     * @return 目录数据
     */
    Directory appendManuscript(Long parentId, Long docId);
}
