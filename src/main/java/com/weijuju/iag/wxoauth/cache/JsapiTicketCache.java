package com.weijuju.iag.wxoauth.cache;/**
 * Created by zhangyin on 2017/1/22.
 */

import com.weijuju.iag.wxoauth.entity.AccessToken;
import com.weijuju.iag.wxoauth.entity.JsapiTicket;
import com.weijuju.iag.wxoauth.wx.WxApi;
import org.apache.commons.lang.StringUtils;
import org.redisson.RedissonClient;
import org.redisson.core.RBucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author zhangyin
 * @create 2017-01-22
 */
@Component
public class JsapiTicketCache {

    private static String  JSAPITICKET=RedisPrefix.KEY+"JsapiTicket";

    @Autowired
    WxApi wxApi;

    @Autowired
    AccessTokenCache accessTokenCache;

    @Autowired
    RedissonClient redissonClient;

    public String  getJSAPITICKET(){
        RBucket<String> bucket = redissonClient.getBucket(JSAPITICKET);
        String s = bucket.get();
        if(StringUtils.isEmpty(s)){
            AccessToken accessToken =   accessTokenCache.get();
            JsapiTicket jsapiTicket = wxApi.getJsapiTicket(accessToken.getAccess_token());
            s=jsapiTicket.getTicket();
            bucket.set(s,jsapiTicket.getExpires_in(), TimeUnit.SECONDS);
        }
        return s;
    }
}
