package com.xyf.video.parse;

public interface IVideoParse {

    VideoInfo getVideoInfo(String link) throws Exception;

    interface ParseListener {

        void onParse(String message);

    }

    void setParseListener(ParseListener listener);

    boolean handler(String link);

}
