package pl.javaparty.fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import pl.javaparty.concertfinder.R;
import pl.javaparty.enums.PHPurls;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kuba on 23/02/2015.
 */
public class TabComment extends Fragment {

    ArrayList commentList;
    ProgressDialog loadingDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
        View view = inflater.inflate(R.layout.tab_fragment_comment, container, false);


        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        Button addComment = (Button) view.findViewById(R.id.add_comment);
        TextView concertInfo = (TextView) view.findViewById(R.id.comment_info);
        EditText commentField = (EditText) view.findViewById(R.id.user_comment);
        ListView commentListView = (ListView) view.findViewById(R.id.comments);
        commentList = new ArrayList<>();
        //todo ustawienie Visibility na gone do dodawania komentarzy jeśli już dodalismy komentarz

        int ID = (getArguments().getInt("ID", -1));

        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new InsertComment().execute();
            }
        });

        return view;
    }

    class InsertComment extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog = new ProgressDialog(getActivity());
            loadingDialog.setMessage("Dodawanie komentarza");
            loadingDialog.setCancelable(false);
            loadingDialog.show();
        }

        @Override
        protected String doInBackground(String... param) {

            try {
                HttpClient mHttpClient = new DefaultHttpClient();
                HttpPost mHttpPost = new HttpPost(PHPurls.insertComment.toString());

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("user_id", "2"));
                params.add(new BasicNameValuePair("concert_id", "23"));
                params.add(new BasicNameValuePair("comment", "asdasfsafas"));
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
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            loadingDialog.dismiss();

            super.onPostExecute(s);
        }
    }


}
