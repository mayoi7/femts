package com.xidian.femts.exception;

/**
 * 由请求参数错误导致的一类异常</br>
 * 往往后果较严重，不能直接由浏览器渲染出错信息，需要通过该异常将用户引导到出错页
 *
 * @author LiuHaonan
 * @date 10:15 2020/2/25
 * @email acerola.orion@foxmail.com
 */
public class ParamException extends RuntimeException {

    public ParamException() {
        super();
    }

    public ParamException(String message) {
        super(message);
    }
}
