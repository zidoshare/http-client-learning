package site.zido.httpclinet.cha2;

import org.apache.http.conn.HttpClientConnectionManager;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class IdleConnectionMonitorTest {
    /**
     * 使用单独的线程主动定时关闭空闲连接
     */
    private static class IdleConnectionMonitorThread extends Thread {
        private final HttpClientConnectionManager manager;
        private volatile boolean shutdown;

        private IdleConnectionMonitorThread(HttpClientConnectionManager manager) {
            super();
            this.manager = manager;
        }

        @Override
        public void run() {
            try {
                while (!shutdown) {
                    synchronized (this) {
                        wait(5000);
                        manager.closeExpiredConnections();
                        manager.closeIdleConnections(30, TimeUnit.SECONDS);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void shutdown() {
            shutdown = true;
            synchronized (this) {
                notifyAll();
            }
        }
    }

    @Test
    public void testIdle() {

    }

}
