package com.example.volunteerconnect;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.viewholder> {
    opportunities opportunitiesActivity;
    ArrayList<Event> eventArrayList;
    ArrayList<Event> filteredList;

    public EventAdapter(opportunities opportunitiesActivity, ArrayList<Event> eventArrayList, ArrayList<Event> filteredList) {
        this.opportunitiesActivity = opportunitiesActivity;
        this.eventArrayList = eventArrayList;
        this.filteredList = filteredList;
    }

    @NonNull
    @Override
    public EventAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(opportunitiesActivity).inflate(R.layout.item_event, parent, false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventAdapter.viewholder holder, int position) {
        Event event = eventArrayList.get(position);
        holder.eventTitle.setText(event.getTitle());
        holder.eventDescription.setText(event.getDescription());
        holder.eventDate.setText(event.getDate());
        holder.eventCategory.setText(event.getCategory());
        Picasso.get().load(event.getImageUrl()).into(holder.eventImage);
    }

    @Override
    public int getItemCount() {
        return eventArrayList.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        CircleImageView eventImage;
        TextView eventTitle, eventDescription, eventDate, eventCategory;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            eventImage = itemView.findViewById(R.id.eventImage);
            eventTitle = itemView.findViewById(R.id.eventTitle);
            eventDescription = itemView.findViewById(R.id.eventDescription);
            eventDate = itemView.findViewById(R.id.eventDate);
            eventCategory = itemView.findViewById(R.id.eventCategory);
        }
    }

    public void filter(@NonNull String searchText) {
        Log.d("Filter", searchText);
        eventArrayList.clear();
        if (searchText.isEmpty()) {
            eventArrayList.addAll(filteredList);
        } else {
            searchText = searchText.toLowerCase();
            Log.d("Filter", filteredList.size() + "");
            for (Event event : filteredList) {
                Log.d("Filter", event.getTitle());
                if (event.getTitle().toLowerCase().contains(searchText)) {
                    eventArrayList.add(event);
                }
            }
        }
        notifyDataSetChanged();
    }
    public void filterByCategory(@NonNull String category) {
        Log.d("FilterByCategory", category);
        eventArrayList.clear();
        if (category.equals("All")) {
            eventArrayList.addAll(filteredList);
        } else {
            for (Event event : filteredList) {
                if (event.getCategory().toLowerCase().contains(category.toLowerCase())) {
                    eventArrayList.add(event);
                }
            }
        }
        notifyDataSetChanged();
    }

}
