package com.xidian.femts.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.util.HashSet;
import java.util.Set;

/**
 * 封装一些实体类相关的操作
 * @author LiuHaonan
 * @date 16:59 2020/4/7
 * @email acerola.orion@foxmail.com
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EntityUtils {

    /**
     * 将一个实体类的属性复制到另一个实体类中
     * @param source 需要覆盖的属性
     * @param target 被覆盖的属性
     * @param ignoreNull true：忽略空值；false：所有属性强制覆盖
     */
    public static void copyProperties(Object source, Object target, boolean ignoreNull) {
        if (ignoreNull) {
            BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
        } else {
            BeanUtils.copyProperties(source, target);
        }
    }

    private static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for(java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }
}
