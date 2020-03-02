package com.xidian.femts.service;

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
    String findNameById(Long id);

    /**
     * 根据id查找目录名称，如果目录不可见则返回空
     * @param id 目录id
     * @return 目录名称，如果为空说明id不存在，或当前用户不可见
     */
    String findNameByIdIfVisible(Long id, Long userId);
}
