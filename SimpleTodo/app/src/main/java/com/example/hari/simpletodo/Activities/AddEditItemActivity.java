package com.example.hari.simpletodo.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.hari.simpletodo.Fragments.ConfirmDeleteDialogFragment;
import com.example.hari.simpletodo.Fragments.DatePickerFragment;
import com.example.hari.simpletodo.Fragments.TimePickerFragment;
import com.example.hari.simpletodo.Models.Item;
import com.example.hari.simpletodo.R;

import java.util.Locale;

import static com.example.hari.simpletodo.Models.Item.priorityToColor;

public class AddEditItemActivity extends AppCompatActivity
        implements DatePickerFragment.DatePickerDialogListener, TimePickerFragment.TimePickerDialogListener,ConfirmDeleteDialogFragment.ConfirmDeleteDialogListener
{


    private final int REQUEST_DELETE = -2;

    private EditText etItem;
    private TextView tvCompletionDate;
    private TextView tvCompletionTime;
    private TextView tvPriorityValue;
    private RatingBar priorityRating;

    private String itemTextOriginal;
    private int position;
    private String completionDate;
    private String completionTime;
    private Item.Priority priority;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_edit_item);

        itemTextOriginal = getIntent().getStringExtra("itemText");
        position = getIntent().getIntExtra("position", -1);
        completionDate = getIntent().getStringExtra("completionDate");
        completionTime = getIntent().getStringExtra("completionTime");
        priority = ((Item.Priority) getIntent().getExtras().get("priority"));

        findControls();
        try {
            etItem.setText(itemTextOriginal);
            if(!itemTextOriginal.equals(null))
                etItem.setSelection(itemTextOriginal.length());
            tvCompletionDate.setText(completionDate);
            tvCompletionTime.setText(completionTime);
            tvPriorityValue.setText(priority.toString());
            setRatingBar(priority);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void findControls()
    {
        try
        {
            etItem = (EditText)findViewById(R.id.etItem);
            tvCompletionDate = (TextView)findViewById(R.id.tvCompletionDate);
            tvCompletionTime = (TextView)findViewById(R.id.tvCompletionTime);
            tvPriorityValue = (TextView)findViewById(R.id.tvPriorityVal);
            priorityRating = (RatingBar)findViewById(R.id.ratingBar);

            setEventListeners();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

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


        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

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
        datePickerFragment.show(getSupportFragmentManager(), "datePicker");
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
        timePickerFragment.show(getSupportFragmentManager(), "timePicker");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_edit, menu);

        return true;
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

    @Override
    public void onFinishConfirmDeleteDialog(boolean shouldDelete, int position) {
        try
        {
            if(shouldDelete)
            {
                Intent data = new Intent();
                data.putExtra("position", position);
                setResult(REQUEST_DELETE, data);
                finish();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void showDeleteConfirmationDialog(int position) {
        FragmentManager fm = getSupportFragmentManager();

        ConfirmDeleteDialogFragment alertDialog = ConfirmDeleteDialogFragment.newInstance("Delete confirmation", position);
        alertDialog.show(fm, "fragment_alert");
    }

    private void saveItem() {
        try {
            Intent data = new Intent();
            String itemText = etItem.getText().toString().trim();

            if (itemText.trim().equals(""))
                itemText = itemTextOriginal;

            data.putExtra("itemText", itemText);
            data.putExtra("position", position);
            data.putExtra("completionDate", completionDate);
            data.putExtra("completionTime", completionTime);
            data.putExtra("priority", priority);
            setResult(RESULT_OK, data);
            finish();
        } catch (Exception ex) {
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
}
