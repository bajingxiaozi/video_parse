package com.xyf.video.parse.util;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpUtils2 {

    private static OkHttpClient okHttpClient;

    public static void setOkHttpClient(OkHttpClient okHttpClient) {
        HttpUtils2.okHttpClient = okHttpClient;
    }

    public static OkHttpClient provideOkHttpClient() {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .addInterceptor(new CommonHeaderInterceptor())
                    .build();
        }

        return okHttpClient;
    }

    private static class CommonHeaderInterceptor implements Interceptor {

        @Nonnull
        @Override
        public Response intercept(@Nonnull Chain chain) throws IOException {
            Request request = chain.request()
                    .newBuilder()
                    .addHeader("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1")
                    .build();
            return chain.proceed(request);
        }

    }

    private static final Pattern LINK_PATTERN =  Pattern.compile(".*(?<realLink>(http|https)://[a-zA-Z:./0-9]+)[\\s\\S]*");

    public static boolean isLink(@Nonnull String link){
        Matcher matcher =LINK_PATTERN.matcher(link);
        return matcher.matches();
    }

    public static String getLink(@Nonnull String link){
        Matcher matcher =LINK_PATTERN.matcher(link);
        if(matcher.matches()){
            return matcher.group("realLink");
        }

        throw new IllegalArgumentException("not a link->" + link);
    }

}
