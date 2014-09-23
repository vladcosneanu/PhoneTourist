package com.avallon.phonetourist.requests;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.avallon.phonetourist.items.LandmarkReview;

public class RequestReviewImage extends AsyncTask<String, Integer, JSONObject> {

    private boolean done = false;
    private LandmarkReview review;
    private String authorId;

    public RequestReviewImage(LandmarkReview review, String authorId) {
        this.review = review;
        this.authorId = authorId;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        byte[] result = null;
        JSONObject json = null;

        String url = "https://www.googleapis.com/plus/v1/people/" + authorId
                + "?fields=image%2Furl&key=AIzaSyClU64iccv6LVTB0IkccBvL3OKPSrh9jPo";

        try {
            Log.e("request", url);
            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(url);
            HttpResponse response = client.execute(get);
            StatusLine statusLine = response.getStatusLine();
            Log.d("status", statusLine.toString());
            if (statusLine.getStatusCode() == HttpURLConnection.HTTP_OK) {
                result = EntityUtils.toByteArray(response.getEntity());
                publishProgress(result.length);
                String str = new String(result, "UTF-8");
                json = new JSONObject(str);
            }

            if (json != null) {
                done = true;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
        }
        return json;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        Log.d("size", values[0].toString());
    }

    @Override
    protected void onPostExecute(JSONObject json) {
        super.onPostExecute(json);

        if (done) {
            review.onReviewImageUrlReceived(json, authorId);
        }
    }
}
