package pl.javaparty.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.os.Bundle;
import pl.javaparty.concertfinder.R;
import pl.javaparty.items.Agencies;

public class FilterDialogFragment extends DialogFragment {
    boolean[] checked;
    CharSequence[] agenciesNames;
    FilterDialogListener mListener;

    public interface FilterDialogListener {
        public void onDialogPositiveClick(boolean[] checked);

        public void onDialogNegativeClick(boolean[] checked);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        checked = getArguments().getBooleanArray("CHECKED");

        agenciesNames = new CharSequence[checked.length];
        int i = 0;
        for (Agencies a : Agencies.values()) {
            if (a.fragmentNumber >= 700)
                agenciesNames[i++] = a.toString;
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.agency_filtr) + ":")
                .setMultiChoiceItems(agenciesNames, checked, new OnMultiChoiceClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        checked[which] = isChecked;
                    }
                })
                .setPositiveButton(getString(R.string.ok), new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mListener != null)
                            mListener.onDialogPositiveClick(checked);
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new OnClickListener()//TODO zmienic xD
                {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mListener != null)
                            mListener.onDialogNegativeClick(null);
                    }
                });

        return builder.create();
    }

    public void setFilterDialogListener(FilterDialogListener f) {
        mListener = f;
    }
}
