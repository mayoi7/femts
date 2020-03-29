package com.xidian.femts.other;

import com.alibaba.fastjson.JSON;
import com.xidian.femts.constants.Operation;
import com.xidian.femts.entity.History;
import org.junit.Test;

import java.util.Date;

/**
 * @author LiuHaonan
 * @date 16:47 2020/1/17
 * @email acerola.orion@foxmail.com
 */
public class JsonTest {

    @Test
    public void object2Json() {
        History history = new History();
        history.setUserId(1L);
        history.setObjectId(1L);
        history.setType(true);
        history.setOperation(Operation.UPDATE);
        history.setId(2L);
        history.setTime(new Date());

        String json = JSON.toJSONString(history);
        System.out.println(json);
    }
}
