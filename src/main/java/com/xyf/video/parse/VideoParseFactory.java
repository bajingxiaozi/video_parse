package com.xyf.video.parse;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class VideoParseFactory {

    private static final List<Class<? extends ILinkParse>> PARSES = Arrays.asList(DouyinLinkParse.class, KuaishouLinkParse.class);

    public static LinkInfo parse(@Nonnull String link) throws Exception {
        for (Class<? extends ILinkParse> pars : PARSES) {
            ILinkParse linkParse = pars.newInstance();
            if (!linkParse.canHandler(link)) {
                continue;
            }

            return linkParse.getLinkInfo(link);
        }

        throw new IllegalArgumentException("can't find available video parse. link->" + link);
    }

}
