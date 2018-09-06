package com.plbear.iweight.http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.plbear.iweight.http.Bean.User;
import com.plbear.iweight.http.Bean.Weight;
import com.plbear.iweight.utils.LogInfo;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ServiceConfigurationError;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpPost {
    private static final String TAG = "HttpPost";
    private static HttpPost sInstance = null;
    public static final String SERVER_URL = "http://111.230.131.164:8080/iweight";
    public static final String URL_CREATE = SERVER_URL + "/create";
    public static final String URL_LOGIN = SERVER_URL + "/login";
    public static final String URL_ADDWEIGHT = SERVER_URL + "/addWeight";
    public static final String URL_DELETEWEIGHT = SERVER_URL + "/deleteWeight";
    public static final String URL_QUERYALL = SERVER_URL + "/queryAll/";
    private static OkHttpClient mClient = null;
    private static HashMap<String, List<Cookie>> cookieStore = new HashMap<>();


    public static HttpPost getInstance() {
        if(sInstance == null){
            synchronized (HttpPost.class){
                if(sInstance == null){
                    sInstance = new HttpPost();
                }
            }
        }
        return sInstance;
    }

    private HttpPost() {
        mClient = new OkHttpClient.Builder()
                .cookieJar(new CookieJar() {
                    @Override
                    public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
                        cookieStore.put(httpUrl.host(), list);
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl httpUrl) {
                        List<Cookie> cookies = cookieStore.get(httpUrl.host());
                        return cookies != null ? cookies : new ArrayList<Cookie>();
                    }
                }).build();
    }

    public boolean create(User user) {
        FormBody body = new FormBody.Builder()
                .add("name", user.getName())
                .add("passwd", user.getPasswd())
                .build();
        Request request = new Request.Builder()
                .url(URL_CREATE)
                .post(body)
                .build();
        Response response = null;
        try {
            response = mClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                Gson gson = new Gson();
                Type type = new TypeToken<HashMap<String, String>>() {
                }.getType();
                HashMap<String, String> responseMap = gson.fromJson(responseBody, type);
                String result = responseMap.get("msg");
                if (result != null && result.equals("success")) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean login(User user) {
        FormBody body = new FormBody.Builder()
                .add("name", user.getName())
                .add("passwd", user.getPasswd())
                .build();
        Request request = new Request.Builder()
                .url(URL_LOGIN)
                .post(body)
                .build();
        Response response = null;
        try {
            response = mClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                Gson gson = new Gson();
                Type type = new TypeToken<HashMap<String, String>>() {
                }.getType();
                LogInfo.e(TAG, "yanlog login response:" + responseBody);
                HashMap<String, String> responseMap = gson.fromJson(responseBody, type);
                String result = responseMap.get("msg");
                if (result != null && result.equals("success")) {
                    user.setUserid(responseMap.get("userid"));
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean addWeight(Weight weight) {
        FormBody body = new FormBody.Builder()
                .add("userid", weight.getUserid())
                .add("time", weight.getTime())
                .add("value", weight.getValue())
                .build();
        Request request = new Request.Builder()
                .url(URL_ADDWEIGHT)
                .post(body)
                .build();
        Response response = null;
        try {
            response = mClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                Gson gson = new Gson();
                Type type = new TypeToken<HashMap<String, String>>() {
                }.getType();
                HashMap<String, String> responseMap = gson.fromJson(responseBody, type);
                String result = responseMap.get("msg");
                if (result != null && result.equals("success")) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteWeight(Weight weight) {
        FormBody body = new FormBody.Builder()
                .add("userid", weight.getUserid())
                .add("time", weight.getTime())
                .add("value", weight.getValue())
                .build();
        Request request = new Request.Builder()
                .url(URL_DELETEWEIGHT)
                .post(body)
                .build();
        Response response = null;
        try {
            response = mClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                Gson gson = new Gson();
                Type type = new TypeToken<HashMap<String, String>>() {
                }.getType();
                HashMap<String, String> responseMap = gson.fromJson(responseBody, type);
                String result = responseMap.get("msg");
                if (result != null && result.equals("success")) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Weight> queryAll(String id) {
        Request request = new Request.Builder()
                .url(URL_QUERYALL + id)
                .build();
        Response response = null;
        try {
            response = mClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                Gson gson = new Gson();
                Type type = new TypeToken<List<Weight>>() {
                }.getType();
                List<Weight> responseMap = gson.fromJson(responseBody, type);
                return responseMap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
