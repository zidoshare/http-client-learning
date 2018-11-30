package site.zido.httpclient;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * 基础实用类测试
 */
public class FundamentalsTest {
    /**
     * UriBuilder
     * 所有HTTP请求都有一个请求行，包括方法名称，请求URI和HTTP协议版本。
     * HttpClient支持开箱即用的HTTP / 1.1规范中定义的所有HTTP方法：GET，HEAD，POST，PUT，DELETE，TRACE和OPTIONS。
     * 每种方法类型都有一个特定的类：HttpGet，HttpHead，HttpPost，HttpPut，HttpDelete，HttpTrace和HttpOptions。
     * Request-URI是统一资源标识符，用于标识应用请求的资源。
     * HTTP请求URI由协议方案，主机名，可选端口，资源路径，可选查询和可选片段组成。
     */
    @Test
    public void testURIBuilder() throws URISyntaxException {
        //httpclient提供一个uriBuilder用于构建uri
        URI uri = new URIBuilder()
                .setScheme("http")
                .setHost("www.baidu.com")
                .setPath("/search")
                .setParameter("q", "httpclient")
                .setParameter("btnG", "Google Search")
                .setParameter("aq", "f")
                .setParameter("oq", "")
                .build();
        HttpGet get = new HttpGet(uri);
        Assert.assertEquals("http://www.baidu.com/search?q=httpclient&btnG=Google+Search&aq=f&oq=", get.getURI().toString());
        Assert.assertEquals("GET", get.getMethod());
        Assert.assertEquals("HTTP/1.1", get.getProtocolVersion().toString());
    }

    /**
     * BasicHttpResponse
     * HTTP响应是服务器在接收并解释请求消息后发送回客户端的消息。
     * 该消息的第一行包括协议版本，后跟数字状态代码及其相关的文本短语。
     */
    @Test
    public void testResponse() {
        final BasicHttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
        Assert.assertEquals("HTTP/1.1", response.getProtocolVersion().toString());
        Assert.assertEquals(200, response.getStatusLine().getStatusCode());
        Assert.assertEquals("OK", response.getStatusLine().getReasonPhrase());
        Assert.assertEquals("HTTP/1.1 200 OK", response.getStatusLine().toString());
    }

    /**
     * Message header
     * HTTP消息可以包含许多描述消息属性的标题，例如内容长度，内容类型等。
     * HttpClient提供了检索，添加，删除和枚举标头的方法。
     */
    @Test
    public void testResponseMessageHeader() {
        final BasicHttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
        response.addHeader("Set-Cookie", "c1=a; path=/; domain=localhost");
        response.addHeader("Set-Cookie", "c2=b; path=\"/\", c2=c; domain=localhost");
        final Header header1 = response.getFirstHeader("Set-Cookie");
        Assert.assertEquals("Set-Cookie: c1=a; path=/; domain=localhost",header1.toString());
        final Header lastHeader = response.getLastHeader("Set-Cookie");
        Assert.assertEquals("Set-Cookie: c2=b; path=\"/\", c2=c; domain=localhost",lastHeader.toString());
        final Header[] headers = response.getHeaders("Set-Cookie");
        Assert.assertEquals(2,headers.length);
    }

    /**
     * HttpEntity
     * HttpClient最重要的功能是执行HTTP方法。
     * 执行HTTP方法涉及一个或多个HTTP请求/ HTTP响应交换，通常由HttpClient内部处理。
     * 期望用户提供要执行的请求对象，并且希望HttpClient将请求发送到目标服务器返回相应的响应对象，或者如果执行不成功则抛出异常。
     * 很自然，HttpClient API的主要入口点是HttpClient接口，它定义了上述合同。
     */
    @Test
    public void testEntity() throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet("http://localhost:8080");
        CloseableHttpResponse response = client.execute(get);
        try {
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity);
            System.out.println(result);
        } finally {
            response.close();
        }
    }
}
