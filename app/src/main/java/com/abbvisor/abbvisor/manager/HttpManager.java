package com.abbvisor.abbvisor.manager;

import com.abbvisor.abbvisor.manager.api.ServiceApi;
import com.abbvisor.abbvisor.util.Constants;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Natavit on 2/12/2016 AD.
 */
public class HttpManager {

    public static final String TAG = HttpManager.class.getSimpleName();
    private static HttpManager instance;
    private ServiceApi service;

    public static HttpManager getInstance() {
        if (instance == null)
            instance = new HttpManager();
        return instance;
    }

    private HttpManager() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.API_URL_ABBVISOR_BASE)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(ServiceApi.class);
    }

    public ServiceApi getService() {
        return service;
    }

}
