package com.xidian.femts.vo;

import lombok.Data;

/**
 * @author LiuHaonan
 * @date 18:45 2020/4/7
 * @email acerola.orion@foxmail.com
 */
@Data
public class SimpleDoc implements SimpleDocInterface {

    private Long id;

    private String title;

    private String creator;

    private Long creatorId;

    public SimpleDoc(SimpleDocInterface docInterface, String creator) {
        this.id = docInterface.getId();
        this.title = docInterface.getTitle();
        this.creatorId = docInterface.getCreatorId();

        this.creator = creator;
    }
}
