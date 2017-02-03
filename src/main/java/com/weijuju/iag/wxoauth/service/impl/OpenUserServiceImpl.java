package com.weijuju.iag.wxoauth.service.impl;/**
 * Created by zhangyin on 2017/1/5.
 */

import com.weijuju.iag.wxoauth.dataobject.OpenUser;
import com.weijuju.iag.wxoauth.dataobject.OpenUserExample;
import com.weijuju.iag.wxoauth.mapper.OpenUserMapper;
import com.weijuju.iag.wxoauth.service.OpenUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhangyin
 * @create 2017-01-05
 */
@Service
public class OpenUserServiceImpl implements OpenUserService {

    @Autowired
    OpenUserMapper openUserMapper;

    @Override
    public OpenUser getOpenUserByopenid(String openid) {
        OpenUserExample example=new OpenUserExample();
        example.createCriteria().andOpenidEqualTo(openid);
        List<OpenUser> openUsers = openUserMapper.selectByExample(example);
        if (openUsers.isEmpty()) {
            return null;
        }
        return  openUsers.get(0);
    }

    @Override
    public void insertOrUpdate(OpenUser openUser) {
        if(openUser.getId()==null){
            openUserMapper.insert(openUser);
        }else{
            openUserMapper.updateByPrimaryKey(openUser);
        }

    }
}
