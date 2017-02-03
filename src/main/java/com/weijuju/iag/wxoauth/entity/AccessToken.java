package com.weijuju.iag.wxoauth.entity;/**
 * Created by zhangyin on 2017/1/5.
 */

import com.weijuju.iag.wxoauth.util.HttpClientUtil;
import net.sf.json.JSONObject;

/**
 * @author zhangyin
 * @create 2017-01-05
 */
public class AccessToken {

    private String access_token;

    private  Integer expires_in;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public Integer getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(Integer expires_in) {
        this.expires_in = expires_in;
    }



    public static void main(String[] args) {

//        AccessToken accessToken = AccessToken.getAccessToken("wx174435a4845d5256", "2d3540551affe3848bbaafffe00241f6");
//        System.out.println(accessToken.getAccess_token());
//        System.out.println(accessToken.getExpires_in());
    }
}
