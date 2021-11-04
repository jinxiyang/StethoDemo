package com.yang.stethodemo;

import android.text.TextUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpMethod;
import okhttp3.internal.http.RealResponseBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.GzipSource;
import okio.Okio;
import okio.Source;

/**
 * 加解密拦截器，加密请求参数的、解密返回数据
 *
 * 1、请求的header必须传过来秘钥，cryptoKey，实际请求时此header会被移除。
 *
 * 2、 可在chrome浏览器的输入，chrome://inspect/#devices，查看网络请求network
 *
 *     2.1  stetho拦截器在前，加密拦截器在后。chrome看到的明文，实际网络请求是密文
 *          builder.addNetworkInterceptor(new StethoInterceptor());
 *          builder.addNetworkInterceptor(new EncryptionInterceptor());
 *
 *     2.2  加密拦截器在前，stetho拦截器在后。chrome看到的和实际网络请求是一样的，都是密文
 *          builder.addNetworkInterceptor(new EncryptionInterceptor());
 *          builder.addNetworkInterceptor(new StethoInterceptor());
 */
public class EncryptionInterceptor implements Interceptor {

    private static final String HEADER_NAME_CONTENT_LENGTH = "Content-Length";
    private static final String HEADER_NAME_ACCEPT_ENCODING = "Accept-Encoding";

    private static final String ACCEPT_ENCODING_GZIP_ENCODING = "gzip";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder requestBuilder = null;

        //加解密秘钥
        //秘钥不为空，我们要进行加解密
        String encryptKey = getEncryptKey(request);

        if (!TextUtils.isEmpty(encryptKey)){
            Headers headers = request.headers();
            String acceptEncoding = headers.get(HEADER_NAME_ACCEPT_ENCODING);
            if (!TextUtils.isEmpty(acceptEncoding) && !TextUtils.equals(acceptEncoding, ACCEPT_ENCODING_GZIP_ENCODING)){
                //response接收的编码类型, 支持zip和空
                requestBuilder = request.newBuilder();
                requestBuilder.removeHeader(HEADER_NAME_ACCEPT_ENCODING);
            }
        }

        //秘钥不为空，且请求body不为空
        if (!TextUtils.isEmpty(encryptKey) && HttpMethod.requiresRequestBody(request.method()) && request.body() != null){
            RequestBody requestBody = request.body();

            //读取请求体的数据
            String body = requestBodyToString(requestBody);

            //加密请求体
            String encryptedBody = encryptBody(encryptKey, body);

            //重新生成请求体
            requestBody = RequestBody.create(requestBody.contentType(), encryptedBody);
            if (requestBuilder == null){
                requestBuilder = request.newBuilder();
            }

            if ("POST".equals(request.method())){
                requestBuilder.post(requestBody);
            } else if ("PUT".equals(request.method())){
                requestBuilder.put(requestBody);
            }

            //复写Content-Length，数据变了，请求的的长度也就变了
            requestBuilder.removeHeader(HEADER_NAME_CONTENT_LENGTH);
            requestBuilder.addHeader(HEADER_NAME_CONTENT_LENGTH, String.valueOf(requestBody.contentLength()));
        }

        if (requestBuilder != null){
            //重新组装request
            request = requestBuilder.build();
        }

        Response response = chain.proceed(request);

        if (TextUtils.isEmpty(encryptKey)){
            //如果不需加解密，直接返回请求响应
            return response;
        }


        String result;
        ResponseBody responseBody = response.body();

        String contentEncoding = response.header(HEADER_NAME_ACCEPT_ENCODING);
        boolean gzipEncoding = ACCEPT_ENCODING_GZIP_ENCODING.equals(contentEncoding);

        //根据请求响应的格式，读取数据
        if (gzipEncoding){
            //读取zip流数据
            GzipSource gzipSource = new GzipSource(responseBody.source());
            BufferedSource bufferedSource = Okio.buffer(gzipSource);
            result = bufferedSource.readUtf8();
        } else {
            result = responseBody.string();
        }

        //解密数据，这里认为是对称加解密
        result = decryptBody(encryptKey, result);

        //重新组装response
        String contentType = response.header("Content-Type");
        long contentLength = result.length();//数据长度发生了变化
        ByteArrayInputStream inputStream = new ByteArrayInputStream(result.getBytes());
        Source source = Okio.source(inputStream);
        return response.newBuilder()
                .body(new RealResponseBody(contentType, contentLength, Okio.buffer(source)))
                .build();
    }

    //读取请求体的数据
    private String requestBodyToString(RequestBody requestBody){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BufferedSink bufferedSink = Okio.buffer(Okio.sink(outputStream));
        try {
            requestBody.writeTo(bufferedSink);
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try {
                bufferedSink.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        byte[] bytes = outputStream.toByteArray();
        return new String(bytes);
    }


    /**
     * 加解密秘钥
     */
    private String getEncryptKey(Request request){
        //TODO
        return null;
    }

    /**
     * 加密请求体
     */
    private String encryptBody(String encryptKey, String body){
        //TODO
        return null;
    }

    /**
     * 解密数据
     */
    private String decryptBody(String decryptKey, String body){
        //TODO
        return null;
    }
}