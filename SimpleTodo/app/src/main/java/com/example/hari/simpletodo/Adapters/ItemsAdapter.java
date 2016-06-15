package com.example.hari.simpletodo.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.hari.simpletodo.Models.Item;
import com.example.hari.simpletodo.R;

import java.util.ArrayList;

/**
 * Created by Hari on 6/11/2016.
 */
public class ItemsAdapter extends ArrayAdapter<Item> {

    private TextView tvItem;
    private final int MAX_ITEM_TEXT_DISPLAY_LENGTH = 10;


    public ItemsAdapter(Context context, ArrayList<Item> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        //Get the item corresponding to the current row (of a ListView)
        Item CurrentItem = getItem(position);
        if(convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
        }

        // Find the controls
        tvItem = (TextView)convertView.findViewById(R.id.tvItem);
        TextView tvPriority = (TextView)convertView.findViewById(R.id.tvPriority);
        TextView tvDate = (TextView)convertView.findViewById(R.id.tvDate);
        TextView tvTime = (TextView)convertView.findViewById(R.id.tvTime);

        //Populate the controls
        setItemTextView(CurrentItem.item);
        tvPriority.setText(CurrentItem.priority.toString());
        tvPriority.setTextColor(getPriorityTextColor(CurrentItem.priority));
        tvDate.setText(CurrentItem.completionDate);
        tvTime.setText(CurrentItem.completionTime);

        return convertView;
    }

    private void setItemTextView(String itemText)
    {
        try {

            String displayText = itemText;
            if(displayText.length() > MAX_ITEM_TEXT_DISPLAY_LENGTH) {
                displayText = displayText.substring(0, MAX_ITEM_TEXT_DISPLAY_LENGTH);
                displayText += "...";
            }
            tvItem.setText(displayText);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }


    private int getPriorityTextColor(Item.Priority priority)
    {
        if(priority == Item.Priority.Low)
            return Color.parseColor("#ff80cbc4");
        else if (priority == Item.Priority.Medium)
            return Color.parseColor("#ffff8800");
        else if (priority == Item.Priority.High)
            return Color.parseColor("#ffff4444");

        return 0;
    }
}
