package site.zido.httpclient.cha1;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.StandardHttpRequestRetryHandler;
import org.apache.http.protocol.HttpContext;
import org.junit.Test;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;

/**
 * HTTP 协议处理能够抛出两种类型的异常：如果发生了 I/O 错误，抛出 java.io.IOException 异常，比如连接超时或者
 * 连接重置，如果发生了 HTTP 错误，抛出 HttpException ，比如违反了 HTTP 协议。通常认为 I/O 错误无关紧要并且
 * 可以修复，而认为 HTTP 协议错误是致命的并且不能自动修复。请注意 HttpClient 的实现是像 ClientProtocolException
 * 一样重复抛出 HttpException， ClientProtocolException 是 java.io.IOException 的子类。这保证 HttpClient
 * 的用户在一个 catch 块中既能处理 I/O 错误，也能处理违反协议的错误。
 * <p>
 * 了解 HTTP 协议并不适合所有类型的应用很重要。HTTP 是一个简单的面向请求/响应式的协议，一开始就设计成支持检索
 * 静态和动态生成的内容。它从未想过去支持事务型操作。例如，如果 HTTP 服务器成功接收和处理了请求，它将会按照服务端的
 * 协议，生成响应，返回状态码给客户。即便客户端由于读入超时、取消请求或者系统崩溃而没能收到响应的全部内容，服务端也
 * 不会尝试回滚事务。如果客户端要重试同样的请求，服务器将不可避免地结束执行多次同样的事务。有时候这将导致应用数据错误
 * 或者应用状态不一致。
 * <p>
 * 即使 HTTP 从没被设计成支持事务处理的协议，如果条件允许，它还是能用作重要任务的传输协议。确保 HTTP 传输层安全，
 * 系统就要确保传输层的 HTTP 方法的幂等性。
 */
public class ExceptionHandlerTest {
    /**
     * HttpClient 默认会尝试自动解决 I/O 异常。默认的自动解决机制仅限于解决一些已知是安全的异常。
     * <p>
     * HttpClient 不会尝试解决逻辑错误或者 HTTP 协议错误（源自 HttpException 类的异常）
     * HttpClient 会自动重试幂等性的方法。
     * HttpClient 会自动重试那些由于传输异常导致的方法，尽管 HTTP 请求仍然被发送到目标服务器上（比如请求还没有
     * 完全发送到服务器）
     * Request retry handler
     */
    @Test
    public void testCustomHandler() {
        HttpRequestRetryHandler handler = new HttpRequestRetryHandler() {

            /**
             * 异常处理
             * @param exception 发生的异常
             * @param executionCount 异常发生次数
             * @param context context
             * @return 是否已解决
             */
            @Override
            public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
                /**
                 * 最多尝试五次，不再重新尝试
                 *
                 * @implNote 此处突然想到chrome自动重试的处理，好像是每次请求都会进行延时，也就是说不是不间断的进行尝试（仅想法，此处没有实现
                 */
                if (executionCount > 5) {
                    return false;
                }
                //线程终端
                if (exception instanceof InterruptedIOException) {
                    // Timeout
                    return false;
                }
                //url问题
                if (exception instanceof UnknownHostException) {
                    // Unknown host
                    return false;
                }
                // 连接超时。(idea提示：always false.查看源码，此异常继承自InterruptedIOException。
                // 官方文档把代码放在这里显然有问题，因为肯定执行不了。需要把这个异常判断放在InterruptedIOException前面
                // 官方github库关闭了issue，没法提。官网提issue麻烦，只能做笔记了
                if (exception instanceof ConnectTimeoutException) {
                    //Connection refused
                    return false;
                }
                if (exception instanceof SSLException) {
                    // SSL handshake exception
                    return false;
                }

                HttpClientContext clientContext = HttpClientContext.adapt(context);
                HttpRequest request = clientContext.getRequest();
                boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
                if (!idempotent) {
                    // Retry if the request is considered idempotent
                    return true;
                }
                return false;
            }
        };
        CloseableHttpClient httpclient = HttpClients.custom()
                .setRetryHandler(handler)
                .build();
    }

    /**
     * 如果单独配置，最好使用standardHttpRequestRetryHandler
     */
    @Test
    public void testStandardRetryHandler(){
        CloseableHttpClient client = HttpClients.custom().setRetryHandler(new StandardHttpRequestRetryHandler()).build();
    }
}
