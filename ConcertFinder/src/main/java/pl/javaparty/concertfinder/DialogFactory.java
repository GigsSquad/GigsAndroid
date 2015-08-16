package pl.javaparty.concertfinder;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import pl.javaparty.enums.DialogType;
import pl.javaparty.prefs.Prefs;
import pl.javaparty.utils.UtilsObject;

/**
 * Created by jakub on 8/14/15.
 */
public class DialogFactory {


    Context context;

    public DialogFactory(Context context) {
        this.context = context;
    }

    public ProgressDialog produceDialog(DialogType type) {
        ProgressDialog dialog = null;

        if (type.equals(DialogType.progress)) {
            dialog = new ProgressDialog(context);
            dialog.setMessage(context.getString(R.string.database_update));
            dialog.setProgress(0);
            dialog.setMax(1);
            dialog.setCancelable(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        } else if (type.equals(DialogType.simple)) {
            dialog = new ProgressDialog(context);
            dialog.setCancelable(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        }
        return dialog;
    }

    public AlertDialog produceAlertDialog(DialogType type) {
        AlertDialog alertDialog = null;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setCancelable(true);

        if (type.equals(DialogType.download)) {
            alertDialogBuilder.setTitle("Czy chcesz teraz pobrać koncerty z bazy?");
            alertDialogBuilder.setMessage("Może zająć nam to kilka minut.");
            alertDialogBuilder.setPositiveButton("Pobierz", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                    //jeśli nie ma pobranyuch jeszcze współrzędnych dla swojego miasta, czyli w Prefs LAT i LON są na -1 to łączy sie z mapami żeby pobrać
                    if (Prefs.getInstance(context).getLon().equals("-1") || Prefs.getInstance(context).getLat().equals("-1")) {
                        new LatLngConnector(context).execute();
                    } else { // w przeciwnym wypadku od razu przechodzimy do pobierania kocnertów
                        new ConcertDownloader(context).execute();
                    }
                }
            });

            alertDialogBuilder.setNegativeButton("Później", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }

            });
            alertDialog = alertDialogBuilder.create();
        } else if (type.equals(DialogType.location)) {
            final AutoCompleteTextView input = new AutoCompleteTextView(context);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.COUNTIES, android.R.layout.simple_dropdown_item_1line);
            input.setAdapter(adapter);

            Prefs.getInstance(context).setStart(false); // żeby już nie pytało o miasto, infomacja o tym że apk już kiedyś była uruchamiana

            alertDialogBuilder.setTitle("Wprowadź swoje miasto");
            alertDialogBuilder.setMessage("Potrzebujemy nazwy Twojej miejscowości, aby dobrze posortować koncerty :)");
            alertDialogBuilder.setView(input);
            alertDialogBuilder.setPositiveButton("Zapisz", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    if (!input.getText().toString().isEmpty()) {
                        Prefs.getInstance(context).setCity(input.getText().toString());
                        Toast.makeText(context, "Dziękujemy, " + input.getText().toString(), Toast.LENGTH_SHORT).show();
                    }
                    //jeśli online to od razu łączymy sie z mapami i pobieramy latlng
                    if (UtilsObject.isOnline(context))
                        new LatLngConnector(context).execute();
                }
            });
            alertDialogBuilder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
        }
        return alertDialog;
    }
}
