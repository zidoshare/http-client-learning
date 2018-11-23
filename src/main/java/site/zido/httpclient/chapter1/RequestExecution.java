package site.zido.httpclient.chapter1;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class RequestExecution {
    /**
     * HttpClient最重要的功能是执行HTTP方法。
     * 执行HTTP方法涉及一个或多个HTTP请求/ HTTP响应交换，通常由HttpClient内部处理。
     * 期望用户提供要执行的请求对象，并且希望HttpClient将请求发送到目标服务器返回相应的响应对象，或者如果执行不成功则抛出异常。
     * 很自然，HttpClient API的主要入口点是HttpClient接口，它定义了上述合同。
     */
    public static void main(String[] args) throws IOException {
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
