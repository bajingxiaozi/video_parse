package com.xyf.video.parse;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class VideoParseFactory {

    private static final List<Class<? extends IVideoParse>> PARSES = Collections.singletonList(DouyinVideoParse.class);

    public static VideoInfo parse(String link) throws Exception {
        for (Class<? extends IVideoParse> pars : PARSES) {
            IVideoParse videoLink = pars.newInstance();
            if (!videoLink.handler(link)) {
                continue;
            }

            return videoLink.getVideoInfo(link);
        }

        throw new IllegalStateException("can't find available video parse. link->" + link);
    }

}
