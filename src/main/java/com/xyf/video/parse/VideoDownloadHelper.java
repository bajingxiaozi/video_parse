package com.xyf.video.parse;

import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.io.FilenameUtils;
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

            index++;
        }
    }

    private static File getNextFile(File file) {
        if (!file.exists()) {
            return file;
        }

        String baseName = FilenameUtils.getBaseName(file.getName());
        String extension = FilenameUtils.getExtension(file.getName());
        int index = 2;
        while (true) {
            File fixFile = new File(file.getParentFile(), baseName + " " + index + "." + extension);
            if (!fixFile.exists()) {
                return fixFile;
            }

            index++;
        }
    }

    public void download(File directory) throws Exception {
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
        boolean renameResult = tempFile.renameTo(getNextFile(new File(directory, fixFileName + ".mp4")));

        listener.onParse("rename from " + tempFile + " to " + fixFileName + " " + renameResult);
    }

}
