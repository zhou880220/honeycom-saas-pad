package com.honeycom.saas.pad.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.google.gson.Gson;
import com.honeycom.saas.pad.App;
import com.honeycom.saas.pad.BuildConfig;
import com.honeycom.saas.pad.R;
import com.honeycom.saas.pad.base.BaseActivity;
import com.honeycom.saas.pad.http.CallBackUtil;
import com.honeycom.saas.pad.http.OkhttpUtil;
import com.honeycom.saas.pad.http.UpdateAppHttpUtil;
import com.honeycom.saas.pad.http.bean.BrowserBean;
import com.honeycom.saas.pad.http.bean.UserInfoBean;
import com.honeycom.saas.pad.http.bean.VersionInfo;
import com.honeycom.saas.pad.util.CleanDataUtils;
import com.honeycom.saas.pad.util.Constant;
import com.honeycom.saas.pad.util.NewToastUtil;
import com.honeycom.saas.pad.util.SPUtils;
import com.honeycom.saas.pad.util.StatusBarCompat;
import com.honeycom.saas.pad.util.VersionUtils;
import com.honeycom.saas.pad.web.MWebChromeClient;
import com.honeycom.saas.pad.web.MyHandlerCallBack;
import com.honeycom.saas.pad.web.MyWebViewClient;
import com.honeycom.saas.pad.web.WebViewSetting;
import com.vector.update_app.UpdateAppManager;
import com.vector.update_app.listener.ExceptionHandler;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import okhttp3.Call;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
* author : zhoujr
* date : 2021/9/18 15:54
* desc : ????????????
*/
public class MainActivity  extends BaseActivity {
    private static final String TAG = "MainActivity_TAG";
    private static final int VIDEO_PERMISSIONS_CODE = 1;
    //????????????
    private static final int REQUEST_CAPTURE = 100;
    //????????????
    private static final int REQUEST_PICK = 101;
    //????????? ?????????
    private static final int REQUEST_CODE_SCAN = 1;
    //????????????
    private static final int NOT_NOTICE = 2;//???????????????????????????
    private static final int ADDRESS_PERMISSIONS_CODE = 200;
    private static final String[] APPLY_PERMISSIONS_APPLICATION = { //??????????????????
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    /**********************view******************************/
    @BindView(R.id.NewWebProgressbar)
    ProgressBar mNewWebProgressbar;
    @BindView(R.id.new_Web)
    BridgeWebView mNewWeb;
    @BindView(R.id.web_error)
    View mWebError;
    @BindView(R.id.closeLoginPage)
    ImageView mCloseLoginPage;
    @BindView(R.id.text_policy_reminder)
    TextView mTextPolicyReminder;
    @BindView(R.id.text_policy_reminder_back)
    RelativeLayout mTextPolicyReminderBack;

    /*************************object***************************/
    private Context mContext;
    private String myOrder;
    private String mVersionName = "";
    private String zxIdTouTiao;
    private boolean ChaceSize = true;
    private String totalCacheSize = "";
    private String clearSize = "";
    //?????????????????????????????????
    private File tempFile;
    private String userToken;

    private MyHandlerCallBack.OnSendDataListener mOnSendDataListener;
    private MWebChromeClient myChromeWebClient;

    private Handler myHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                default:
                    break;
            }
            return false;
        }
    });

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        mContext = this;

        //?????????????????????
        StatusBarCompat.compat(this, ContextCompat.getColor(this, R.color.status_text));
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            //????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        //????????????
        webView(Constant.text_url+"?r="+new Date().getTime());

        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateApp();
            }
        }, 5000);
    }

    //????????????
    public void updateApp() {
        int sysVersion = VersionUtils.getVersion(this);
        Log.e(TAG, "updateApp: "+sysVersion);
        new UpdateAppManager
                .Builder()
                //??????Activity
                .setActivity(this)
                //????????????
                .setUpdateUrl(Constant.WEBVERSION + sysVersion)
                .handleException(new ExceptionHandler() {
                    @Override
                    public void onException(Exception e) {
                        Log.e(TAG, "updateApp Exception: "+e.getMessage());
                        e.printStackTrace();
                    }
                })
                //??????httpManager???????????????
                .setHttpManager(new UpdateAppHttpUtil())
                .setTopPic(R.mipmap.top_3)
                //????????????????????????????????????
                .setThemeColor(0xff47bbf1)
                .build()
                //????????????????????????????????????
                .update();
    }

    /**
     * ?????????webview js??????
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void webView(String url) {
        String hasUpdate = (String) SPUtils.getInstance().get(Constant.HAS_UDATE, "0");
        if (hasUpdate.equals("1")) {
            Log.e(TAG,"----> h5 page has update");
            mNewWeb.clearCache(true);
        }

        if (Build.VERSION.SDK_INT >= 19) {
            mNewWeb.getSettings().setLoadsImagesAutomatically(true);
        } else {
            mNewWeb.getSettings().setLoadsImagesAutomatically(false);
        }
        WebSettings webSettings = mNewWeb.getSettings();
        String userAgentString = webSettings.getUserAgentString();
        webSettings.setUserAgentString(userAgentString + "; ");
        if (webSettings != null) {
            WebViewSetting.initweb(webSettings);
        }

        //Handler????????????????????????????????????????????????H5???????????????Native?????????????????????h5??????send()??????????????????????????????MyHandlerCallBack
        mNewWeb.setDefaultHandler(new MyHandlerCallBack(mOnSendDataListener));
        myChromeWebClient = new MWebChromeClient(this, mNewWebProgressbar, mWebError);
        MyWebViewClient myWebViewClient = new MyWebViewClient(mNewWeb, mWebError);
        myWebViewClient.setOnCityClickListener(new MyWebViewClient.OnCityChangeListener() {
            @Override
            public void onCityClick(String name) {  //??????????????????????????????
                myOrder = name;
                Log.e(TAG, "onCityClick: "+name);
                try {
//                        mApplyBackImage1.setVisibility(View.VISIBLE);
                    if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO)
                            != PackageManager.PERMISSION_GRANTED) {
                        //??????READ_EXTERNAL_STORAGE??????
                        ActivityCompat.requestPermissions(MainActivity.this, APPLY_PERMISSIONS_APPLICATION,
                                ADDRESS_PERMISSIONS_CODE);
                    }
                } catch (Exception e) {
                    if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO)
                            != PackageManager.PERMISSION_GRANTED) {
                        //??????READ_EXTERNAL_STORAGE??????
                        ActivityCompat.requestPermissions(MainActivity.this, APPLY_PERMISSIONS_APPLICATION,
                                ADDRESS_PERMISSIONS_CODE);
                    }
//                    mApplyBackImage1.setVisibility(View.VISIBLE);
                }

//                if (name != null) {
//                    if (name.equals(Constant.login_url)) {
//                        mTextPolicyReminder.setVisibility(View.VISIBLE);
//                        mCloseLoginPage.setVisibility(View.VISIBLE);
//                        mTextPolicyReminderBack.setVisibility(View.VISIBLE);
//                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
//                    } else if (name.equals(Constant.register_url)) {
//                        mTextPolicyReminder.setVisibility(View.VISIBLE);
//                        mCloseLoginPage.setVisibility(View.VISIBLE);
//                        mTextPolicyReminderBack.setVisibility(View.GONE);
//                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
//                    } else if (name.contains("bindPhone")) {
//                        Log.e(TAG, "onCityClick: bind");
//                        mTextPolicyReminder.setVisibility(View.VISIBLE);
//                        mCloseLoginPage.setVisibility(View.VISIBLE);
//                        mTextPolicyReminderBack.setVisibility(View.GONE);
//                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
//                    }  else if (name.contains("/about")) {
//                        mTextPolicyReminder.setVisibility(View.GONE);
//                        mCloseLoginPage.setVisibility(View.GONE);
//                        mTextPolicyReminderBack.setVisibility(View.GONE);
//                    } else {
//                        mTextPolicyReminder.setVisibility(View.GONE);
//                        mCloseLoginPage.setVisibility(View.GONE);
//                        mTextPolicyReminderBack.setVisibility(View.GONE);
//                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);  //SOFT_INPUT_ADJUST_RESIZE
//                    }
//                }
            }
        });
        mNewWeb.setWebViewClient(myWebViewClient);
        mNewWeb.setWebChromeClient(myChromeWebClient);
        mNewWeb.loadUrl(url);

        //????????????
        mNewWeb.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 && event.getAction() == KeyEvent.ACTION_DOWN) {
                    Log.e(TAG, "onKey: web back  1");
                    if (mNewWeb != null && mNewWeb.canGoBack()) {
                        Log.e(TAG, "onKey: web back  2"+myOrder);
//                        SharedPreferences sb = getSharedPreferences("userInfoSafe", MODE_PRIVATE);
//                        String userInfo = sb.getString("userInfo", "");
                        if (myOrder.contains("/home")) { //???????????????????????????  ??????????????????
                            exit();
                        } else if (myOrder.contains("/information")) { //????????????????????????????????????
                            webView(Constant.text_url);
                        } else {
                            mNewWeb.goBack();
                        }
                        return true;
                    }
                }
                return false;
            }
        });

        //?????????????????????????????????????????? ????????????
        mCloseLoginPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (mNewWeb.canGoBack()) {
                        webView(Constant.text_url);
                        mCloseLoginPage.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //??????????????????????????????Handler?????????  ???????????????
        mNewWeb.registerHandler("getVersionName", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    if (!mVersionName.isEmpty()) {
//                        function.onCallBack("V" + mVersionName);
                        int sysVersion = VersionUtils.getVersion(App.getContext());
                        VersionInfo versionInfo = new VersionInfo(mVersionName, sysVersion);
                        function.onCallBack(new Gson().toJson(versionInfo));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        /**
         * ?????????????????????h5?????????
         */
        mNewWeb.registerHandler("isInSurface", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    Log.e(TAG, "isInSurface: " + data);
                    function.onCallBack("true");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        /**
         * ??????????????????
         */
        mNewWeb.registerHandler("setCookie", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    if (!data.isEmpty()) {
                        zxIdTouTiao = data;
                        Log.e(TAG, "setCookie: " + data);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //???????????? ????????????????????? ????????????
        mNewWeb.registerHandler("getCache", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    if (ChaceSize == true) {
                        if (!totalCacheSize.isEmpty()) {
                            function.onCallBack(totalCacheSize);
                        }
                    } else {
                        function.onCallBack("0.00MB");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //??????????????????????????????
        mNewWeb.registerHandler("ClearCache", new BridgeHandler() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    CleanDataUtils.clearAllCache(Objects.requireNonNull(MainActivity.this));
                    clearSize = CleanDataUtils.getTotalCacheSize(Objects.requireNonNull(MainActivity.this));
                    if (!clearSize.isEmpty()) {
                        ChaceSize = false;
                        function.onCallBack(clearSize);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //???????????????????????????????????????
        mNewWeb.registerHandler("setUserInfo", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    Log.e(TAG, "????????????????????????: " + data);
                    if (!data.isEmpty()) {
                        SPUtils.getInstance().put("userInfo", data);
                        //????????????deviceToken
                        UserInfoBean userInfoBean = new Gson().fromJson(data, UserInfoBean.class);
                        if (userInfoBean !=null && !TextUtils.isEmpty(userInfoBean.getCompanyId())) {
                            userToken = userInfoBean.getAccessToken();
                            String deviceToken = (String) SPUtils.getInstance().get("deviceToken","");
                            sendDeviceToken(deviceToken);
                        }
                        function.onCallBack("success");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //??????????????????(??????????????????)
        mNewWeb.registerHandler("saveLoginInfo", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                Log.e(TAG, "handler = saveLoginInfo, data from web = " + data);
                if (!TextUtils.isEmpty(data)) {
                    SPUtils.getInstance().put("loginData", data);
                    function.onCallBack("success");
                }
            }
        });


//        ??????????????????
        mNewWeb.registerHandler("getLoginInfo", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                String _data = (String)SPUtils.getInstance().get("loginData", "");
                Log.e(TAG, "_loginData : "+_data);
                function.onCallBack(_data);
            }
        });

//        ??????????????????
        mNewWeb.registerHandler("clearLoginInfo", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                Log.e(TAG, "handler = clearLoginInfo" + data);
                SPUtils.getInstance().remove("loginData");
                SPUtils.getInstance().remove("userInfo");
                String deviceToken = (String) SPUtils.getInstance().get("deviceToken","");
//                unBindDeviceToken(deviceToken);
                Map<String, String> headerMap =  new HashMap<>();
                headerMap.put("authorization", "Bearer "+userToken);
                Map<String, String> paramsMap =  new HashMap<>();
                paramsMap.put("deviceToken", deviceToken);
                paramsMap.put("deviceType", Constant.equipment_type);
                paramsMap.put("platformType", Constant.platform_type);
                String jsonStr = new Gson().toJson(paramsMap);
                Log.e(TAG, "request params: "+jsonStr);
                Log.e(TAG, "request header: "+headerMap);
                Log.e(TAG, "request api: "+Constant.userUnbindRelation);
                OkhttpUtil.okHttpPostJson(Constant.userUnbindRelation, jsonStr, headerMap, new CallBackUtil.CallBackString() {
                    @Override
                    public void onFailure(Call call, Exception e) {
                        Log.e(TAG, "onFailure: "+e.getMessage());
                        function.onCallBack("fail");
                    }

                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG, "-----onResponse: " + response);
                        function.onCallBack("success");
                    }
                });
            }
        });

        //?????????????????????
        mNewWeb.registerHandler("getTakeCamera", new BridgeHandler() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    //????????????
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        //??????READ_EXTERNAL_STORAGE??????
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                VIDEO_PERMISSIONS_CODE);
                    } else {
                        if (!data.isEmpty()) {
                            String replace1 = data.replace("\"", "");
                            String replace2 = replace1.replace("token:", "");
                            String replace3 = replace2.replace("{", "");
                            String replace4 = replace3.replace("}", "");
                            String[] s = replace4.split(" ");
//                            token1 = s[0];
//                            userid = s[1];
                            gotoCamera();
                        } else {

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //???????????????????????????
        mNewWeb.registerHandler("getPhotoAlbum", new BridgeHandler() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    //????????????
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        //??????READ_EXTERNAL_STORAGE??????
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                VIDEO_PERMISSIONS_CODE);
                    } else {
                        if (!data.isEmpty()) {
                            String replace1 = data.replace("\"", "");
                            String replace2 = replace1.replace("token:", "");
                            String replace3 = replace2.replace("{", "");
                            String replace4 = replace3.replace("}", "");
                            String[] s = replace4.split(" ");
//                            token1 = s[0];
//                            userid = s[1];
                            gotoPhoto();
                        } else {

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        //???????????????????????????????????????
        mNewWeb.registerHandler("getUserInfo", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    String userInfo = (String) SPUtils.getInstance().get("userInfo", "");
                    Log.e(TAG, userInfo);
                    if (!userInfo.isEmpty()) {
                        function.onCallBack(userInfo);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //???????????????????????????????????????
        mNewWeb.registerHandler("showApplyParams", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    Log.e(TAG, "???????????????:1 " + data);
                    if (!data.isEmpty()) {
                        Map map = new Gson().fromJson(data, Map.class);
//                        String _redirectUrl = (String) map.get("redirectUrl");//"https://mobileclientthird.zhizaoyun.com/jsapi/view/api.html";//
//                        Map<String,String> reqMap = BaseUtils.urlSplit(_redirectUrl);
                        String redirectUrl = (String) map.get("redirectUrl");//"http://172.16.41.239:3001/equipment/app/home?access_token="+reqMap.get("access_token");//
                        String currentUrl = mNewWeb.getUrl();
                        Log.e(TAG, "currentUrl: "+currentUrl );
                        if (!redirectUrl.isEmpty()) {
                            Intent intent;
                            //??????app????????????????????????
                            if (redirectUrl.contains("/p/")) {
                                intent = new Intent(MainActivity.this, WeighActivity.class);
                                if (redirectUrl.contains("?")) {
                                    redirectUrl = redirectUrl +"&r="+new Date().getTime();
                                }else {
                                    redirectUrl = redirectUrl +"?r="+new Date().getTime();
                                }
                                intent.putExtra("url", redirectUrl);
                                startActivity(intent);
                            }else if (redirectUrl.contains("/ws")) {
                                intent = new Intent(MainActivity.this, WeighActivity.class);
                                if (redirectUrl.contains("?")) {
                                    redirectUrl = redirectUrl +"&r="+new Date().getTime();
                                }else {
                                    redirectUrl = redirectUrl +"?r="+new Date().getTime();
                                }
                                intent.putExtra("url", redirectUrl);
                                startActivity(intent);
                            }else {
                                //??????????????????
                                intent = new Intent(MainActivity.this, ExecuteActivity.class);
                                intent.putExtra("url", redirectUrl);
                                startActivity(intent);
                            }
                        }
                        function.onCallBack("success");
                    }else {
                        function.onCallBack("fail");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //????????????
        mNewWeb.registerHandler("openNotification", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
//                    gotoSet();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //????????????????????????????????????????????????  ????????????????????????????????????
        mNewWeb.registerHandler("openCall", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    if (!data.isEmpty()) {
                        Map map = new Gson().fromJson(data, Map.class);
                        String num = (String) map.get("num");
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + num));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //?????????????????? ????????????????????????
        mNewWeb.registerHandler("ClearUserInfo", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    SPUtils.getInstance().put("userInfoSafe","");
                    function.onCallBack("success");
                } catch (Exception e) {
                    e.printStackTrace();
                    function.onCallBack("fail");
                }
            }
        });

        //?????????????????????????????????????????????
        mNewWeb.registerHandler("intentBrowser", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    if (!data.isEmpty()) {
                        Map map = new Gson().fromJson(data, Map.class);
                        String Url = (String) map.get("url");
                        Gson gson = new Gson();
                        BrowserBean browserBean = gson.fromJson(Url, BrowserBean.class);
                        if (!Url.isEmpty()) {
                            Intent intent = new Intent();
                            intent.setAction("android.intent.action.VIEW");
                            Uri content_url = Uri.parse(browserBean.getUrl());
                            intent.setData(content_url);
                            startActivity(intent);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        /**
         * ?????????????????????type???????????????????????????
         */
        mNewWeb.registerHandler("shareInterface", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                boolean isShareSuc = false;
                try {
                    Log.e(TAG, "shareInterface: " + data);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                function.onCallBack(isShareSuc+"");
            }
        });

        /**
         * ?????????????????????
         */
        mNewWeb.registerHandler("startIntentZing", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    // ????????????
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.RECORD_AUDIO)
                            != PackageManager.PERMISSION_GRANTED) {
                        Log.e(TAG, "handler: no permission");
                        //???????????????????????????????????????????????????????????????
                        ActivityCompat.requestPermissions(MainActivity.this,
                                APPLY_PERMISSIONS_APPLICATION, 200);
                    } else {
                        Log.e(TAG, "startIntentZing: start" );
                        ZxingConfig config = new ZxingConfig();
                        config.setShowAlbum(false);
                        Intent intent = new Intent(mContext, CaptureActivity.class);
                        intent.putExtra(com.yzq.zxinglibrary.common.Constant.INTENT_ZXING_CONFIG, config);
                        startActivityForResult(intent, REQUEST_CODE_SCAN);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        /**
         * ?????????????????????????????????
         */
        mNewWeb.registerHandler("toPolicy", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    if (!data.isEmpty()) {
                        Map map = new Gson().fromJson(data, Map.class);
                        String type = (String) map.get("type");
                        Intent intent = new Intent(MainActivity.this, ReminderActivity.class);
                        intent.putExtra("type", type);
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void sendDeviceToken(String deviceToken){
        Map<String, String> headerMap =  new HashMap<>();
        headerMap.put("authorization", "Bearer "+userToken);
        Map<String, String> paramsMap =  new HashMap<>();
        paramsMap.put("deviceToken", deviceToken);
        paramsMap.put("deviceType", Constant.equipment_type);
        paramsMap.put("platformType", Constant.platform_type);
        String jsonStr = new Gson().toJson(paramsMap);
        Log.e(TAG, "jsonStr: "+jsonStr);
        OkhttpUtil.okHttpPostJson(Constant.userPushRelation, jsonStr, headerMap, new CallBackUtil.CallBackString() {
            @Override
            public void onFailure(Call call, Exception e) {
                Log.e(TAG, "onFailure: "+e.getMessage());
            }

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "-----onResponse: " + response);
//                Result result = new Gson().fromJson(response, Result.class);
//                if (result.getCode() == 200) {
//                    SPUtils.getInstance().put(Constant.HAS_INSTALL, "1");
//                } else {
//                    Log.e("StartPageActivity", "?????????????????????");
//                }
            }
        });
    }


    /**
     * ??????????????????
     */
    private void gotoCamera() {
        //	???????????????????????????
        File dPictures = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //????????????
        String mFileName = "IMG_" + System.currentTimeMillis() + ".jpg";
        //????????????
        String mFilePath = dPictures.getAbsolutePath() + "/" + mFileName;
        //?????????????????????????????????
        tempFile = new File(mFilePath);
        //???????????????????????????
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //??????7.0???????????????????????????????????????xml/file_paths.xml
            intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
        } else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
        }
        startActivityForResult(intent, REQUEST_CAPTURE);
    }

    /**
     * ???????????????
     */
    private void gotoPhoto() {
        //???????????????????????????
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "???????????????"), REQUEST_PICK);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Log.e(TAG, "onClick: ????????????1");
        //????????????
        if (mNewWeb != null && mNewWeb.canGoBack()) {
            if (mWebError.getVisibility() == View.VISIBLE) {
                finish();
            } else {
                mNewWeb.goBack();
            }
        } else {
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e(TAG, "onRequestPermissionsResult: "+grantResults.length + "   ---"+APPLY_PERMISSIONS_APPLICATION.length);
        switch (requestCode) {
            case ADDRESS_PERMISSIONS_CODE:
                //??????????????????
                if (grantResults.length == APPLY_PERMISSIONS_APPLICATION.length) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            //????????????????????????????????????
                            showDialog();
                            Toast.makeText(mContext, "?????????????????????", Toast.LENGTH_LONG).show();
                            break;
                        } else {
                        }
                    }
                }
                break;
        }
    }

    //???????????????
    private void showDialog() {
        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("??????")
                .setMessage("??????????????????????????????????????????????????????????????????")
                .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        goToAppSetting();
                    }
                })
                .setCancelable(false)
                .show();
    }

    // ????????????????????????????????????
    private void goToAppSetting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    // ??????????????????????????????????????????
    private long exitTime = 0;
    private void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            //????????????????????????????????????
            Toast.makeText(mContext, "????????????????????????", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
//            CleanDataUtils.clearAllCache(Objects.requireNonNull(MainActivity.this));
//            WebStorage.getInstance().deleteAllData();
//            mNewWeb.clearCache(true);
//            mNewWeb.clearHistory();
//            mNewWeb.clearFormData();
        } else {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && resultCode == 2) {//????????????????????????????????????????????????
            String apply_url = data.getStringExtra("apply_url");//data:???????????????putExtra()??????????????????
            webView(apply_url);
        }
        switch (requestCode) {
            case NOT_NOTICE:
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED) {
                    //??????READ_EXTERNAL_STORAGE??????
                    ActivityCompat.requestPermissions(MainActivity.this, APPLY_PERMISSIONS_APPLICATION,
                            ADDRESS_PERMISSIONS_CODE);
                }//????????????????????????????????????????????????????????????
                break;
            case REQUEST_CODE_SCAN: //???????????????
            {
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        String stringExtra = data.getStringExtra(Constant.CODED_CONTENT);
                        Log.e(TAG, "stringExtra length: " + stringExtra.length());
                        Log.e(TAG, "onActivityResult: " + stringExtra);
                        mNewWeb.evaluateJavascript("window.sdk.getCodeUrl(\"" + stringExtra + "\")", new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String value) {

                            }
                        });
                        /**
                         * ????????????????????????????????????
                         */
                        mNewWeb.callHandler("getCodeUrl", stringExtra, new CallBackFunction() {
                            @Override
                            public void onCallBack(String data) {

                            }
                        });
                    }
                }
            }
            break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onStart() {
        String apply_url = (String) SPUtils.getInstance().get("apply_url", "");//???????????????????????????????????????????????????
        Log.e(TAG, " onStart: "+ apply_url);
        if (!TextUtils.isEmpty(apply_url)) {
            webView(apply_url);
        }
        //??????????????????
        SPUtils.getInstance().put("apply_url","");
        super.onStart();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Uri uri = getIntent().getData(); //????????????????????????  ?????????????????????  oppo????????????
        if (uri != null) {
            Log.e(TAG, "open notice list: " + uri);


//            String thirdId = uri.getQueryParameter("thirdId");
//            if (thirdId != null) {
//                intent = new Intent(this, NewsActivity.class);
//                intent.putExtra("url", thirdId);
//                startActivity(intent);
//            }
            String open = uri.getQueryParameter("open");
            if (open.equals("message")) {
                Log.e(TAG, "huaweiUrl: " + uri);
                //test://zzy:8080/home?open=message&appid=2&appName=????????????????????????  ????????????????????????
//                String huaWei = uri.getQueryParameter("appid");
//                String appName = uri.getQueryParameter("appName");
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.put("appid", huaWei);
//                jsonObject.put("appName", appName);
//                String s = jsonObject.toJSONString();
//                Log.e(TAG, "onNewIntent: " + s);
//                webView(Constant.APP_NOTICE_LIST);
//                Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        //??????????????????
//                        mNewWeb.callHandler("PushMessageIntent", s, new CallBackFunction() {
//                            @Override
//                            public void onCallBack(String data) {
//
//                            }
//                        });
//                    }
//                }, 1000);//2????????????Runnable??????run??????
//            }
            }

            String app_notice_list = intent.getStringExtra("APP_NOTICE_LIST");
//            String xiaomiMessage = intent.getStringExtra("pushContentMessage");
            if (app_notice_list != null) {
//            webView(Constant.APP_NOTICE_LIST);
                if (app_notice_list.equals("??????")) { //?????????????????????
//                    webView(Constant.MyNews);
                } else if (app_notice_list.equals(Constant.NOTICE_LIST)) {
                    webView(Constant.text_url);
//                    Log.e(TAG, "xiaomiMessage: " + xiaomiMessage);
//                    Handler handler = new Handler();
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            //??????????????????
//                            mNewWeb.callHandler("PushMessageIntent", xiaomiMessage, new CallBackFunction() {
//                                @Override
//                                public void onCallBack(String data) {
//
//                                }
//                            });
//                        }
//                    }, 1000);//2????????????Runnable??????run??????
                }
            }
        }
    }




}
