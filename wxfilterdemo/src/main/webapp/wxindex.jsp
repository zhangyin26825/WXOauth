<%--
  Created by IntelliJ IDEA.
  User: zhangyin
  Date: 2017/1/9
  Time: 16:38
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.weijuju.iag.wxfilter.WXFliter" %>
<%@ page import="com.weijuju.iag.wxfilter.PublicWxUser" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
 Hello
<%=WXFliter.getOpenid()%><br>
<%
    String openid=WXFliter.getOpenid();
    PublicWxUser userInfo = WXFliter.getUserInfo(openid);
    out.println(userInfo.getNickname());
%>


</body>
<script src="//cdn.bootcss.com/jquery/2.2.2/jquery.min.js"></script>
<script type="application/javascript" src="http://res.wx.qq.com/open/js/jweixin-1.0.0.js"></script>
<script type="application/javascript">
    wx.error(function(res){
        alert("初始化失败"+res)
    });
    wx.ready(function(){
        wx.onMenuShareAppMessage({
            title: '噶的所发生的规范', // 分享标题
            desc: '分享描述', // 分享描述
            link: 'http://iag-oauth-test.weijuju.com/wxfilterdemo/wxindex.jsp', // 分享链接
            imgUrl: 'http://static.resource.youyu.weijuju.com/iag_new/midea-gohome/Media/images/scarf1.png', // 分享图标
            type: 'link', // 分享类型,music、video或link，不填默认为link
            dataUrl: '', // 如果type是music或video，则要提供数据链接，默认为空
            success: function () {
                alert("分享成功了");
            },
            cancel: function () {
                // 用户取消分享后执行的回调函数
                alert("取消分享le");
            }
        });
        wx.onMenuShareTimeline({
            title: '噶的所发生的规范', // 分享标题
            link: 'http://iag-oauth-test.weijuju.com/wxfilterdemo/wxindex.jsp', // 分享链接
            imgUrl: 'http://static.resource.youyu.weijuju.com/iag_new/midea-gohome/Media/images/scarf1.png', // 分享图标
            success: function () {
                // 用户确认分享后执行的回调函数
                alert("分享成功了");
            },
            cancel: function () {
                // 用户取消分享后执行的回调函数
                alert("取消分享le");
            }
        });


    });
    $.ajax({
        type: "GET",
        url: "http://iag-oauth-test.weijuju.com/sign?url="+encodeURIComponent(location.href.split('#')[0]),
        datatype:"json",
        success: function(data){
            wx.config({
                debug: true, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
                appId: 'wx88a065d6eb1d505a', // 必填，公众号的唯一标识
                timestamp: data.timestamp , // 必填，生成签名的时间戳
                nonceStr:  data.noncestr, // 必填，生成签名的随机串
                signature: data.signature,// 必填，签名，见附录1
                jsApiList: ["onMenuShareTimeline","onMenuShareAppMessage","onMenuShareQQ","onMenuShareWeibo","onMenuShareQZone","startRecord","stopRecord","onVoiceRecordEnd","playVoice","pauseVoice","stopVoice","onVoicePlayEnd","uploadVoice","downloadVoice","chooseImage","previewImage","uploadImage","downloadImage","translateVoice","getNetworkType","openLocation","getLocation","hideOptionMenu","showOptionMenu","hideMenuItems","showMenuItems","hideAllNonBaseMenuItem","showAllNonBaseMenuItem","closeWindow","scanQRCode","chooseWXPay","openProductSpecificView","addCard","chooseCard","openCard"]
            });
        }
    });

</script>
</html>
