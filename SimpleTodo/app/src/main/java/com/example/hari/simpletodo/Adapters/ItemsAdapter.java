package com.example.hari.simpletodo.Adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.hari.simpletodo.Models.Item;
import com.example.hari.simpletodo.R;

import java.util.ArrayList;

import static com.example.hari.simpletodo.Activities.MainActivity.writeToDB;
import static com.example.hari.simpletodo.Models.Item.priorityToColor;

/**
 * Created by Hari on 6/11/2016.
 */
public class ItemsAdapter extends ArrayAdapter<Item> {

    private TextView tvItem;
    private CheckBox itemCheckBox;

    private Item currentItem;

    public ItemsAdapter(Context context, ArrayList<Item> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        //Get the item corresponding to the current row (of a ListView)
        currentItem = getItem(position);
        if(convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
        }

        convertView.setEnabled(false);

        // Find the controls
        tvItem = (TextView)convertView.findViewById(R.id.tvItem);
        TextView tvPriority = (TextView)convertView.findViewById(R.id.tvPriority);
        TextView tvDate = (TextView)convertView.findViewById(R.id.tvDate);
        TextView tvTime = (TextView)convertView.findViewById(R.id.tvTime);

        //Populate the controls
        setItemTextView(currentItem.item);
        tvPriority.setText(currentItem.priority.toString());
        tvPriority.setTextColor(priorityToColor(currentItem.priority));
        tvDate.setText(currentItem.completionDate);
        tvTime.setText(currentItem.completionTime);

        itemCheckBox = (CheckBox)convertView.findViewById(R.id.cbIsCompleted);
        itemCheckBox.setTag(currentItem);
        itemCheckBox.setChecked(currentItem.isCompleted);

        setCheckBoxListener(convertView);
        onCompletedVisualEffects(currentItem, convertView, currentItem.isCompleted);
        if(currentItem.shouldHighlight) {
            blink(convertView, currentItem);
        }

        return convertView;
    }

    private void setCheckBoxListener(final View thisView)
    {
        itemCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                final Item thisItem = (Item) buttonView.getTag();
                thisItem.isCompleted = isChecked;
                onCompletedVisualEffects(thisItem, thisView, isChecked);
                writeToDB(thisItem);
            }
        });
    }

    private void onCompletedVisualEffects(Item thisItem, View thisView, boolean isCompleted)
    {
        try
        {
            if(thisItem != null) {

                TextView tvItem = (TextView) thisView.findViewById(R.id.tvItem);
                TextView tvPriority = (TextView)thisView.findViewById(R.id.tvPriority);
                TextView tvDate = (TextView)thisView.findViewById(R.id.tvDate);
                TextView tvTime = (TextView)thisView.findViewById(R.id.tvTime);

                //Strike-through the controls to indicate that this item has been completed
                if(isCompleted) {
                    tvItem.setPaintFlags(tvItem.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    tvPriority.setPaintFlags(tvItem.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    tvDate.setPaintFlags(tvItem.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    tvTime.setPaintFlags(tvItem.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                }
                //Remove strike-through effect
                else {
                    tvItem.setPaintFlags(tvItem.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                    tvPriority.setPaintFlags(tvItem.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                    tvDate.setPaintFlags(tvItem.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                    tvTime.setPaintFlags(tvItem.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                }

                notifyDataSetChanged();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void blink(final View v, final Item item) {
        v.animate().setDuration(600).alpha(0).withEndAction(new Runnable() {
            @Override
            public void run() {
                v.setAlpha(1);
                item.shouldHighlight = false;
            }
        });
    }

    private void setItemTextView(String itemText)
    {
        try {

            if(itemText.equals(""))
                return;

            //Add additional validation if required

            tvItem.setText(itemText);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

}
