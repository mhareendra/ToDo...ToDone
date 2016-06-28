package com.example.hari.simpletodo.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class ConfirmDeleteDialogFragment extends DialogFragment {
    public ConfirmDeleteDialogFragment() {
        // Empty constructor required for DialogFragment
    }


    public static ConfirmDeleteDialogFragment newInstance(String title, int position) {
        ConfirmDeleteDialogFragment frag = new ConfirmDeleteDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putInt("position", position);
        frag.setArguments(args);
        return frag;
    }

    // 1. Defines the listener interface with a method passing back data result.
    public interface ConfirmDeleteDialogListener {
        void onFinishConfirmDeleteDialog(boolean shouldDelete, int position);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(title);

        final int position = getArguments().getInt("position");

        String dialogMessage = "Are you sure you want to delete this item?";
        if(position == -1)
            dialogMessage = "Are you sure you want to delete all items?";
        else if (position == -2)
            dialogMessage = "Are you sure you want to delete the selected item(s)?";

        alertDialogBuilder.setMessage(dialogMessage);
        alertDialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // on success
                ConfirmDeleteDialogListener listener = (ConfirmDeleteDialogListener) getTargetFragment();

                if(listener == null)
                    listener = (ConfirmDeleteDialogListener) getActivity();
                listener.onFinishConfirmDeleteDialog(true, position);

            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ConfirmDeleteDialogListener listener = (ConfirmDeleteDialogListener) getTargetFragment();
                if(listener == null)
                    listener = (ConfirmDeleteDialogListener) getActivity();
                listener.onFinishConfirmDeleteDialog(false, position);
                dialog.dismiss();
            }
        });

        return alertDialogBuilder.create();
    }
}