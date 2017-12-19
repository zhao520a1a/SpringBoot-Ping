package com.springboot.ping.common.util;

import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 *
 * Created by 刘江平 on 2016-10-13.
 */
public class ResourceUtil {

    public static String readResourceAsString(Resource resource) {
        StringBuffer buffer = new StringBuffer();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    resource.getInputStream()), 1024);
            while (true) {
                String line = reader.readLine();
                if (line != null) {
                    buffer.append(line);
                    buffer.append("\r\n");
                } else{
                    break;
                }
            }
            reader.close();
            return buffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
}
