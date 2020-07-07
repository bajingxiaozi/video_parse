package com.xyf.video.parse;

import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KuaishouLinkParse implements ILinkParse {

    @Override
    public LinkInfo getLinkInfo(String link) throws Exception {
        Request request = new Request.Builder()
                .url(link)
                .build();
        Response response = HttpUtils2.provideOkHttpClient().newCall(request).execute();

        if (!response.isSuccessful()) {
            throw new IOException("error response->" + request + "->" + response);
        }

        try (ResponseBody body = response.body()) {
            if (body == null) {
                throw new IOException("error ResponseBody->" + request + "->" + response);
            }

            String content = body.string();

            if (content.contains("长图分享")) {
                return new LinkInfo(false, null, null);
            }

            if (content.contains("请进行安全验证")) {
                throw new IOException("need user security check->" + request + "->" + response);
            }

            // "srcNoMark":"https://txmov2.a.yximgs.com/upic/2019/09/19/17/BMjAxOTA5MTkxNzQxMzNfMTUxODQ5MzAwXzE3NjM3ODU3NzAxXzJfMw==_b_B723b7f16a1dbcfe47a267abd94a947aa.mp4?tt=b&di=3cb0ab95&bp=10000"}
            Matcher linkMatcher = Pattern.compile("[\\s\\S]*\"srcNoMark\" *: *\"(?<link>https://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|])\"[\\s\\S]*").matcher(content);

            if (!linkMatcher.matches()) {
                throw new IOException("can't get link->" + request + "->" + response);
            }

            Matcher titleMatcher = Pattern.compile("[\\s\\S]*\"title\" *: *\"(?<title>[^\"]+)\"[\\s\\S]*").matcher(content);
            String videoDescription = titleMatcher.matches() ? titleMatcher.group("title") : "下载";
            return new LinkInfo(true, linkMatcher.group("link"), videoDescription);
        }
    }

    @Override
    public boolean canHandler(String link) {
        // https://v.kuaishou.com/6OBxDo
        return Pattern.compile(".+v[.]kuaishou[.]com.+").matcher(link).matches();
    }

}
