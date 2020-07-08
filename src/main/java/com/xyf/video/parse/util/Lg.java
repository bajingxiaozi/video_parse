package com.xyf.video.parse.util;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Lg {

    private static final Map<String, Logger> LOGGER_MAP = new HashMap<>();

    private static Logger getLogger(@Nonnull String tag) {
        if (!LOGGER_MAP.containsKey(tag)) {
            LOGGER_MAP.put(tag, LoggerFactory.getLogger(tag));
        }

        return LOGGER_MAP.get(tag);
    }

    private static final int METHOD_BORDER_LENGTH = 120;
    private static final String METHOD_HEAD;
    private static final String METHOD_SEPARATE;
    private static final String MESSAGE_TAIL;

    static {
        METHOD_HEAD = "┌" + Strings.repeat("─", METHOD_BORDER_LENGTH) + "┐";
        METHOD_SEPARATE = "│" + Strings.repeat("-", METHOD_BORDER_LENGTH) + "│";
        MESSAGE_TAIL = "└" + Strings.repeat("─", METHOD_BORDER_LENGTH) + "┘";
    }

    private static void logMethodHead(@Nonnull TYPE type, @Nonnull String tag, @Nonnull String methodHead) {
        logMessage(type, tag, METHOD_HEAD);
        logLine(type, tag, methodHead);
        logMessage(type, tag, METHOD_SEPARATE);
    }

    private enum TYPE {
        DEBUG, INFO, WARN, ERROR
    }


    public static void e(@Nonnull String tag, @Nonnull Object... messages) {
        log(TYPE.ERROR, tag, messages);
    }

    public static void w(@Nonnull String tag, @Nonnull Object... messages) {
        log(TYPE.WARN, tag, messages);
    }

    public static void d(@Nonnull String tag, @Nonnull Object... messages) {
        log(TYPE.DEBUG, tag, messages);
    }

    public static void i(@Nonnull String tag, @Nonnull Object... messages) {
        log(TYPE.INFO, tag, messages);
    }

    private static void log(@Nonnull TYPE type, @Nonnull String tag, @Nonnull Object... objects) {
        final String methodHead;
        {
            final StackTraceElement element = new Throwable().getStackTrace()[2];
            methodHead = String.format("%s(%s:%d)", element.getMethodName(), element.getFileName(), element.getLineNumber());
        }

        final Thread thread = Thread.currentThread();

        logMethodHead(type, tag, methodHead);
        logLine(type, tag, thread);
        for (Object obj : objects) {
            if (obj instanceof Throwable) {
                Throwable throwable = (Throwable) obj;
                logLine(type, tag, throwable);
                for (StackTraceElement element : throwable.getStackTrace()) {
                    logLine(type, tag, element);
                }
            } else if (obj instanceof Collection) {
                Collection collection = (Collection) obj;
                for (Object item : collection) {
                    logLine(type, tag, item);
                }
            } else if (obj == null) {
                logLine(type, tag, "null");
            } else {
                for (String s : obj.toString().split("\n")) {
                    logLine(type, tag, s);
                }
            }
        }
        logMethodTail(type, tag);
    }

    private static void logMethodTail(@Nonnull TYPE type, @Nonnull String tag) {
        logMessage(type, tag, MESSAGE_TAIL);
    }

    private static void logLine(@Nonnull TYPE type, @Nonnull String tag, @Nonnull Object message) {
        final String line = String.format("│%-" + METHOD_BORDER_LENGTH + "s│", message);
        logMessage(type, tag, line);
    }

    private static void logMessage(@Nonnull TYPE type, @Nonnull String tag, @Nonnull String message) {
        switch (type) {
            case ERROR:
                getLogger(tag).error(message);
                break;
            case WARN:
                getLogger(tag).warn(message);
                break;
            case INFO:
                getLogger(tag).info(message);
                break;
            default:
                getLogger(tag).debug(message);
                break;
        }
    }

}
