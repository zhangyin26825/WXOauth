package com.weijuju.iag.wxoauth.util;/**
 * Created by zhangyin on 2017/1/5.
 */

import org.apache.http.conn.ssl.TrustStrategy;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * @author zhangyin
 * @create 2017-01-05
 */
public class AnyTrustStrategy  implements TrustStrategy {

    @Override
    public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        return true;
    }
}
