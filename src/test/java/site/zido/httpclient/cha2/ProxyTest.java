package site.zido.httpclient.cha2;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.apache.http.protocol.HttpContext;
import org.junit.Test;

import java.net.ProxySelector;

public class ProxyTest {
    /**
     *
     */
    @Test
    public void testSimpleProxy() {
        HttpHost proxy = new HttpHost("someproxy", 8080);
        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
        final CloseableHttpClient client = HttpClients.custom().setRoutePlanner(routePlanner).build();
    }

    @Test
    public void testUseJreDefaultProxy() {
        SystemDefaultRoutePlanner routePlanner = new SystemDefaultRoutePlanner(ProxySelector.getDefault());
        final CloseableHttpClient client = HttpClients.custom().setRoutePlanner(routePlanner).build();
    }

    @Test
    public void testCustomRoutePlanner(){
        HttpRoutePlanner routePlanner = new HttpRoutePlanner() {
            @Override
            public HttpRoute determineRoute(HttpHost target, HttpRequest httpRequest, HttpContext httpContext) throws HttpException {
                return new HttpRoute(target, null,  new HttpHost("someproxy", 8080),
                        "https".equalsIgnoreCase(target.getSchemeName()));
            }
        };
        final CloseableHttpClient client = HttpClients.custom().setRoutePlanner(routePlanner).build();
    }
}
