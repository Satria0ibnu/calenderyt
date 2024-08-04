package com.example.calenderyt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private mySQLiteDBHandler dbHandler;
    private EventListAdapter adapter;

    private EditText editText;
    private Button saveButton;
    private CalendarView calendarView;
    private String selectedDate;
    private SQLiteDatabase sqLiteDatabase;

    private ListView eventListView;
    private ArrayList<String> eventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        calendarView = findViewById(R.id.calendarView);
        saveButton = findViewById(R.id.buttonSave);
        eventListView = findViewById(R.id.eventListView);

        initializeDatabase();
        setupCalendarView();
        setupEventListView();
        setupSaveButton();
    }

    private void initializeDatabase() {
        try {
            dbHandler = new mySQLiteDBHandler(this, "CalendarDatabase", null, 1);
            sqLiteDatabase = dbHandler.getWritableDatabase();
            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS EventCalendar(Date TEXT, Event TEXT)");

            // Set selectedDate to today's date
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            selectedDate = String.format("%04d%02d%02d", year, month + 1, dayOfMonth);

            // Read events for today's date
            ReadDatabase(null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupCalendarView() {
        calendarView.setDate(Calendar.getInstance().getTimeInMillis());

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                selectedDate = String.format("%04d%02d%02d", year, month + 1, dayOfMonth);
                ReadDatabase(view);
            }
        });
    }

    private void setupEventListView() {
        eventList = new ArrayList<>();
        adapter = new EventListAdapter(this, eventList, dbHandler, selectedDate);
        eventListView.setAdapter(adapter);
    }

    private void setupSaveButton() {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InsertDatabase(v);
                editText.setText("");
            }
        });
    }

    public void InsertDatabase(View view) {

        String eventText = editText.getText().toString().trim();
        if (eventText.isEmpty()) {
            Toast.makeText(this, "Event cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("Date", selectedDate);
            contentValues.put("Event", eventText);
            sqLiteDatabase.insert("EventCalendar", null, contentValues);
            eventList.add(eventText);
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "Event added successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to add event", Toast.LENGTH_SHORT).show();
        }
    }

    public void ReadDatabase(View view) {
        String query = "SELECT Event FROM EventCalendar WHERE Date = ?";
        try {
            Cursor cursor = sqLiteDatabase.rawQuery(query, new String[]{selectedDate});
            eventList.clear(); // Clear the existing event list

            while (cursor.moveToNext()) {
                String event = cursor.getString(0);
                eventList.add(event);
            }

            // Notify the adapter that the data has changed
            adapter.updateSelectedDate(selectedDate);
            adapter.notifyDataSetChanged();

            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            editText.setText("");
        }
    }
}
