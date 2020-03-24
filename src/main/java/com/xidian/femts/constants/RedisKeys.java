package com.xidian.femts.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * redis中一些key的常量，避免不一致修改带来的漏洞<br>
 * 如果是带有个人信息标识的key，则后面可以直接加上个人信息，不需要添加分隔符
 * @author LiuHaonan
 * @date 8:45 2020/2/25
 * @email acerola.orion@foxmail.com
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RedisKeys {

    /** 使用时需要在后面追加个人信息 */
    public static final String PASSWORD_RESET_KEY = "pwd_reset$";

    /** 注册人数 */
    public static final String REGIST_COUNT_KEY = "registered";

    /** 激活人数 */
    public static final String ACTIVED_COUNT_KEY = "actived";

    /** 在线人数 */
    public static final String ONLINE_COUNT_KEY = "online";

    /** 文档总数 */
    public static final String DOCUMENT_COUNT_KEY = "document";

}
