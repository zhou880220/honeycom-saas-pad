package com.honeycom.saas.pad.http;



import com.honeycom.saas.pad.http.api.MesApi;
import com.honeycom.saas.pad.http.bean.AdMessagePackage;
import com.honeycom.saas.pad.util.Constant;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;

//import io.reactivex.Observable;

/**
* author : zhoujr
* date : 2021/10/14 16:58
* desc : 远程通信对象
*/
public class RemoteRepository {

    private static final String TAG = "RemoteRepository";

    private static RemoteRepository sInstance;
    private Retrofit mRetrofit;
    private MesApi mesApi;

    private RemoteRepository(){
        mRetrofit = RemoteHelper.getInstance()
                .getRetrofit();

        mesApi = mRetrofit.create(MesApi.class);
    }

    public static RemoteRepository getInstance(){
        if (sInstance == null){
            synchronized (RemoteHelper.class){
                if (sInstance == null){
                    sInstance = new RemoteRepository();
                }
            }
        }
        return sInstance;
    }



    /**************************启动**********************************/
    public Single<AdMessagePackage> getAdMessage(String url) {
        return mesApi.getAdMessage(url, 2, Constant.platform_type);
    }

    public Single<ResponseBody> downLoadFile(String fileUrl) {
        return mesApi.downLoadFile(fileUrl);
    }



}
