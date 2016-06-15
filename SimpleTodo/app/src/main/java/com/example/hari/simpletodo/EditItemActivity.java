package com.example.hari.simpletodo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class EditItemActivity extends AppCompatActivity {


    private EditText etItem;
    private String originalText;

    private int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        String itemText = getIntent().getStringExtra("itemText");

        etItem = (EditText)findViewById(R.id.etItem);

        position = getIntent().getIntExtra("position", -1);
        //etItem.setText(itemText, TextView.BufferType.EDITABLE);

        etItem.setText(itemText);
        etItem.setSelection(etItem.getText().length());
        originalText = etItem.getText().toString();
    }


    public void onBtnSaveClick(View view) {

        Intent data = new Intent();
        String editedText = etItem.getText().toString();
        if (editedText.trim().equals(""))
            editedText = originalText;

        data.putExtra("editedItem", editedText);
        data.putExtra("position", position);
        setResult(RESULT_OK, data);
        finish();
    }
}
