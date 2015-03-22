package pl.javaparty.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import pl.javaparty.concertfinder.R;

public class AboutFragment extends Fragment {


    private static final int CLICK_NR = 4;
    int clicks = CLICK_NR;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
		View view = inflater.inflate(R.layout.fragment_about, container, false);

        getActivity().getActionBar().setTitle(getString(R.string.information));
        TextView authors = (TextView)view.findViewById(R.id.aboutTextView);
        authors.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(clicks == 0)
                {
                    Intent intent = new Intent(getActivity(), EsterEgg.class);
                    startActivity(intent);
                    clicks = CLICK_NR;
                }
                else
                    clicks--;
            }
        });
		return view;
	}

    public static class EsterEgg extends Activity
    {
        @Override
        protected void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_team);
            setTheme(android.R.style.Theme_Material_NoActionBar);
        }
    }
}