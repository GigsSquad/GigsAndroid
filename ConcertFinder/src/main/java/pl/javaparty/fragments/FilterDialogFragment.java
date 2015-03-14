package pl.javaparty.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.os.Bundle;
import pl.javaparty.concertfinder.R;
import pl.javaparty.items.Concert.AgencyName;

public class FilterDialogFragment extends DialogFragment {
    boolean[] checked;
    FilterDialogListener mListener;

    public interface FilterDialogListener {
        public void onDialogPositiveClick(boolean[] checked);

        public void onDialogNegativeClick(boolean[] checked);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        checked = getArguments().getBooleanArray("CHECKED");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.agency_filtr) + ":")
                .setMultiChoiceItems(getArguments().getCharSequenceArray("AGENCIES"), checked, new OnMultiChoiceClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        checked[which] = isChecked;
                    }
                })
                .setPositiveButton(getString(R.string.ok), new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onDialogPositiveClick(checked);
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new OnClickListener()//TODO zmienic xD
                {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onDialogNegativeClick(null);
                    }
                });

        return builder.create();
    }

    private CharSequence[] getAgencies() {
        AgencyName[] agencies = AgencyName.values();
        CharSequence[] returned = new CharSequence[agencies.length];
        int i = 0;
        for (AgencyName a : agencies) {
            returned[i++] = a.name();
        }
        return returned;
    }

    public void setFilterDialogListener(FilterDialogListener f) {
        mListener = f;
    }
}
