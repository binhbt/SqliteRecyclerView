package com.bip.recyclerviewdemo;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.bip.recyclerviewdemo.database.MySqliteHelper;

/**
 * Created by leobui on 12/27/2017.
 */

public class TestActivity  extends AppCompatActivity {
    public static final String PERSON_ID ="PERSON_ID";
    private ListView listView;
    private MySqliteHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        listView = (ListView) findViewById(R.id.listView1);
        dbHelper = new MySqliteHelper(this);
        final Cursor cursor = dbHelper.getAllStudent();
        String[] columns= new String[]{
                MySqliteHelper.STUDENT_COLUMN_ID, MySqliteHelper.STUDENT_COLUMN_NAME
        };
        int[] widgets = new int[]{
                R.id.person_id, R.id.person_name
        };
        SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.item_list_person,
                cursor, columns, widgets);
        listView.setAdapter(simpleCursorAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor itemCursor = (Cursor)listView.getItemAtPosition(i);
                int id = itemCursor.getInt(itemCursor.getColumnIndex(MySqliteHelper.STUDENT_COLUMN_ID));
                Intent intent = new Intent(getApplicationContext(), CreateOrEditActivity.class);
                intent.putExtra(PERSON_ID, id);
                startActivity(intent);
            }
        });
        findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CreateOrEditActivity.class);
                intent.putExtra(PERSON_ID, 0);
                startActivity(intent);
            }
        });
    }
}
