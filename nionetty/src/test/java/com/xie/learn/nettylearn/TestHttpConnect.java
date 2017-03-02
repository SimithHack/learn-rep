package com.xie.learn.nettylearn;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import org.apache.http.Header;
import org.apache.http.HttpEntity;import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

/**
 * Created by xiefu on 2017/2/23 0023.
 */
public class TestHttpConnect {
    @Test
    public void testHttpConnect() throws URISyntaxException, IOException {
        // Prepare the HTTP request.
        String host = "http://192.168.1.11:20300/login?username=test5&password=123456";
        HttpPost httpPost = new HttpPost(host);
        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse response = httpClient.execute(httpPost);

        Header[] headers = response.getAllHeaders();
        for(int i=0;i<headers.length;i++){
            Header header = headers[i];
            System.out.println(header.getName()+":"+header.getValue());
        }
    }
    @Test
    public void test_getAccessToken() throws IOException {
        // Prepare the HTTP request.
        String host = "http://localhost:20300/access_token?X-Token=f365aa7a-b057-4091-a2cd-23a77f754588";
        HttpGet httpGet = new HttpGet(host);
        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse response = httpClient.execute(httpGet);

        HttpEntity entity = response.getEntity();
        ByteOutputStream outputStream = new ByteOutputStream();
        InputStream inputStream = entity.getContent();
        byte[] buffer = new byte[1024];
        int position=0;
        while ((position=inputStream.read(buffer))>0){
            outputStream.write(buffer,0,position);
        }
        outputStream.flush();
        outputStream.close();
        inputStream.close();
        System.out.println(new String(outputStream.getBytes()));
    }
}
