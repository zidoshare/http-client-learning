package site.zido.httpclient.cha3;

import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.util.PublicSuffixMatcher;
import org.apache.http.conn.util.PublicSuffixMatcherLoader;
import org.apache.http.cookie.ClientCookie;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.impl.cookie.DefaultCookieSpecProvider;
import org.apache.http.impl.cookie.RFC6265CookieSpecProvider;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Test;
import site.zido.httpclient.ServerUtils;

import java.io.IOException;

public class CookieTest {
    @Test
    public void testCookie() {
        BasicClientCookie cookie = new BasicClientCookie("name", "value");
        cookie.setDomain("zido.site");
        cookie.setPath("/");
        cookie.setAttribute(ClientCookie.PATH_ATTR, "/");
        cookie.setAttribute(ClientCookie.DOMAIN_ATTR, "zido.site");
        System.out.println(cookie.getAttribute(ClientCookie.PATH_ATTR));
        System.out.println(cookie.getPath());
    }

    /**
     * 为了实现自定义cookie策略，应该创建CookieSpec接口的自定义实现，创建CookieSpecProvider实现来创建和初始化自定义规范的实例，并使用HttpClient注册工厂。
     * 一旦注册了自定义规范，就可以像标准cookie规范一样激活它。
     */
    @Test
    public void testChooseCookiePolicy() {
        RequestConfig globalConfig = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.DEFAULT)
                .build();
        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultRequestConfig(globalConfig)
                .setUserAgent("")
                .build();
        RequestConfig localConfig = RequestConfig.copy(globalConfig)
                .setCookieSpec(CookieSpecs.STANDARD_STRICT)
                .build();
        HttpGet httpGet = new HttpGet("/");
        httpGet.setConfig(localConfig);
    }

    @Test
    public void testCustomCookiePolicy() {
        PublicSuffixMatcher publicSuffixMatcher = PublicSuffixMatcherLoader.getDefault();
        Registry<CookieSpecProvider> r = RegistryBuilder.<CookieSpecProvider>create()
                .register(CookieSpecs.DEFAULT,
                        new DefaultCookieSpecProvider(publicSuffixMatcher))
                .register(CookieSpecs.STANDARD,
                        new RFC6265CookieSpecProvider(publicSuffixMatcher))
                .register("easy", new CookieSpecProvider() {
                    @Override
                    public CookieSpec create(HttpContext context) {
                        return null;
                    }
                })
                .build();

        RequestConfig requestConfig = RequestConfig.custom()
                .setCookieSpec("easy")
                .build();

        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCookieSpecRegistry(r)
                .setDefaultRequestConfig(requestConfig)
                .build();
    }

    /**
     * HttpClient可以与实现CookieStore接口的持久性cookie存储的任何物理表示一起使用。
     * 名为BasicCookieStore的默认CookieStore实现是一个由java.util.ArrayList支持的简单实现。
     * 当容器对象被垃圾收集时，存储在BasicClientCookie对象中的Cookie将丢失。
     * 如有必要，用户可以提供更复杂的实现。
     */
    @Test
    public void testCookieStore() {
        BasicCookieStore store = new BasicCookieStore();
        BasicClientCookie cookie = new BasicClientCookie("name", "value");
        cookie.setDomain("zido.site");
        cookie.setPath("/");
        store.addCookie(cookie);
        CloseableHttpClient client = HttpClients.custom().setDefaultCookieStore(store).build();
    }

    /**
     * 在HTTP请求执行过程中，HttpClient将以下与状态管理相关的对象添加到执行上下文中：
     * 查找实例表示实际的cookie规范注册表。
     * CookieSpec实例表示实际的cookie规范。
     * CookieOrigin实例表示原始服务器的实际详细信息。
     * CookieStore实例表示实际的cookie存储。
     * 在本地上下文中设置的此属性的值优先于默认值。
     * 本地HttpContext对象可用于在请求执行之前自定义HTTP状态管理上下文，或在请求执行后检查其状态。
     * 还可以使用单独的执行上下文来实现每个用户（或每个线程）状态管理。
     * 在本地上下文中定义的cookie规范注册表和cookie存储将优先于在HTTP客户端级别设置的默认值
     */
    @Test
    public void testStoreManagement() {
        ServerUtils.runTestServer();
        try {
            CloseableHttpClient client = HttpClients.custom()
                    .setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build())
                    .build();
            BasicCookieStore store = new BasicCookieStore();

            HttpClientContext context = HttpClientContext.create();
            context.setCookieStore(store);

            HttpGet get = new HttpGet("http://localhost:8080/login");
            try (final CloseableHttpResponse ignored = client.execute(get, context)) {
                HttpGet userGet = new HttpGet("http://localhost:8080/user/get");
                try (final CloseableHttpResponse response2 = client.execute(userGet, context)) {
                    Assert.assertEquals("ok", EntityUtils.toString(response2.getEntity()));
                }
                try (final CloseableHttpResponse response2 = client.execute(userGet)) {
                    Assert.assertEquals("no", EntityUtils.toString(response2.getEntity()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } finally {
            ServerUtils.stopServer();
        }
    }
}
