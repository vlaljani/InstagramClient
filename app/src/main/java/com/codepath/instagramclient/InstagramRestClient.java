package com.codepath.instagramclient;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by vibhalaljani on 2/6/15.
 */
public class InstagramRestClient {
    private static final String BASE_URL = "https://api.instagram.com/v1/";
    private static final String CLIENT_ID = "a2f340751f2b4452a0b5bd499d00abd8";
    private static final String CLIENT_ID_EXT = "?client_id=" + CLIENT_ID;

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl + CLIENT_ID_EXT;
    }
}

