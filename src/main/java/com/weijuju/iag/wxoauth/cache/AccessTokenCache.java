package com.weijuju.iag.wxoauth.cache;/**
 * Created by zhangyin on 2017/1/5.
 */

import com.weijuju.iag.wxoauth.entity.AccessToken;
import com.weijuju.iag.wxoauth.wx.WxApi;
import org.redisson.RedissonClient;
import org.redisson.core.RBucket;
import org.redisson.core.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author zhangyin
 * @create 2017-01-05
 */
@Component
public class AccessTokenCache {

    private static String  ACCESSTOKENKEY=RedisPrefix.KEY+"accesstoken";

    @Autowired
    WxApi wxApi;


    @Autowired
    RedissonClient redissonClient;

    public void init(){
        RLock lock = redissonClient.getLock(ACCESSTOKENKEY + "lock");
        boolean b = lock.tryLock();
        if(!b){
            return;
        }
        try {
            AccessToken accessToken = wxApi.getAccessToken();
            RBucket<AccessToken> bucket = redissonClient.getBucket(ACCESSTOKENKEY);
            bucket.set(accessToken);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public  AccessToken   get(){
        RBucket<AccessToken> bucket = redissonClient.getBucket(ACCESSTOKENKEY);
        AccessToken accessToken = bucket.get();
        if(accessToken==null){
             accessToken = wxApi.getAccessToken();
             bucket.set(accessToken,accessToken.getExpires_in(), TimeUnit.SECONDS);
        }
        return accessToken;
    }


}
