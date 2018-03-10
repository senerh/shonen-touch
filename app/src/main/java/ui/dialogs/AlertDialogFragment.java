package ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;

public class AlertDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {

    // Intent argument optionally received and sent back with some completion
    private static Intent mIntent;

    public static final String TAG = AlertDialogFragment.class.getSimpleName();

    // Arguments received
    private static final String KEY_TITLE_RES_ID = "title";
    private static final String KEY_MESSAGE_RES_ID = "message";
    private static final String KEY_NOT_CANCELABLE = "cancelable";
    private static final String KEY_HAS_CANCEL_BUTTON = "hasCancelButton";

    public static AlertDialogFragment newInstance(int titleResId, int messageResId, Intent intent, boolean cancelable) {
        mIntent = intent;
        final AlertDialogFragment fragment = new AlertDialogFragment();
        final Bundle arguments = new Bundle();
        arguments.putInt(KEY_TITLE_RES_ID, titleResId);
        arguments.putInt(KEY_MESSAGE_RES_ID, messageResId);
        arguments.putBoolean(KEY_NOT_CANCELABLE, cancelable);
        fragment.setArguments(arguments);
        return fragment;
    }

    public static AlertDialogFragment newInstance(int titleResId, int messageResId, Intent intent, boolean cancelable, boolean hasCancelButton) {
        mIntent = intent;
        final AlertDialogFragment fragment = new AlertDialogFragment();
        final Bundle arguments = new Bundle();
        arguments.putInt(KEY_TITLE_RES_ID, titleResId);
        arguments.putInt(KEY_MESSAGE_RES_ID, messageResId);
        arguments.putBoolean(KEY_NOT_CANCELABLE, cancelable);
        arguments.putBoolean(KEY_HAS_CANCEL_BUTTON, hasCancelButton);
        fragment.setArguments(arguments);
        return fragment;
    }

    public static AlertDialogFragment newInstance(int titleResId, int messageResId, Intent intent) {
        mIntent = intent;
        final AlertDialogFragment fragment = new AlertDialogFragment();
        final Bundle arguments = new Bundle();
        arguments.putInt(KEY_TITLE_RES_ID, titleResId);
        arguments.putInt(KEY_MESSAGE_RES_ID, messageResId);
        fragment.setArguments(arguments);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Bundle arguments = getArguments();
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        if (arguments.getBoolean(KEY_NOT_CANCELABLE)) {
            setCancelable(false);
        }
        if (arguments.getInt(KEY_TITLE_RES_ID) != 0) {
            builder.setTitle(arguments.getInt(KEY_TITLE_RES_ID));
        }
        if (arguments.getInt(KEY_MESSAGE_RES_ID) != 0) {
            builder.setMessage(arguments.getInt(KEY_MESSAGE_RES_ID));
        }
        builder.setPositiveButton(android.R.string.ok, this);
        if (arguments.getBoolean(KEY_HAS_CANCEL_BUTTON)) {
            builder.setNegativeButton(android.R.string.cancel, this);
        }

        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        Fragment targetFragment = getTargetFragment();
        if (targetFragment != null) {
            if (which == DialogInterface.BUTTON_POSITIVE) {
                targetFragment.onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, mIntent);
            } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                targetFragment.onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, mIntent);
            }
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }
}
