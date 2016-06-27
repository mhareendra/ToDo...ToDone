package com.example.hari.simpletodo.Fragments;


import android.annotation.TargetApi;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.hari.simpletodo.Models.Item;
import com.example.hari.simpletodo.R;

import java.util.Locale;

import static com.example.hari.simpletodo.Models.Item.priorityToColor;


public class EditItemFragment extends DialogFragment
        implements DatePickerFragment.DatePickerDialogListener, TimePickerFragment.TimePickerDialogListener,ConfirmDeleteDialogFragment.ConfirmDeleteDialogListener
{

    private EditText etItem;
    private TextView tvCompletionDate;
    private TextView tvCompletionTime;
    private TextView tvPriorityValue;
    private RatingBar priorityRating;
    private Toolbar toolbar;

    private String completionDate;
    private String completionTime;
    private Item.Priority priority;

    private int position;
    private String itemTextOriginal;

    public EditItemFragment()
    {

    }

    public static EditItemFragment newInstance(Item item, int position) {
        EditItemFragment frag = new EditItemFragment();
        Bundle args = new Bundle();
        args.putString("itemText", item.item);
        args.putInt("position", position);
        args.putString("completionDate", item.completionDate);
        args.putString("completionTime", item.completionTime);
        args.putString("priority", item.priority.toString());

        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_item, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        itemTextOriginal = getArguments().getString("itemText");
        position = getArguments().getInt("position", -1);
        completionDate = getArguments().getString("completionDate");
        completionTime = getArguments().getString("completionTime");
        priority = Item.toPriority(getArguments().getString("priority"));

        findControls(view);
        try {
            etItem.setText(itemTextOriginal);
            if(!itemTextOriginal.equals(null))
                etItem.setSelection(itemTextOriginal.length());
            tvCompletionDate.setText(completionDate);
            tvCompletionTime.setText(completionTime);
            tvPriorityValue.setText(priority.toString());
            setRatingBar(priority);
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }


    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    private void findControls(View view)
    {
        try
        {
            etItem = (EditText)view.findViewById(R.id.etItem);
            tvCompletionDate = (TextView)view.findViewById(R.id.tvCompletionDate);
            tvCompletionTime = (TextView)view.findViewById(R.id.tvCompletionTime);
            tvPriorityValue = (TextView)view.findViewById(R.id.tvPriorityVal);
            priorityRating = (RatingBar)view.findViewById(R.id.ratingBar);
            toolbar = (Toolbar) view.findViewById(R.id.edit_fragment_toolbar);
            toolbar.setTitle("Edit Item");
            setEventListeners();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setEventListeners()
    {
        try
        {
            priorityRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    priority = Item.intToPriority(((int) rating) - 1);
                    setPriorityValue(priority);
                }
            });

            tvCompletionDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setDatePickerListener();
                }
            });


            tvCompletionTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setTimePickerListener();
                }
            });

            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    int id = item.getItemId();

                    //noinspection SimplifiableIfStatement
                    if (id == R.id.delete_option)
                    {
                        deleteItem();
                        return true;
                    }
                    else if(id == R.id.save_option)
                    {
                        saveItem();
                        return true;
                    }
                    else
                        return false;
                }
            });

            toolbar.inflateMenu(R.menu.menu_add_edit);

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    private void setPriorityValue(Item.Priority priority) {
        tvPriorityValue.setText(priority.toString());
        //set color to text
        tvPriorityValue.setTextColor(priorityToColor(priority));
    }

    private void setRatingBar(Item.Priority priority)
    {
        int rating = Item.priorityToInt(priority);
        priorityRating.setRating(rating + 1);
    }


    private void setDatePickerListener()
    {
        if(completionDate.equals("") | !completionDate.contains("/"))
            return;
        String[] splitDate = completionDate.split("/");
        int month = Integer.parseInt(splitDate[0]);
        int day = Integer.parseInt(splitDate[1]);
        int year = Integer.parseInt(splitDate[2]);

        DialogFragment datePickerFragment = DatePickerFragment.newInstance(day, month - 1, year);
        datePickerFragment.setTargetFragment(this, 0);
        datePickerFragment.show(getFragmentManager(), "datePicker");
    }


    private void setTimePickerListener()
    {
        if(completionTime.equals("") | !completionTime.contains(":"))
            return;
        String[] splitTime = completionTime.split(":");
        int hour = Integer.parseInt(splitTime[0]);
        String[] secondSplitString = (splitTime[1].split(" "));
        int minute = Integer.parseInt(secondSplitString[0]);
        String amPm = secondSplitString[1];
        DialogFragment timePickerFragment = TimePickerFragment.newInstance(minute, hour, amPm);
        timePickerFragment.setTargetFragment(this, 0);
       // timePickerFragment.show(getSupportFragmentManager(), "timePicker");
        timePickerFragment.show(getFragmentManager(), "timePicker");
    }


    @Override
    public void onFinishDatePickDialog(int year, int monthOfYear, int dayOfMonth)
    {
        try
        {
            String date = String.format(Locale.US, "%02d/%02d/%d", monthOfYear, dayOfMonth, year);
            tvCompletionDate.setText(date);
            completionDate = date;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onFinishTimePickDialog(int hourOfDay, int minute, String amPm) {
        try
        {
            String time = String.format(Locale.US, "%d:%02d %s", hourOfDay, minute, amPm.toUpperCase(Locale.US));
            tvCompletionTime.setText(time);
            completionTime = time;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }


    @Override
    public void onFinishConfirmDeleteDialog(boolean shouldDelete, int position) {
        try
        {
            if(shouldDelete)
            {
                EditItemFragmentListener listener = (EditItemFragmentListener) getActivity();
                listener.onFinishEditItemFragment(null, position, false, true);
                dismiss();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_add_edit, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.delete_option)
        {
            deleteItem();
            return true;
        }
        else if(id == R.id.save_option)
        {
            saveItem();
            return true;
        }
        else
            return super.onOptionsItemSelected(item);
    }


    private void deleteItem()
    {
        try
        {
            showDeleteConfirmationDialog(position);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void showDeleteConfirmationDialog(int position) {
        FragmentManager fm = getFragmentManager();

        ConfirmDeleteDialogFragment alertDialog = ConfirmDeleteDialogFragment.newInstance("Delete confirmation", position);
        alertDialog.setTargetFragment(this, 0);
        alertDialog.show(fm, "fragment_alert");
    }


    private void saveItem() {
        try {
            String itemText = etItem.getText().toString().trim();

            if (itemText.trim().equals(""))
                itemText = itemTextOriginal;
            Item newItem = new Item();
            newItem.initialize(itemText, priority, completionDate, completionTime, -1);

            EditItemFragmentListener listener = (EditItemFragmentListener) getActivity();
            listener.onFinishEditItemFragment(newItem, position, true, false);

            dismiss();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        // Get existing layout params for the window
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        // Assign window properties to fill the parent
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        // Call super onResume after sizing
        super.onResume();
    }

    public interface EditItemFragmentListener
    {
        void onFinishEditItemFragment(Item item, int position, boolean shouldSave, boolean shouldDelete);
    }

}
