package com.weijuju.iag.wxoauth.entity;

/**
 * 通过微信授权的code获取到的授权信息
 * 获取此信息后可以继续获取 openid 或者  openuser 信息
 * @author ctz
 */
public class OauthInfo {
	private String access_token;
	private String openid;
	private String refresh_token;
	private Integer expires_in;
	private String scope;

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getRefresh_token() {
		return refresh_token;
	}

	public void setRefresh_token(String refresh_token) {
		this.refresh_token = refresh_token;
	}

	public Integer getExpires_in() {
		return expires_in;
	}

	public void setExpires_in(Integer expires_in) {
		this.expires_in = expires_in;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}
}
