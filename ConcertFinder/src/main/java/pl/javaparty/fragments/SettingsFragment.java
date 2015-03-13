package pl.javaparty.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;
import pl.javaparty.concertfinder.MainActivity;
import pl.javaparty.concertfinder.R;
import pl.javaparty.imageloader.FileExplorer;
import pl.javaparty.prefs.Prefs;
import pl.javaparty.sql.dbManager;

import java.util.Arrays;

public class SettingsFragment extends Fragment {

	AutoCompleteTextView citySearchBox;
	Button saveButton, clearButton;
	Context context;
	ArrayAdapter<CharSequence> adapter;
	static dbManager dbm;

	private static final String TAG = "Facebook";
	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		uiHelper = new UiLifecycleHelper(getActivity(), callback);
		uiHelper.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
		View view = inflater.inflate(R.layout.fragment_settings, container, false);
		context = inflater.getContext();
		getActivity().getActionBar().setTitle("Preferencje");
		dbm = MainActivity.getDBManager();// przekazujemy dbm od mainActivity
		//citySearchBox = (AutoCompleteTextView) view.findViewById(R.id.cityTextView);
		saveButton = (Button) view.findViewById(R.id.saveSettingsButton);
		clearButton = (Button) view.findViewById(R.id.clearFilesButton);

		adapter = ArrayAdapter.createFromResource(getActivity(), R.array.COUNTIES, android.R.layout.simple_dropdown_item_1line);
		citySearchBox.setAdapter(adapter);

		citySearchBox.setThreshold(1);

		if (Prefs.getCity(getActivity()) != null)
			citySearchBox.setText(Prefs.getCity(getActivity()));

		saveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			Prefs.setCity(getActivity(), citySearchBox.getText().toString());
				Log.i("SETTINGS", "Zapisano");
				Log.i("SETTINGS", "Miasto: " + citySearchBox.getText().toString());
				Toast.makeText(getActivity(), "Zapisano!", Toast.LENGTH_SHORT).show();
			}

		});

		clearButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DialogFragment dialog = new ClearDialog();
				dialog.show(getActivity().getFragmentManager(), "CLEAR");
			}
		});

		LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
		authButton.setFragment(this);
		authButton.setReadPermissions(Arrays.asList("user_location", "user_birthday", "user_likes"));

		return view;
	}

	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		if (state.isOpened()) {
			Log.i(TAG, "Logged in...");
		} else if (state.isClosed()) {
			Log.i(TAG, "Logged out...");
		}
	}

	public static class ClearDialog extends DialogFragment {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("Czy na pewno chcesz wyczyścić pamięć?")
					.setMessage("Wszystkie obrazki zespołów, oraz cała baza danych zostanie usunięta!")
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							//dbm.deleteDB(context); Czemu tak? :O
							FileExplorer f = new FileExplorer(getActivity());
							f.clear();
							Log.i("SETTINGS", "Usunięto obrazki z dysku");
							dbm.deleteBase();
							Log.i("SETTINGS", "Wyczyszczono bazę");
							MainActivity.updateCounters();
							Prefs.setLastID(getActivity(), -1);
							Toast.makeText(getActivity(), "Wyczyszczono pamięć!", Toast.LENGTH_SHORT).show();
							Prefs.setLastID(getActivity(), -1);
						}
					})
					.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							//puste :(
						}
					});
			return builder.create();
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		Session session = Session.getActiveSession();

		if (session != null &&
				(session.isOpened() || session.isClosed())) {
			onSessionStateChange(session, session.getState(), null);
		}

		uiHelper.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}
}