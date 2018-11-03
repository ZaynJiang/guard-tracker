package cn.guard.tracker.bootstrap.bootstrap.core.utils;

public class StringUtils {

    public static boolean isEmpty(final String string) {
        return string == null || string.length() == 0;
    }

    public static boolean isNotEmpty(final String string) {
        return string != null && string.length() > 0;
    }
}
