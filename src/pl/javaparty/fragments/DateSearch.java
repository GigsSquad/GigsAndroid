package pl.javaparty.fragments;

import java.util.Calendar;

import pl.javaparty.adapters.ConcertAdapter;
import pl.javaparty.concertfinder.R;
import pl.javaparty.sql.dbManager;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;

public class DateSearch extends Fragment {
	

	Context context;
	ListView concertList;
	ConcertAdapter adapter;
	dbManager dbm;
	Button bFrom,bTo,bSearch;
	int dF,mF,yF,dT,mT,yT;
	private String lastSearching;
	private int lastPosition;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
		View view = inflater.inflate(R.layout.tab_search_date, container, false);
		getActivity().getActionBar().setTitle("Szukaj wg miejsca");
		dbm = (dbManager) getArguments().getSerializable("dbManager");//przekazujemy dbm od mainActivity
		context = inflater.getContext();
		concertList = (ListView) view.findViewById(R.id.concertListPlace);
		bFrom = (Button) view.findViewById(R.id.bFr);
		bTo = (Button) view.findViewById(R.id.bTo);
		bSearch = (Button) view.findViewById(R.id.bSe);


		bFrom.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	Calendar mcurrentDate=Calendar.getInstance();
	            int mYear=mcurrentDate.get(Calendar.YEAR);
	            int mMonth=mcurrentDate.get(Calendar.MONTH);
	            int mDay=mcurrentDate.get(Calendar.DAY_OF_MONTH);
	            DatePickerDialog mDatePicker = new DatePickerDialog(context, new OnDateSetListener() {                  
	                public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
	                    dF = selectedday;
	                    mF = selectedmonth+1;
	                    yF = selectedyear;
	                    bFrom.setText(dF+"."+mF+"."+yF);
	                }
	            },mYear, mMonth, mDay);
	            mDatePicker.setTitle("Wybierz datê");                
	            mDatePicker.show(); 
	        }
	    });
		
		bTo.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	            DatePickerDialog mDatePicker = new DatePickerDialog(context, new OnDateSetListener() {                  
	                public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
	                    dT = selectedday;
	                    mT = selectedmonth+1;
	                    yT = selectedyear;
	                    bTo.setText(dT+"."+mT+"."+yT);
	                }
	            },yF, mF, dF);
	            mDatePicker.setTitle("Wybierz datê");                
	            mDatePicker.show(); 
	        }
	    });
		
		bSearch.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Log.i("DATE", "Iloœæ: "+dbm.getConcertsByDateRange(dF, mF, yF, dT, mT, yT).length);
				adapter = new ConcertAdapter(context, R.layout.list_row, dbm.getConcertsByDateRange(dF, mF, yF, dT, mT, yT));
				concertList.setAdapter(adapter);
				lastSearching = dF+"."+mF+"."+yF+" - "+dT+"."+mT+"."+yT;
				getActivity().getActionBar().setTitle("Szukaj: " + lastSearching);
				bFrom.setText("Wybierz datê od"); bTo.setText("Wybierz datê do");
			}
		});
		
		return view;
	}
	

}
