package com.xyf.video.parse.util;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

public class FileNameUtils {

    private static final Pattern FILE_NAME_PATTERN = Pattern.compile("[\\\\/:*?\"<>|]");

    public static String normalize(String name) {
        String fixFileName = FILE_NAME_PATTERN.matcher(name).replaceAll("");
        return fixFileName.substring(0, Math.min(fixFileName.length(), 80));
    }

}
