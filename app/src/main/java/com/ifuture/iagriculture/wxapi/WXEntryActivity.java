package com.ifuture.iagriculture.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ifuture.iagriculture.R;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.GetMessageFromWX;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**==========================================================================================================
 * @CopyRight: 王辰浩 2016~2026
 * @qq:975559549
 * @Author Feather Hunter(猎羽)
 * @Version: 0.1
 * @Date: 2016/4/25
 * @Description: 测试微信功能的Activity
 *
 * @doc
 *
 *  如果你的程序需要接收微信发送的请求，或者接收发送到微信请求的响应结果，需要下面3步操作：
 *  a. 在你的包名相应目录下新建一个wxapi目录，并在该wxapi目录下新增一个WXEntryActivity类，该类继承自Activity
 *   并在manifest文件里面加上exported属性，设置为true
 *   <activity android:name=".wxapi.WXEntryActivity" android:label="@string/app_name" android:exported="true"/>
 *  b. 实现IWXAPIEventHandler接口，微信发送的请求将回调到onReq方法，发送到微信请求的响应结果将回调到onResp方法
 *  c. 在WXEntryActivity中将接收到的intent及实现了IWXAPIEventHandler接口的对象传递给IWXAPI接口的handleIntent方法
 *
 *=============================================================================================================*/
public class WXEntryActivity extends Activity implements IWXAPIEventHandler{
    private static final String APP_ID = "wxcb35eeaafdc24a50";  //APP_ID为应用从官方网站申请到的合法appId
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
     * @Function: public void shareTextToMoments(String text);
     * @Description: 分享文字信息到朋友圈
     * @param text 需要分享的信息
     *------------------------------------------------------------------------------------------------------------------------------------*/
    public void shareTextToMoments(String text)
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
        req.scene = SendMessageToWX.Req.WXSceneTimeline;  //分享到朋友圈

        //调用api接口发送数据到微信
        api.sendReq(req);
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

        regToWx(); //注册到微信
        Button shareButton = (Button) findViewById(R.id.weixin_share_button);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //regToWx(); //注册到微信
                shareTextToMoments("这个demo测试，自动分享的信息");
            }
        });
        api.handleIntent(getIntent(), this);

    }

    /**---------------------------------------------------
     * @Function: public void onReq(BaseReq baseReq);
     * @Description: 微信发送的请求将回调到onReq
     *---------------------------------------------------*/
    public void onReq(BaseReq baseReq) {

    }

    /**-----------------------------------------------------
     * @Function: public void onResp(BaseResp baseResp);
     * @Description: 发送到微信请求的响应结果将回调到onResp方法
     *-----------------------------------------------------*/
    public void onResp(BaseResp baseResp) {
        this.finish();

    }
}
