package com.express56.xq.http;

/**
 * 接口：处理网络返回 处理的对象是JSONObject
 *
 * @author Kevin Wong
 *
 */
public interface IHttpResponse {

	/**
	 * 处理返回的网络消息
	 *
	 * @param param
	 *            网络返回的对象
	 *            发送请求对应的ID
	 */
	public void doHttpResponse(Object... param);

	/**
	 * 处理取消网络连接
	 *
	 * @param param
	 *            发送请求对应的ID
	 */
	public void doHttpCanceled(Object... param);
}
