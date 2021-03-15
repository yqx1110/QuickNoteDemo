package com.example.yqx1110.quicknotedemo;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.HashMap;

public class ViewNoteActivity extends AppCompatActivity {
    MyDBHelper DBHelper;
    int _id;
    HashMap<String, Object> data;
    TextView editTitle;
    TextView editContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);
        Toolbar toolbar = findViewById(R.id.toolbar_view);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        _id = intent.getIntExtra("_id", -1);
        System.out.println(_id);
        DBHelper = new MyDBHelper(ViewNoteActivity.this,
                "notebook.db", null, 1);
        data = DBHelper.queryByID(_id);
        editTitle = findViewById(R.id.viewNoteTitle);
        editContent = findViewById(R.id.viewNoteContent);
        editTitle.setText((String)data.get("title"));
        editContent.setText((String)data.get("content"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_delete:
                DBHelper.deleteByID(_id);
                setResult(RESULT_OK);
                finish();
                return true;
            /*编辑按钮监听器*/
            case R.id.action_edit:
                Intent editNote = new Intent(ViewNoteActivity.this, EditNoteActivity.class);
                editNote.putExtra("_id", _id);
                startActivityForResult(editNote, 3);
                return true;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        if (requestCode == 3)
            if (resultCode == RESULT_OK)
            {
                data = DBHelper.queryByID(_id);
                editTitle.setText((String)data.get("title"));
                editContent.setText((String)data.get("content"));
                setResult(RESULT_OK);
            }

    }
}
