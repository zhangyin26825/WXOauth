package com.weijuju.iag.wxoauth.controller;
/**
 * Created by zhangyin on 2017/1/6.
 */

import com.weijuju.iag.wxoauth.cache.JsapiTicketCache;
import com.weijuju.iag.wxoauth.cache.OpenUserCache;
import com.weijuju.iag.wxoauth.dataobject.OpenUser;
import com.weijuju.iag.wxoauth.entity.PublicWxUser;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.Map;
import java.util.TreeMap;

/**
提供给别的系统的接口调用
 */
@Controller
public class APIController {


    private static Logger LOGGER = LoggerFactory.getLogger(APIController.class);

    @Autowired
    OpenUserCache  openUserCache;

    @Autowired
    JsapiTicketCache jsapiTicketCache;


    @RequestMapping("/userinfo")
    @ResponseBody
    public ModelMap  getUserInfo(String openid){
        LOGGER.info("调用/userinfo接口  openid="+openid);
        ModelMap map=new ModelMap();
        OpenUser openUser = openUserCache.getOpenUser(openid);
        LOGGER.info("查询openid对应的OpenUser为"+openUser);
        PublicWxUser user=new PublicWxUser();
        try {
            BeanUtils.copyProperties(user,openUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
        map.addAttribute("userinfo",user);
        return map;
    }


    @RequestMapping("/sign")
    @ResponseBody
    public ModelMap  sign(String url, String nonceStr, Long timestamp, HttpServletResponse response){
        ModelMap map=new ModelMap();
        settingCORS(response);
        if(StringUtils.isEmpty(url)){
            map.addAttribute("message","url不能为空");
            return  map;
        }
        map.put("url",url);
        if(StringUtils.isEmpty(nonceStr)){
            nonceStr="weijuju";
        }
        map.put("noncestr",nonceStr);

        if(timestamp==null){
            timestamp=System.currentTimeMillis()/1000;
        }
        map.put("timestamp",timestamp);

        String jsapiTicket=jsapiTicketCache.getJSAPITICKET();
        map.put("jsapiTicket",jsapiTicket);

        String string1 = "jsapi_ticket=" +jsapiTicket+
                "&noncestr=" + nonceStr +
                "&timestamp=" + timestamp +
                "&url=" + url;
        map.put("string1",string1);
        try
        {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(string1.getBytes("UTF-8"));
            String signature = byteToHex(crypt.digest());
            map.put("signature",signature);
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

        return map;
    }

    private void settingCORS( HttpServletResponse response){
        response.setHeader("Access-Control-Allow-Origin", "*"); //解决跨域访问报错
        response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600"); //设置过期时间
        response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, client_id, uuid, Authorization");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // 支持HTTP 1.1.
        response.setHeader("Pragma", "no-cache"); // 支持HTTP 1.0. response.setHeader("Expires", "0");
    }
    private static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash)
        {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

}
