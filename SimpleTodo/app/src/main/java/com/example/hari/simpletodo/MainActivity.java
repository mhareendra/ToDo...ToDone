package com.example.hari.simpletodo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ConfirmDeleteDialogFragment.ConfirmDeleteDialogListener {

    ListView lvItems;
    EditText etNewItem;

    ItemsAdapter customItemsAdapter;
    ArrayList<Item> customItems;

    FloatingActionButton fabAdd;

    private long itemIdCounter =0;
    private static ShareActionProvider mShareActionProvider;

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


        etNewItem = (EditText)findViewById(R.id.etNewItem);
        fabAdd = (FloatingActionButton) findViewById(R.id.btnSaveItem);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etNewItem.getVisibility() == View.INVISIBLE) {
                    etNewItem.setVisibility(View.VISIBLE);
                    etNewItem.requestFocus();
                    etNewItem.setSelection(etNewItem.getText().length());

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(etNewItem, InputMethodManager.SHOW_IMPLICIT);
                }
                else
                    onAddItem(v);
            }
        });
    }


    private void showDeleteConfirmationDialog(int position) {
        FragmentManager fm = getSupportFragmentManager();

        ConfirmDeleteDialogFragment alertDialog = ConfirmDeleteDialogFragment.newInstance("Delete confirmation", position);
        alertDialog.show(fm, "fragment_alert");
    }

    private void setupListItemLongClickListener() {

        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                showDeleteConfirmationDialog(position);
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
    private final int RESULT_DELETE = -2;
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
            int position = data.getIntExtra("position", -99);
            String completionDate = data.getStringExtra("completionDate");
            String completionTime = data.getStringExtra("completionTime");
            Item.Priority priority = ((Item.Priority) data.getExtras().get("priority"));

            long originalItemId = customItems.get(position).itemId;

            customItems.remove(position);
            Item item = new Item();
            item.initialize(editedItem, priority, completionDate, completionTime, originalItemId);
            item.shouldHighlight = true;

            customItems.add(position, item);
            customItemsAdapter.notifyDataSetChanged();

            writeToDB(item);

        }
        else if(resultCode == RESULT_DELETE && requestCode == REQUEST_CODE)
        {
            int position = data.getIntExtra("position", -99);
            deleteFromList(position);
        }
        etNewItem.setVisibility(View.INVISIBLE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem menuItem = menu.findItem(R.id.share_option);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        setUpShare();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.delete_all_option)
        {
            deleteAllItems();
            return true;
        }
        else if (id == R.id.refresh_option)
        {
            refreshList();
            return true;
        }
        else if (id == R.id.reverse_list_option)
        {
            reverseList();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshList()
    {
        try
        {
            customItems.clear();

            List<Item> allResults = Item.getAll();
            if(allResults.size() > 0)
                itemIdCounter = allResults.get(allResults.size() - 1).itemId + 1;
            customItemsAdapter.addAll(allResults);
            customItemsAdapter.notifyDataSetChanged();

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void reverseList()
    {
        try
        {
            Collections.reverse(customItems);
            customItemsAdapter.notifyDataSetChanged();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private static void setUpShare()
    {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);

        String textToShare = generateTextToShare();

        sendIntent.putExtra(Intent.EXTRA_TEXT, textToShare);
        sendIntent.setType("text/plain");

        setShareIntent(sendIntent);
    }

    private static void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null & shareIntent!=null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    ///Read  all items from the db and generate a message that groups  items by 'isCompleted'
    private static String generateTextToShare()
    {
        try
        {
            List<Item> dbCustomItems = Item.getAll(); //read data from the db to get latest data

            String defaultText = "There are no items to be completed!";
            if(dbCustomItems.size() == 0)
                return defaultText;

            String messageToDoItems= "To do:\n";
            String messageCompletedItems = "Completed:\n";
            int countToDo = 0;
            int countCompleted = 0;


            for(Item i : dbCustomItems)
            {
                if(i.isCompleted) {
                    messageCompletedItems += String.format("\n%d. %s\n", countCompleted + 1, i.item);
                    countCompleted++;
                }
                else {
                    messageToDoItems += String.format("\n%d. %s by %s on %s\n", countToDo + 1, i.item, i.completionTime, i.completionDate);
                    countToDo++;
                }
            }

            String returnText;

            if(countToDo == 0)
            {
                returnText = messageCompletedItems;
            }
            else if (countCompleted==0)
            {
                returnText = messageToDoItems;
            }
            else if ((countCompleted + countToDo) == 0)
                returnText = defaultText;
            else
                returnText = String.format("%s\n\n%s" ,messageToDoItems, messageCompletedItems);

            return returnText;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return "";
    }

    private void deleteAllItems()
    {
        try
        {
            if(customItems.size() == 0) {

                Toast.makeText(this, "No items to delete!", Toast.LENGTH_SHORT).show();
                return;
            }
            showDeleteConfirmationDialog(-1);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }


    public void onAddItem(View view) {
        String newItemText = etNewItem.getText().toString().trim();
        if(newItemText.trim().equals(""))
            return;
        Item item = new Item();
        item.initialize(newItemText, null, null, null, getItemId());

        //item.shouldHighlight = true;
        customItemsAdapter.add(item);

        lvItems.smoothScrollToPosition(customItems.size());
        etNewItem.setText("");
        writeToDB(item);
        //lvItems.requestFocus();
    }

    private long getItemId()
    {
        return(itemIdCounter++);
    }

    public static void writeToDB(Item item)
    {
        item.save();
        setUpShare();
    }

    @Override
    public void onFinishConfirmDeleteDialog(boolean shouldDelete, int position) {
        try
        {
            if(shouldDelete)
            {
                if(position == -1)
                {
                    int numItems = customItems.size();
                    for(int i =0 ; i < numItems; i++)
                    {
                        deleteFromList(-1);
                    }
                    Toast.makeText(this, "All Items have been deleted!", Toast.LENGTH_SHORT).show();
                }
                else
                    deleteFromList(position);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }


    ///If position is -1, it means that all items in the list have to be deleted.
    ///This can be done by deleting just the first item in this method call.
    ///The callee is responsible for making multiple calls to this method as required
    private void deleteFromList(int position) {
        try {

            Item itemToDelete = new Item();
            if(position > -1) {
                itemToDelete = customItems.get(position);
                customItems.remove(position);
                String toastMessage = String.format("%s has been deleted", itemToDelete.item);
                Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
            }
            else if(position == -1) {
                itemToDelete = customItems.get(0);
                customItems.remove(0);
            }
            itemToDelete.delete();  //this deletes directly from the db
            customItemsAdapter.notifyDataSetChanged();
            setUpShare();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
