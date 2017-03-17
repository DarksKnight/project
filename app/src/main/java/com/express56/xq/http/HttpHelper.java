package com.express56.xq.http;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.express56.xq.R;
import com.express56.xq.constant.ExpressConstant;
import com.express56.xq.model.AreaPriceInfo;
import com.express56.xq.model.BindInfo;
import com.express56.xq.okhttp.OkHttpUtils;
import com.express56.xq.okhttp.callback.BitmapCallback;
import com.express56.xq.okhttp.callback.FileCallBack;
import com.express56.xq.okhttp.callback.StringCallback;
import com.express56.xq.util.BitmapUtils;
import com.express56.xq.util.DialogUtils;
import com.express56.xq.util.Digests;
import com.express56.xq.util.DisplayUtil;
import com.express56.xq.util.Encodes;
import com.express56.xq.util.LogUtil;
import com.express56.xq.util.NetWorkUtil;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import alibaba.fastjson.JSON;
import alibaba.fastjson.JSONArray;
import okhttp3.Call;
import okhttp3.MediaType;

/**
 * Created by Administrator on 2016/5/24.
 */
public class HttpHelper {

    public static final MediaType MEDIA_TYPE = MediaType.parse("application/json");
    public static final MediaType MEDIA_TYPE_TEXT = MediaType.parse("application/text");

    public static final String TAG = HttpHelper.class.getSimpleName();


    public static final String HTTP = "http://";
//    public static final String IP = "58.220.216.74:82";
    public static final String IP = "192.168.1.102:8081";

    //  http://120.195.137.162:82
    //  http://192.168.31.234:8080
    //  http://192.168.1.102:8888
    //  http://58.220.216.74
    //  192.168.1.102:8081

    public static final String URL_1 = HTTP + IP + "/express/rest/registerCode";
    public static final String URL_2 = HTTP + IP + "/express/rest/login";
    public static final String URL_3 = HTTP + IP + "/express/rest/image/upload";
    public static final String URL_4 = HTTP + IP + "/express/rest/register";
    public static final String URL_6 = HTTP + IP + "/express/rest/user/info";
    public static final String URL_7 = HTTP + IP + "/express/rest/logout";
    public static final String URL_8 = HTTP + IP + "/express/rest/image/find";
    public static final String URL_9 = HTTP + IP + "/express/rest/barcode/add";
    public static final String URL_10 = HTTP + IP + "/express/rest/forget";
    public static final String URL_11 = HTTP + IP + "/express/rest/resetPwd";
    public static final String URL_12 = HTTP + IP + "/express/rest/user/updatePwd";
    public static final String URL_13 = HTTP + IP + "/express/rest/user/uploadHeaderImage";
    public static final String URL_14 = HTTP + IP + "/express/rest/user/setWifiUpload";
    public static final String URL_15 = HTTP + IP + "/express/rest/user/settings"; //get
    public static final String URL_16 = HTTP + IP + "/express/rest/company";//关于 get
    public static final String URL_17 = HTTP + IP + "/express/rest/ads/avaiable";//广告 get  广告图片比例为5:3
    public static final String URL_18 = HTTP + IP + "/express/rest/version?deviceType=android";//升级 get
    public static final String URL_19 = HTTP + IP + "/express/rest/user/introduce";//推广扫码 post
    public static final String URL_20 = HTTP + IP + "/express/rest/user/area";//区域价格查询接口
    public static final String URL_21 = HTTP + IP + "/express/rest/user/area/save"; //区域价格设置接口
    public static final String URL_22 = HTTP + IP + "/express/rest/config/area/get";//查询区域
    public static final String URL_23 = HTTP + IP + "/express/rest/config/area/edit";//查询区域
    public static final String URL_24 = HTTP + IP + "/express/rest/order/save";//下单接口
    public static final String URL_25 = HTTP + IP + "/express/rest/pay/alipay/get";//获取订单号
    public static final String URL_26 = HTTP + IP + "/express/rest/pay/log/save";//支付成功
    public static final String URL_27 = HTTP + IP + "/express/rest/order/find";//获取订单列表
    public static final String URL_28 = HTTP + IP + "/express/rest/config/company";//获取快递公司
    public static final String URL_29 = HTTP + IP + "/express/rest/order/get";//获取快递单数据
    public static final String URL_30 = HTTP + IP + "/express/rest/quotation/init";//下单页面初始化
    public static final String URL_31 = HTTP + IP + "/express/rest/user/push/open";//推送开
    public static final String URL_32 = HTTP + IP + "/express/rest/user/push/close";//推送关
    public static final String URL_33 = HTTP + IP + "/express/rest/user/service/get";//获取快递公司
    public static final String URL_34 = HTTP + IP + "/express/rest/user/service/save";//快递公司保存
    public static final String URL_35 = HTTP + IP + "/express/rest/user/account/common";//普通用户余额查询
    public static final String URL_36 = HTTP + IP + "/express/rest/quotation/save";//下单接口保存
    public static final String URL_37 = HTTP + IP + "/express/rest/order/quotations";//保价列表
    public static final String URL_38 = HTTP + IP + "/express/rest/order/cancel";//取消单子
    public static final String URL_39 = HTTP + IP + "/express/rest/recharge/list";//充值列表
    public static final String URL_40 = HTTP + IP + "/express/rest/recharge/add";//充值
    public static final String URL_41 = HTTP + IP + "/express/rest/recharge/flow";//充值记录列表
    public static final String URL_42 = HTTP + IP + "/express/rest/quotation/pay";//订单支付
    public static final String URL_43 = HTTP + IP + "/express/rest/order/apply";//申请退款

//    {"code":9,"result":{"version":"20161115.1.0beta","isRequire":"1","remarks":"测试","downloadPath":"app/android/express.apk"}}
//    返回结果说明：isRequire 是否必须升级 remarks 升级内容 downloadPath:升级地址
//    完整下载地址 http://localhost:8080/app/android/express.apk

    /**
     * 加密
     *
     * @param plainPassword
     * @return
     */
    public static String entryptPassword(String plainPassword) {
        String plain = Encodes.unescapeHtml(plainPassword);
        byte[] salt = Digests.generateSalt(SALT_SIZE);
        byte[] hashPassword = Digests.sha1(plain.getBytes(), salt, HASH_INTERATIONS);
        return Encodes.encodeHex(salt) + Encodes.encodeHex(hashPassword);
    }


    public static final int HASH_INTERATIONS = 1024;
    public static final int SALT_SIZE = 8;

    /**
     * 获取验证码
     *
     * @param page
     * @param requestID
     * @param mobilePhoneNumber
     * @param dialog
     */
    public static void sendRequest_getValidateCodeRegister(final Context page,
                                                           final int requestID,
                                                           String mobilePhoneNumber,
                                                           final Dialog dialog) {
        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        DialogUtils.showLoadingDialog(dialog);

        OkHttpUtils
                .get()
                .tag(page)
                .url(URL_1)
                .addParams("mobile", mobilePhoneNumber)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_getValidateCodeRegister", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_getValidateCodeRegister", requestTime);

//                        LogUtil.d(TAG, "api:sendRequest_getValidateCodeRegister: " + response);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        //网络返回处理
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }
                });
    }

    /**
     * 登录服务器接口
     *
     * @param page      context
     * @param requestID
     * @param loginName
     * @param password
     * @param deviceNo
     * @param dialog
     */
    public static void sendRequest_login(final Context page,
                                         final int requestID,
                                         String loginName,
                                         String password,
                                         String deviceNo,
                                         final Dialog dialog) {
        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        DialogUtils.showLoadingDialog(dialog);
        Map<String, String> map = new HashMap<String, String>();
        map.put("loginName", loginName);
        map.put("password", password);
        map.put("deviceType", "android");
        map.put("deviceNo", deviceNo);
        String content = JSON.toJSONString(map);
        LogUtil.d(TAG, "content:" + content);
//        LogUtil.d(TAG, "url:" + URL_2);
        OkHttpUtils
                .postString()
                .tag(page)
                .url(URL_2)
                .mediaType(MEDIA_TYPE)
                .content(content)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_login", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
//                            responsePage.doHttpResponse(null, requestID, e.getMessage());
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_login", requestTime);

//                        LogUtil.d(TAG, "api: sendRequest_login->: " + response);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }

                });

    }

    /**
     * 上传图片
     *
     * @param page        context
     * @param file        s
     * @param token
     * @param requestID
     * @param expressType
     */
    public static void sendRequest_uploadPic(final Context page,
                                             File file,
                                             String token,
                                             final int requestID,
                                             int expressType,
                                             String expressNo,
                                             String sender,
                                             String receiver,
                                             final Dialog dialog) {

        final long requestTime = System.currentTimeMillis();
        if (!(page instanceof IHttpProgressResponse)) {
            LogUtil.d(TAG, "this Activity must implements IHttpProgressResponse interface");
        }
        final IHttpResponse responsePage = (IHttpResponse) page;
        if (!NetWorkUtil.isNetworkConnected(page)) {
            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
            return;
        }
        if (dialog != null) {
            DialogUtils.showLoadingDialog(dialog);
        }
//        final ProgressDialog progressDialog = ProgressDialog.show(page, "", "正在上传", false, false);


        String fileName = file.getName();
        Map<String, String> map = new HashMap<String, String>();
        map.put("expressNo", expressNo);
        map.put("sender", sender);
        map.put("receiver", receiver);

        String content = JSON.toJSONString(map);
        LogUtil.d(TAG, "content:" + content);
        LogUtil.d(TAG, "sendRequest_uploadPic token : " + token);

        OkHttpUtils.post()
                .addFile("file", fileName, file)
//                .addParams("expressNo", expressNo)
//                .addParams("sender", sender)
//                .addParams("receiver", receiver)
//                .url(URL_20 + "?token=" + token + "&expressType=" + String.valueOf(expressType))
                .url(URL_3 + "?token=" + token + "&expressType=" + String.valueOf(expressType))
                .params(map)
//                .addHeader("Content-Type", "application/json")
//                .addHeader("appId", HttpHelper.entryptPassword("admin"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_uploadPic", requestTime);
                        if (responsePage != null) {
                            if (dialog != null) {
                                dialog.dismiss();
                                responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                            }
//                            progressDialog.dismiss();
//                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_uploadPic", requestTime);
                        if (responsePage != null) {
                            if (dialog != null) {
                                dialog.dismiss();
                            }
//                            progressDialog.dismiss();
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }
                });


    }

    /**
     * 注册
     *
     * @param page        context
     * @param requestID
     * @param username
     * @param pwd
     * @param repwd
     * @param realName
     * @param phoneNumber
     * @param userType
     * @param serviceArea
     * @param dialog
     */
    public static void sendRequest_registerNow(final Context page,
                                               final int requestID,
                                               String username,
                                               String pwd,
                                               String repwd,
                                               String realName,
                                               String phoneNumber,
                                               String verificationCode,
                                               int userType,
                                               String serviceArea,
                                               final Dialog dialog) {
        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        DialogUtils.showLoadingDialog(dialog);
        Map<String, String> map = new HashMap<String, String>();
        map.put("loginName", username);
        map.put("password", pwd);
        map.put("repwd", repwd);
        map.put("name", realName);
        map.put("phone", phoneNumber);
        map.put("email", "q1@163.com");
        map.put("verificationCode", verificationCode);
        map.put("userType", String.valueOf(userType));
        if (userType == ExpressConstant.USER_TYPE_COURIER) {
            map.put("serviceArea", serviceArea);
        }
        String content = JSON.toJSONString(map);
        LogUtil.d(TAG, "content:" + content);

        OkHttpUtils
                .postString()
                .tag(page)
                .url(URL_4)
                .mediaType(MEDIA_TYPE)
                .content(content)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_register", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_register", requestTime);

//                        LogUtil.d(TAG, "api: sendRequest_register->: " + response);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }

                });

    }

    /**
     * 获取用户个人信息
     *
     * @param page
     * @param requestID
     * @param token
     * @param dialog
     */
    public static void sendRequest_getUserInfo(final Context page,
                                               final int requestID,
                                               String token,
                                               final Dialog dialog) {
        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        if (dialog != null) {
            DialogUtils.showLoadingDialog(dialog);
        }
        OkHttpUtils
                .get()
                .tag(page)
                .url(URL_6 + "?token=" + token)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_getUserInfo", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_getUserInfo", requestTime);


//                        LogUtil.d(TAG, "api:sendRequest_getUserInfo: " + response);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        //网络返回处理
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }
                });
    }

    /**
     * 登出
     *
     * @param page
     * @param requestID
     * @param token
     * @param dialog
     */
    public static void sendRequest_logout(final Context page,
                                          final int requestID,
                                          String token,
                                          final Dialog dialog) {
        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        DialogUtils.showLoadingDialog(dialog);

        Map<String, String> map = new HashMap<String, String>();
//        map.put("token", token);
        String content = JSON.toJSONString(map);

//        LogUtil.d(TAG, "content : " + content);
        LogUtil.d(TAG, "sendRequest_logout token : " + token);

        OkHttpUtils
                .postString()
                .tag(page)
                .mediaType(MEDIA_TYPE)
                .url(URL_7 + "?token=" + token)
                .content(content)
//                .addHeader("appId", HttpHelper.entryptPassword("admin"))
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_logout", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_logout", requestTime);

//                        LogUtil.d(TAG, "api: sendRequest_logout->: " + response);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }

                });
    }

    /**
     * @param page
     * @param requestID
     * @param token
     * @param no        单号
     * @param pageNo    分页索引
     * @param dialog
     */
    public static void sendRequest_searchExpress(final Context page,
                                                 final int requestID,
                                                 String token,
                                                 String no,
                                                 String startDate,
                                                 String endDate,
                                                 String code,
                                                 int expressType,
                                                 int pageNo,
                                                 final Dialog dialog) {
        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        if (dialog != null) {
            DialogUtils.showLoadingDialog(dialog);
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("token", token);
        map.put("no", no);
        map.put("startDate", startDate);
        map.put("endDate", endDate);
        map.put("code", code);
        if (expressType == ExpressConstant.EXPRESS_TYPE_SEND || expressType == ExpressConstant.EXPRESS_TYPE_RECEIVE) {
            map.put("expressType", String.valueOf(expressType));
        }
        map.put("pageNo", String.valueOf(pageNo));
        String content = JSON.toJSONString(map);

        LogUtil.d(TAG, "content : " + content);
        LogUtil.d(TAG, "sendRequest_searchExpress token : " + token);
//        LogUtil.d(TAG, "sendRequest_searchExpress appId : " + HttpHelper.entryptPassword("admin"));

        OkHttpUtils
                .get()
                .tag(page)
                .url(URL_8)
                .params(map)
//                .addHeader("appId", HttpHelper.entryptPassword("admin"))
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_searchExpress", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_searchExpress", requestTime);

//                        LogUtil.d(TAG, "api: sendRequest_searchExpress->: " + response);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }

                });
    }


    /**
     * 上传条形码
     *
     * @param page
     * @param requestID
     * @param token
     * @param dialog
     */
    public static void sendRequest_uploadBarcodes(final Context page,
                                                  final int requestID,
                                                  String token,
                                                  String barcodes,
                                                  final Dialog dialog) {
        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        if (dialog != null) {
            DialogUtils.showLoadingDialog(dialog);
        }

//        Map<String, String> map = new HashMap<String, String>();
//        map.put("barcodes", barcodes);
//        String content = JSON.toJSONString(map);

//        LogUtil.d(TAG, "content : " + content);
//        String pw = HttpHelper.entryptPassword("admin");
//        LogUtil.d(TAG, "pw : " + pw);
        LogUtil.d(TAG, "barcodes : " + barcodes);
        LogUtil.d(TAG, "sendRequest_uploadBarcodes token : " + token);

        OkHttpUtils
                .postString()
                .tag(page)
                .mediaType(MEDIA_TYPE)
                .url(URL_9 + "?token=" + token)
                .content(barcodes)
//                .addHeader("appId", pw)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_uploadBarcodes", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        e.printStackTrace();
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_uploadBarcodes", requestTime);

                        LogUtil.d(TAG, "api: sendRequest_uploadBarcodes->: " + response);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }

                });


//
//        OkHttpClient okHttpClient = new OkHttpClient();
//        //创建一个RequestBody(参数1：数据类型 参数2传递的json串)
//        RequestBody requestBody = RequestBody.create(MEDIA_TYPE, content);
//        //创建一个请求对象
//        Request request = new Request.Builder()
//                .url(URL_52 + "?token=" + token)
//                .post(requestBody)
//                .addHeader("appId", pw)
//                .build();
//        //发送请求获取响应
//        try {
//            Response response=okHttpClient.newCall(request).execute();
//            //判断请求是否成功
//            if(response.isSuccessful()){
//                //打印服务端返回结果
//                Log.i(TAG,response.body().string());
//
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    /**
     * 获取验证码
     *
     * @param page
     * @param requestID
     * @param mobilePhoneNumber
     * @param dialog
     */
    public static void sendRequest_getValidateCodeForgetPWD(final Context page,
                                                            final int requestID,
                                                            String mobilePhoneNumber,
                                                            final Dialog dialog) {
        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        DialogUtils.showLoadingDialog(dialog);

        OkHttpUtils
                .get()
                .tag(page)
                .url(URL_10)
                .addParams("mobile", mobilePhoneNumber)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_getValidateCodeForgetPWD", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_getValidateCodeForgetPWD", requestTime);

//                        LogUtil.d(TAG, "api:sendRequest_getValidateCodeForgetPWD: " + response);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        //网络返回处理
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }
                });
    }

    /**
     * @param page
     * @param requestID
     * @param password
     * @param mobile
     * @param verificationCode
     * @param dialog
     * @param context
     */
    public static void sendRequest_resetPwd(final IHttpResponse page,
                                            final int requestID,
                                            String password,
                                            String mobile,
                                            String verificationCode,
                                            final Dialog dialog,
                                            Context context) {
        final long requestTime = System.currentTimeMillis();

        DialogUtils.showLoadingDialog(dialog);

        Map<String, String> map = new HashMap<String, String>();
        map.put("password", password);
        map.put("mobile", mobile);
        map.put("verificationCode", verificationCode);
        String content = JSON.toJSONString(map);

//        LogUtil.d(TAG, "content : " + content);

        OkHttpUtils
                .postString()
                .tag(page)
                .mediaType(MEDIA_TYPE)
                .url(URL_11)
                .content(content)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_resetPwd", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (page != null) {
                            page.doHttpResponse(null, requestID, ((Context) page).getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_resetPwd", requestTime);

//                        LogUtil.d(TAG, "api: sendRequest_resetPwd->: " + response);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (page != null) {
                            page.doHttpResponse(response, requestID);
                        }
                    }

                });
    }


    /**
     * 修改密码
     *
     * @param page        context
     * @param requestID
     * @param password
     * @param oldPassword
     * @param dialog
     */
    public static void sendRequest_modifyPwd(final Context page,
                                             final int requestID,
                                             String password,
                                             String oldPassword,
                                             String token,
                                             final Dialog dialog) {
        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        DialogUtils.showLoadingDialog(dialog);
        Map<String, String> map = new HashMap<String, String>();
        map.put("password", password);
        map.put("oldPassword", oldPassword);
        String content = JSON.toJSONString(map);
        LogUtil.d(TAG, "content:" + content);
//        LogUtil.d(TAG, "url:" + URL_2);
        OkHttpUtils
                .postString()
                .tag(page)
                .url(URL_12 + "?token=" + token)
                .mediaType(MEDIA_TYPE)
                .content(content)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_modifyPwd", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
//                            responsePage.doHttpResponse(null, requestID, e.getMessage());
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_modifyPwd", requestTime);

//                        LogUtil.d(TAG, "api: sendRequest_modifyPwd->: " + response);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }

                });

    }

    /**
     * 上传头像图片
     *
     * @param page      context
     * @param file      s
     * @param token
     * @param requestID
     * @param dialog
     */
    public static void sendRequest_uploadPortraitPic(final Context page,
                                                     File file,
                                                     String token,
                                                     final int requestID,
                                                     final Dialog dialog) {

        final long requestTime = System.currentTimeMillis();
        if (!(page instanceof IHttpProgressResponse)) {
            LogUtil.d(TAG, "this Activity must implements IHttpProgressResponse interface");
        }
        final IHttpResponse responsePage = (IHttpResponse) page;
        if (!NetWorkUtil.isNetworkConnected(page)) {
            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
            return;
        }
        if (dialog != null) {
            DialogUtils.showLoadingDialog(dialog);
        }

        String fileName = file.getName();
        Map<String, String> map = new HashMap<String, String>();

        String content = JSON.toJSONString(map);
        LogUtil.d(TAG, "content:" + content);
        LogUtil.d(TAG, "sendRequest_uploadPortraitPic token : " + token);

        OkHttpUtils.post()
                .addFile("file", fileName, file)
                .url(URL_13 + "?token=" + token)
                .params(map)
//                .addHeader("Content-Type", "application/json")
//                .addHeader("appId", HttpHelper.entryptPassword("admin"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_uploadPortraitPic", requestTime);
                        if (responsePage != null) {
                            if (dialog != null) {
                                dialog.dismiss();
                                responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                            }
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_uploadPortraitPic", requestTime);
                        if (responsePage != null) {
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }
                });


    }

    /**
     * 登录服务器接口
     *
     * @param page             context
     * @param requestID
     * @param token
     * @param isOnlyWifiUpload
     * @param dialog
     */
    public static void sendRequest_updateWifiUploadFlag(final Context page,
                                                        final int requestID,
                                                        String token,
                                                        boolean isOnlyWifiUpload,
                                                        final Dialog dialog) {
        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        DialogUtils.showLoadingDialog(dialog);
        Map<String, String> map = new HashMap<String, String>();
        String content = JSON.toJSONString(map);
        LogUtil.d(TAG, "content:" + content);
//        LogUtil.d(TAG, "url:" + URL_2);
        String flag = (isOnlyWifiUpload) ? "1" : "0";
        OkHttpUtils
                .postString()
                .tag(page)
                .url(URL_14 + "?token=" + token)
                .mediaType(MEDIA_TYPE_TEXT)
                .content(flag)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_updateWifiUploadFlag", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
//                            responsePage.doHttpResponse(null, requestID, e.getMessage());
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_updateWifiUploadFlag", requestTime);

//                        LogUtil.d(TAG, "api: sendRequest_login->: " + response);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }

                });

    }

    /**
     * get setting info
     *
     * @param page
     * @param requestID
     * @param dialog
     */
    public static void sendRequest_getSetting(final Context page,
                                              final int requestID,
                                              String token,
                                              final Dialog dialog) {
        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        if (dialog != null) {
            DialogUtils.showLoadingDialog(dialog);
        }

        OkHttpUtils
                .get()
                .tag(page)
                .url(URL_15 + "?token=" + token)
//                .addParams("mobile", mobilePhoneNumber)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_getSetting", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_getSetting", requestTime);

//                        LogUtil.d(TAG, "api:sendRequest_getSetting: " + response);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        //网络返回处理
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }
                });

    }

    /**
     * get about info
     *
     * @param page
     * @param requestID
     * @param dialog
     */
    public static void sendRequest_getAboutUs(final Context page,
                                              final int requestID,
                                              String token,
                                              final Dialog dialog) {
        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        DialogUtils.showLoadingDialog(dialog);

        OkHttpUtils
                .get()
                .tag(page)
                .url(URL_16 + "?token=" + token)
//                .addParams("mobile", mobilePhoneNumber)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_getAboutUs", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_getAboutUs", requestTime);

//                        LogUtil.d(TAG, "api:sendRequest_getAboutUs: " + response);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        //网络返回处理
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }
                });

    }

    /**
     * get advertizement info
     *
     * @param page
     * @param requestID
     * @param dialog
     */
    public static void sendRequest_getAdvertizement(final Context page,
                                                    final int requestID,
                                                    String token,
                                                    final Dialog dialog) {
        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        DialogUtils.showLoadingDialog(dialog);

        OkHttpUtils
                .get()
                .tag(page)
                .url(URL_17 + "?token=" + token)
//                .addParams("mobile", mobilePhoneNumber)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_getAdvertizement", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_getAdvertizement", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        //网络返回处理
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }
                });

    }

    /**
     * get new version info
     *
     * @param page
     * @param requestID
     * @param dialog
     */
    public static void sendRequest_getUpgradeInfo(final Context page,
                                                  final int requestID,
                                                  final Dialog dialog) {
        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        DialogUtils.showLoadingDialog(dialog);

        OkHttpUtils
                .get()
                .tag(page)
                .url(URL_18)
//                .url(URL_18 + "?token=" + token)
//                .addParams("mobile", mobilePhoneNumber)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_getUpgradeInfo", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_getUpgradeInfo", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        //网络返回处理
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }
                });

    }

    public static void sendRequest_introduce(final Context page,
                                             final int requestID,
                                             String token,
                                             String barCode,
                                             final Dialog dialog) {
        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        if (dialog != null) {
            DialogUtils.showLoadingDialog(dialog);
        }
        Map<String, String> map = new HashMap<String, String>();
        String content = JSON.toJSONString(map);
        LogUtil.d(TAG, "content:" + content);
//        LogUtil.d(TAG, "url:" + URL_2);
        OkHttpUtils
                .postString()
                .tag(page)
                .url(URL_19 + "?token=" + token)
                .mediaType(MEDIA_TYPE_TEXT)
                .content(barCode)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_introduce", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_introduce", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }

                });

    }


    /**
     * 用户绑定蓝牙设备
     *
     * @param page
     * @param requestID
     * @param bindInfo
     */
    public static void sendRequest_bindBike(final IHttpResponse page,
                                            final int requestID,
                                            BindInfo bindInfo,
                                            final Dialog dialog,
                                            Context context) {
        final long requestTime = System.currentTimeMillis();

        DialogUtils.showLoadingDialog(dialog);
        String content = JSON.toJSONString(bindInfo);

//        LogUtil.d(TAG, "content : " + content);

        OkHttpUtils
                .postString()
                .tag(page)
                .mediaType(MEDIA_TYPE)
                //.url(URL_11)
                .content(content)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_bindBike", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (page != null) {
                            page.doHttpResponse(null, requestID, ((Context) page).getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_bindBike", requestTime);

//                        LogUtil.d(TAG, "api: sendRequest_bindBike->: " + response);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (page != null) {
                            page.doHttpResponse(response, requestID);
                        }
                    }

                });
    }

    /**
     * 用户绑定蓝牙设备
     *
     * @param page
     * @param requestID
     * @param bindInfo
     */
    public static void sendRequest_UpdateUser(final IHttpResponse page,
                                              final int requestID,
                                              BindInfo bindInfo,
                                              final Dialog dialog,
                                              Context context) {
        final long requestTime = System.currentTimeMillis();

        DialogUtils.showLoadingDialog(dialog);
        String content = JSON.toJSONString(bindInfo);

//        LogUtil.d(TAG, "content : " + content);

        OkHttpUtils
                .postString()
                .tag(page)
                .mediaType(MEDIA_TYPE)
                //.url(URL_11)
                .content(content)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_UpdateUser", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (page != null) {
                            page.doHttpResponse(null, requestID, ((Context) page).getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_UpdateUser", requestTime);

//                        LogUtil.d(TAG, "api: sendRequest_bindBike->: " + response);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (page != null) {
                            page.doHttpResponse(response, requestID);
                        }
                    }

                });
    }

    /**
     * 注册
     *
     * @param page         context
     * @param requestID
     * @param phoneNumber
     * @param validateCode
     * @param dialog
     */
    public static void sendRequest_register(final Context page,
                                            final int requestID,
                                            String phoneNumber,
                                            String validateCode,
                                            final Dialog dialog) {
        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        DialogUtils.showLoadingDialog(dialog);
        Map<String, String> map = new HashMap<String, String>();
        map.put("Phone", phoneNumber);
        map.put("CheckCode", validateCode);
        map.put("device_os", "2");
        String content = JSON.toJSONString(map);
//        LogUtil.d(TAG, "content:" + content);
        OkHttpUtils
                .postString()
                .tag(page)
                //.url(URL_24)
                .mediaType(MEDIA_TYPE)
                .content(content)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_register", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_register", requestTime);

//                        LogUtil.d(TAG, "api: sendRequest_register->: " + response);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }

                });

    }

    /**
     * 登录服务器接口
     *
     * @param page        context
     * @param requestID
     * @param phoneNumber
     * @param token
     * @param dialog
     */
    public static void sendRequest_autoLogin(final Context page,
                                             final int requestID,
                                             String phoneNumber,
                                             String token,
                                             final Dialog dialog,
                                             boolean showDialog) {
        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        if (showDialog) {
            DialogUtils.showLoadingDialog(dialog);
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("Phone", phoneNumber);
        map.put("Token", token);
        map.put("device_os", "2");
        String content = JSON.toJSONString(map);
//        LogUtil.d(TAG, "content:" + content);
        OkHttpUtils
                .postString()
                .tag(page)
                //.url(URL_7)
                .mediaType(MEDIA_TYPE)
                .content(content)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_autoLogin", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_autoLogin", requestTime);

//                        LogUtil.d(TAG, "api: sendRequest_autoLogin->: " + response);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }

                });

    }

    /**
     * 获取所有学校信息 在地图上标点
     *
     * @param page      context
     * @param requestID
     * @param dialog
     */
    public static void sendRequest_getAllSchoolInfo(final Context page,
                                                    final int requestID,
                                                    final Dialog dialog,
                                                    boolean showDialog) {
        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        if (showDialog) {
            DialogUtils.showLoadingDialog(dialog);
        }
//        Map<String, String> map = new HashMap<String, String>();
//        String content = JSON.toJSONString(map);
//        LogUtil.d(TAG, "content:" + content);
        OkHttpUtils
                .get()
                .tag(page)
                //.url(URL_8)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_getAllSchoolInfo", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_getAllSchoolInfo", requestTime);

//                        LogUtil.d(TAG, "api: sendRequest_getAllSchoolInfo->: " + response);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }

                });

    }

    /**
     * 获取所有学校信息 在地图上标点
     *
     * @param page       context
     * @param requestID
     * @param latitude
     * @param longitude
     * @param dialog
     * @param showDialog true ：显示dialog
     */
    public static void sendRequest_getNearbyBikeSiteInfo(final Context page,
                                                         final int requestID,
                                                         double latitude,
                                                         double longitude,
                                                         final Dialog dialog,
                                                         boolean showDialog) {
        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        if (showDialog) {
            DialogUtils.showLoadingDialog(dialog);
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("lat", String.valueOf(latitude));
        map.put("lon", String.valueOf(longitude));
        String content = JSON.toJSONString(map);
//        LogUtil.d(TAG, "content:" + content);
        OkHttpUtils
                .get()
                .tag(page)
                //.url(URL_9)
                .params(map)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_getNearbyBikeSiteInfo", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_getNearbyBikeSiteInfo", requestTime);

//                        LogUtil.d(TAG, "api: sendRequest_getNearbyBikeSiteInfo->: " + response);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }

                });

    }

    /**
     * 获取用户的车辆绑定信息
     *
     * @param page       context
     * @param requestID
     * @param userID
     * @param dialog
     * @param showDialog true ：显示dialog
     */
    public static void sendRequest_getUserBindLockInfo(final Context page,
                                                       final int requestID,
                                                       String userID,
                                                       final Dialog dialog,
                                                       boolean showDialog) {
        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        if (showDialog) {
            DialogUtils.showLoadingDialog(dialog);
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("User_id", userID);
//        String content = JSON.toJSONString(map);
//        LogUtil.d(TAG, "content:" + content);
        OkHttpUtils
                .get()
                .tag(page)
                //.url(URL_10)
                .params(map)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_getUserBindLockInfo", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_getUserBindLockInfo", requestTime);

//                        LogUtil.d(TAG, "api: sendRequest_getUserBindLockInfo->: " + response);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }

                });

    }

    /**
     * lock bike
     *
     * @param page
     * @param requestID
     * @param bindInfo
     */
    public static void sendRequest_lockBike(final Context page,
                                            final int requestID,
                                            BindInfo bindInfo,
                                            final Dialog dialog) {
        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        DialogUtils.showLoadingDialog(dialog);
        String content = JSON.toJSONString(bindInfo);
//        LogUtil.d(TAG, "content : " + content);

        OkHttpUtils
                .postString()
                .tag(page)
                .mediaType(MEDIA_TYPE)
                //.url(URL_12)
                .content(content)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_lockBike", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_lockBike", requestTime);

//                        LogUtil.d(TAG, "api: sendRequest_lockBike->: " + response);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }

                });

//        RequestBody body = RequestBody.create(MEDIA_TYPE, content);
//
//        OkHttpUtils
//                .put()
//                .tag(page)
//                .url(URL_3)
//                .requestBody(body)
//                .build()
//                .execute(new StringCallback() {
//
//                    @Override
//                    public void onError(Call call, Exception e) {
//                        if (dialog != null) {
//                            dialog.dismiss();
//                        }
//                        if (page != null) {
//                            page.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
//                        }
//                    }
//
//                    @Override
//                    public void onResponse(String response) {
//                        LogUtil.d(TAG, "api: bindPhone->: " + response);
//                        if (dialog != null) {
//                            dialog.dismiss();
//                        }
//                        if (page != null) {
//                            page.doHttpResponse(response, requestID);
//                        }
//                    }
//
//                });
    }

    /**
     * unlock bike
     *
     * @param page
     * @param requestID
     * @param bindInfo
     */
    public static void sendRequest_unlockBike(final Context page,
                                              final int requestID,
                                              BindInfo bindInfo,
                                              final Dialog dialog) {
        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        DialogUtils.showLoadingDialog(dialog);
        String content = JSON.toJSONString(bindInfo);
//        LogUtil.d(TAG, "content : " + content);

        OkHttpUtils
                .postString()
                .tag(page)
                .mediaType(MEDIA_TYPE)
                //.url(URL_13)
                .content(content)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_unlockBike", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_unlockBike", requestTime);

//                        LogUtil.d(TAG, "api: sendRequest_unlockBike->: " + response);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }

                });
    }

    /**
     * release alarm bike
     *
     * @param page
     * @param requestID
     * @param bindInfo
     */
    public static void sendRequest_releaseAlarmBike(final Context page,
                                                    final int requestID,
                                                    BindInfo bindInfo,
                                                    final Dialog dialog) {
        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        DialogUtils.showLoadingDialog(dialog);
        String content = JSON.toJSONString(bindInfo);
//        LogUtil.d(TAG, "content : " + content);

        OkHttpUtils
                .postString()
                .tag(page)
                .mediaType(MEDIA_TYPE)
                //.url(URL_14)
                .content(content)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_releaseAlarmBike", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_releaseAlarmBike", requestTime);

//                        LogUtil.d(TAG, "api: sendRequest_releaseAlarmBike->: " + response);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }

                });
    }

    /**
     * release alarm bike
     *
     * @param page
     * @param requestID
     * @param bindInfo
     */
    public static void sendRequest_releaseAlarmBike(Dialog page, final Context context,
                                                    final int requestID,
                                                    BindInfo bindInfo,
                                                    final Dialog dialog) {
        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        DialogUtils.showLoadingDialog(dialog);
        final String content = JSON.toJSONString(bindInfo);
//        LogUtil.d(TAG, "content : " + content);

        OkHttpUtils
                .postString()
                .tag(page)
                .mediaType(MEDIA_TYPE)
                //.url(URL_14)
                .content(content)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_releaseAlarmBike2", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, context.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_releaseAlarmBike2", requestTime);

//                        LogUtil.d(TAG, "api: sendRequest_releaseAlarmBike2->: " + response);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }

                });
    }

    /**
     * confirm alert
     *
     * @param page
     * @param requestID
     * @param bindInfo
     */
    public static void sendRequest_confirmAlert(final Context page,
                                                final int requestID,
                                                BindInfo bindInfo,
                                                final Dialog dialog) {
        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        DialogUtils.showLoadingDialog(dialog);
        String content = JSON.toJSONString(bindInfo);
//        LogUtil.d(TAG, "content : " + content);

        OkHttpUtils
                .postString()
                .tag(page)
                .mediaType(MEDIA_TYPE)
                //.url(URL_19)
                .content(content)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_confirmAlert", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_confirmAlert", requestTime);

//                        LogUtil.d(TAG, "api: sendRequest_confirmAlert->: " + response);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }

                });
    }

    /**
     * confirm alert
     *
     * @param page
     * @param requestID
     * @param bindInfo
     */
    public static void sendRequest_confirmAlert(Dialog page, final Context context,
                                                final int requestID,
                                                BindInfo bindInfo,
                                                final Dialog dialog) {
        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        DialogUtils.showLoadingDialog(dialog);
        String content = JSON.toJSONString(bindInfo);
//        LogUtil.d(TAG, "content : " + content);

        OkHttpUtils
                .postString()
                .tag(page)
                .mediaType(MEDIA_TYPE)
                //.url(URL_19)
                .content(content)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_confirmAlert1", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, context.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_confirmAlert1", requestTime);

//                        LogUtil.d(TAG, "api: sendRequest_confirmAlert1->: " + response);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }

                });
    }

    /**
     * removeBind bike
     *
     * @param page
     * @param requestID
     * @param bindInfo
     */
    public static void sendRequest_removeBindBike(final Context page,
                                                  final int requestID,
                                                  BindInfo bindInfo,
                                                  final Dialog dialog) {
        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        DialogUtils.showLoadingDialog(dialog);
        String content = JSON.toJSONString(bindInfo);
//        LogUtil.d(TAG, "content : " + content);

        OkHttpUtils
                .postString()
                .tag(page)
                .mediaType(MEDIA_TYPE)
                //.url(URL_15)
                .content(content)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_removeBindBike", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_removeBindBike", requestTime);
//                        LogUtil.d(TAG, "api: sendRequest_removeBindBike->: " + response);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }

                });
    }

    /**
     * 获取用户的车辆绑定信息
     *
     * @param page       context
     * @param requestID
     * @param school_ID
     * @param dialog
     * @param showDialog true ：显示dialog
     */
    public static void sendRequest_getBikeSiteOfSchool(final Context page,
                                                       final int requestID,
                                                       String school_ID,
                                                       final Dialog dialog,
                                                       boolean showDialog) {
        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        if (showDialog) {
            DialogUtils.showLoadingDialog(dialog);
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("id", school_ID);
//        String content = JSON.toJSONString(map);
//        LogUtil.d(TAG, "content:" + content);
        OkHttpUtils
                .get()
                .tag(page)
                //.url(URL_17)
                .params(map)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_getBikeSiteOfSchool", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_getBikeSiteOfSchool", requestTime);

//                        LogUtil.d(TAG, "api: sendRequest_getBikeSiteOfSchool->: " + response);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }

                });

    }

    /**
     * 获取用户的车辆绑定信息
     *
     * @param page       context
     * @param requestID
     * @param dialog
     * @param showDialog true ：显示dialog
     */
    public static void sendRequest_checkUpgrade(final Context page,
                                                final int requestID,
                                                final Dialog dialog,
                                                boolean showDialog) {
        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        if (showDialog) {
            DialogUtils.showLoadingDialog(dialog);
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("device_os", "2");
//        String content = JSON.toJSONString(map);
//        LogUtil.d(TAG, "content:" + content);
        OkHttpUtils
                .get()
                .tag(page)
                //.url(URL_18)
                .params(map)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_checkUpgrade", requestTime);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_checkUpgrade", requestTime);
//                        LogUtil.d(TAG, "api: sendRequest_checkUpdate->: " + response);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }

                });

    }


    /**
     * 下载安装新版本apk
     *
     * @param page      context
     * @param url       下载地址
     * @param requestID
     * @throws Exception
     */
    public static void sendRequest_downloadApk(final Context page,
                                               String url,
                                               final int requestID) {
        final long requestTime = System.currentTimeMillis();

        final IHttpProgressResponse responsePage = (IHttpProgressResponse) page;

        final ProgressDialog progressDialog = new ProgressDialog(page);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        //显示进度条
        progressDialog.setMessage("下载中");
        progressDialog.setMax(100);
        progressDialog.show();
        progressDialog.setCancelable(false);

        OkHttpUtils
                .get()
                .tag(page)
//    .url("http://121.40.34.43/isriding/Upload/LockBikeApp_0.0.2.apk")
                .url(url)
                .build()
                .execute(new FileCallBack(ExpressConstant.DOWNLOAD_APK_DIR, ExpressConstant.APK_FILE_NAME) {
                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_downloadApk", requestTime);
                        LogUtil.d(TAG, "sendRequest_downloadApk = " + e.getMessage());
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(File response) {
                        printAPI_TimeConsuming("sendRequest_downloadApk", requestTime);
//                        LogUtil.d(TAG, "sendRequest_downloadApk response = " + response);
                        progressDialog.dismiss();
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }

                    @Override
                    public void inProgress(float progress, long total) {
//                        LogUtil.d(TAG, "progress = " + progress);
//                        LogUtil.d(TAG, "total = " + total);
//                        LogUtil.d(TAG, "(int) (progress * 100) = " + (int) (progress * 100));
                        progressDialog.setProgress((int) (progress * 100));
                    }
                });
    }

    /**
     * 上传图片
     *
     * @param page      context
     * @param filePath
     * @param requestID
     */
    public static void sendRequest_uploadHeadPortrait(final Context page,
                                                      String filePath,
                                                      final int requestID,
                                                      String userID) {
        final long requestTime = System.currentTimeMillis();
        if (!(page instanceof IHttpProgressResponse)) {
            LogUtil.d(TAG, "this Activity must implements IHttpProgressResponse interface");
        }
        final IHttpProgressResponse responsePage = (IHttpProgressResponse) page;

        final ProgressDialog progressDialog = ProgressDialog.show(page, "", "正在上传", false, false);

        String fileName = new File(filePath).getName();
        File file = new File(filePath);
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", userID);
        String content = JSON.toJSONString(map);
//        LogUtil.d(TAG, "content:" + content);
        OkHttpUtils.post()
                .addFile("filedata", fileName, file)
                //.url(URL_38)
                .params(map)
                .addHeader("Content-Type", "application/json")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_uploadHeadPortrait", requestTime);
                        if (responsePage != null) {
                            progressDialog.dismiss();
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_uploadHeadPortrait", requestTime);
                        if (responsePage != null) {
                            progressDialog.dismiss();
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }
                });
    }

    /**
     * 上传图片
     *
     * @param page      context
     * @param filePath
     * @param requestID
     */
    public static void sendRequest_uploadPic(final Context page,
                                             String filePath,
                                             final int requestID,
                                             int actionType) {
        final long requestTime = System.currentTimeMillis();
        if (!(page instanceof IHttpProgressResponse)) {
            LogUtil.d(TAG, "this Activity must implements IHttpProgressResponse interface");
        }
        final IHttpResponse responsePage = (IHttpResponse) page;
//        final ProgressDialog progressDialog = new ProgressDialog(page);
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        //显示进度条
//        progressDialog.setMessage("正在上传");
//        progressDialog.show();
//        progressDialog.setCancelable(false);
        final ProgressDialog progressDialog = ProgressDialog.show(page, "", "正在上传", false, false);

//        PostFileBuilder fileBuilder = OkHttpUtils.postFile();
//        fileBuilder.mediaType(MediaType.parse("image/jpg"));
//        fileBuilder.file(new File(filePath))
//                .tag(page)
//                .url(URL_20 + "?bike")
//                .build()
//                .execute(new Callback() {
//
//                    @Override
//                    public void inProgress(float progress) {
//                        responsePage.doHttpProgressResponse(progress);
////                        progressDialog.setProgress((int) (progress * 100));
//                    }
//
//                    @Override
//                    public Object parseNetworkResponse(Response response) throws Exception {
//                        return null;
//                    }
//
//                    @Override
//                    public void onError(Call call, Exception e) {
//                        if (responsePage != null) {
//                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
//                        }
//                    }
//
//                    @Override
//                    public void onResponse(Object response) {
//                        progressDialog.dismiss();
//                        if (responsePage != null) {
//                            responsePage.doHttpResponse(response, requestID);
//                        }
//                    }
//                });

//        OkHttpClient mOkHttpClient = new OkHttpClient();
//
//        MultipartBody.Builder builder = new MultipartBody.Builder();
//        // 这里演示添加用户ID
//        builder.addFormDataPart("bikeid", bikeID);
//        String fileName = new File(filePath).getName();
//        File file = new File(filePath);
//        builder.addFormDataPart("filedata", fileName,
//                RequestBody.create(MediaType.parse("image/jpeg"), file));
//        if (file.exists()) {
//            FileInputStream fis = null;
//            try {
//                fis = new FileInputStream(file);
//                try {
//                    int size = fis.available();
//                    LogUtil.d(TAG, "size : " + size);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//        }

        String fileName = new File(filePath).getName();
        File file = new File(filePath);
        Map<String, String> map = new HashMap<String, String>();
        String content = JSON.toJSONString(map);
//        LogUtil.d(TAG, "content:" + content);
        String url = null;
        if (actionType == ExpressConstant.UPLOAD_TYPE_BIKE) {
            url = URL_3;
        } else if (actionType == ExpressConstant.UPLOAD_TYPE_USER) {
            // url = URL_22;
        }

        OkHttpUtils.post()
                .addFile("filedata", fileName, file)
                .url(url)
//                .params(map)
                .addHeader("Content-Type", "application/json")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_uploadPic", requestTime);
                        if (responsePage != null) {
                            progressDialog.dismiss();
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_uploadPic", requestTime);
                        if (responsePage != null) {
                            progressDialog.dismiss();
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }
                });


//        RequestBody requestBody = builder.build();
//        Request.Builder reqBuilder = new Request.Builder();
//        Request request = reqBuilder
//                .url(URL_20)
//                .post(requestBody)
//                .build();
//        try {
//            Call call = mOkHttpClient.newCall(request);
//            Response response = mOkHttpClient.newCall(request).execute();
//            LogUtil.d(TAG, "响应码 " + response.code());
//            if (response.isSuccessful()) {
//                String resultValue = response.body().string();
//                LogUtil.d(TAG, "响应体 " + resultValue);
//                if (responsePage != null) {
//                    responsePage.doHttpResponse(resultValue, requestID);
//                }
//            } else {
//                if (responsePage != null) {
//                    LogUtil.d(TAG, "response =" + response.message() + " response.code = " + response.code());
//                    responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_upload_pic_fail));
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            if (responsePage != null) {
//                responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_upload_pic_fail));
//            }
//        }

//        OkHttpUtils.post()//
//                .addFile("mFile", "messenger_01.png", file)//
//                .addFile("mFile", "test1.txt", file2)//
//                .url(url)
//                .params(params)//
//                .headers(headers)//
//                .build()//
//                .execute(new MyStringCallback());

    }

    /**
     * 加载网络图片
     *
     * @param page
     * @param requestID
     * @param url
     * @param imageView
     * @param dialog
     */
    public static void sendRequest_loadImage(final Context page,
                                             final int requestID,
                                             String url,
                                             final ImageView imageView,
                                             final Dialog dialog,
                                             boolean showDialog) {
        sendRequest_loadImage(page, requestID, url, imageView, null, dialog, showDialog, false, 0.0f);
    }

    /**
     * 加载快递单图片
     *
     * @param page
     * @param requestID
     * @param url
     * @param imageView
     * @param file
     */
    public static void sendRequest_loadExpressImage(final Context page,
                                                    final int requestID,
                                                    String url,
                                                    final ImageView imageView,
                                                    File file,
                                                    float iamgeShowWidth) {
        sendRequest_loadImage(page, requestID, url, imageView, file, null, false, true, iamgeShowWidth);
    }

    public static void sendRequest_loadImage(final Context page,
                                             final int requestID,
                                             String url,
                                             final ImageView imageView,
                                             final Dialog dialog,
                                             boolean showDialog,
                                             final boolean isImageViewLinearLayout) {
        sendRequest_loadImage(page, requestID, url, imageView, null, null, false, false, 0.0f);
    }

    public static void sendRequest_loadImage(final Context page,
                                             final int requestID,
                                             String url,
                                             final ImageView imageView,
                                             final File file,
                                             final Dialog dialog,
                                             boolean showDialog,
                                             final boolean isImageViewLinearLayout,
                                             final float iamgeShowWidth) {
        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        if (showDialog && dialog != null) {
            DialogUtils.showLoadingDialog(dialog);
//            dialog.setCancelable(false);
        }
        LogUtil.d(TAG, "loadImage url" + url);
        OkHttpUtils
                .get()
                .url(url)
                .build()
                .execute(new BitmapCallback() {

                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_loadImage", requestTime);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            imageView.setImageResource(R.drawable.default_pic);
//                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }


                    @Override
                    public void onResponse(Bitmap bitmap) {
                        printAPI_TimeConsuming("sendRequest_loadImage", requestTime);
//                        LogUtil.d(TAG, "api: sendRequest_loadImage->: " + response);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (bitmap != null) {
                            if (!isImageViewLinearLayout) {
                                setImagViewParameters(imageView);
                            }
                            if (file != null) {
                                BitmapUtils.saveBitmapToFile(bitmap, file);
                            }
                            if (iamgeShowWidth > 0) {
                                bitmap = BitmapUtils.getZoomBitmap(bitmap, iamgeShowWidth / bitmap.getWidth());
                            }
                            imageView.setImageBitmap(bitmap);
                            LogUtil.e(TAG, "---------------------6----------------------");
                        } else {
//                            imageView.setImageResource(R.drawable.default_pic);
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(bitmap, requestID);
                        }
                    }
                });

    }

    public static void setImagViewParameters(ImageView imageView) {
        int showWidth = (int) (DisplayUtil.screenWidth - 22 * DisplayUtil.density);
        RelativeLayout.LayoutParams layoutParams =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.width = showWidth;
        layoutParams.height = showWidth * 3 / 5;
        imageView.setLayoutParams(layoutParams);
    }

    /**
     * 加载网络图片
     *
     * @param page
     * @param requestID
     * @param url
     * @param imageView
     * @param dialog
     */
    public static void sendRequest_loadHeadPortraitImage(final Context page,
                                                         final int requestID,
                                                         String url,
                                                         final ImageView imageView,
                                                         final Dialog dialog) {
        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        if (dialog != null) {
            DialogUtils.showLoadingDialog(dialog);
//            dialog.setCancelable(false);
        }
        OkHttpUtils
                .get()
                .url(url)
                .build()
                .execute(new BitmapCallback() {

                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_loadHeadPortraitImage", requestTime);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }


                    @Override
                    public void onResponse(Bitmap bitmap) {
                        printAPI_TimeConsuming("sendRequest_loadHeadPortraitImage", requestTime);
//                        LogUtil.d(TAG, "api: sendRequest_loadImage->: " + response);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (bitmap != null) {
//                            imageView.setSrcDrawable(bitmap);
                            imageView.setImageBitmap(bitmap);
                            imageView.postInvalidate();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(bitmap, requestID);
                        }
                    }
                });

    }

    /**
     * 上传车辆图片地址
     *
     * @param page      context
     * @param serial    车牌编号
     * @param bike_img  服务器返回的图片地址
     * @param requestID
     */
    public static void sendRequest_updateBikePic(final Context page,
                                                 String serial,
                                                 String bike_img,
                                                 final int requestID,
                                                 final Dialog dialog) {
        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        DialogUtils.showLoadingDialog(dialog);

        Map<String, String> map = new HashMap<String, String>();
        map.put("serial", serial);
        map.put("bike_img", bike_img);
        String content = JSON.toJSONString(map);
//        LogUtil.d(TAG, "content:" + content);

        OkHttpUtils
                .postString()
                .tag(page)
                //.url(URL_23)
                .mediaType(MEDIA_TYPE)
                .content(content)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_updateBikePic", requestTime);

                        if (responsePage != null) {
                            dialog.dismiss();
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_updateBikePic", requestTime);

                        if (responsePage != null) {
                            dialog.dismiss();
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }
                });
    }

    /**
     * 提交认证申请
     *
     * @param page      context
     * @param id
     * @param realName
     * @param img
     * @param user_type
     * @param school_id
     * @param requestID
     * @param dialog
     */
    public static void sendRequest_submitUserAuthentication(final Context page,
                                                            String id,
                                                            String realName,
                                                            String img,
                                                            int user_type,
                                                            int school_id,
                                                            final int requestID,
                                                            final Dialog dialog) {
        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        DialogUtils.showLoadingDialog(dialog);

        Map<String, String> map = new HashMap<String, String>();
        map.put("id", id);
        map.put("name", realName);
        map.put("user_type", String.valueOf(user_type));
        map.put("school_id", String.valueOf(school_id));
        map.put("img", img);
        String content = JSON.toJSONString(map);
//        LogUtil.d(TAG, "content:" + content);

        OkHttpUtils
                .postString()
                .tag(page)
                //.url(URL_21)
                .mediaType(MEDIA_TYPE)
                .content(content)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_submitUserAuthentication", requestTime);
                        if (responsePage != null) {
                            dialog.dismiss();
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_submitUserAuthentication", requestTime);
                        if (responsePage != null) {
                            dialog.dismiss();
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }
                });
    }

    /**
     * 提交认证申请
     *
     * @param page      context
     * @param id
     * @param realName
     * @param user_type
     * @param id_no
     * @param requestID
     * @param dialog
     */
    public static void sendRequest_submitUserAuthentication(final Context page,
                                                            String id,
                                                            String realName,
                                                            int user_type,
                                                            String id_no,
                                                            final int requestID,
                                                            final Dialog dialog) {
        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        DialogUtils.showLoadingDialog(dialog);

        Map<String, String> map = new HashMap<String, String>();
        map.put("id", id);
        map.put("name", realName);
        map.put("user_type", String.valueOf(user_type));
        map.put("id_no", id_no);
        String content = JSON.toJSONString(map);
//        LogUtil.d(TAG, "content:" + content);

        OkHttpUtils
                .postString()
                .tag(page)
                //.url(URL_21)
                .mediaType(MEDIA_TYPE)
                .content(content)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_submitUserAuthentication", requestTime);
                        if (responsePage != null) {
                            dialog.dismiss();
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_submitUserAuthentication", requestTime);
                        if (responsePage != null) {
                            dialog.dismiss();
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }
                });
    }

    /**
     * 获取用户协议
     *
     * @param page
     * @param requestID
     * @param dialog
     * @param context
     */
    public static void sendRequest_getRegisterProtocol(final IHttpResponse page,
                                                       final int requestID,
                                                       final Dialog dialog,
                                                       Context context) {
        final long requestTime = System.currentTimeMillis();
        DialogUtils.showLoadingDialog(dialog);
        OkHttpUtils
                .get()
                .tag(page)
                //.url(URL_25)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_getRegisterProtocol", requestTime);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (page != null) {
                            page.doHttpResponse(null, requestID, ((Context) page).getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_getRegisterProtocol", requestTime);
//                        LogUtil.d(TAG, "api:sendRequest_getRegisterProtocol->: " + response);
                        if (dialog != null) {
//                            dialog.dismiss();
                        }

                        //网络返回处理
                        if (page != null) {
                            page.doHttpResponse(response, requestID);
                        }
                    }
                });
    }

    /**
     * 获取关于信息
     *
     * @param page
     * @param requestID
     * @param dialog
     * @param context
     */
    public static void sendRequest_getAboutUs(final IHttpResponse page,
                                              final int requestID,
                                              final Dialog dialog,
                                              Context context) {
        final long requestTime = System.currentTimeMillis();
        DialogUtils.showLoadingDialog(dialog);
        OkHttpUtils
                .get()
                .tag(page)
                //.url(URL_41)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_getAboutUs", requestTime);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (page != null) {
                            page.doHttpResponse(null, requestID, ((Context) page).getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_getAboutUs", requestTime);
//                        LogUtil.d(TAG, "api:sendRequest_getRegisterProtocol->: " + response);
                        if (dialog != null) {
//                            dialog.dismiss();
                        }

                        //网络返回处理
                        if (page != null) {
                            page.doHttpResponse(response, requestID);
                        }
                    }
                });
    }

    /**
     * 打印接口调用耗时时间
     *
     * @param apiName
     * @param requestTime
     */
    private static void printAPI_TimeConsuming(String apiName,
                                               long requestTime) {
        LogUtil.d(TAG, apiName + " consuming time is : " + (System.currentTimeMillis() - requestTime));
    }

    /**
     * 获取用户的车辆绑定信息
     *
     * @param page       context
     * @param requestID
     * @param phone      用户手机号
     * @param dialog
     * @param showDialog true ：显示dialog
     */
    public static void sendRequest_getAlarmBikeRoute(final Context page,
                                                     final int requestID,
                                                     String phone,
                                                     final Dialog dialog,
                                                     boolean showDialog) {
        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        if (showDialog) {
            DialogUtils.showLoadingDialog(dialog);
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("phone", phone);
//        String content = JSON.toJSONString(map);
//        LogUtil.d(TAG, "content:" + content);
        OkHttpUtils
                .get()
                .tag(page)
                //.url(URL_27)
                .params(map)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_getAlarmBikeRoute", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_getAlarmBikeRoute", requestTime);
//                        LogUtil.d(TAG, "api: sendRequest_getAlarmBikeRoute->: " + response);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }

                });

    }

    /**
     * 支付宝支付订单加密接口
     *
     * @param page      context
     * @param requestID
     * @param trade_no
     * @param subject
     * @param body
     * @param price
     * @param dialog
     */
    public static void sendRequest_AlipayOrderSign(final Context page,
                                                   final int requestID,
                                                   String trade_no,
                                                   String subject,
                                                   String body,
                                                   String price,
                                                   final Dialog dialog) {
        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        DialogUtils.showLoadingDialog(dialog);
        Map<String, String> map = new HashMap<String, String>();
        map.put("out_trade_no", trade_no);
        map.put("subject", subject);
        map.put("body", body);
        map.put("total_fee", price);
        String content = JSON.toJSONString(map);
//        LogUtil.d(TAG, "content:" + content);
        OkHttpUtils
                .postString()
                .tag(page)
                //.url(URL_29)
                .mediaType(MEDIA_TYPE)
                .content(content)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_AlipayOrderSign", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_AlipayOrderSign", requestTime);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }

                });

    }

    /**
     * 租车接口
     *
     * @param page      context
     * @param requestID
     * @param userID
     * @param bikeID
     * @param gps
     * @param dialog
     */
    public static void sendRequest_RentBike(final Context page,
                                            final int requestID,
                                            String userID,
                                            String bikeID,
                                            String gps,
                                            final Dialog dialog) {
        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        DialogUtils.showLoadingDialog(dialog);
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", userID);
        map.put("ble_name", bikeID);
        map.put("gps_point", gps);
        String content = JSON.toJSONString(map);
        LogUtil.d(TAG, "content:" + content);
        OkHttpUtils
                .postString()
                .tag(page)
                //.url(URL_31)
                .mediaType(MEDIA_TYPE)
                .content(content)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_RentBike", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_RentBike", requestTime);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }

                });

    }

    /**
     * 刷新租车信息接口
     *
     * @param page      context
     * @param requestID
     * @param userID
     * @param bikeID
     * @param dialog
     */
    public static void sendRequest_RefreshRentInfo(final Context page,
                                                   final int requestID,
                                                   String userID,
                                                   String bikeID,
                                                   String gps,
                                                   final Dialog dialog) {
        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        DialogUtils.showLoadingDialog(dialog);
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", userID);
        map.put("ble_name", bikeID);
        map.put("gps_point", gps);
        String content = JSON.toJSONString(map);
//        LogUtil.d(TAG, "content:" + content);
        OkHttpUtils
                .postString()
                .tag(page)
                //.url(URL_34)
                .mediaType(MEDIA_TYPE)
                .content(content)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_RefreshRentInfo", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_RefreshRentInfo", requestTime);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }

                });

    }

    /**
     * 刷新租车信息接口
     *
     * @param page         context
     * @param requestID
     * @param userID
     * @param out_trade_no
     * @param gps
     * @param dialog
     */
    public static void sendRequest_RefreshRentInfoMapView(final Context page,
                                                          final int requestID,
                                                          String userID,
                                                          String out_trade_no,
                                                          String gps,
                                                          final Dialog dialog) {
        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        DialogUtils.showLoadingDialog(dialog);
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", userID);
        map.put("out_trade_no", out_trade_no);
        map.put("gps_point", gps);
        String content = JSON.toJSONString(map);
//        LogUtil.d(TAG, "content:" + content);
        OkHttpUtils
                .postString()
                .tag(page)
                //.url(URL_34)
                .mediaType(MEDIA_TYPE)
                .content(content)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_RefreshRentInfoMapView", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_RefreshRentInfoMapView", requestTime);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }

                });

    }

    /**
     * 刷新租车信息接口
     *
     * @param page         context
     * @param requestID
     * @param out_trade_no
     * @param dialog
     */
    public static void sendRequest_GetRentInfo(final Context page,
                                               final int requestID,
                                               String out_trade_no,
                                               final Dialog dialog) {
        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        DialogUtils.showLoadingDialog(dialog);
        Map<String, String> map = new HashMap<String, String>();
        map.put("out_trade_no", out_trade_no);
        String content = JSON.toJSONString(map);
//        LogUtil.d(TAG, "content:" + content);
        OkHttpUtils
                .postString()
                .tag(page)
                //.url(URL_34)
                .mediaType(MEDIA_TYPE)
                .content(content)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_GetRentInfo", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_GetRentInfo", requestTime);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }

                });

    }


    /**
     * 支付结果确认接口
     *
     * @param page      context
     * @param requestID
     * @param userID
     * @param bikeID
     * @param orderID
     * @param dialog
     */
    public static void sendRequest_getPaymentResult(final Context page,
                                                    final int requestID,
                                                    String userID,
                                                    String bikeID,
                                                    String orderID,
                                                    final Dialog dialog) {
        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        DialogUtils.showLoadingDialog(dialog);
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", userID);
        map.put("ble_name", bikeID);
        map.put("out_trade_no", orderID);
        String content = JSON.toJSONString(map);
//        LogUtil.d(TAG, "content:" + content);
        OkHttpUtils
                .postString()
                .tag(page)
                //.url(URL_32)
                .mediaType(MEDIA_TYPE)
                .content(content)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_getPaymentResult", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_getPaymentResult", requestTime);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }

                });

    }

    /**
     * 获取用户的骑行历程信息
     *
     * @param page        context
     * @param requestID
     * @param user_id
     * @param pageIndex
     * @param perPageSize
     * @param dialog
     * @param showDialog  true ：显示dialog
     */
    public static void sendRequest_getMyTrips(final Context page,
                                              final int requestID,
                                              String user_id,
                                              int pageIndex,
                                              int perPageSize,
                                              final Dialog dialog,
                                              boolean showDialog) {
        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        if (showDialog) {
            DialogUtils.showLoadingDialog(dialog);
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user_id);
        map.put("index", String.valueOf(pageIndex));
        map.put("pagesize", String.valueOf(perPageSize));
//        String content = JSON.toJSONString(map);
//        LogUtil.d(TAG, "content:" + content);
        OkHttpUtils
                .get()
                .tag(page)
                //.url(URL_33)
                .params(map)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_getMyTrips", requestTime);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_getMyTrips", requestTime);
//                        LogUtil.d(TAG, "api: sendRequest_getBikeSiteOfSchool->: " + response);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }

                });

    }

    /**
     * 还车
     *
     * @param page      context
     * @param requestID
     * @param userID
     * @param bikeID
     * @param orderID
     * @param dialog
     */
    public static void sendRequest_sendBackBike(final Context page,
                                                final int requestID,
                                                String userID,
                                                String bikeID,
                                                String orderID,
                                                String gps,
                                                final Dialog dialog) {
        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        DialogUtils.showLoadingDialog(dialog);
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", userID);
        map.put("ble_name", bikeID);
        map.put("out_trade_no", orderID);
        map.put("gps_point", gps);
        String content = JSON.toJSONString(map);
        LogUtil.d(TAG, "content:" + content);
        OkHttpUtils
                .postString()
                .tag(page)
                //.url(URL_35)
                .mediaType(MEDIA_TYPE)
                .content(content)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_sendBackBike", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_sendBackBike", requestTime);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }

                });

    }

    /**
     * 评论
     *
     * @param page         context
     * @param requestID
     * @param user_id
     * @param out_trade_no
     * @param remark
     * @param dialog
     */
    public static void sendRequest_submitComment(final Context page,
                                                 final int requestID,
                                                 String user_id,
                                                 String out_trade_no,
                                                 String remark,
                                                 final Dialog dialog) {
        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        DialogUtils.showLoadingDialog(dialog);
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user_id);
        map.put("out_trade_no", out_trade_no);
        map.put("remark", remark);
        String content = JSON.toJSONString(map);
//        LogUtil.d(TAG, "content:" + content);
        OkHttpUtils
                .postString()
                .tag(page)
                //.url(URL_36)
                .mediaType(MEDIA_TYPE)
                .content(content)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_submitComment", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_submitComment", requestTime);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }

                });

    }

    /**
     * 个人资料页面 修改昵称
     *
     * @param page      context
     * @param requestID
     * @param id
     * @param nickname
     * @param dialog
     */
    public static void sendRequest_modifyNickname(final Context page,
                                                  final int requestID,
                                                  String id,
                                                  String nickname,
                                                  final Dialog dialog) {
        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        DialogUtils.showLoadingDialog(dialog);
        Map<String, String> map = new HashMap<String, String>();
        map.put("id", id);
        map.put("nickname", nickname);
        String content = JSON.toJSONString(map);
//        LogUtil.d(TAG, "content:" + content);
        OkHttpUtils
                .postString()
                .tag(page)
                //.url(URL_37)
                .mediaType(MEDIA_TYPE)
                .content(content)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_modifyNickname", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_modifyNickname", requestTime);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }

                });

    }

    /**
     * 获取验证码
     *
     * @param page
     * @param requestID
     * @param dialog
     */
    public static void sendRequest_getSchoolList(final Context page,
                                                 final int requestID,
                                                 final Dialog dialog) {
        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        DialogUtils.showLoadingDialog(dialog);

        OkHttpUtils
                .get()
                .tag(page)
                //.url(URL_39)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_getSchoolList", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_getSchoolList", requestTime);


//                        LogUtil.d(TAG, "api:getValidateCode: " + response);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        //网络返回处理
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }
                });
    }

    /**
     * 获取某笔订单支付信息
     *
     * @param page
     * @param requestID
     * @param out_trade_no
     * @param dialog
     */
    public static void sendRequest_getTripPaymentInfo(final Context page,
                                                      final int requestID,
                                                      String out_trade_no,
                                                      final Dialog dialog) {
        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        DialogUtils.showLoadingDialog(dialog);

        OkHttpUtils
                .get()
                .tag(page)
                //.url(URL_40)
                .addParams("out_trade_no", out_trade_no)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_getTripPaymentInfo", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_getTripPaymentInfo", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        //网络返回处理
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }
                });
    }

    /**
     * 无需支付 结单接口
     *
     * @param page         context
     * @param requestID
     * @param userID
     * @param bikeID
     * @param out_trade_no
     * @param gps
     * @param dialog
     */
    public static void sendRequest_colseOffOrder(final Context page,
                                                 final int requestID,
                                                 String userID,
                                                 String bikeID,
                                                 String out_trade_no,
                                                 String gps,
                                                 final Dialog dialog) {
        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        DialogUtils.showLoadingDialog(dialog);
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", userID);
        map.put("ble_name", bikeID);
        map.put("out_trade_no", out_trade_no);
        map.put("gps_point", gps);
        String content = JSON.toJSONString(map);
//        LogUtil.d(TAG, "content:" + content);
        OkHttpUtils
                .postString()
                .tag(page)
                //.url(URL_42)
                .mediaType(MEDIA_TYPE)
                .content(content)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_colseOffOrder", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_colseOffOrder", requestTime);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }

                });

    }

    /**
     * 获取用户未完成的订单 以及订单的状态 是 未还车 还是未支付
     *
     * @param page      context
     * @param requestID
     * @param userID
     * @param dialog
     */
    public static void sendRequest_getOpeningOrderInfo(final Context page,
                                                       final int requestID,
                                                       String userID,
                                                       final Dialog dialog) {
        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        DialogUtils.showLoadingDialog(dialog);
//        Map<String, String> map = new HashMap<String, String>();
//        map.put("user_id", userID);
//        String content = JSON.toJSONString(map);
//        LogUtil.d(TAG, "content:" + content);
        OkHttpUtils
                .get()
                .tag(page)
                //.url(URL_43)
                .addParams("user_id", userID)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_getOpeningOrderInfo", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_getOpeningOrderInfo", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        //网络返回处理
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }
                });
    }

    public static void sendRequest_getAreaPrice(final Context page, final int requestID, String token, final Dialog dialog) {
        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        DialogUtils.showLoadingDialog(dialog);
        OkHttpUtils
                .get()
                .tag(page)
                .url(URL_20 + "?token=" + token)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_getAreaPrice", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_getAreaPrice", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        //网络返回处理
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }
                });
    }

    public static void sendRequest_saveAreaPrice(final Context page, final int requestID, String token, List<AreaPriceInfo> infos, final Dialog dialog) {
        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        DialogUtils.showLoadingDialog(dialog);
        String content = JSONArray.toJSONString(infos);
        OkHttpUtils
                .postString()
                .tag(page)
                .url(URL_21 + "?token=" + token)
                .content(content)
                .mediaType(MEDIA_TYPE)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_saveAreaPrice", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_saveAreaPrice", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        //网络返回处理
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }
                });
    }

    public static void sendRequest_getArea(final IHttpResponse page, final Context context, final int requestID, String parentId, String token, final Dialog dialog) {

        Map<String, String> map = new HashMap<>();
        map.put("parentId", parentId);
        map.put("token", token);

        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = page;
        DialogUtils.showLoadingDialog(dialog);
        OkHttpUtils
                .get()
                .params(map)
                .tag(page)
                .url(URL_22)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_getArea", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, context.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_getArea", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        //网络返回处理
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }
                });
    }

    public static void sendRequest_editArea(final IHttpResponse page, final Context context, final int requestID, String areaCode, String token, final Dialog dialog) {
        Map<String, String> map = new HashMap<>();
        map.put("areaCode", areaCode);
        map.put("token", token);

        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = page;
        DialogUtils.showLoadingDialog(dialog);
        OkHttpUtils
                .get()
                .params(map)
                .tag(page)
                .url(URL_23)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_editArea", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, context.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_editArea", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        //网络返回处理
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }
                });
    }

    public static void sendRequest_saveOrder(final Context page, final int requestID, String submitType, Map<String, String> order, String token, final Dialog dialog) {
        final long requestTime = System.currentTimeMillis();

        Map<String, Object> map = new HashMap<>();
        map.put("submitType", submitType);
        map.put("order", JSON.parseObject(JSON.toJSONString(order)));
        String content = JSON.toJSONString(map);
        LogUtil.d("aaa", content);

        final IHttpResponse responsePage = (IHttpResponse) page;
        DialogUtils.showLoadingDialog(dialog);
        OkHttpUtils
                .postString()
                .tag(page)
                .url(URL_24 + "?token=" + token)
                .content(content)
                .mediaType(MEDIA_TYPE)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_saveOrder", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_saveOrder", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        //网络返回处理
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }
                });
    }

    public static void sendRequest_getOrderInfo(final Context page, final int requestID, String token, final Dialog dialog) {

        Map<String, String> map = new HashMap<>();
        map.put("token", token);

        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        DialogUtils.showLoadingDialog(dialog);
        OkHttpUtils
                .get()
                .tag(page)
                .params(map)
                .url(URL_25)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_getOrderInfo", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_getOrderInfo", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        //网络返回处理
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }
                });
    }

    public static void sendRequest_payComplete(final Context page, final int requestID, String token, String info, final Dialog dialog) {
        Map<String, String> map = new HashMap<>();
        map.put("token", token);
        map.put("content", info);

        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        DialogUtils.showLoadingDialog(dialog);
        OkHttpUtils
                .get()
                .tag(page)
                .params(map)
                .url(URL_26)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_payComplete", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_payComplete", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        //网络返回处理
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }
                });
    }

    public static void sendRequest_getOrderList(final Context page, final int requestID, String token, String orderStatus, String keyword, String pageNo, final Dialog dialog) {
        Map<String, String> map = new HashMap<>();
        map.put("token", token);
        map.put("keyword", keyword);
        map.put("pageNo", pageNo);
        map.put("orderStatus", orderStatus);
        LogUtil.i("aaa", map.toString());

        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        DialogUtils.showLoadingDialog(dialog);
        OkHttpUtils
                .get()
                .tag(page)
                .params(map)
                .url(URL_27)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_getOrderList", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_getOrderList", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        //网络返回处理
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }
                });
    }

    public static void sendRequest_getExpressCompany(final Context page, final int requestID, String token, final Dialog dialog) {
        Map<String, String> map = new HashMap<>();
        map.put("token", token);

        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        DialogUtils.showLoadingDialog(dialog);
        OkHttpUtils
                .get()
                .tag(page)
                .params(map)
                .url(URL_28)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_getExpressCompany", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_getExpressCompany", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        //网络返回处理
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }
                });
    }

    public static void sendRequest_getOrder(final Context page, final int requestID, String id, String token, final Dialog dialog) {
        Map<String, String> map = new HashMap<>();
        map.put("id", id);
        map.put("token", token);

        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        DialogUtils.showLoadingDialog(dialog);
        OkHttpUtils
                .get()
                .tag(page)
                .params(map)
                .url(URL_29)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_getOrder", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_getOrder", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        //网络返回处理
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }
                });
    }

    public static void sendRequest_getReceivingOrder(final Context page, final int requestID, String token, String orderStatus, String keyword, String pageNo, final Dialog dialog) {
        Map<String, String> map = new HashMap<>();
        map.put("token", token);
        map.put("keyword", keyword);
        map.put("pageNo", pageNo);
        map.put("orderStatus", orderStatus);

        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        DialogUtils.showLoadingDialog(dialog);
        OkHttpUtils
                .get()
                .tag(page)
                .params(map)
                .url(URL_30)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_getReceivingOrder", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_getReceivingOrder", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        //网络返回处理
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }
                });
    }

    public static void sendRequest_pushOpen(final Context page, final int requestID, String token, final Dialog dialog) {
        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        DialogUtils.showLoadingDialog(dialog);
        OkHttpUtils
                .postString()
                .tag(page)
                .url(URL_31 + "?token=" + token)
                .content("")
                .mediaType(MEDIA_TYPE)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_pushOpen", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_pushOpen", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        //网络返回处理
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }
                });
    }

    public static void sendRequest_pushClose(final Context page, final int requestID, String token, final Dialog dialog) {
        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        DialogUtils.showLoadingDialog(dialog);
        OkHttpUtils
                .postString()
                .tag(page)
                .url(URL_32 + "?token=" + token)
                .content("")
                .mediaType(MEDIA_TYPE)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_pushClose", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_pushClose", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        //网络返回处理
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }
                });
    }

    public static void sendRequest_getExpressCompanyList(final Context page, final int requestID, String token, final Dialog dialog) {
        Map<String, String> map = new HashMap<>();
        map.put("token", token);

        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        DialogUtils.showLoadingDialog(dialog);
        OkHttpUtils
                .get()
                .tag(page)
                .params(map)
                .url(URL_33)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_getExpressCompany", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_getExpressCompany", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        //网络返回处理
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }
                });
    }

    public static void sendRequest_saveExpressCompany(final Context page, final int requestID, String companyId, String areaCode, String areaName, String token, final Dialog dialog) {
        final long requestTime = System.currentTimeMillis();

        Map<String, String> map = new HashMap<>();
        map.put("companyId", companyId);
        map.put("areaCode", areaCode);
        map.put("areaName", areaName);
        String content = JSON.toJSONString(map);
        LogUtil.i("aaa", content);

        final IHttpResponse responsePage = (IHttpResponse) page;
        DialogUtils.showLoadingDialog(dialog);
        OkHttpUtils
                .postString()
                .tag(page)
                .url(URL_34 + "?token=" + token)
                .content(content)
                .mediaType(MEDIA_TYPE)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_saveExpressCompany", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_saveExpressCompany", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        //网络返回处理
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }
                });
    }

    public static void sendRequest_getUserMoney(final Context page, final int requestID, String token, final Dialog dialog) {
        Map<String, String> map = new HashMap<>();
        map.put("token", token);

        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        DialogUtils.showLoadingDialog(dialog);
        OkHttpUtils
                .get()
                .tag(page)
                .params(map)
                .url(URL_35)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_getUserMoney", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_getUserMoney", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        //网络返回处理
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }
                });
    }

    public static void sendRequest_saveQuotation(final Context page, final int requestID, String insuranceMoney, String expressMoney, String orderId, String remarks, String token, final Dialog dialog) {
        final long requestTime = System.currentTimeMillis();

        Map<String, String> map = new HashMap<>();
        map.put("insuranceMoney", insuranceMoney);
        map.put("expressMoney", expressMoney);
        map.put("orderId", orderId);
        map.put("remarks", remarks);
        String content = JSON.toJSONString(map);

        final IHttpResponse responsePage = (IHttpResponse) page;
        DialogUtils.showLoadingDialog(dialog);
        OkHttpUtils
                .postString()
                .tag(page)
                .url(URL_36 + "?token=" + token)
                .content(content)
                .mediaType(MEDIA_TYPE)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_saveExpressCompany", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_saveExpressCompany", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        //网络返回处理
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }
                });
    }

    public static void sendRequest_getQuotationList(final Context page, final int requestID, String token, String id, final Dialog dialog) {
        Map<String, String> map = new HashMap<>();
        map.put("token", token);
        map.put("id", id);

        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        DialogUtils.showLoadingDialog(dialog);
        OkHttpUtils
                .get()
                .tag(page)
                .params(map)
                .url(URL_37)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_getQuotationList", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_getQuotationList", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        //网络返回处理
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }
                });
    }

    public static void sendRequest_cancelOrder(final Context page, final int requestID, String id, String token, final Dialog dialog) {
        final long requestTime = System.currentTimeMillis();

        Map<String, String> map = new HashMap<>();
//        map.put("id", id);
        String content = JSON.toJSONString(map);
        LogUtil.i("aaa", content);

        final IHttpResponse responsePage = (IHttpResponse) page;
        DialogUtils.showLoadingDialog(dialog);
        OkHttpUtils
                .postString()
                .tag(page)
                .url(URL_38 + "?token=" + token + "&id=" + id)
                .content(content)
                .mediaType(MEDIA_TYPE)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_cancelOrder", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_cancelOrder", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        //网络返回处理
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }
                });
    }

    public static void sendRequest_getRechargeList(final Context page, final int requestID, String token, final Dialog dialog) {
        Map<String, String> map = new HashMap<>();
        map.put("token", token);

        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        DialogUtils.showLoadingDialog(dialog);
        OkHttpUtils
                .get()
                .tag(page)
                .params(map)
                .url(URL_39)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_getRechargeList", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_getRechargeList", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        //网络返回处理
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }
                });
    }

    public static void sendRequest_getRechargeInfo(final Context page, final int requestID, String payType, String rechargeId, String token, final Dialog dialog) {
        final long requestTime = System.currentTimeMillis();

        Map<String, String> map = new HashMap<>();
        map.put("payType", payType);
        map.put("rechargeId", rechargeId);
        String content = JSON.toJSONString(map);

        final IHttpResponse responsePage = (IHttpResponse) page;
        DialogUtils.showLoadingDialog(dialog);
        OkHttpUtils
                .postString()
                .tag(page)
                .url(URL_40 + "?token=" + token)
                .content(content)
                .mediaType(MEDIA_TYPE)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_getRechargeInfo", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_getRechargeInfo", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        //网络返回处理
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }
                });
    }

    public static void sendRequest_getRechargeRecordList(final Context page, final int requestID, String pageNo, String token, final Dialog dialog) {
        Map<String, String> map = new HashMap<>();
        map.put("pageNo", pageNo);
        map.put("token", token);

        final long requestTime = System.currentTimeMillis();

        final IHttpResponse responsePage = (IHttpResponse) page;
        DialogUtils.showLoadingDialog(dialog);
        OkHttpUtils
                .get()
                .tag(page)
                .params(map)
                .url(URL_41)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_getRechargeRecordList", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_getRechargeRecordList", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        //网络返回处理
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }
                });
    }

    public static void sendRequest_quotationPay(final Context page, final int requestID, String orderId, String id, String orderMoney, String expressMoney, String payType, String insuranceMoney, String token, final Dialog dialog) {
        final long requestTime = System.currentTimeMillis();

        Map<String, String> map = new HashMap<>();
        map.put("payType", payType);
        map.put("orderId", orderId);
        map.put("id", id);
        map.put("orderMoney", orderMoney);
        map.put("expressMoney", expressMoney);
        map.put("insuranceMoney", insuranceMoney);
        String content = JSON.toJSONString(map);
        LogUtil.i("aaa", content);

        final IHttpResponse responsePage = (IHttpResponse) page;
        DialogUtils.showLoadingDialog(dialog);
        OkHttpUtils
                .postString()
                .tag(page)
                .url(URL_42 + "?token=" + token)
                .content(content)
                .mediaType(MEDIA_TYPE)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_getRechargeInfo", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_getRechargeInfo", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        //网络返回处理
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }
                });
    }

    public static void sendRequest_reimburse(final Context page, final int requestID, String id, String token, final Dialog dialog) {
        final long requestTime = System.currentTimeMillis();
        Map<String, String> map = new HashMap<>();
        map.put("id", id);
        String content = JSON.toJSONString(map);
        final IHttpResponse responsePage = (IHttpResponse) page;
        DialogUtils.showLoadingDialog(dialog);
        OkHttpUtils
                .postString()
                .tag(page)
                .url(URL_43 + "?token=" + token + "&id=" + id)
                .content(content)
                .mediaType(MEDIA_TYPE)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        printAPI_TimeConsuming("sendRequest_reimburse", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (responsePage != null) {
                            responsePage.doHttpResponse(null, requestID, page.getString(R.string.str_network_error));
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        printAPI_TimeConsuming("sendRequest_reimburse", requestTime);

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        //网络返回处理
                        if (responsePage != null) {
                            responsePage.doHttpResponse(response, requestID);
                        }
                    }
                });
    }
}
