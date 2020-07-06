package com.xyf.video.parse;

import java.util.Collections;
import java.util.List;

public class VideoParseFactory {

    private static final List<Class<? extends IVideoParse>> PARSES = Collections.singletonList(DouyinVideoParse.class);

    public static VideoInfo parse(String link) throws Exception {
        for (Class<? extends IVideoParse> pars : PARSES) {
            IVideoParse videoParse = pars.newInstance();
            if (!videoParse.canHandler(link)) {
                continue;
            }

            return videoParse.getVideoInfo(link);
        }

        throw new IllegalArgumentException("can't find available video parse. link->" + link);
    }

}
