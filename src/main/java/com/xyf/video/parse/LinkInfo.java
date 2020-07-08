package com.xyf.video.parse;

import com.google.common.base.Objects;

import java.util.StringJoiner;

public class LinkInfo {

    private final boolean isVideo;
    private final String videoDownloadLink;
    private final String description;

    public LinkInfo(boolean isVideo, String videoDownloadLink, String description) {
        this.isVideo = isVideo;
        this.videoDownloadLink = videoDownloadLink;
        this.description = description;
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

    @Override
    public String toString() {
        return new StringJoiner(", ", LinkInfo.class.getSimpleName() + "[", "]")
                .add("isVideo=" + isVideo)
                .add("videoDownloadLink='" + videoDownloadLink + "'")
                .add("description='" + description + "'")
                .toString();
    }

}
