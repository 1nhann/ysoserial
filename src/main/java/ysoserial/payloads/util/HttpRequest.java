package ysoserial.payloads.util;

import okhttp3.*;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    public String url;
    public OkHttpClient client = new OkHttpClient();
    public Request.Builder requestBuilder = new Request.Builder();
    public HttpUrl.Builder urlBuilder;
    public FormBody.Builder formBodyBuilder = null;

    public HttpRequest(String url){
        this.url = url;
        urlBuilder = HttpUrl.parse(url).newBuilder();
    }

    public HttpRequest addPostData(byte[] raw){
        formBodyBuilder = null;
        RequestBody body = RequestBody.create(raw);
        requestBuilder.post(body);
        return this;
    }
    public HttpRequest addPostData(Map<String,String> data){
        if(formBodyBuilder == null){
            formBodyBuilder = new FormBody.Builder();
        }
        for (Map.Entry<String,String> entry : data.entrySet()){
            formBodyBuilder.addEncoded(p(entry.getKey()),p(entry.getValue()));
        }
        RequestBody body = formBodyBuilder.build();
        return this;
    }
    public HttpRequest addPostData(String key,String value){
        if(formBodyBuilder == null){
            formBodyBuilder = new FormBody.Builder();
        }
        formBodyBuilder.addEncoded(p(key),p(value));
        return this;
    }

    public HttpRequest addHeaders(HashMap<String,String> headers){
        for (Map.Entry<String,String> entry : headers.entrySet()){
            requestBuilder.addHeader(entry.getKey(),entry.getValue());
        }
        return this;
    }
    public HttpRequest addHeader(String key , String value){
        requestBuilder.addHeader(key,value);
        return this;
    }

    public HttpRequest addParams(HashMap<String,String> params){
        for (Map.Entry<String,String> entry : params.entrySet()){
            urlBuilder.addEncodedQueryParameter(p(entry.getKey()),p(entry.getValue()));
        }
        return this;
    }

    public HttpRequest addParam(String key , String value){
        urlBuilder.addEncodedQueryParameter(p(key),p(value));
        return this;
    }

    private Request buildRequest(){
        requestBuilder.url(urlBuilder.build());
        if(formBodyBuilder != null){
            requestBuilder.post(formBodyBuilder.build());
        }
        return requestBuilder.build();
    }

    public byte[] send(){
        Request request = buildRequest();
        try {
            Response response = client.newCall(request).execute();
            InputStream s = response.body().byteStream();
            return ReadWrite.readAllBytesFromInputStream(s);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
    public static byte[] post(String url , byte[] raw){
        HttpRequest r = new HttpRequest(url);
        r.addPostData(raw);
        return r.send();
    }

    public static byte[] post(String url , Map<String,String> data){
        HttpRequest r = new HttpRequest(url);
        r.addPostData(data);
        return r.send();
    }

    public static byte[] get(String url){
        HttpRequest r = new HttpRequest(url);
        return r.send();
    }

    public static byte[] get(String url, HashMap<String,String> params){
        HttpRequest r = new HttpRequest(url);
        r.addParams(params);
        return r.send();
    }
    private String urldecode(String data) throws Exception{
        return Encoder.urldecode(data);
    }
    private String urlencode(String data) throws Exception{
        return Encoder.urlencode(data);
    }
    private String p(String data){
        String s = null;
        try{
            if(data.contains("%")){
                s = urlencode(urldecode(data));
            }else {
                s = urlencode(data);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return s;
    }
}
