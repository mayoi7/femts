package com.xidian.femts.service;

import com.xidian.femts.entity.Manuscript;

/**
 * 为了解决一些service内部方法调用无法触发缓存的问题，
 * 采用将这类方法同一管理作为新service引入的手段来解决。<br/>
 * 同时，原方法中如需要也可保留一份备份
 * @author LiuHaonan
 * @date 16:23 2020/3/2
 * @email acerola.orion@foxmail.com
 */
public interface InternalCacheService {

    /**
     * 根据id查找目录名称<br/>
     * 管理员接口，一般情况下不允许使用，应使用{@link DirectoryService#f}
     * @param id 目录id
     * @return 目录名称，如果为空说明id不存在
     */
    String findNameById_Directory(Long id);

    /**
     * 根据id查找目录名称，如果目录不可见则返回空
     * @param id 目录id
     * @return 目录名称，如果为空说明id不存在，或当前用户不可见
     */
    String findNameByIdIfVisible_Directory(Long id, Long userId);

    /**
     * 根据文稿id查找数据库中对象
     * @param id 文稿id（不可为空）
     * @return 数据库中文稿对象，为空说明id不正确
     */
    Manuscript findById_Manuscript(Long id);

    /**
     * 查询文档内容
     * @param contentId 文档内容id（非文档id）
     * @return 返回文档内容，如果为空说明不存在
     */
    String findContentById_Content(Long contentId);
}
