import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.regex.Pattern;

public class VideoDownloadHelper {

    private final String link;
    private final IVideoParse.ParseListener listener;

    public VideoDownloadHelper(String link, IVideoParse.ParseListener listener) {
        this.link = link;
        this.listener = listener;
    }

    private static File getNextTempFile(File directory) {
        int index = 1;
        while (true) {
            File tempFile = new File(directory, "temp" + index);
            if (!tempFile.exists()) {
                return tempFile;
            }
        }
    }

    private void download(File directory) throws Exception {
        VideoInfo videoInfo = VideoParseFactory.parse(link, listener);

        Request request = new Request.Builder()
                .url(videoInfo.getDownloadLink())
                .build();
        Response response = HttpUtils2.provideOkHttpClient().newCall(request).execute();

        File tempFile = getNextTempFile(directory);
        tempFile.deleteOnExit();

        try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(tempFile)); ResponseBody body = response.body(); InputStream inputStream = IOUtils.toBufferedInputStream(body.byteStream())) {
            IOUtils.copy(inputStream, outputStream);
        }

        Pattern pattern = Pattern.compile("[\\\\/:*?\"<>|]");
        String fixFileName = pattern.matcher(videoInfo.getDescription()).replaceAll("");
        boolean renameResult = tempFile.renameTo(new File(directory, fixFileName + ".mp4"));

        listener.onParse("rename from " + tempFile + " to " + fixFileName + " " + renameResult);
    }

}
