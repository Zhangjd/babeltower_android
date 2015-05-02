package com.bbt.babeltower.base;

public class ApiUrl {

	// API�ĵ� �� http://docs.babeltower.apiary.io/
	public static String BABIETA_BASE_URL = "http://218.192.166.167:3030/";

	// �����б�
	// /v1/contents{?since_id,max_id,limit,on_focus,on_timeline,content_type}
	// ���ڻ�ȡ�����б�
	// ������Ϣ�������²���:
	// -status(0:��ȡ�ɹ�, 1:�������Ϸ�)
	// -message(״̬����)
	// -list(�����б�)
	public static String BABIETA_CONTENT_LIST = "/v1/contents";

	// ��������
	// /v1/contents/{id}
	// ��ȡ��������
	// ������Ϣʧ��ʱ�������²���:
	// -status(1:���ݲ�����)
	public static String BABIETA_ARTICLE = "/v1/contents/";

	// ר���������б�
	// /v1/contents/{id}/subcontents
	// ��ȡĳר���µ������б�(������)
	// ������Ϣ�������²���:
	// -status(0:�ɹ�,1:���ݲ�����)
	// -message(״̬����)
	// -list(�����б�)
	public static String BABIETA_SUBCONTENTS = "/v1/contents/{id}/subcontents";

	// ��ȡ����sections����Ϣ�б�
	// ������Ϣʧ��ʱ�������²���:
	// -status(1:���ݲ�����)
	public static String BABIETA_SECTION_LIST = "/v1/sections";
	
	// ��ȡĳ��section�µ������б�(��Ϊsection�µ����²���,��˲���Ҫ��GET����ɸѡ,���Ǹýӿ�Ҳ֧��,��contents�÷���ͬ,���ﲻ�о�)
	// ������Ϣ�������²���:
	// -status(0:�ɹ�,1:id���Ϸ�,2:section������)
	// -message(״̬����)
	// -list(�����б�)
	public static String BABIETA_SECTION_CONTENTS = "/v1/sections/{id}/contents";

	// �汾���
	public static String BABIETA_VERSION_CHECK = "http://babel.100steps.net/android/version_check.php";
	// ����
	public static String BABIETA_DOWNLOAD = "http://babel.100steps.net/android/down.php";

}
