package com.xidian.femts.vo;

/**
 * @author LiuHaonan
 * @date 18:47 2020/4/7
 * @email acerola.orion@foxmail.com
 */
public interface SimpleDocInterface {

    void setId(Long id);

    void setTitle(String title);

    void setCreator(String creator);

    void setCreatorId(Long creatorId);

    Long getId();

    String getTitle();

    String getCreator();

    Long getCreatorId();
}
