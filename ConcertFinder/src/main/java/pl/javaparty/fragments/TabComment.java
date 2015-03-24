package pl.javaparty.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import pl.javaparty.concertfinder.R;
import pl.javaparty.enums.PHPurls;
import pl.javaparty.prefs.Prefs;
import pl.javaparty.sql.JSONthing;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kuba on 23/02/2015.
 */
public class TabComment extends Fragment {

    Button addComment;
    ProgressDialog loadingDialog;
    ListView commentListView;
    EditText commentField;
    TextView concertInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
        View view = inflater.inflate(R.layout.tab_fragment_comment, container, false);


        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        loadingDialog = new ProgressDialog(getActivity());
        loadingDialog.setCancelable(false);

        addComment = (Button) view.findViewById(R.id.add_comment);
        concertInfo = (TextView) view.findViewById(R.id.comment_info);
        commentField = (EditText) view.findViewById(R.id.user_comment);
        commentListView = (ListView) view.findViewById(R.id.comments);
        //todo ustawienie Visibility na gone do dodawania komentarzy jeśli już dodalismy komentarz

        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnline())
                    new InsertComment().execute();
                else
                    Toast.makeText(getActivity(), getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }

    @Override
    public void onResume() {
        if (isOnline())
            new DownloadComments().execute();
        super.onResume();
    }

    class InsertComment extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog.setMessage(getString(R.string.add_comment_progress));
            loadingDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("user_id", Prefs.getUserID(getActivity()) + ""));
            params.add(new BasicNameValuePair("concert_id", getArguments().getInt("ID", -1) + ""));
            params.add(new BasicNameValuePair("comment", commentField.getText().toString()));
            JSONthing.makeRequest(PHPurls.insertComment, params); //TIGHT and ELEGANT
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            loadingDialog.dismiss();

            addComment.setEnabled(false); //blokujemy możliwość wysłania kolejnych komentarzy
            commentField.setEnabled(false); //TODO: nie wiem czy dawać do na GONE czy blokować żeby móc np później edytować komentarz

            new DownloadComments().execute(); //po dodaniu komentarza niech pobierze od nowa komentarze zeby wyświetlić nasz
            super.onPostExecute(s);
        }
    }

    class DownloadComments extends AsyncTask<String, Void, String> {

        ArrayAdapter arrayAdapter = null;
        ArrayList commentArrayList = null;
        JSONthing jsoNthing;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog.setMessage(getString(R.string.download_comments_progress));
            loadingDialog.show();
            commentArrayList = new ArrayList<>();
            jsoNthing = new JSONthing();
        }

        @Override
        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("concert_id", getArguments().getInt("ID", -1) + ""));

            Log.d("JSON", "Wysyłane id koncertu: " + getArguments().getInt("ID", -1));

            JSONObject mJsonObject = jsoNthing.makeHttpRequest(PHPurls.getComments.toString(), "GET", params);

            try {
                int success = mJsonObject.getInt("success");
                if (success == 1) {
                    JSONArray mJsonArray = mJsonObject.getJSONArray("comments");
                    for (int i = 0; i < mJsonArray.length(); i++) {
                        JSONObject JSONcomment = mJsonArray.getJSONObject(i);
                        String author = JSONcomment.getString("user_id");
                        String comment = JSONcomment.getString("comment");

                        commentArrayList.add(comment + " ~" + author);
                    }
                    arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, commentArrayList);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (isAdded()) {
                commentListView.setAdapter(arrayAdapter);

                if (!commentArrayList.isEmpty())
                    concertInfo.setVisibility(View.GONE);

                loadingDialog.dismiss();
            }
            super.onPostExecute(s);
        }
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


}
