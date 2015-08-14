package pl.javaparty.concertfinder;

import android.app.ProgressDialog;
import android.content.Context;
import pl.javaparty.enums.DialogType;

/**
 * Created by jakub on 8/14/15.
 */
public class ProgressDialogFabric {


    Context context;

    public ProgressDialogFabric(Context context) {
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
}
