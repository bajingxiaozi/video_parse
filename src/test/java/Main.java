import com.xyf.video.parse.VideoDownloadHelper;

import java.io.File;

public class Main {

    public static void main(String[] args) throws Exception {
        new VideoDownloadHelper(" https://v.kuaishou.com/6OBxDo ").download(new File("."));
    }

}
