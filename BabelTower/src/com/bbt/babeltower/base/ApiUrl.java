package com.bbt.babeltower.base;

public class ApiUrl {

	// API文档 ： http://docs.babeltower.apiary.io/
	public static String BABIETA_BASE_URL = "http://218.192.166.167:3030/";

	// 新闻列表
	// /v1/contents{?since_id,max_id,limit,on_focus,on_timeline,content_type}
	// 用于获取新闻列表
	// 返回信息包含以下参数:
	// -status(0:获取成功, 1:参数不合法)
	// -message(状态描述)
	// -list(新闻列表)
	public static String BABIETA_CONTENT_LIST = "/v1/contents";

	// 完整文章
	// /v1/contents/{id}
	// 获取完整文章
	// 返回信息失败时包含以下参数:
	// -status(1:内容不存在)
	public static String BABIETA_ARTICLE = "/v1/contents/";

	// 专题下文章列表
	// /v1/contents/{id}/subcontents
	// 获取某专题下的文章列表(子内容)
	// 返回信息包含以下参数:
	// -status(0:成功,1:内容不存在)
	// -message(状态描述)
	// -list(新闻列表)
	public static String BABIETA_SUBCONTENTS = "/v1/contents/{id}/subcontents";

	// 获取所有sections的信息列表
	// 返回信息失败时包含以下参数:
	// -status(1:内容不存在)
	public static String BABIETA_SECTION_LIST = "/v1/sections";
	
	// 获取某个section下的文章列表(因为section下的文章不多,因此不需要用GET参数筛选,但是该接口也支持,跟contents用法相同,这里不列举)
	// 返回信息包含以下参数:
	// -status(0:成功,1:id不合法,2:section不存在)
	// -message(状态描述)
	// -list(新闻列表)
	public static String BABIETA_SECTION_CONTENTS = "/v1/sections/{id}/contents";

	// 版本检查
	public static String BABIETA_VERSION_CHECK = "http://babel.100steps.net/android/version_check.php";
	// 下载
	public static String BABIETA_DOWNLOAD = "http://babel.100steps.net/android/down.php";

}
