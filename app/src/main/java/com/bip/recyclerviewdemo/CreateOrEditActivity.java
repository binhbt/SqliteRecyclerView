package com.bip.recyclerviewdemo;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bip.recyclerviewdemo.database.MySqliteHelper;

/**
 * Created by t430 on 10/18/2017.
 */

public class CreateOrEditActivity extends AppCompatActivity{
    private MySqliteHelper dbHelper;
    private EditText txtName;
    private EditText txtAvatar;

    private Button btnSave;
    private Button btnEdit;
    private Button btnDelete;
    private View viewEditLayout;
    private int personId;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        txtName = (EditText)findViewById(R.id.txt_name);
        txtAvatar = (EditText)findViewById(R.id.txt_avatar);
        btnSave = (Button) findViewById(R.id.btn_save);
        btnEdit = (Button) findViewById(R.id.btn_edit);
        btnDelete = (Button) findViewById(R.id.btn_delete);
        viewEditLayout = findViewById(R.id.btnLayout);

        personId = getIntent().getIntExtra(MainActivity.STUDENT_ID, 0);
        dbHelper = new MySqliteHelper(this);
        if (personId >0){
            btnSave.setVisibility(View.GONE);
            viewEditLayout.setVisibility(View.VISIBLE);
            Cursor cursor = dbHelper.getPerson(personId);
            if (cursor != null && cursor.moveToFirst()) {
                String name = cursor.getString(cursor.getColumnIndex(MySqliteHelper.STUDENT_COLUMN_NAME));
                String gender = cursor.getString(cursor.getColumnIndex(MySqliteHelper.STUDENT_COLUMN_AVATAR));
                if (!cursor.isClosed()) {
                    cursor.close();
                }
                txtName.setText(name);
                txtName.setFocusable(false);
                txtName.setClickable(false);

                txtAvatar.setText(gender);
                txtAvatar.setFocusable(false);
                txtAvatar.setClickable(false);
            }
        }else{
            btnSave.setVisibility(View.VISIBLE);
            viewEditLayout.setVisibility(View.GONE);
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePerson();
            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSave.setVisibility(View.VISIBLE);
                viewEditLayout.setVisibility(View.GONE);
                txtName.setFocusable(true);
                txtName.setClickable(true);
                txtName.setFocusableInTouchMode(true);
                txtAvatar.setFocusable(true);
                txtAvatar.setClickable(true);
                txtAvatar.setFocusableInTouchMode(true);

            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateOrEditActivity.this);
                builder.setMessage("Do you want delete?")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dbHelper.deletePerson(personId);
                                Intent in = new Intent(CreateOrEditActivity.this, MainActivity.class);
                                startActivity(in);
                                finish();
                                Toast.makeText(CreateOrEditActivity.this, "Person deleted", Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Do nothing
                            }
                        }).show();
            }
        });
    }

    private void savePerson(){
        if(personId >0){
            if(dbHelper.updateStudent(personId,
                    txtName.getText().toString(), txtAvatar.getText().toString())){
                Toast.makeText(this, "Update success", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this, "Update failed", Toast.LENGTH_LONG).show();
            }
        }else{
            if(dbHelper.insertStudent(
                    txtName.getText().toString(), txtAvatar.getText().toString())){
                Toast.makeText(this, "Update success", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this, "Update failed", Toast.LENGTH_LONG).show();
            }
        }

        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}
