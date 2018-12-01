package site.zido.httpclinet.cha2;

import org.apache.http.HttpClientConnection;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.ConnectionRequest;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import site.zido.httpclient.ServerUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class HttpConnectManagerTest {
    @Before
    public void startServer() {
        ServerUtils.runTestServer();
    }

    @After
    public void stopServer() {
        ServerUtils.stopServer();
    }

    /**
     * HTTP连接是复杂的，有状态的，线程不安全的对象，需要正确管理才能正常运行。
     * HTTP连接一次只能由一个执行线程使用。
     * HttpClient使用一个特殊实体来管理对HTTP连接的访问​​，称为HTTP连接管理器，并由HttpClientConnectionManager接口表示。
     * HTTP连接管理器的目的是充当新HTTP连接的工厂，管理持久连接的生命周期以及同步对持久连接的访问​​，确保一次只有一个线程可以访问连接。
     * 内部HTTP连接管理器使用ManagedHttpClientConnection实例作为管理连接状态和控制I / O操作执行的实际连接的代理。
     * 如果托管连接被其消费者释放或显式关闭，则底层连接将从其代理中分离并返回给管理器。
     * 即使服务使用者仍然拥有对代理实例的引用，它也不再能够有意或无意地执行任何I / O操作或更改真实连接的状态。
     * <p>
     * 如果需要，可以通过调用ConnectionRequest＃cancelify（）来提前终止连接请求。
     * 这将取消阻止在ConnectionRequest＃get（）方法中阻塞的线程。
     */
    @Test
    public void testConnectionManager() throws InterruptedException, ExecutionException, ConnectionPoolTimeoutException {
        HttpClientContext context = HttpClientContext.create();
        HttpClientConnectionManager connectionManager = new BasicHttpClientConnectionManager();
        HttpRoute route = new HttpRoute(new HttpHost("localhost", 8080));
        final ConnectionRequest request = connectionManager.requestConnection(route, null);
        HttpClientConnection connection = request.get(10, TimeUnit.SECONDS);
        try {
            if (!connection.isOpen()) {
                connectionManager.connect(connection, route, 1000, context);
                connectionManager.routeComplete(connection, route, context);
            }
            //DO useful thing with connection
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            connectionManager.releaseConnection(connection, null, 1, TimeUnit.MINUTES);
        }
    }

    /**
     * PoolingHttpClientConnectionManager是一个更复杂的实现，它管理客户端连接池，并且能够为来自多个执行线程的连接请求提供服务。
     * 连接以每个路由为基础进行池化。
     * 管理员已经在池中提供持久连接的路由请求将通过从池租用连接而不是创建全新连接来进行服务。
     * PoolingHttpClientConnectionManager维护每个路由和总计的最大连接数限制。
     * 默认情况下，此实现将为每个给定路由创建不超过2个并发连接，并且总共不再有20个连接。
     * 对于许多实际应用程序而言，这些限制可能过于严格，尤其是当它们使用HTTP作为其服务的传输协议时。
     */
    @Test
    public void testPoolConnectionManager() {
        PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager();
        manager.setMaxTotal(200);
        manager.setDefaultMaxPerRoute(20);
        final HttpHost host = new HttpHost("localhost", 8080);
        manager.setMaxPerRoute(new HttpRoute(host), 50);
        final CloseableHttpClient client = HttpClients.custom().setConnectionManager(manager).build();
//        client.close();//关闭客户端时，连接池自动管理
    }

    @Test
    public void testMultiThread() {
        PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager();
        manager.setMaxTotal(200);
        CloseableHttpClient client = HttpClients.custom().setConnectionManager(manager).build();
        List<Map<String, String>> set = new LinkedList<>();
        for (int i = 0; i < 100; i++) {
            Map<String, String> map = new HashMap<>(3);
            map.put("http://localhost:8080/red", "pong");
            map.put("http://localhost:8080/", "<h1>welcome</h1>");
            map.put("http://localhost:8080/ping", "pong");
            set.add(map);
        }
        LinkedList<GetThread> threads = new LinkedList<>();
        for (Map<String, String> map : set) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                HttpGet get = new HttpGet(entry.getKey());
                threads.add(new GetThread(client, get, entry.getValue()));
            }
        }
        for (GetThread thread : threads) {
            thread.start();
        }
        for (GetThread thread : threads) {
            Assert.assertTrue(thread.block());
        }
    }

    /**
     * 虽然HttpClient实例是线程安全的，并且可以在多个执行线程之间共享，但强烈建议每个线程维护自己的HttpContext专用实例。
     */
    private static class GetThread extends Thread {
        private final CloseableHttpClient client;
        private final HttpContext context;
        private final HttpGet get;
        private CountDownLatch latch = new CountDownLatch(1);
        private String result;
        private String expect;

        public GetThread(CloseableHttpClient client, HttpGet get, String expect) {
            if (expect == null) {
                throw new NullPointerException();
            }
            this.client = client;
            context = HttpClientContext.create();
            this.get = get;
            this.expect = expect;
        }

        public void run() {
            try (CloseableHttpResponse response = client.execute(get, context)) {
                final HttpEntity entity = response.getEntity();
                result = EntityUtils.toString(entity);
                latch.countDown();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public boolean block() {
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return expect.equals(result);
        }
    }
}
