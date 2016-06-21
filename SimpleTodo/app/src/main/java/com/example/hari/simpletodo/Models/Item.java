package com.example.hari.simpletodo.Models;

import android.graphics.Color;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by Hari on 6/11/2016.
 */
@Table(name = "Items")
public class Item extends Model{

    @Column(name = "item_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public long itemId;

    @Column(name = "Item")
    public String item;

    @Column(name = "Priority")
    public Priority priority;

    @Column(name = "CompletionDate")
    public String completionDate;

    @Column(name = "CompletionTime")
    public String completionTime;

    @Column(name = "Is_Completed")
    public boolean isCompleted;

    public boolean shouldHighlight;

    public Item()
    {
        super();
    }

    public void initialize(String item, Priority priority, String date, String time, long itemId)
    {
        this.item = item;
        if(priority == null)
            this.priority = Priority.Medium;
        else
            this.priority = priority;

        if (date==null)
            this.completionDate = getDefaultCompletionDate();
        else
            this.completionDate = date;

        if(time == null)
        {
            this.completionTime = "11:59 PM";
        }
        else
            this.completionTime = time;

        this.isCompleted = false;
        this.itemId = itemId;

        this.shouldHighlight = false;

    }

    private String getDefaultCompletionDate()
    {
        try {

            Calendar calendar = Calendar.getInstance();

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            return String.format(Locale.US, "%02d/%02d/%d", month , day, year );
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

    public enum Priority {High, Medium, Low}

    public static int priorityToInt(Priority priority)
    {
        if(priority == Priority.Low)
            return 0;
        else if (priority == Priority.Medium)
            return 1;
        else if(priority == Priority.High)
            return 2;
        else
            return 0;
    }

    public static List<Item> getAll()
    {
        List<Item> allItems = new Select().from(Item.class).orderBy("item_id ASC").execute();
        return(allItems);
    }

    public static Priority intToPriority(int value)
    {
        if(value == 0)
            return Priority.Low;
        else if(value == 1)
            return Priority.Medium;
        else if (value == 2)
            return Priority.High;
        else
            return Priority.Medium;
    }


    public static int priorityToColor(Priority priority)
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
