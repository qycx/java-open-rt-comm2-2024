package com.qycx;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

public class ErrorDialog extends DialogFragment {
    private static final String TAG = ErrorDialog.class.getSimpleName();
    private static final String ARG_MESSAGE = "message";

    private class PositiveListener implements DialogInterface.OnClickListener {

        ErrorDialog er;

        public PositiveListener(ErrorDialog e) {
            er = e;
        }

        public void onClick(DialogInterface dialog, int which) {
            Log.d(TAG, "PositiveListener.er.activity.finish");
            //
            er.getActivity().finish();
        }

    }

    //
    public static ErrorDialog newInstance(String message) {
        ErrorDialog dialog = new ErrorDialog();
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);
        dialog.setArguments(args);
        return dialog;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        var alert = new AlertDialog.Builder(getActivity());
        alert.setMessage(getArguments().getString(ARG_MESSAGE));
        alert.setPositiveButton(R.string.ok, new PositiveListener(this));
        return alert.show();
    }
}

