package site.zido.httpclinet.cha2;

import org.apache.http.HttpHost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import site.zido.httpclient.ServerUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ConnectionSocketFactoryTest {
    @Before
    public void startServer() {
        ServerUtils.runTestServer();
    }

    @After
    public void stopServer() {
        ServerUtils.stopServer();
    }

    /**
     * HTTP连接在内部使用java.net.Socket对象来处理通过线路传输数据。
     * 但是，它们依赖于ConnectionSocketFactory接口来创建，初始化和连接套接字。
     * 这使HttpClient的用户能够在运行时提供特定于应用程序的套接字初始化代码。
     * PlainConnectionSocketFactory是创建和初始化普通（未加密）套接字的默认工厂。
     * 创建套接字的过程以及将其连接到主机的过程是分离的，以便在连接操作中阻塞时可以关闭套接字。
     */
    @Test
    public void testCreateFactory() throws IOException {
        HttpClientContext context = HttpClientContext.create();
        PlainConnectionSocketFactory sf = PlainConnectionSocketFactory.getSocketFactory();
        Socket socket = sf.createSocket(context);
        int timeout = 1000;
        HttpHost target = new HttpHost("localhost:8080");
        InetSocketAddress remoteAddress = new InetSocketAddress(InetAddress.getByAddress(new byte[]{127, 0, 0, 1}), 8080);
        sf.connectSocket(timeout, socket, target, remoteAddress, null, context);
        socket.close();
    }
}
