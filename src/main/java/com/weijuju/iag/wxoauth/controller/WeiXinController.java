package com.weijuju.iag.wxoauth.controller;/**
 * Created by zhangyin on 2017/1/5.
 */

import com.weijuju.iag.wxoauth.cache.OpenUserCache;
import com.weijuju.iag.wxoauth.dataobject.OpenUser;
import com.weijuju.iag.wxoauth.entity.OauthInfo;
import com.weijuju.iag.wxoauth.util.WebUtil;
import com.weijuju.iag.wxoauth.wx.WxApi;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Arrays;

/** 提供给微信的接口
 * @author zhangyin
 * @create 2017-01-05
 */
@Controller
public class WeiXinController {


    private static Logger LOGGER = LoggerFactory.getLogger(WeiXinController.class);

     @Value("${TOKEN}")
     String TOKEN;

    @Autowired
    WxApi wxApi;
    @Autowired
    OpenUserCache openUserCache;

    /**
     * 验证 TOKEN
     */
    @RequestMapping("/wx")
    @ResponseBody
    public void wx(HttpServletRequest request, HttpServletResponse response){
        // 微信加密签名
        String signature = request.getParameter("signature");
        // 随机字符串
        String echostr = request.getParameter("echostr");
        // 时间戳
        String timestamp = request.getParameter("timestamp");
        // 随机数
        String nonce = request.getParameter("nonce");

        try {
            response.getWriter().print(echostr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/oauth2login")
    public ModelAndView wxoauthlogin(String code,String state){

        LOGGER.info("进入微信授权链接 code="+code);

         ModelAndView mav=new ModelAndView();
         if(StringUtils.isEmpty(code)){
             //用户禁止授权
             mav.setViewName("erroe.jsp");
             return  mav;
         }
        try {
            //根据 code 获取 access_token   openid
            OauthInfo oauthInfo = wxApi.getOauthInfo(code);
            String openid = oauthInfo.getOpenid();
            //保存 openid到 cookies中
            WebUtil.saveOpenId(openid);
            //异步调用微信接口，保存用户信息
            openUserCache.init(oauthInfo);
            mav.getModelMap().addAttribute("openid",openid);
            if(StringUtils.isNotEmpty(state)){
                String decode = URLDecoder.decode(state, "UTF-8");
                String url=null;
                if(decode.contains("?")){
                    url=decode+"&openid="+openid;
                }else{
                    url=decode+"?openid="+openid;
                }

                mav.setViewName("redirect:"+url);
            }else{
                mav.setViewName("index.jsp");
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("oauth2login请求失败",e);
        }
        return mav;
    }



}
