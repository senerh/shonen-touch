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
    public static final int RESULT_NEGATIVE = 555;

    // Arguments received
    private static final String KEY_TITLE_RES_ID = "titleResId";
    private static final String KEY_MESSAGE_RES_ID = "messageResId";
    private static final String KEY_TITLE_STRING = "title";
    private static final String KEY_MESSAGE_STRING = "message";
    private static final String KEY_MODAL = "modal";
    private static final String KEY_HAS_CANCEL_BUTTON = "hasCancelButton";
    private static final String KEY_HAS_NEUTRAL_BUTTON = "hasNeutralButton";
    private static final String KEY_NEUTRAL_BUTTON_TEXT_RES_ID = "neutralButtonTextResId";
    private static final String KEY_POSITIVE_BUTTON_TEXT_RES_ID = "positiveButtonTextResId";
    private static final String KEY_CANCEL_BUTTON_TEXT_RES_ID = "cancelButtonTextResId";

    public static AlertDialogFragment newInstance(int titleResId, int messageResId, Intent intent, boolean modal) {
        mIntent = intent;
        final AlertDialogFragment fragment = new AlertDialogFragment();
        final Bundle arguments = new Bundle();
        arguments.putInt(KEY_TITLE_RES_ID, titleResId);
        arguments.putInt(KEY_MESSAGE_RES_ID, messageResId);
        arguments.putBoolean(KEY_MODAL, modal);
        fragment.setArguments(arguments);
        return fragment;
    }

    public static AlertDialogFragment newInstance(int titleResId, int messageResId, Intent intent, boolean modal, boolean hasCancelButton) {
        mIntent = intent;
        final AlertDialogFragment fragment = new AlertDialogFragment();
        final Bundle arguments = new Bundle();
        arguments.putInt(KEY_TITLE_RES_ID, titleResId);
        arguments.putInt(KEY_MESSAGE_RES_ID, messageResId);
        arguments.putBoolean(KEY_MODAL, modal);
        arguments.putBoolean(KEY_HAS_CANCEL_BUTTON, hasCancelButton);
        fragment.setArguments(arguments);
        return fragment;
    }

    public static AlertDialogFragment newInstance(String title, String message, Intent intent, boolean modal, boolean hasCancelButton) {
        mIntent = intent;
        final AlertDialogFragment fragment = new AlertDialogFragment();
        final Bundle arguments = new Bundle();
        arguments.putString(KEY_TITLE_STRING, title);
        arguments.putString(KEY_MESSAGE_STRING, message);
        arguments.putBoolean(KEY_MODAL, modal);
        arguments.putBoolean(KEY_HAS_CANCEL_BUTTON, hasCancelButton);
        fragment.setArguments(arguments);
        return fragment;
    }

    public static AlertDialogFragment newInstance(String title, String message, Intent intent, boolean modal, boolean hasCancelButton, boolean hasNeutralButton,
                                                  int neutralButtonTextResId, int positiveButtonTextResId, int cancelButtonTextResId) {
        mIntent = intent;
        final AlertDialogFragment fragment = new AlertDialogFragment();
        final Bundle arguments = new Bundle();
        arguments.putString(KEY_TITLE_STRING, title);
        arguments.putString(KEY_MESSAGE_STRING, message);
        arguments.putBoolean(KEY_MODAL, modal);
        arguments.putBoolean(KEY_HAS_CANCEL_BUTTON, hasCancelButton);
        arguments.putBoolean(KEY_HAS_NEUTRAL_BUTTON, hasNeutralButton);
        arguments.putInt(KEY_NEUTRAL_BUTTON_TEXT_RES_ID, neutralButtonTextResId);
        arguments.putInt(KEY_POSITIVE_BUTTON_TEXT_RES_ID, positiveButtonTextResId);
        arguments.putInt(KEY_CANCEL_BUTTON_TEXT_RES_ID, cancelButtonTextResId);
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
//        if (arguments.getBoolean(KEY_MODAL)) {
//            setCancelable(false);
//        }
        if (arguments.getInt(KEY_TITLE_RES_ID) != 0) {
            builder.setTitle(arguments.getInt(KEY_TITLE_RES_ID));
        } else if (arguments.getString(KEY_TITLE_STRING) != null && !"".equals(arguments.getString(KEY_TITLE_STRING))) {
            builder.setTitle(arguments.getString(KEY_TITLE_STRING));
        }
        if (arguments.getInt(KEY_MESSAGE_RES_ID) != 0) {
            builder.setMessage(arguments.getInt(KEY_MESSAGE_RES_ID));
        } else if (arguments.getString(KEY_MESSAGE_STRING) != null && !"".equals(arguments.getString(KEY_MESSAGE_STRING))) {
            builder.setMessage(arguments.getString(KEY_MESSAGE_STRING));
        }
        if (arguments.getInt(KEY_POSITIVE_BUTTON_TEXT_RES_ID) != 0) {
            builder.setPositiveButton(arguments.getInt(KEY_POSITIVE_BUTTON_TEXT_RES_ID), this);
        } else {
            builder.setPositiveButton(android.R.string.ok, this);
        }
        if (arguments.getBoolean(KEY_HAS_CANCEL_BUTTON)) {
            if (arguments.getInt(KEY_CANCEL_BUTTON_TEXT_RES_ID) != 0) {
                builder.setNegativeButton(arguments.getInt(KEY_CANCEL_BUTTON_TEXT_RES_ID), this);
            } else {
                builder.setNegativeButton(android.R.string.cancel, this);
            }
        }
        if (arguments.getBoolean(KEY_HAS_NEUTRAL_BUTTON) && arguments.getInt(KEY_NEUTRAL_BUTTON_TEXT_RES_ID) != -1) {
            builder.setNeutralButton(arguments.getInt(KEY_NEUTRAL_BUTTON_TEXT_RES_ID), this);
        }

        Dialog d = builder.create();
        if (!arguments.getBoolean(KEY_MODAL)) {
            d.setCanceledOnTouchOutside(true);
        }

        return d;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        Fragment targetFragment = getTargetFragment();
        if (targetFragment != null) {
            if (which == DialogInterface.BUTTON_POSITIVE) {
                targetFragment.onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, mIntent);
            } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                targetFragment.onActivityResult(getTargetRequestCode(), RESULT_NEGATIVE, mIntent);
            } else if (which == DialogInterface.BUTTON_NEUTRAL) {
                targetFragment.onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, mIntent);
            }
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }
}
