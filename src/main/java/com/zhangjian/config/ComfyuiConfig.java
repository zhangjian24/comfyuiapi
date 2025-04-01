package com.zhangjian.config;

import com.zhangjian.api.IComfyuiAPI;
import lombok.extern.log4j.Log4j2;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import java.io.IOException;

@Configuration
@Log4j2
public class ComfyuiConfig {

    @Value("${comfyui.baseurl}")
    private String baseurl;

    /**
     * comfyui http api
     * @return
     */
    @Bean
    public IComfyuiAPI comfyuiAPI(){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        return chain.proceed(chain.request().newBuilder()
                                .addHeader("X-Pinggy-No-Screen", "X-Pinggy-No-Screen")
                                .build());
                    }
                })
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))//日志
                .build();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(baseurl).
                addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();

        return retrofit.create(IComfyuiAPI.class);
    }
}
