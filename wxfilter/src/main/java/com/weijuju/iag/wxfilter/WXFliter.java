package com.weijuju.iag.wxfilter;/**
 * Created by zhangyin on 2017/1/9.
 */

import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * @author zhangyin
 * @create 2017-01-09
 */
public class WXFliter implements Filter {

    private static Logger LOGGER = LoggerFactory.getLogger(WXFliter.class);

    private static String COOKIEKEY="common_wx_openid";
    private static ThreadLocal<String> openid=new ThreadLocal();

    public static String getOpenid(){
        return openid.get();
    }

    public static PublicWxUser getUserInfo(String openid){
        String s = OauthUrlDomain + "/userinfo?openid="+openid;
        String userJson = getUserJson(s);
        JSONObject jsonObject = JSONObject.fromObject(userJson);
        JSONObject userinfo = jsonObject.getJSONObject("userinfo");
        PublicWxUser o = (PublicWxUser)JSONObject.toBean(userinfo, PublicWxUser.class);
        return o;
    }

    private static  boolean isTest=false;
    private static  String Appid=null;
    private static  String OauthUrlDomain =null;
    private static  String Scope=null;

    public void init(FilterConfig filterConfig) throws ServletException {
        {
            String test = filterConfig.getInitParameter("test");
             isTest = Boolean.parseBoolean(test);
        }
        {
            String appid = filterConfig.getInitParameter("appid");
            if (isEmpty(appid)) {
                Appid = "wx88a065d6eb1d505a";
            } else {
                Appid = appid;
            }
        }
        {
                String oauthUrl = filterConfig.getInitParameter("oauthUrlDomain");
                if (isEmpty(oauthUrl)) {
                    OauthUrlDomain = "http://iag-oauth-test.weijuju.com";
                } else {
                    OauthUrlDomain ="http://"+oauthUrl;
                }

        }
        {
            String scope = filterConfig.getInitParameter("scope");
            if(isEmpty(scope)){
                Scope="snsapi_userinfo";
            }else{
                if(scope.equals("snsapi_userinfo")){
                    Scope=scope;
                }else if(scope.equals("snsapi_base")){
                    Scope=scope;
                }else {
                    Scope="snsapi_userinfo";
                }
            }
        }

    }

    private  boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public  void addOpenCookie(String value, HttpServletResponse response){
        Cookie c=new Cookie(COOKIEKEY,value);
        c.setPath("/");
        response.addCookie(c);
    }
    private String getOpenCookie(HttpServletRequest request){
        try {
            Cookie[] cookies = request.getCookies();
            if (null==cookies) {
            } else {
                for(Cookie cookie : cookies){
                    if(cookie.getName().equals(COOKIEKEY)){
                        return cookie.getValue();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        if(isTest){
            //本地测试 不调用真正的授权
            String testopenid = request.getParameter("testopenid");
            if(isEmpty(testopenid)){
                openid.set("oBKBFuAgdw6SwGpacOvBd7BPCoZ0");
                addOpenCookie("oBKBFuAgdw6SwGpacOvBd7BPCoZ0",response);
            }else{
                openid.set(testopenid);
                addOpenCookie(testopenid,response);
            }
            filterChain.doFilter(servletRequest,servletResponse);
            return;
        }
        String openCookie = getOpenCookie(request);
        if(!isEmpty(openCookie)){
            //不为空 代表已经 授过权了
            openid.set(openCookie);
            filterChain.doFilter(servletRequest,servletResponse);
            return;
        }
        String wxopenid = request.getParameter("openid");
        if(!isEmpty(wxopenid)){
            //判断是不是授权完之后，重定向回来的地址
            openid.set(wxopenid);
            addOpenCookie(wxopenid,response);
            filterChain.doFilter(servletRequest,servletResponse);
            return;
        }
        //授权跳转地址 重定向到指定的地址
        String fullURL = getFullURL(request);
        String redirectUrl=URLEncoder.encode(fullURL,"UTF-8");
        String s = redirectOauthUrl(redirectUrl);
        response.sendRedirect(s);
        return;
    }
    private String getFullURL(HttpServletRequest request) {
        StringBuffer url = request.getRequestURL();
        if (request.getQueryString() != null) {
            url.append('?');
            url.append(request.getQueryString());
        }
        return url.toString();
    }

    private String redirectOauthUrl(String redirectUrl){
        String encode=null;
        try {
            String s = OauthUrlDomain + "/oauth2login";
            encode = URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        StringBuffer sb=new StringBuffer("https://open.weixin.qq.com/connect/oauth2/authorize");
        sb.append("?")
                             .append("appid").append("=").append(Appid)
                 .append("&").append("redirect_uri").append("=").append(encode)
                 .append("&").append("response_type").append("=").append("code")
                 .append("&").append("scope").append("=").append(Scope)
                 .append("&").append("state").append("=").append(redirectUrl)
                 .append("#wechat_redirect");
        return sb.toString();
    }

    public void destroy() {

    }

    private static String  getUserJson(String surl){
        LOGGER.info("http 请求 "+surl);
        try {
            URL url = new URL(surl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(30000);
            urlConnection.setReadTimeout(30000);
            LOGGER.info("http 请求url "+surl+"    请求状态码 "+urlConnection.getResponseCode());
            if(200 == urlConnection.getResponseCode()){
                //得到输入流
                InputStream is =urlConnection.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[is.available()];
                int len = 0;
                while(-1 != (len = is.read(buffer))){
                    baos.write(buffer,0,len);
                    baos.flush();
                }
                String result=baos.toString("utf-8");
                LOGGER.info("http 请求url返回json "+result);
                return result;
            }
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("根据openid请求用户信息接口调用失败",e);
        }
        return null;
    }

    public static void main(String[] args) {
//        String userJson = getUserJson("http://localhost:8080/userinfo?openid=oBKBFuAgdw6SwGpacOvBd7BPCoZ0");
//        System.out.println(userJson);
        String userJson="{\"userinfo\":{\"openid\":\"oBKBFuAgdw6SwGpacOvBd7BPCoZ0\",\"nickname\":\"张银\",\"sex\":1,\"language\":null,\"city\":\"娄底\",\"province\":\"湖南\",\"country\":\"中国\",\"headimgurl\":\"http://wx.qlogo.cn/mmopen/PiajxSqBRaEK9KDOYFniaIoiaANs7hRYficVWSAW9mgV57cyvBwPLb6quPTUMiaYv8bkrNOP5O1dKGiaXPpPVB3zia2qA/0\"}}";
        JSONObject jsonObject = JSONObject.fromObject(userJson);
        JSONObject userinfo = jsonObject.getJSONObject("userinfo");
        PublicWxUser o = (PublicWxUser)JSONObject.toBean(userinfo, PublicWxUser.class);
        System.out.println(o.getNickname()  );
    }
}
