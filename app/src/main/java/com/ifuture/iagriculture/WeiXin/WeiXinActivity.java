package com.ifuture.iagriculture.WeiXin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.mm.sdk.modelmsg.GetMessageFromWX;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.openapi.*;
import com.ifuture.iagriculture.R;

/**==========================================================================================================
 * @CopyRight: 王辰浩 2016~2026
 * @qq:975559549
 * @Author Feather Hunter(猎羽)
 * @Version: 0.1
 * @Date: 2016/4/25
 * @Description: 测试微信功能的Activity
 *=============================================================================================================*/
public class WeiXinActivity extends Activity {

    private static final String APP_ID = "wxfeather";  //APP_ID为应用从官方网站申请到的合法appId
    private IWXAPI api;       //IWXAPI 是第三方api和微信通信的openapi接口

    /**--------------------------------------------
     * @Function: public void regToWx();
     * @Description: 注册到微信
     *---------------------------------------------*/
    private void regToWx()
    {
        api = WXAPIFactory.createWXAPI(this, APP_ID, true);//通过WXAPIFactory工厂，获取IWXAPI实例
        api.registerApp(APP_ID);                           //将应用的appId注册到微信
    }
    /**----------------------------------------------------------------------------------------------------------------------------------
     * @Function: public void sendReqToWx(String text);
     * @Description: 发送请求到微信
     * @param text 需要发送的信息
     *
     * @doc:
     *  boolean sendReq(BaseReq req);
     *      sendReq是第三方app主动发送消息给微信，发送完成之后会切回到第三方app界面。
     *  boolean sendResp(BaseResp resp);
     *      sendResp是微信向第三方app请求数据，第三方app回应数据之后会切回到微信界面。
     *
     *  特别注意---SendMessageToWX.Req的scene成员:
     *  1.如果scene填WXSceneSession，那么消息会发送至微信的会话内。
     *  2.如果scene填WXSceneTimeline，那么消息会发送至朋友圈。
     *      （微信4.2以上支持，如果需要检查微信版本支持API的情况，可调用IWXAPI的getWXAppSupportAPI方法,0x21020001及以上支持发送朋友圈）
     *  scene默认值为WXSceneSession。
     *------------------------------------------------------------------------------------------------------------------------------------*/
    private void sendReqToWx(String text)
    {
        //初始化WXTextObject对象
        WXTextObject textObject = new WXTextObject();
        textObject.text = text;      //需要发送的信息

        //用WXTextObject对象初始化一个WXMediaMessage对象
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObject;
        msg.description = text;

        //构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());//transaction字段用于唯一标识一个请求
        req.message = msg;

        //调用api接口发送数据到微信
        api.sendReq(req);
    }
    private void sendRespToWx(Intent intent, String text)
    {
        Bundle bundle = intent.getExtras();//其中bundle为微信传递过来的intent所戴的内容，通过getExtras方法来获取
        //初始化WXTextObject对象
        WXTextObject textObject = new WXTextObject();
        textObject.text = text;      //需要发送的信息

        //用WXTextObject对象初始化一个WXMediaMessage对象
        WXMediaMessage msg = new WXMediaMessage(textObject);
        msg.description = text;

        //构造一个Resp
        GetMessageFromWX.Resp resp = new GetMessageFromWX.Resp();
        //将rep的transaction设置到resp对象中，其中bundle为微信传递过来的intent所戴的内容，通过getExtras方法来获取
        resp.transaction = new GetMessageFromWX.Req(bundle).transaction;
        resp.message = msg;

        //调用api接口相应数据数据到微信
        api.sendResp(resp);

    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wei_xin);
    }
}
