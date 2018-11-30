package site.zido.httpclient;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * HTTP 协议截获器是实现了 HTTP 协议特殊方面的程序。通常认为协议截获器作用在接收到的消息的特殊的头部或
 * 一组相关联的头部中，或者是用特殊的头部或一组相关的头部构成将要发送的消息。协议截获器可以操作封装了消息透明的
 * 内容实体，实现压缩/解压缩就是一个不错的例子。通常这是通过装饰模式来完成的，用包含实体的类来装饰原始的实体。
 * 几个协议截获器能够组合形成一个逻辑单元。
 * <p>
 * 协议截获器可以通过共享信息来协同工作，比如通过 HTTP 运行上下文共享处理状态。协议截获器能够用 HTTP 上下文来给
 * 一个或几个连续的请求存储状态信息。
 * <p>
 * 协议截获器必须是线程安全的。类似 servlets，协议截获器不应该使用实例变量，除非这些变量的访问是同步的。
 */
public class InterceptorTest {
    @Test
    public void testAddInterceptor() {
        ServerUtils.runTestServer();
        CloseableHttpClient client = HttpClients.custom().addInterceptorLast(new HttpRequestInterceptor() {
            private AtomicInteger now = new AtomicInteger();

            @Override
            public void process(HttpRequest httpRequest, HttpContext httpContext) throws HttpException, IOException {
                AtomicInteger count = (AtomicInteger) httpContext.getAttribute("count");
                httpRequest.addHeader("count", Integer.toString(count.getAndIncrement()));
                now.getAndIncrement();
                Assert.assertEquals(now.intValue(), count.intValue());
            }
        }).build();
        AtomicInteger count = new AtomicInteger();
        HttpClientContext context = HttpClientContext.create();
        context.setAttribute("count", count);
        HttpGet get = new HttpGet("http://localhost:8080/");
        for (int i = 0; i < 10; i++) {
            try (CloseableHttpResponse response = client.execute(get, context)) {
                System.out.println(i);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    @After
    public void closeServer(){
        ServerUtils.stopServer();
    }
}
