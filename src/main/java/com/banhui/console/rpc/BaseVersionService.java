package com.banhui.console.rpc;

import org.apache.logging.log4j.util.PropertiesUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;

public class BaseVersionService {

    /**
     * return：-1 需更新 1,0 无需更新
     */
    public static int compareVersion(
            String v1,
            String v2
    ) {
        if (v1.equals(v2)) {
            return 0;
        }
        String[] version1Array = v1.split("[._]");
        String[] version2Array = v2.split("[._]");
        int index = 0;
        int minLen = Math.min(version1Array.length, version2Array.length);
        long diff = 0;

        while (index < minLen
                && (diff = Long.parseLong(version1Array[index])
                - Long.parseLong(version2Array[index])) == 0) {
            index++;
        }
        if (diff == 0) {
            for (int i = index; i < version1Array.length; i++) {
                if (Long.parseLong(version1Array[i]) > 0) {
                    return 1;
                }
            }

            for (int i = index; i < version2Array.length; i++) {
                if (Long.parseLong(version2Array[i]) > 0) {
                    return -1;
                }
            }
            return 0;
        } else {
            return diff > 0 ? 1 : -1;
        }
    }

    public static String getLatestVersion(String urlStr) {
        URL url;
        BufferedReader in = null;
        String str = null;
        try {
            url = new URL(urlStr);
            in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
            str = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return str;
    }


    public static String getBhtVersion() {
        String version = null;
        Properties properties = new Properties();
        InputStream in = PropertiesUtil.class.getResourceAsStream("/default-settings.properties");
        try {
            properties.load(in);
            version = properties.getProperty("bht-version");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return version;
    }
}
