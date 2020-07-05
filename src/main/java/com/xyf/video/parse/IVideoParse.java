package com.xyf.video.parse;

public interface IVideoParse {

    VideoInfo getVideoInfo(String link) throws Exception;

    boolean handler(String link);

}
