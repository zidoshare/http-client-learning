package site.zido.httpclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ServerUtils {
    private static Thread serverThread;
    private static Process exec;

    public static void runTestServer() {
        CountDownLatch latch = new CountDownLatch(1);
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
                    if ("Application started. Press CTRL+C to shut down.".equals(line)) {
                        latch.countDown();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        serverThread.start();
        try {
            if (!latch.await(5, TimeUnit.SECONDS)) {
                throw new RuntimeException("server start up time out");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("thread interrupted");
        }
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
