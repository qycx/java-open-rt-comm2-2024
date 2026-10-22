package com.qycx;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

public class ConfirmationDialog extends DialogFragment {
    private static final String TAG = ConfirmationDialog.class.getSimpleName();
    private static Fragment mParent;

    class PositiveListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
        }
    }

    class NegativeListener implements DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int which) {
            Activity activity = mParent.getActivity();
            if (activity != null) {
                Log.d(TAG, "NegativeListener.OnClick,activity.finish");
                //
                activity.finish();
            }

        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mParent = getParentFragment();
        return new AlertDialog.Builder(getActivity())
                .setMessage(R.string.request_permission)
                .setPositiveButton(R.string.ok, new PositiveListener())
                .setNegativeButton(R.string.cancel, new NegativeListener())
                .create();
    }
}
