package pl.javaparty.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import pl.javaparty.concertfinder.R;


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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args){
        View view = inflater.inflate(R.layout.tab_fragment_comment,container,false);

        addComment = (Button) view.findViewById(R.id.add_comment);
        clearField = (Button) view.findViewById(R.id.clear_field);
        infoRead = (TextView) view.findViewById(R.id.comment_info);
        commentField = (EditText) view.findViewById(R.id.user_comment);
        commentList = (ListView) view.findViewById(R.id.comments);

        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    /*
                    tu będzie wrzucanie komentarza na serwer
                     */
            }
        });

        clearField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    commentField.setText("");
            }
        });

        try{
            //tu będzie ściaganie komentarzy
            String[] comments = {"WYJEBANE KONCERCIDŁO","Udany występ","Oddajcie hajsy"}; //jakaś fajna metoda
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,comments);
            commentList.setAdapter(adapter);

        }catch(Exception e){
            Log.i("Comment","Coś jebło");
        }

        return view;
    }


}
