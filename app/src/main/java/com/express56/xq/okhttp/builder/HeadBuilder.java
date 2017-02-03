package com.express56.xq.okhttp.builder;

import com.express56.xq.okhttp.OkHttpUtils;
import com.express56.xq.okhttp.request.OtherRequest;
import com.express56.xq.okhttp.request.RequestCall;

/**
 * Created by zhy on 16/3/2.
 */
public class HeadBuilder extends GetBuilder
{
    @Override
    public RequestCall build()
    {
        return new OtherRequest(null, null, OkHttpUtils.METHOD.HEAD, url, tag, params, headers).build();
    }
}
