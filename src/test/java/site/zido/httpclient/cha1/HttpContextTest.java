package site.zido.httpclient.cha1;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.junit.Test;

import java.io.IOException;

public class HttpContextTest {
    @Test
    public void testRunWithContext() {
        HttpContext context = new BasicHttpContext();
        CloseableHttpClient client = HttpClients.createDefault();
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(1000)
                .setConnectTimeout(1000)
                .build();
        HttpGet get1 = new HttpGet("http://localhost:8080");
        get1.setConfig(requestConfig);
        try (CloseableHttpResponse response1 = client.execute(get1, context)) {
            HttpEntity entity1 = response1.getEntity();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
