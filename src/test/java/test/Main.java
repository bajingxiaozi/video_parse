package test;

import com.google.gson.GsonBuilder;
import com.xyf.video.parse.LinksBean;
import com.xyf.video.parse.VideoDownloadHelper;
import org.apache.commons.io.IOUtils;

import java.io.File;

public class Main {

    public static void main(String[] args) throws Exception {
        LinksBean linksBean = new GsonBuilder().create().fromJson("", LinksBean.class);
        for (LinksBean.Link link : linksBean.links) {
            new VideoDownloadHelper(link.link).download(new File("."));
        }
    }

}
