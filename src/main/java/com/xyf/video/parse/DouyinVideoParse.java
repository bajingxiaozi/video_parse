package com.xyf.video.parse;

import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DouyinVideoParse implements IVideoParse {

    private static String getDownloadLink(String videoId) {
        return "https://aweme.snssdk.com/aweme/v1/play/?video_id=" + videoId + "&ratio=720p&line=0";
    }

    private VideoInfo getVideoInfoWithVideoFakeId(String videoFakeId) throws IOException {
        parseListener.onParse("getVideoId()->:" + videoFakeId);
        Request request = new Request.Builder()
                .url("https://www.iesdouyin.com/web/api/v2/aweme/iteminfo/?item_ids=" + videoFakeId)
                .build();
        Response response = HttpUtils2.provideOkHttpClient().newCall(request).execute();

        try (ResponseBody body = response.body()) {
            parseListener.onParse("getVideoId()->response.code():" + response.code());
            String content = body.string();
            parseListener.onParse("getVideoId()->body.string():" + content);
            // {"url_list":["https://aweme.snssdk.com/aweme/v1/playwm/?video_id=v0200f700000bruluaclbum6m7vf96hg&ratio=720p&line=0"],
            Matcher videoIdMatcher = Pattern.compile("[\\s\\S]+video_id=(?<videoId>[\\w]+)[\\s\\S]+").matcher(content);
            Matcher videoDescriptionMatcher = Pattern.compile("[\\s\\S]*\"desc\":[ ]*\"(?<description>[^\"]+)\"[\\s\\S]*").matcher(content);
            if (videoIdMatcher.matches() && videoDescriptionMatcher.matches()) {
                String videoId = videoIdMatcher.group("videoId");
                parseListener.onParse("getVideoId()->videoId:" + videoId);
                String videoDescription = videoDescriptionMatcher.group("description");
                parseListener.onParse("getVideoId()->videoDescription:" + videoDescription);
                return new VideoInfo(getDownloadLink(videoId), videoDescription);
            }
        }

        parseListener.onParse("getVideoId()->error:" + videoFakeId);
        throw new IllegalStateException("can't get video id. video=" + videoFakeId);
    }

    private String getVideoFakeId(String url) throws Exception {
        parseListener.onParse("getVideoFakeId()->:" + url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = HttpUtils2.provideOkHttpClient().newCall(request).execute();
        parseListener.onParse("getVideoFakeId()->response.code():" + response.code());
        String redirectUrl = response.request().url().url().toExternalForm();
        parseListener.onParse("getVideoFakeId()->redirectUrl:" + response.code());
        Matcher matcher = Pattern.compile(".*video/(?<video>\\d+)/.*").matcher(redirectUrl);
        if (matcher.matches()) {
            String video = matcher.group("video");
            parseListener.onParse("getVideoFakeId()->video:" + video);
            return video;
        }

        parseListener.onParse("getVideoFakeId()->error:" + url);
        throw new IllegalStateException("can't get video fake id. url=" + url);
    }

    @Override
    public VideoInfo getVideoInfo(String link) throws Exception {
        String video = getVideoFakeId(link);
        return getVideoInfoWithVideoFakeId(video);
    }

    @Override
    public void setParseListener(ParseListener listener) {
        parseListener = listener;
    }

    @Override
    public boolean handler(String link) {
        // https://v.douyin.com/JLQFYhN/
        return  Pattern.compile(".+v[.]douyin[.]com.+").matcher(link).matches();
    }

    private ParseListener parseListener = new ParseListener() {
        @Override
        public void onParse(String message) {

        }
    };

}
