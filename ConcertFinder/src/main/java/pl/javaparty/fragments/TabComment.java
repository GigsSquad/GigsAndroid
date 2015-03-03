package pl.javaparty.fragments;

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

/**
 * Created by Kuba on 23/02/2015.
 */
public class TabComment extends Fragment {

	private Button addComment;
	private Button clearField;
	// private TextView infoAdd;
	private TextView infoRead;
	private EditText commentField;
	private ListView commentList;
	private int ID;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
		View view = inflater.inflate(R.layout.tab_fragment_comment, container, false);

		//todo ustawienie Visibility na gone do dodawania komentarzy jeśli już dodalismy komentarz

		addComment = (Button) view.findViewById(R.id.add_comment);
		clearField = (Button) view.findViewById(R.id.clear_field);
		infoRead = (TextView) view.findViewById(R.id.comment_info);
		commentField = (EditText) view.findViewById(R.id.user_comment);
		commentList = (ListView) view.findViewById(R.id.comments);
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
						clearField.setVisibility(View.GONE);
					}
				}
			}
		});

		clearField.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				commentField.setText("");
			}
		});

		try {
			//tu będzie ściaganie komentarzy
			String[] comments = { "WYJEBANE KONCERCIDŁO", "Udany występ", "Oddajcie hajsy" }; //jakaś fajna metoda
			ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, comments);
			commentList.setAdapter(adapter);

		} catch (Exception e) {
			Log.i("Comment", "Coś jebło");
		}

		return view;
	}

	private void getComments() {
	}

	private boolean insertComment(int userID, int concertID, String comment) throws JSONException {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://www.javaparty.com.pl/addcomment.php");
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

}
