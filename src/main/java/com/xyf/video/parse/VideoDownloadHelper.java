package com.xyf.video.parse;

import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.regex.Pattern;

public class VideoDownloadHelper {

    private final String link;

    public VideoDownloadHelper(String link) {
        this.link = link;
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

    public LinkInfo download(File directory) throws Exception {
        LinkInfo linkInfo = VideoParseFactory.parse(link);

        if (!linkInfo.isVideo()) {
            return linkInfo;
        }

        Request request = new Request.Builder()
                .url(linkInfo.getVideoDownloadLink())
                .build();
        Response response = HttpUtils2.provideOkHttpClient().newCall(request).execute();

        if (!response.isSuccessful()) {
            throw new IOException("error response->" + request + "->" + response);
        }

        File tempFile = getNextTempFile(directory);

        try (FileOutputStream outputStream = new FileOutputStream(tempFile); ResponseBody body = response.body()) {
            if (body == null) {
                throw new IOException("error ResponseBody->" + request + "->" + response);
            }

            InputStream inputStream = IOUtils.toBufferedInputStream(body.byteStream());
            IOUtils.copy(inputStream, new BufferedOutputStream(outputStream));
            outputStream.getFD().sync();
        }

        if (tempFile.length() == 0) {
            throw new IOException("save file failed->" + tempFile);
        }

        Pattern pattern = Pattern.compile("[\\\\/:*?\"<>|]");
        String fixFileName = pattern.matcher(linkInfo.getDescription()).replaceAll("");
        fixFileName = StringUtils.defaultIfEmpty(fixFileName, "video");
        File fixFile = getNextFile(new File(directory, fixFileName + ".mp4"));
        boolean renameSuccess = tempFile.renameTo(fixFile);
        if (!renameSuccess) {
            throw new IOException("rename failed from->" + tempFile + "->" + fixFile);
        }

        return linkInfo;
    }

}
