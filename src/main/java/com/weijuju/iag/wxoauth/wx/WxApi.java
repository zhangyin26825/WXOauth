package com.weijuju.iag.wxoauth.wx;/**
 * Created by zhangyin on 2017/1/5.
 */

import com.weijuju.iag.wxoauth.dataobject.OpenUser;
import com.weijuju.iag.wxoauth.entity.AccessToken;
import com.weijuju.iag.wxoauth.entity.JsapiTicket;
import com.weijuju.iag.wxoauth.entity.OauthInfo;
import com.weijuju.iag.wxoauth.util.HttpClientUtil;
import net.sf.json.JSONObject;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

/**
 * @author zhangyin
 * @create 2017-01-05
 */
@Component
public class WxApi {

    private static Logger LOGGER = LoggerFactory.getLogger(WxApi.class);

    @Value("${AppID}")
    String appid;
    @Value("${AppSecret}")
    String appsecret;

    /**  根据 appid    appsecret
     *   获取   AccessToken
     */
    public  AccessToken getAccessToken(){
        String url="https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+appid+"&secret="+appsecret+"";
        String s = HttpClientUtil.get(url);
        JSONObject jsonObject = JSONObject.fromObject(s);
        AccessToken o =(AccessToken) JSONObject.toBean(jsonObject, AccessToken.class);
        return o;
    }

    /**
     * 根据openid 获取微信用户信息
     */
    public  OpenUser  getUserInfo(String openid,String access_token){
        String url="https://api.weixin.qq.com/sns/userinfo?access_token="+access_token+"&openid="+openid+"&lang=zh_CN";
        String s = HttpClientUtil.get(url);
        LOGGER.info("获取用户个人信息   openid={}  access_token={}  返回json={} ",openid,access_token,s);
        JSONObject jsonObject = JSONObject.fromObject(s);
        //TODO   这里json转换还有问题
        OpenUser openUser=new OpenUser();
        openUser.setOpenid(jsonObject.getString("openid"));
        openUser.setNickname(jsonObject.getString("nickname"));
        openUser.setSex(jsonObject.getInt("sex"));
        openUser.setProvince(jsonObject.getString("province"));
        openUser.setCity(jsonObject.getString("city"));
        openUser.setCountry(jsonObject.getString("country"));
        openUser.setHeadimgurl(jsonObject.getString("headimgurl"));
        openUser.setCreateTime(new Date());
        openUser.setLastUpdateTime(new Date());
        return openUser;
    }

    /**
     * 根据  code得到用户的 openid  accessToken;
     */
    public OauthInfo getOauthInfo(String code){
        try {
            URIBuilder builder = new URIBuilder("https://api.weixin.qq.com/sns/oauth2/access_token");
            builder.addParameter("appid", appid);
            builder.addParameter("secret", appsecret);
            builder.addParameter("code", code);
            builder.addParameter("grant_type", "authorization_code");
            URI uri = builder.build();
            String s = HttpClientUtil.get(uri.toString());
            LOGGER.info("通过code获取access_token   code={}   返回json={} ",code,s);
            JSONObject jsonObject = JSONObject.fromObject(s);
            OauthInfo o=(OauthInfo)JSONObject.toBean(jsonObject,OauthInfo.class);
            return o;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public  JsapiTicket getJsapiTicket(String access_token){
        String url="https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token="+access_token+"&type=jsapi";
        String s = HttpClientUtil.get(url);
        JSONObject jsonObject = JSONObject.fromObject(s);
        JsapiTicket o =(JsapiTicket) JSONObject.toBean(jsonObject, JsapiTicket.class);
        return o;
    }



    public static void main(String[] args) {
        String s="{\n" +
                "   \"subscribe\": 1, \n" +
                "   \"openid\": \"o6_bmjrPTlm6_2sgVt7hMZOPfL2M\", \n" +
                "   \"nickname\": \"Band\", \n" +
                "   \"sex\": 1, \n" +
                "   \"language\": \"zh_CN\", \n" +
                "   \"city\": \"广州\", \n" +
                "   \"province\": \"广东\", \n" +
                "   \"country\": \"中国\", \n" +
                "   \"headimgurl\":  \"http://wx.qlogo.cn/mmopen/g3MonUZtNHkdmzicIlibx6iaFqAc56vxLSUfpb6n5WKSYVY0ChQKkiaJSgQ1dZuTOgvLLrhJbERQQ4\n" +
                "eMsv84eavHiaiceqxibJxCfHe/0\",\n" +
                "  \"subscribe_time\": 1382694957,\n" +
                "  \"unionid\": \" o6_bmasdasdsad6_2sgVt7hMZOPfL\"\n" +
                "  \"remark\": \"\",\n" +
                "  \"groupid\": 0,\n" +
                "  \"tagid_list\":[128,2]\n" +
                "}";
        JSONObject jsonObject = JSONObject.fromObject(s);
        OpenUser openUser=(OpenUser)JSONObject.toBean(jsonObject, OpenUser.class);
        System.out.println(openUser.getNickname());
    }
}
