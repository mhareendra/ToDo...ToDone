package com.example.hari.simpletodo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.hari.simpletodo.Fragments.DatePickerFragment;
import com.example.hari.simpletodo.Fragments.TimePickerFragment;
import com.example.hari.simpletodo.Models.Item;

import java.util.Locale;

import static com.example.hari.simpletodo.Models.Item.priorityToColor;

public class AddEditItemActivity extends AppCompatActivity
        implements DatePickerFragment.DatePickerDialogListener, TimePickerFragment.TimePickerDialogListener
{


    private EditText etItem;
    private TextView tvCompletionDate;
    private TextView tvCompletionTime;
    private TextView tvPriorityValue;
    private RatingBar priorityRating;
    private Button btnSave;

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
            btnSave = (Button)findViewById(R.id.btnSaveItem);


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

            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SaveItem();
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


    private void SaveItem()
    {
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
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void setPriorityValue(Item.Priority priority)
    {
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
