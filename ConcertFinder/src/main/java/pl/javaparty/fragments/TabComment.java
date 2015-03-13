package pl.javaparty.fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import pl.javaparty.concertfinder.R;
import pl.javaparty.prefs.Prefs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Kuba on 23/02/2015.
 */
public class TabComment extends Fragment {

	private Button addComment;
	private TextView concertInfo;
	private EditText commentField;
	private ListView commentListView;
	private int ID;
	private ProgressBar pBar;
	ArrayList commentList;

    ListAdapter adapter;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
		View view = inflater.inflate(R.layout.tab_fragment_comment, container, false);

		addComment = (Button) view.findViewById(R.id.add_comment);
		concertInfo = (TextView) view.findViewById(R.id.comment_info);
		commentField = (EditText) view.findViewById(R.id.user_comment);
		commentListView = (ListView) view.findViewById(R.id.comments);
      //  pBar = (ProgressBar) view.findViewById(R.id.comments_progress);
		commentList = new ArrayList<>();
		//todo ustawienie Visibility na gone do dodawania komentarzy jeśli już dodalismy komentarz

		ID = (getArguments().getInt("ID", -1));

		addComment.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean commentAdded = false;
				try {
					commentAdded = insertComment(Prefs.getUserID(getActivity()), ID, commentField.getText().toString());
				} catch (JSONException e) {
					e.printStackTrace();
				} finally {
					if (commentAdded) {
						Toast.makeText(getActivity(), "Komentarz został dodany", Toast.LENGTH_LONG).show();
						commentField.setVisibility(View.GONE);
						addComment.setVisibility(View.GONE);
					}
				}
			}
		});

		new getComments().execute();

		return view;
	}

	private boolean insertComment(int userID, int concertID, String comment) throws JSONException {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://37.187.52.160/comments.php");
		JSONObject json = new JSONObject();

		try {
			httppost.setEntity(new ByteArrayEntity(json.toString().getBytes("UTF-8")));
			json.put("userID", userID);
			json.put("concertID", concertID);
			json.put("comment", comment);
			//data i godzina z serwera żeby niezaginać czasoprzestrzeni

			JSONArray postjson = new JSONArray();
			postjson.put(json);

			// Post the data:
			httppost.setHeader("json", json.toString());
			httppost.getParams().setParameter("jsonpost", postjson);

			// Execute HTTP Post Request
			System.out.print(json);
			HttpResponse response = httpclient.execute(httppost);

			// for JSON:
			if (response != null) {
				InputStream is = response.getEntity().getContent();

				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				StringBuilder sb = new StringBuilder();

				String line = null;
				try {
					while ((line = reader.readLine()) != null) {
						sb.append(line + "\n");
					}
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				} finally {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
						return false;
					}
				}

				Log.i("JSON", "Co wypluł serwer: " + sb.toString());
			}

		} catch (ClientProtocolException cpe) {
			cpe.printStackTrace();
			return false;
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return false;
		}
		Log.i("JSON", "Dodano komentarz");
		return true;
	}

	private class getComments extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
           pBar.setVisibility(View.VISIBLE);

		}

		@Override
		protected Void doInBackground(Void... arg0) {

            adapter = new SimpleAdapter(getActivity(), commentList, android.R.layout.simple_list_item_1, new String[] { "author", "comment" }, new int[] {});
            commentListView.setAdapter(adapter);
            return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
          //  if (adapter != null) {

           pBar.setVisibility(View.GONE);

		}

	}

}
