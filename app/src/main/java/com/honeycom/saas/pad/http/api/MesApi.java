package com.honeycom.saas.pad.http.api;




import com.honeycom.saas.pad.http.bean.AdMessageBean;
import com.honeycom.saas.pad.http.bean.AdMessagePackage;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
* author : zhoujr
* date : 2021/10/14 16:57
* desc : 请求接口配置
*/
public interface MesApi {


    /************************************系统******************************************************/

    @GET("/api-p/tAdvSet/phoneOne")
    Single<AdMessagePackage> getAdMessage(@Query("compare") String url, @Query("equipmentType") int eqType);

    @GET
    Single<ResponseBody> downLoadFile(@Url String fileUrl);




}
