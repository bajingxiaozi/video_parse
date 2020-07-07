package com.xyf.video.parse;

public interface ILinkParse {

    LinkInfo getLinkInfo(String link) throws Exception;

    boolean canHandler(String link);

}
