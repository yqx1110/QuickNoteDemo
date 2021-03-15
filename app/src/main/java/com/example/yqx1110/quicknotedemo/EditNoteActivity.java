package com.example.yqx1110.quicknotedemo;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.HashMap;

public class EditNoteActivity extends AppCompatActivity {
    Intent intent;
    SQLiteDatabase writeDatabase;
    int _id;
    TextView editTitle, editContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        Toolbar toolbar = findViewById(R.id.toolbar_edit);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true); //启用顶栏HomeButton
        MyDBHelper myDBHelper = new MyDBHelper(EditNoteActivity.this,
                "notebook.db", null, 1);
        intent = getIntent();
        writeDatabase = myDBHelper.getWritableDatabase();
        _id = intent.getIntExtra("_id", -1);
        editTitle = findViewById(R.id.editNoteTitle);
        editContent = findViewById(R.id.editNoteContent);
        if (_id != -1) {
            HashMap data = myDBHelper.queryByID(_id);
            editTitle.setText((String)data.get("title"));
            editContent.setText((String)data.get("content"));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_note, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            case R.id.action_discard:    //点击HomeButton时什么也不做, 直接finish
                Toast.makeText(this, R.string.toast_discarded, Toast.LENGTH_SHORT).show();
                setResult(RESULT_CANCELED);
                finish();
                return true;
            case R.id.action_save:
                Calendar calendar = Calendar.getInstance(); //获取当前日期
                String date = calendar.get(Calendar.MONTH) + 1 + "月" +
                        calendar.get(Calendar.DAY_OF_MONTH) + "日";
                ContentValues editNote = new ContentValues();
                editNote.put("title", editTitle.getText().toString());
                editNote.put("date", date);
                editNote.put("content", editContent.getText().toString());
                if (_id == -1)
                    writeDatabase.insert("notes", "title", editNote);
                else {
                    String whereClause = "_id = ?;";
                    writeDatabase.update("notes", editNote, whereClause,
                            new String[]{String.valueOf(_id)});
                }
                Toast.makeText(this, R.string.toast_saved, Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
                return true;
        }
        return false;
    }

}
