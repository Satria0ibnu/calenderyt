package com.example.calenderyt;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import java.util.ArrayList;
import android.widget.Toast;


public class EventListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> eventList;
    private mySQLiteDBHandler dbHandler;
    private String selectedDate; // Store selectedDate as an instance variable

    public EventListAdapter(Context context, ArrayList<String> eventList, mySQLiteDBHandler dbHandler, String selectedDate) {
        this.context = context;
        this.eventList = eventList;
        this.dbHandler = dbHandler;
        this.selectedDate = selectedDate; // Initialize the selectedDate
    }

    @Override
    public int getCount() {
        return eventList.size();
    }

    @Override
    public Object getItem(int position) {
        return eventList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public void updateSelectedDate(String newSelectedDate) {
        this.selectedDate = newSelectedDate;
    }

    // Method to remove an item from the database and update the list
    public void deleteItem(int position) {
        try {
        SQLiteDatabase sqLiteDatabase = dbHandler.getWritableDatabase();
        String eventToDelete = eventList.get(position);

        // Delete from the database
         sqLiteDatabase.delete("EventCalendar", "Date = ? AND Event = ?", new String[]{selectedDate, eventToDelete});

        // Remove from the list
        eventList.remove(position);
        notifyDataSetChanged();
        Toast.makeText(context, "Event deleted successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to delete event", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_event, parent, false);
        }

        TextView eventTextView = convertView.findViewById(R.id.eventTextView);
        ImageButton deleteButton = convertView.findViewById(R.id.deleteButton);

        final String event = eventList.get(position);
        eventTextView.setText(event);

        // Set click listener for delete button
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the deleteItem method to handle deletion
                deleteItem(position);
            }
        });


        return convertView;
    }
}
