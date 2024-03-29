package com.example.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    FloatingActionButton floatingActionButtonAddNote;
    public static final ArrayList<Note> notes = new ArrayList<>();
    NotesAdapter notesAdapter;
    NotesDBHelper notesDBHelper;
    SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        floatingActionButtonAddNote = findViewById(R.id.floatingActionButtonAddNote);

        floatingActionButtonAddNote.setOnClickListener(view -> {
            Intent intentAddNewNote = new Intent(this, NewNote.class);
            startActivity(intentAddNewNote);
        });

        notesDBHelper = new NotesDBHelper(this);
        sqLiteDatabase = notesDBHelper.getWritableDatabase();

        for (Note note : notes) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(NotesContract.NotesEntry.COLUMN_TITLE,note.getTitle());
            contentValues.put(NotesContract.NotesEntry.COLUMN_DESCRIPTION,note.getDescription());
            contentValues.put(NotesContract.NotesEntry.COLUMN_DAY_OF_WEEK,note.getDayOfWeek());
            contentValues.put(NotesContract.NotesEntry.COLUMN_PRIORITY, note.getPriority());
            sqLiteDatabase.insert(NotesContract.NotesEntry.TABLE_NAME, null,contentValues);
        }

        ArrayList<Note> notesDB = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.query(NotesContract.NotesEntry.TABLE_NAME, null,null,null,null,null,null,null);

        while (cursor.moveToNext()){
            @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(NotesContract.NotesEntry.COLUMN_TITLE));
            @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex(NotesContract.NotesEntry.COLUMN_DESCRIPTION));
            @SuppressLint("Range") String dayOfWeek = cursor.getString(cursor.getColumnIndex(NotesContract.NotesEntry.COLUMN_DAY_OF_WEEK));
            @SuppressLint("Range") int priority = cursor.getInt(cursor.getColumnIndex(NotesContract.NotesEntry.COLUMN_PRIORITY));
            Note note = new Note(title,description,dayOfWeek,priority);
            notesDB.add(note);
        }
        cursor.close();


        recyclerView = findViewById(R.id.recyclerView);

        notesAdapter = new NotesAdapter(notes);
        notesAdapter.setOnNoteClicklistener(new NotesAdapter.onNoteClicklistener() {

            @Override
            public void onNoteClick(int position) {
                Toast.makeText(MainActivity.this, "Нажат номер: " + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(int position) {
                remove(position);
            }

        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                remove(viewHolder.getAdapterPosition());
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(notesAdapter);
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }

    @SuppressLint("NotifyDataSetChanged")
    private void remove(int position) {
        notes.remove(position);
        notesAdapter.notifyDataSetChanged();
    }
}