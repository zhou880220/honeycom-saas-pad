package com.honeycom.saas.pad.util;

//常量类
public class Constant {

    public static String profile = "prod";//dev prod test "http://172.16.41.239:9542";//

    public static final String PAGE_URL = "https://fmtest-app.zhizaoyun.com:30443";//"http://mestestwebk8s.zhizaoyun.com:31811";//String.format("https://%s.zhizaoyun.com/", getCurrentDomain()[0]);
    public static final String INTERFACE_URL =  "https://mestestapik8s.zhizaoyun.com:30443";//"http://mestestapik8s.zhizaoyun.com:31008";
    public static final String equipmentId = "2";
    public static final String platform_type = "saas";
    public static final String equipment_type = "pad";


    // 测试及调试环境桶名
//    public static final String bucket_Name = "njdeveloptest";
    // 生产环境桶名
    public static final String bucket_Name = "honeycom-service";

    ///接口调用
    // APP_ID 替换为你的应用从官方网站申请到的合法appId
    public static final String APP_ID = "wx5b3f59728cb6aa71"; //微信支付ID
    // QQ
    public static final String QQ_APP_ID = "1110555495";
    //以下为页面前缀
    public static final String locahost_url = PAGE_URL + "cashierDesk"; //路径前缀  "http://172.16.23.116:3001/"
    public static final String text_url = PAGE_URL + "/home"; //用户首页
    public static final String login_url = PAGE_URL + "/login"; //登录页
    public static final String apply_url = PAGE_URL + "apply"; //用户中心
    public static final String register_url = PAGE_URL + "register"; //用户注册
    public static final String NOTICE_LIST = PAGE_URL + "/notice"; //消息页
    public static final String MyOrderList = PAGE_URL + "myOrder";//订单列表
    public static final String MyNews = PAGE_URL + "news"; //咨询页面
    public static final String test_shoppingCart = PAGE_URL + "shoppingCart"; //支付页面订单列表
    //以下为接口前缀      TEST_INTERFACE_URL = "https://njtesthoneycomb.zhizaoyun.com/gateway/";
    public static final String Apply_Details = INTERFACE_URL + "api-apps/client/recentlyApps?equipmentId=2&userId="; //获取悬浮窗应用
    public static final String Apply_Details_POP = INTERFACE_URL + "api-apps/client/recentlyApps"; //获取悬浮窗应用
    public static final String upload_multifile = INTERFACE_URL + "api-f/upload/multifile"; //上传图片
    public static final String WEBVERSION = INTERFACE_URL+"/api-p/tClientVersion/newVersion?equipmentType=2&updateVersion=";//apk升级功能
    public static final String APP_AUTH_CHECK = INTERFACE_URL+"api-apps/apps-anon/client/platformPermissionAndPutaway";//http://172.16.14.231:18089/
    public static final String userPushRelation = INTERFACE_URL+"/api-n/userDevice/register";//保存用户推送关系
    public static final String userUnbindRelation = INTERFACE_URL+"/api-n/userDevice/logout";//保存用户推送关系


    public static final String NO_AUTH_TIP = "您的企业暂未开通此应用，请联系企业管理页开通后再试。";

    public static final String ERROR_SERVER_TIP = "平台服务器出现未知异常。";

    public static final String HAS_UDATE = "has_update";
    public static final  String CODED_CONTENT = "codedContent";


    public static String[] getCurrentDomain() {
        String page_head = "";
        String interface_head = "";
        switch (profile) {
            case "test" :
                page_head = "njtestyyzxpad";
                interface_head = "njtesthoneycomb";
                break;
            case "prod" :
                page_head = "padclient";
                interface_head = "ulogin";
                break;
            case "dev":
//                page_head = "njtestyyzx";
                interface_head = "mobileclientthird";
        }
        String[] str = {page_head, interface_head};
        return  str;
    }

    //扫描条码服务广播
    //Scanning barcode service broadcast.
    public static final String SCN_CUST_ACTION_SCODE = "com.scanner.broadcast";
    //条码扫描数据广播
    //Barcode scanning data broadcast.
    public static final String SCN_CUST_EX_SCODE = "scannerdata";
    //扫描条码结果
    public static final String SCN_CUST_EX_RESULT = "scanner_data";
    //移动扫描条码服务广播
    public static final String SCAN_ACTION = "scan.rcv.message";

    public static final int PDA_TYPE = 1; // 0:肖邦； 1：移动PDA


}
