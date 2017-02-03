package com.weijuju.iag.wxoauth.service;/**
 * Created by zhangyin on 2017/1/5.
 */

import com.weijuju.iag.wxoauth.dataobject.OpenUser;

/**
 * @author zhangyin
 * @create 2017-01-05
 */
public interface OpenUserService {

    OpenUser getOpenUserByopenid(String openid);

    void insertOrUpdate(OpenUser openUser);
}
