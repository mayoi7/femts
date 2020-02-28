package com.xidian.femts.constants;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import static com.xidian.femts.constants.UserQueryCondition.judgeParamType;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author LiuHaonan
 * @date 12:27 2020/2/19
 * @email acerola.orion@foxmail.com
 */
class UserQueryConditionTest {

    @Test
    void testJudgeParamType() {
        UserQueryCondition c1 = judgeParamType("aaaaa@qq.com");
        UserQueryCondition c2 = judgeParamType("115245sr@sda.edu");
        UserQueryCondition c3 = judgeParamType("aweret4545s");
        UserQueryCondition c4 = judgeParamType("_1234454545");
        UserQueryCondition c5 = judgeParamType("12345789417871");
        UserQueryCondition c6 = judgeParamType("13521566218");
        UserQueryCondition c7 = judgeParamType("46545631");

        Assert.assertEquals(UserQueryCondition.EMAIL, c1);
        Assert.assertEquals(UserQueryCondition.EMAIL, c2);
        Assert.assertEquals(UserQueryCondition.USERNAME, c3);
        Assert.assertEquals(UserQueryCondition.USERNAME, c4);
        Assert.assertEquals(UserQueryCondition.JOBID, c5);
        Assert.assertEquals(UserQueryCondition.PHONE, c6);
        Assert.assertEquals(UserQueryCondition.JOBID, c7);
    }
}