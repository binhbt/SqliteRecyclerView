package com.bip.recyclerviewdemo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.bip.recyclerviewdemo.model.Student;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by leobui on 12/27/2017.
 */

public class MySqliteHelper  extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "studentexample.db";
    public static final int DATABASE_VERSION = 1;

    public static final String STUDENT_TABLE_NAME = "student";
    public static final String STUDENT_COLUMN_ID = "_id";
    public static final String STUDENT_COLUMN_NAME = "name";
    public static final String STUDENT_COLUMN_AVATAR = "avatar";

    public MySqliteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //CREATE TABLE student (_id INTEGER PRIMARY KEY, name TEXT, avatar TEXT)
        sqLiteDatabase.execSQL(
                "CREATE TABLE " + STUDENT_TABLE_NAME +
                        "("+ STUDENT_COLUMN_ID + " INTEGER PRIMARY KEY, "+
                        STUDENT_COLUMN_NAME + " TEXT, "+
                        STUDENT_COLUMN_AVATAR + " TEXT)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ STUDENT_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public boolean insertStudent(String name, String avatar){
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(STUDENT_COLUMN_NAME, name);
            contentValues.put(STUDENT_COLUMN_AVATAR, avatar);
            db.insert(STUDENT_TABLE_NAME, null, contentValues);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    public int numberOfRows(){
        SQLiteDatabase db = getWritableDatabase();
        int numrows = (int) DatabaseUtils.queryNumEntries(db, STUDENT_TABLE_NAME);
        return numrows;
    }
    public boolean updateStudent(int id, String name, String avatar){
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(STUDENT_COLUMN_NAME, name);
            contentValues.put(STUDENT_COLUMN_AVATAR, avatar);
            db.update(STUDENT_TABLE_NAME, contentValues, STUDENT_COLUMN_ID +" = ?", new String[]{id+""});
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    public int deletePerson(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(STUDENT_TABLE_NAME, STUDENT_COLUMN_ID +" = ?", new String[]{id+""});
    }
    public List<Student> getAllStudent1(){
        List<Student> studentList = new ArrayList<>();
        //SELECT * FROM person
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM "+ STUDENT_TABLE_NAME, null);
        if(cur != null && cur.getCount() != 0){
            cur.moveToFirst();
            do{
                int idIdx = cur.getColumnIndex(STUDENT_COLUMN_ID);
                int nameIdx = cur.getColumnIndex(STUDENT_COLUMN_NAME);
                int avatarIdx = cur.getColumnIndex(STUDENT_COLUMN_AVATAR);
                Student student = new Student();
                student.setId(cur.getInt(idIdx));
                student.setName(cur.getString(nameIdx));
                student.setAvatar(cur.getString(avatarIdx));
                studentList.add(student);
            }while (cur.moveToNext());
        }
        return  studentList;
    }
    public Cursor getAllStudent(){
        //SELECT * FROM person
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+ STUDENT_TABLE_NAME, null);
        return  cursor;
    }
    public Cursor getPerson(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+ STUDENT_TABLE_NAME +" WHERE "+ STUDENT_COLUMN_ID +" = ?", new String[]{id+""});
        return cursor;
    }
}
