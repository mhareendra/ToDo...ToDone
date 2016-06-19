package com.example.hari.simpletodo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.example.hari.simpletodo.Adapters.ItemsAdapter;
import com.example.hari.simpletodo.Fragments.ConfirmDeleteDialogFragment;
import com.example.hari.simpletodo.Models.Item;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ConfirmDeleteDialogFragment.ConfirmDeleteDialogListener {

    ListView lvItems;
    EditText etNewItem;

    ItemsAdapter customItemsAdapter;
    ArrayList<Item> customItems;


    private long itemIdCounter =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvItems = (ListView)findViewById(R.id.lvItems);


        customItems = new ArrayList<>();
        customItemsAdapter = new ItemsAdapter(this, customItems);
        lvItems.setAdapter(customItemsAdapter);

        List<Item> allResults = Item.getAll();

        if(allResults.size() > 0)
            itemIdCounter = allResults.get(allResults.size() - 1).itemId + 1;
            //Assumes that the last item in the list contains the max itemId
            //Add item starts from this itemId

        customItemsAdapter.addAll(allResults);

        setupListItemLongClickListener();
        setupListItemClickListener();

        Configuration.Builder config = new Configuration.Builder(this);
        config.addModelClasses(Item.class);
        ActiveAndroid.initialize(config.create());

    }

    private void showAlertDialog(int position) {
        FragmentManager fm = getSupportFragmentManager();
        ConfirmDeleteDialogFragment alertDialog = ConfirmDeleteDialogFragment.newInstance("Delete confirmation", position);
        alertDialog.show(fm, "fragment_alert");
    }

    private void setupListItemLongClickListener() {

        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                showAlertDialog(position);
                return true;
            }
        });
    }

    private void setupListItemClickListener()
    {
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemText = customItems.get(position).item;
                String date = customItems.get(position).completionDate;
                Item.Priority priority = customItems.get(position).priority;
                String time = customItems.get(position).completionTime;
                launchAddEditItemActivity(itemText, position, date, priority, time);
            }
        });
    }

    private int REQUEST_CODE = 0;
    private void launchAddEditItemActivity(String itemText, int position, String date, Item.Priority priority, String time)
    {
        Intent addEditIntent = new Intent(this, AddEditItemActivity.class);
        addEditIntent.putExtra("itemText", itemText);
        addEditIntent.putExtra("position", position);
        addEditIntent.putExtra("completionDate", date);
        addEditIntent.putExtra("completionTime", time);
        addEditIntent.putExtra("priority", priority);
        startActivityForResult(addEditIntent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == RESULT_OK && requestCode == REQUEST_CODE)
        {
            String editedItem = data.getStringExtra("itemText");
            int position = data.getIntExtra("position", -1);
            String completionDate = data.getStringExtra("completionDate");
            String completionTime = data.getStringExtra("completionTime");
            Item.Priority priority = ((Item.Priority) data.getExtras().get("priority"));

            long originalitemId = customItems.get(position).itemId;

            customItems.remove(position);
            Item item = new Item();
            item.initialize(editedItem, priority, completionDate, completionTime, originalitemId);

            customItems.add(position, item);
            customItemsAdapter.notifyDataSetChanged();
            writeToDB(item);
            //completedEditActivityVisualEffects(final View v);

            View editedItemView = lvItems.getChildAt(position);
            customItemsAdapter.completedEditActivityVisualEffects(editedItemView);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onAddItem(View view) {
        etNewItem = (EditText)findViewById(R.id.etNewItem);
        String newItemText = etNewItem.getText().toString().trim();
        if(newItemText.trim().equals(""))
            return;
        Item item = new Item();
        item.initialize(newItemText, null, null, null, getItemId());
        customItemsAdapter.add(item);

        lvItems.smoothScrollToPosition(customItems.size());
        etNewItem.setText("");
        writeToDB(item);

    }

    private long getItemId()
    {
        return(itemIdCounter++);
    }

    public static void writeToDB(Item item)
    {
        item.save();
    }

    @Override
    public void onFinishConfirmDeleteDialog(boolean shouldDelete, int position) {
        try
        {
            if(shouldDelete)
            {
                deleteFromList(position);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void deleteFromList(int position) {
        try {
            Item itemToDelete = customItems.get(position);
            String toastMessage = String.format("%s has been deleted", itemToDelete.item);
            customItems.remove(position);
            itemToDelete.delete();
            customItemsAdapter.notifyDataSetChanged();
            Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
