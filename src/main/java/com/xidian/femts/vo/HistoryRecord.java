package com.xidian.femts.vo;

import com.xidian.femts.entity.History;
import lombok.Data;

import java.util.Date;

/**
 * 历史记录信息
 *
 * @author LiuHaonan
 * @date 20:07 2020/2/16
 * @email acerola.orion@foxmail.com
 */
@Data
public class HistoryRecord {

    private Long userId;

    private String username;

    private Long docId;

    private String title;

    private Date time;

    private String option;

    public HistoryRecord(History history, String username, String title) {
        if (history == null) {
            return;
        }
        this.userId = history.getUserId();
        this.username = username;
        this.docId = history.getOptionId();
        this.title = title;
        this.time = history.getTime();
        this.option = history.getOptionType().getName();
    }
}
