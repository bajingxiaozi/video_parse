package com.xyf.video.parse.util;

import com.xyf.video.parse.LinkInfo;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VideoDownloadHelper {

    private static final String TAG = "VideoDownloadHelper";

    @Nonnull
    private final String link;

    public VideoDownloadHelper(@Nonnull String link) {
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

    @Nonnull
    public LinkInfo download(@Nonnull File directory) throws Exception {
        Lg.d(TAG, "download", directory, "[" + link + "]");

        LinkInfo linkInfo = VideoParseFactory.parse(link);

        Lg.d(TAG, "download", link, linkInfo);

        if (!linkInfo.isVideo()) {
            return linkInfo;
        }

        Request request = new Request.Builder()
                .url(linkInfo.getVideoDownloadLink())
                .build();
        Response response = HttpUtils2.provideOkHttpClient().newCall(request).execute();

        Lg.d(TAG, "download", request, response);

        if (!response.isSuccessful()) {
            throw new IOException("error response->" + request + "->" + response);
        }

        File tempFile = getNextTempFile(directory);
        FileUtils.forceMkdirParent(tempFile);
        try (FileOutputStream outputStream = new FileOutputStream(tempFile); ResponseBody body = response.body()) {
            if (body == null) {
                throw new IOException("error ResponseBody->" + request + "->" + response);
            }

            IOUtils.copy(IOUtils.toBufferedInputStream(body.byteStream()), new BufferedOutputStream(outputStream));
            outputStream.getFD().sync();
        }

        if (tempFile.length() == 0) {
            throw new IOException("save file failed->" + tempFile);
        }

        String fixFileName = StringUtils.defaultIfEmpty(FileNameUtils.normalize(linkInfo.getDescription()), "video");
        File fixFile = getNextFile(new File(directory, fixFileName + ".mp4"));
        boolean renameSuccess = tempFile.renameTo(fixFile);

        Lg.d(TAG, "download", tempFile, fixFile, renameSuccess);

        if (!renameSuccess) {
            throw new IOException("rename failed from->" + tempFile + "->" + fixFile);
        }

        return linkInfo;
    }

}
