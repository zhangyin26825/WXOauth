package com.weijuju.iag.wxoauth.mapper;

import com.weijuju.iag.wxoauth.dataobject.OpenUser;
import com.weijuju.iag.wxoauth.dataobject.OpenUserExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface OpenUserMapper {
    int countByExample(OpenUserExample example);

    int deleteByExample(OpenUserExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(OpenUser record);

    int insertSelective(OpenUser record);

    List<OpenUser> selectByExampleWithRowbounds(OpenUserExample example, RowBounds rowBounds);

    List<OpenUser> selectByExample(OpenUserExample example);

    OpenUser selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") OpenUser record, @Param("example") OpenUserExample example);

    int updateByExample(@Param("record") OpenUser record, @Param("example") OpenUserExample example);

    int updateByPrimaryKeySelective(OpenUser record);

    int updateByPrimaryKey(OpenUser record);
}