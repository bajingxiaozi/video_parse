package com.xyf.video.parse;

import com.xyf.video.parse.util.HttpUtils2;
import com.xyf.video.parse.util.Lg;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KuaishouLinkParse implements ILinkParse {

    private static final String TAG = "KuaishouLinkParse";

    @Nonnull
    @Override
    public LinkInfo getLinkInfo(@Nonnull String link) throws Exception {
        Request request = new Request.Builder()
                .url(link)
                .build();
        Response response = HttpUtils2.provideOkHttpClient().newCall(request).execute();

        Lg.d(TAG, "getLinkInfo", request, response);

        if (!response.isSuccessful()) {
            throw new IOException("error response->" + request + "->" + response);
        }

        try (ResponseBody body = response.body()) {
            if (body == null) {
                throw new IOException("error ResponseBody->" + request + "->" + response);
            }

            String content = body.string().replaceAll("&#34;", "\"");
            Lg.d(TAG, "getLinkInfo", request, content);

            if (content.contains("长图分享") || content.contains("图集分享") || content.contains("单图分享")) {
                return new LinkInfo(false, null, null, "未知");
            }

            if (content.contains("请进行安全验")) {
                throw new IOException("need user security check->" + request + "->" + response);
            }

            // "srcNoMark":"https://txmov2.a.yximgs.com/upic/2019/09/19/17/BMjAxOTA5MTkxNzQxMzNfMTUxODQ5MzAwXzE3NjM3ODU3NzAxXzJfMw==_b_B723b7f16a1dbcfe47a267abd94a947aa.mp4?tt=b&di=3cb0ab95&bp=10000"}
            Matcher linkMatcher = Pattern.compile("[\\s\\S]*\"srcNoMark\" *: *\"(?<link>https://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|])\"[\\s\\S]*").matcher(content);

            if (!linkMatcher.matches()) {
                throw new IOException("can't get link->" + request + "->" + response);
            }

            Matcher titleMatcher = Pattern.compile("[\\s\\S]*\"title\" *: *\"(?<title>[^\"]+)\"[\\s\\S]*").matcher(content);
            String videoDescription = titleMatcher.matches() ? titleMatcher.group("title") : "下载";

            Matcher nameMatcher = Pattern.compile("[\\s\\S]*\"name\" *: *\"(?<name>[^\"]+)\"[\\s\\S]*").matcher(content);
            String name = nameMatcher.matches() ? nameMatcher.group("name") : "未知";
            return new LinkInfo(true, linkMatcher.group("link"), videoDescription, "name");
        }
    }

    @Override
    public boolean canHandler(String link) {
        // https://v.kuaishou.com/6OBxDo
        return Pattern.compile(".+v[.]kuaishou[.]com.+").matcher(link).matches();
    }

}
