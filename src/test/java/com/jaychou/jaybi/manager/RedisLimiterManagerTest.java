package com.jaychou.jaybi.manager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * ClassName: RedisLimiterManagerTest
 * Package: com.jaychou.jaybi.manager
 * Description:
 *
 * @Author: 红模仿
 * @Create: 2023/7/12 - 16:52
 * @Version: v1.0
 */
@SpringBootTest
class RedisLimiterManagerTest {

    @Resource
    private RedisLimiterManager redisLimiterManager;
    @Test
    void doRateLimit() throws InterruptedException {
        String userId = "1";
        for(int i = 0;i< 2;i++){
            redisLimiterManager.doRateLimit(userId);
            System.out.println("成功");
        }
        Thread.sleep(1000);
        for(int i = 0;i<5;i++){
        redisLimiterManager.doRateLimit(userId);
            System.out.println("成功");
        }
    }

}