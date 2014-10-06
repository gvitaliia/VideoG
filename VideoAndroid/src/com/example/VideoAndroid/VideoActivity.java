package com.example.VideoAndroid;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;

public class VideoActivity extends Activity {

    private ImageView imgView;
    private String URL = "http://videogorillas.com/img/icons-s25c0e1c348.png";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        imgView = (ImageView) findViewById(R.id.satellite);
        imgView.setTag(URL);
        new ImageDownloader().execute(imgView);
    }

    private class ImageDownloader extends AsyncTask<ImageView, Void, Bitmap> {
        ImageView imageView = null;
        @Override
        protected Bitmap doInBackground(ImageView... imageViews) {
            this.imageView = imageViews[0];
            return downloadBitmap((String)imageView.getTag());
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }

        private Bitmap downloadBitmap(String url) {
            final DefaultHttpClient client = new DefaultHttpClient();

            final HttpGet getRequest = new HttpGet(url);
            try {

                HttpResponse response = client.execute(getRequest);

                final int statusCode = response.getStatusLine().getStatusCode();

                if (statusCode != HttpStatus.SC_OK) {
                    Log.w("ImageDownloader", "Error " + statusCode +
                            " while retrieving bitmap from " + url);
                    return null;

                }

                final HttpEntity entity = response.getEntity();
                if (entity != null) {
                    InputStream inputStream = null;
                    try {
                        inputStream = entity.getContent();
                        final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                        return bitmap;
                    } finally {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                        entity.consumeContent();
                    }
                }
            } catch (Exception e) {
                getRequest.abort();
                Log.e("ImageDownloader", "Something went wrong while" +
                        " retrieving bitmap from " + url + e.toString());
            }

            return null;
        }
    }
}
