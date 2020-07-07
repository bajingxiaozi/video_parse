package com.xyf.video.parse;

import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DouyinLinkParse implements ILinkParse {

    private static String getDownloadLink(String videoId) {
        return "https://aweme.snssdk.com/aweme/v1/play/?video_id=" + videoId + "&ratio=720p&line=0";
    }

    private LinkInfo getVideoInfoWithVideoFakeId(String videoFakeId) throws IOException {
        Request request = new Request.Builder()
                .url("https://www.iesdouyin.com/web/api/v2/aweme/iteminfo/?item_ids=" + videoFakeId)
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
            // {"url_list":["https://aweme.snssdk.com/aweme/v1/playwm/?video_id=v0200f700000bruluaclbum6m7vf96hg&ratio=720p&line=0"],
            Matcher videoIdMatcher = Pattern.compile("[\\s\\S]+video_id=(?<videoId>[\\w]+)[\\s\\S]+").matcher(content);

            if (!videoIdMatcher.matches()) {
                throw new IOException("can't get video id->" + request + "->" + response);
            }

            String videoId = videoIdMatcher.group("videoId");
            Matcher videoDescriptionMatcher = Pattern.compile("[\\s\\S]*\"desc\":[ ]*\"(?<description>[^\"]+)\"[\\s\\S]*").matcher(content);
            String videoDescription = videoDescriptionMatcher.matches() ? videoDescriptionMatcher.group("description") : "下载";
            return new LinkInfo(true, getDownloadLink(videoId), videoDescription);
        }

    }

    private String getVideoFakeId(String url) throws Exception {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = HttpUtils2.provideOkHttpClient().newCall(request).execute();

        if (!response.isSuccessful()) {
            throw new IOException("error response->" + request + "->" + response);
        }

        try (ResponseBody body = response.body()) {
            String redirectUrl = response.request().url().url().toExternalForm();
            Matcher matcher = Pattern.compile(".*video/(?<video>\\d+)/.*").matcher(redirectUrl);
            if (!matcher.matches()) {
                throw new IOException("can't get video fake id->" + request);
            }

            return matcher.group("video");
        }
    }

    @Override
    public LinkInfo getLinkInfo(String link) throws Exception {
        String videoFakeId = getVideoFakeId(link);
        return getVideoInfoWithVideoFakeId(videoFakeId);
    }

    @Override
    public boolean canHandler(String link) {
        // https://v.douyin.com/JLQFYhN/
        return Pattern.compile(".+v[.]douyin[.]com.+").matcher(link).matches();
    }

}
