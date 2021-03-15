package com.example.yqx1110.quicknotedemo;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.FilterQueryProvider;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    private MyDBHelper myDBHelper;
    private SimpleCursorAdapter adapter;
    Cursor dataCursor;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab); //获取悬浮按钮
        fab.setOnClickListener(new View.OnClickListener() {  //为悬浮按钮设置监听器
            @Override
            public void onClick(View view) {
                Intent newNote = new Intent(MainActivity.this,
                        EditNoteActivity.class);
                startActivityForResult(newNote, 1);  //进入EditNoteActivity进行编辑
            }
        });
        myDBHelper = new MyDBHelper(MainActivity.this,
                "notebook.db", null, 1);
        db = myDBHelper.getReadableDatabase();
        dataCursor = db.rawQuery("select * from notes;", null);



        /*
        adapter = new MyAdapter(MainActivity.this, dataCursor);
        RecyclerView recyclerView = findViewById(R.id.mainList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        */



        adapter = new SimpleCursorAdapter(MainActivity.this,
                R.layout.main_list_item, dataCursor, new String[]{"title", "date", "content"},
                new int[]{R.id.itemTitle, R.id.itemDate, R.id.itemContent}, 0);

        FilterQueryProvider filter = new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                String selectionArg =
                        "%" + constraint.toString().replaceAll("\\s+", "%") + "%";
                return db.rawQuery("select * from notes where title||content||date like ?;",
                        new String[]{selectionArg});
            }
        };

        adapter.setFilterQueryProvider(filter);
        final ListView listView = findViewById(R.id.mainList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = adapter.getCursor();
                cursor.moveToPosition(position);
                int _id = cursor.getInt(cursor.getColumnIndex("_id")); //获取_id进入ViewNoteActivity
                Intent viewNote = new Intent(MainActivity.this, ViewNoteActivity.class);
                viewNote.putExtra("_id", _id);
                startActivityForResult(viewNote, 2);
            }
        });

        registerForContextMenu(listView);

        /*
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position,
                                                  long id, boolean checked) {
                //Object view = listView.getItemAtPosition(position);
                System.out.println(adapter.getCount());
                System.out.println(position);
                View view = adapter.getView(position, null, listView);
                //v.getId();
                if (checked){
                    view.setBackgroundColor(getResources().getColor(R.color.colorAccent, null));

                    //imageView.setImageResource(R.drawable.round_check_circle_24);
                }
                    //adapter.setViewImage(imgView, "R.drawable.round_check_circle_24)");
                else;
                    //imgView.setImageDrawable(getDrawable(R.drawable.round_event_note_24));
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                getMenuInflater().inflate(R.menu.context_main, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });
*/


        /*list = myDBHelper.queryAll();
        simpleAdapter = new SimpleAdapter(MainActivity.this,
                list, R.layout.main_list_item, new String[]{"title", "date", "content"},
                new int[]{R.id.itemTitle, R.id.itemDate, R.id.itemContent});
        ListView listView = findViewById(R.id.mainList);
        listView.setAdapter(simpleAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent viewNote = new Intent(MainActivity.this, ViewNoteActivity.class);
                int _id = (int)list.get(position).get("_id");
                viewNote.putExtra("_id", _id);
                startActivityForResult(viewNote, 2);
            }
        });*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                adapter.getFilter().filter(s);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        if (requestCode == 1 || requestCode == 2)
            if (resultCode == RESULT_OK) {
                //list = myDBHelper.queryAll();
                Cursor newDataCursor;
                newDataCursor = db.rawQuery("select * from notes;", null);
                adapter.changeCursor(newDataCursor);
                //adapter.notifyDataSetChanged();
            }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_main, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.action_delete_selected:
                Cursor cursor = adapter.getCursor();
                cursor.moveToPosition(info.position);
                int _id = cursor.getInt(cursor.getColumnIndex("_id"));
                myDBHelper.deleteByID(_id);
                Cursor newDataCursor = db.rawQuery("select * from notes;", null);
                adapter.changeCursor(newDataCursor);
                return true;
            default:
                return true;
        }
    }
}
