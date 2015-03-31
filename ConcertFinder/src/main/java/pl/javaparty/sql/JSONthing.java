package pl.javaparty.sql;

import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import pl.javaparty.enums.PHPurls;

import java.io.*;
import java.util.List;

/**
 * Created by Jakub on 2015-03-13.
 */
public class JSONthing {

    public static void makeRequest(PHPurls url, List<NameValuePair> params) {

        try {
            HttpClient mHttpClient = new DefaultHttpClient();
            HttpPost mHttpPost = new HttpPost(url.toString());
            mHttpPost.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse mHttpResponse = mHttpClient.execute(mHttpPost);

            // for JSON:
            if (mHttpResponse != null) {
                InputStream is = mHttpResponse.getEntity().getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line = null;
                try {
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("JSON", "SERVER: " + sb.toString());
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";

    public JSONObject makeHttpRequest(String url, String method,
                                      List<NameValuePair> params) {
        try {
            if (method == "POST") {
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);
                httpPost.setEntity(new UrlEncodedFormEntity(params));

                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();

            } else if (method == "GET") {
                DefaultHttpClient httpClient = new DefaultHttpClient();
                String paramString = URLEncodedUtils.format(params, "utf-8");
                url += "?" + paramString;
                HttpGet httpGet = new HttpGet(url);

                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
            Log.i("JSON", "SERVER: " + sb.toString());
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }


        return jObj;

    }


    public static JSONObject getThisShit(PHPurls url, List<NameValuePair> params) {

        String jsonString = "";
        JSONObject mJsonObject = null;
        try {
            HttpClient mHttpClient = new DefaultHttpClient();
            String paramString = URLEncodedUtils.format(params, "utf-8");

            String urlString = url.toString() + "?" + paramString;
            HttpGet httpGet = new HttpGet(urlString);

            HttpResponse httpResponse = mHttpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            InputStream mInputStream = httpEntity.getContent();
            // for JSON:
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(mInputStream, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                mInputStream.close();
                jsonString = sb.toString();
            } catch (Exception e) {
                Log.e("Buffer Error", "Error converting result " + e.toString());
            }

            // try parse the string to a JSON object
            try {
                mJsonObject = new JSONObject(jsonString);
            } catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }

            // return JSON String

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mJsonObject;
    }
}