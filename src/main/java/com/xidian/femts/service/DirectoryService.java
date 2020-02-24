package com.xidian.femts.service;

import com.xidian.femts.entity.Directory;
import com.xidian.femts.vo.DirList;

/**
 * 文档目录树服务接口
 *
 * @author LiuHaonan
 * @date 20:06 2020/2/2
 * @email acerola.orion@foxmail.com
 */
public interface DirectoryService {

    /**
     * 查询某目录下的目录结构
     * @param id 目录id（directory表主键），禁止为空
     * @return {@link DirList}表示的目录数据结构
     */
    DirList listTitles(Long id);

    /**
     * 根据id查找
     * @param id 主键
     * @return 数据表中对象
     */
    Directory findById(Long id);

    /**
     * 根据id查找目录名称
     * @param id 目录id
     * @return 目录名称，如果为空说明id不存在
     */
    String findNameById(Long id);
}
