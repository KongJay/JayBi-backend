package com.jaychou.jaybi.manager;

import com.jaychou.jaybi.common.ErrorCode;
import com.jaychou.jaybi.exception.BusinessException;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * ClassName: RedisLimiterManager
 * Package: com.jaychou.jaybi.manager
 * Description:
 *
 * @Author: 红模仿
 * @Create: 2023/7/12 - 16:39
 * @Version: v1.0
 */
@Service
public class RedisLimiterManager {

    @Resource
    private RedissonClient redissonClient;

    public void doRateLimit(String key){
        RRateLimiter rRateLimiter = redissonClient.getRateLimiter(key);
        rRateLimiter.trySetRate(RateType.OVERALL,2,1, RateIntervalUnit.SECONDS);
        boolean canOp = rRateLimiter.tryAcquire(1);
        if(!canOp){
            throw new BusinessException(ErrorCode.TOO_MANY_REQUEST);
        }
    }
}
