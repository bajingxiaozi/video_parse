package com.xyf.video.parse.util;

import com.xyf.video.parse.DouyinLinkParse;
import com.xyf.video.parse.ILinkParse;
import com.xyf.video.parse.KuaishouLinkParse;
import com.xyf.video.parse.LinkInfo;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class VideoParseFactory {

    private static final String TAG = "VideoParseFactory";

    private static final List<Class<? extends ILinkParse>> PARSES = Arrays.asList(DouyinLinkParse.class, KuaishouLinkParse.class);

    @Nonnull
    public static LinkInfo parse(@Nonnull String link) throws Exception {
        for (Class<? extends ILinkParse> pars : PARSES) {
            ILinkParse linkParse = pars.newInstance();
            boolean canHandle = linkParse.canHandler(link);
            Lg.d(TAG, "parse", linkParse, canHandle);
            if (!canHandle) {
                continue;
            }

            return linkParse.getLinkInfo(link);
        }

        throw new IllegalArgumentException("can't find available video parse. link->" + link);
    }

}
