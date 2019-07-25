package br.com.ffroliva.mimecast.utils;

import java.io.IOException;
import java.net.*;

public class ServerUtils {

    public static boolean pingHost(String url) {
        try {
            URL u = new URL(url);
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(u.getHost(), u.getPort()), 1000);
                return true;
            } catch (IOException e) {
                return false; // Either timeout or unreachable or failed DNS lookup.
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
