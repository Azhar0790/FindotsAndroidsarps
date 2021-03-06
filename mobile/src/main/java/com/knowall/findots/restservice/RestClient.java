package com.knowall.findots.restservice;

import android.util.Log;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by parijathar on 6/6/2016.
 */
public class RestClient {

    private static final String ACCEPT_KEY = "Accept";
    private static final String APPLICATION_JSON = "application/json";

    private static final String AUTHORIZATION_KEY = "Authorization";
    private static final String AUTH_TOKEN = "auth-token";

    static NetworkController apiService = null;
    static Retrofit retrofit = null;
    static OkHttpClient client = null;

    public RestClient() {

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        client = new OkHttpClient();
        client.setConnectTimeout(60, TimeUnit.SECONDS);
        client.setReadTimeout(60, TimeUnit.SECONDS);
        client.setWriteTimeout(60, TimeUnit.SECONDS);
        client.interceptors().add(loggingInterceptor);
        client.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                Request request = original.newBuilder()
                        .header(ACCEPT_KEY, APPLICATION_JSON)
                        .header(AUTHORIZATION_KEY, AUTH_TOKEN)
                        .method(original.method(), original.body())
                        .build();

                Response response = chain.proceed(request);

                return response;
            }
        });

        retrofit = new Retrofit.Builder()
                .baseUrl(RestURLs.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(NetworkController.class);

    }

    public NetworkController getApiService() {
        Log.d("jomy","createdd...");
        return apiService;
    }



}
