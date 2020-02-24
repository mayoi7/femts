package com.xidian.femts.vo;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * 通用返回对象
 *
 * @author LiuHaonan
 * @date 15:47 2020/1/14
 * @email acerola.orion@foxmail.com
 */
@Data
public class ResultVO {

    public final static ResultVO SUCCESS = new ResultVO("success");

    /**
     * 响应码
     */
    private Integer code;

    /**
     * 响应体（Json字符串格式）
     */
    private String data;

    public ResultVO() {
        this.code = HttpStatus.OK.value();
    }

    public ResultVO(HttpStatus httpStatus, Object obj) {
        this.code = httpStatus.value();
        if (obj instanceof String) {
            this.data = obj.toString();
        } else {
            this.data = JSON.toJSONString(obj);
        }
    }

    public ResultVO(Object obj) {
        if(obj == null) {
            this.code = HttpStatus.SERVICE_UNAVAILABLE.value();
            return;
        }
        this.code = HttpStatus.OK.value();
        if (obj instanceof String) {
            this.data = (String) obj;
        } else {
            this.data = JSON.toJSONString(obj);
        }
    }
}
