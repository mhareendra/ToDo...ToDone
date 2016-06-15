package com.example.hari.simpletodo.Fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.hari.simpletodo.R;
// ...

public class EditItemDialogFragment extends DialogFragment implements View.OnClickListener {

    private EditText mEditText;
    private Button saveButton;

    public EditItemDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static EditItemDialogFragment newInstance(String title, String item, int position) {
        EditItemDialogFragment frag = new EditItemDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("item", item);
        args.putInt("position", position);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onClick(View v) {
        // Return input text back to activity through the implemented listener
        EditItemDialogListener listener = (EditItemDialogListener) getActivity();
        int position = getArguments().getInt("position");
        listener.onFinishEditDialog(mEditText.getText().toString(), position);
        // Close the dialog and return back to the parent activity
        dismiss();
    }


    // 1. Defines the listener interface with a method passing back data result.
    public interface EditItemDialogListener {
        void onFinishEditDialog(String editedText, int position);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_edit_item, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        mEditText = (EditText) view.findViewById(R.id.etEditItem);

        saveButton = (Button) view.findViewById(R.id.btnSaveEdit);

        saveButton.setOnClickListener(this);

        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Edit Item");
        String itemText = getArguments().getString("item");
        getDialog().setTitle(title);
        mEditText.setText(itemText);
        // Show soft keyboard automatically and request focus to field
        mEditText.requestFocus();
        mEditText.setSelection(mEditText.getText().length());
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

}