package site.zido.httpclient;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.junit.Test;

/**
 * HttpClient 自动处理所有类型的重定向，除了某些 HTTP 规则因需要用户干预而明令禁止的类型。参见其它重定向（状态码：303），
 * HTTP 规则要求将 POST 和 PUT 请求被转化为 GET 请求。可以使用自定义的重定向策略，利用 HTTP 规则来解除
 * POST 方法自动重定向的限制。
 */
public class RedirectTest {

    /**
     * HttpClient 一般能在执行过程中重写请求消息，每个默认的 HTTP/1.0 和 HTTP/1.1 一般用相关联的请求 URI。同样的，
     * 原始请求可能会被多次重定向到其它地方。最终解译出的 HTTP 绝对地址可以用原始请求和上下文来构建。协助方法 URIUtils#resolve
     * 可以用来构建解译的绝对 URI，从而生成最终的请求。这个方法包含了在重定向请求或者原始请求中的最后一个分片校验器。
     */
    @Test
    public void testRedirect(){
        CloseableHttpClient client = HttpClients.custom().setRedirectStrategy(new LaxRedirectStrategy()).build();
    }

}
