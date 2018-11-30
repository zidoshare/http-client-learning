package site.zido.httpclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ServerUtils {
    private static Thread serverThread;
    private static Process exec;

    public static void runTestServer() {
        final String property = System.getProperty("os.name");
        String serverBin;
        if (property.startsWith("Mac OS")) {
            serverBin = "demo-server-mac";
        } else if (property.startsWith("Windows")) {
            serverBin = "demo-server-win.exe";
        } else {
            serverBin = "demo-server-linux";
        }
        final String bin = ServerUtils.class.getResource("/demo-server/bin/" + serverBin).getPath();
        serverThread = new Thread(() -> {
            try {
                exec = Runtime.getRuntime().exec(bin);
                final BufferedReader reader = new BufferedReader(new InputStreamReader(exec.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        serverThread.start();
    }

    public static void stopServer() {
        if (serverThread != null && exec != null) {
            exec.destroy();
            serverThread.interrupt();
            exec = null;
            serverThread = null;
        }
    }
}
