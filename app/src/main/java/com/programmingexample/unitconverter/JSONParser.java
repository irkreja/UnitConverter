package com.programmingexample.unitconverter;


import android.util.Log;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.CloseableHttpClient;
import cz.msebera.android.httpclient.impl.client.HttpClients;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class JSONParser {
    static InputStream sInputStream = null;
    static JSONObject sReturnJsonObject = null;
    static String sRawJsonString = "";

    public JSONParser() {}

    public JSONObject getJSONFromUrl(String url) {

    //attempt to get response from server
        try {
            //DefaultHttpClient httpClient = new DefaultHttpClient();
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            sInputStream = httpEntity.getContent();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //read stream into string-builder
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    sInputStream, "iso-8859-1"), 8);
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }sInputStream.close();
            sRawJsonString = stringBuilder.toString();
        } catch (Exception e) {
            Log.e("ErrorReadingFromBuffer:" + e.toString(), this.getClass().getSimpleName());
        }
        try {
            sReturnJsonObject = new JSONObject(sRawJsonString);
        } catch (JSONException e) {
            Log.e("Parser", "Error when parsing data " + e.toString());
        }

        //return json object
        return sReturnJsonObject;
    }
}