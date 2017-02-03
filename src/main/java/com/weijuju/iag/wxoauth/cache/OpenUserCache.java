package com.weijuju.iag.wxoauth.cache;/**
 * Created by zhangyin on 2017/1/5.
 */

import com.weijuju.iag.wxoauth.dataobject.OpenUser;
import com.weijuju.iag.wxoauth.entity.OauthInfo;
import com.weijuju.iag.wxoauth.service.OpenUserService;
import com.weijuju.iag.wxoauth.wx.WxApi;
import org.redisson.RedissonClient;
import org.redisson.core.RBucket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangyin
 * @create 2017-01-05
 */
@Component
public class OpenUserCache {

    private static Logger LOGGER = LoggerFactory.getLogger(OpenUserCache.class);

    private static String  USERKEY=RedisPrefix.KEY+"user:";

    @Autowired
    RedissonClient redissonClient;
    @Autowired
    OpenUserService openUserService;
    @Autowired
    WxApi wxApi;

    public void saveOpenUser(OpenUser openUser){
        //保存两个小时
        RBucket<OpenUser> bucket = redissonClient.getBucket(USERKEY + openUser.getOpenid());
        bucket.set(openUser,2, TimeUnit.HOURS);
    }

    public OpenUser getOpenUser(String  openid){
        //查询redis  OperUser对象
        RBucket<OpenUser> bucket = redissonClient.getBucket(USERKEY + openid);
        OpenUser openUser = bucket.get();
        if(openUser!=null){
            return openUser;
        }
        //查询 数据库 OperUser对象
        OpenUser openUserByopenid = openUserService.getOpenUserByopenid(openid);
        //为空  直接返回空
        if(openUserByopenid==null){
            return null;
        }
        //不为空 存进 redis  然后返回
        saveOpenUser(openUserByopenid);
        return openUserByopenid;
    }
    @Async
    public void init(OauthInfo oauthInfo){

        try {
            OpenUser userInfo = getOpenUser(oauthInfo.getOpenid());
            if(userInfo==null){
                //这是  第一次 登录
                userInfo = wxApi.getUserInfo(oauthInfo.getOpenid(), oauthInfo.getAccess_token());

            }
            //修改 accesstoken  refreshTOken 信息

            LOGGER.info("OauthInfo  信息 "+oauthInfo.getAccess_token()+"    "+oauthInfo.getExpires_in()+"    "+oauthInfo.getRefresh_token());

            userInfo.setAccessToken(oauthInfo.getAccess_token());
            userInfo.setRefreshToken(oauthInfo.getRefresh_token());
            userInfo.setExpiresIn(oauthInfo.getExpires_in());
            userInfo.setRefreshTime(new Date());
            userInfo.setScope(oauthInfo.getScope());
            openUserService.insertOrUpdate(userInfo);
            saveOpenUser(userInfo);

            //不是  第一次登录  这里需不需要 刷新token
            // 然后重新调用查询 微信用户信息的接口，更新下微信用户信息，这里再说
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("异步调用微信用户信息接口 异常",e);
        }
    }




}
