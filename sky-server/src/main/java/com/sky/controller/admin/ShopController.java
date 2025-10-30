package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/admin/shop")
@RestController("adminShopController")
@Slf4j
@Api(tags = "商铺相关接口")
public class ShopController {
    @Autowired
    private RedisTemplate redisTemplate;
    @ApiOperation("设置商铺状态")
    @PutMapping("{status}")
    public Result setShopStatus(@PathVariable Integer status) {
        log.info("设置商铺状态：{}", status);
        // 将商铺状态存入Redis
        redisTemplate.opsForValue().set("shop_status", status);
        return Result.success();
    }

    @ApiOperation("获取商铺状态")
    @GetMapping("/status")
    public Result getShopStatus() {

        Integer status = (Integer) redisTemplate.opsForValue().get("shop_status");
        log.info("获取商铺状态{}", status);
        return Result.success(status);
    }
}
