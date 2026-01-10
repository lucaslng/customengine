package com.lucaslng.engine.utils;

public final class OsFetcher {

    private static String OS = null;

    private OsFetcher() {}

    public static String getOsName() {
        if (OS == null) {
            OS = System.getProperty("os.name");
        }
        return OS;
    }

    public static boolean isMac() {
        return getOsName().startsWith("Mac");
    }

    // public static boolean isWindows() {
    //     return getOsName().startsWith("Windows");
    // }

    // public static boolean isUnix() {
    //     String os = getOsName().toLowerCase();
    //     return os.contains("nix") || os.contains("nux") || os.contains("aix");
    // }
}
