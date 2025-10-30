package com.sky.controller.user;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/user/shop")
@RestController("userShopController")
@Slf4j
@Api(tags = "商铺相关接口")
public class ShopController {
    @Autowired
    private RedisTemplate redisTemplate;


    @ApiOperation("获取商铺状态")
    @GetMapping("/status")
    public Result getShopStatus() {
        log.info("获取商铺状态");
        Integer status = (Integer) redisTemplate.opsForValue().get("shop_status");
        return Result.success(status);
    }
}
