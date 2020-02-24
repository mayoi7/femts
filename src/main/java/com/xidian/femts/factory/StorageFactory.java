package com.xidian.femts.factory;

import com.xidian.femts.service.StorageService;
import com.xidian.femts.service.impl.FastDFSStorageService;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件存储服务工厂
 *
 * @author LiuHaonan
 * @date 12:53 2020/1/21
 * @email acerola.orion@foxmail.com
 */
public class StorageFactory implements FactoryBean<StorageService> {

    @Autowired
    private AutowireCapableBeanFactory capableBeanFactory;

    @Value("${storage.type}")
    private String type;

    private Map<String, Class<? extends StorageService>> classMap;

    public StorageFactory() {
        classMap = new HashMap<>();
        classMap.put("fastdfs", FastDFSStorageService.class);
    }

    @Override
    public StorageService getObject() throws Exception {
        Class<? extends StorageService> clazz = classMap.get(type);
        if (clazz == null) {
            throw new RuntimeException("Unsupported storage type [" + type
                    + "], valid are " + classMap.keySet());
        }
        StorageService bean = clazz.newInstance();
        capableBeanFactory.autowireBean(bean);
        capableBeanFactory.initializeBean(bean, bean.getClass().getSimpleName());
        return bean;
    }

    @Override
    public Class<?> getObjectType() {
        return StorageService.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
