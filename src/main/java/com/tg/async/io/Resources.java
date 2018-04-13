package com.tg.async.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by twogoods on 2018/4/12.
 */
public class Resources {
    public static InputStream getResourceAsStream(String resource) throws IOException {
        InputStream in = Resources.class.getClassLoader().getResourceAsStream(resource);
        if (in == null) {
            throw new IOException("Could not find resource " + resource);
        }
        return in;
    }
}
