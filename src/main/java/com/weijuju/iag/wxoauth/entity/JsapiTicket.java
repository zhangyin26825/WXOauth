package com.weijuju.iag.wxoauth.entity;/**
 * Created by zhangyin on 2017/1/22.
 */

/**
 * @author zhangyin
 * @create 2017-01-22
 */
public class JsapiTicket {

    private Integer errcode;

    private String  errmsg;

    private String  ticket;

    private Integer expires_in;

    public JsapiTicket() {
    }

    public Integer getErrcode() {
        return errcode;
    }

    public void setErrcode(Integer errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public Integer getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(Integer expires_in) {
        this.expires_in = expires_in;
    }
}
