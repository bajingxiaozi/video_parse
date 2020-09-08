package com.xyf.video.parse;

import java.util.StringJoiner;

public class LinkInfo {

    private final boolean isVideo;
    private final String videoDownloadLink;
    private final String description;
    private final String author;

    public LinkInfo(boolean isVideo, String videoDownloadLink, String description, String author) {
        this.isVideo = isVideo;
        this.videoDownloadLink = videoDownloadLink;
        this.description = description;
        this.author = author;
    }

    public String getVideoDownloadLink() {
        return videoDownloadLink;
    }

    public String getDescription() {
        return description;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public String getAuthor() {
        return author;
    }

}
