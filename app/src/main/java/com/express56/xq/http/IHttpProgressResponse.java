package com.express56.xq.http;

/**
 * 接口：处理网络返回 下载、上传的进度更新
 *
 * @author Kevin Wong
 *
 */
public interface IHttpProgressResponse extends IHttpResponse {

	/**
	 * 处理返回的网络消息
	 *
	 * @param progress
	 */
	public void doHttpProgressResponse(float progress);

}
