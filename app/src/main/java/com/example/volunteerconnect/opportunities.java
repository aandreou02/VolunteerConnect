package com.example.volunteerconnect;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class opportunities extends AppCompatActivity {

    FirebaseAuth auth;
    RecyclerView mainUserRecyclerView;
    EventAdapter adapter;
    FirebaseDatabase database;
    ArrayList<Event> eventList;
    ArrayList<Event> filteredList;
    ImageView imglogout;
    ImageView chatBut, setbut;
    SearchView searchView;
    Button btnFilterFood, btnFilterTrees, btnFilterAnimals, btnFilterAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opportunities);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        chatBut = findViewById(R.id.chatBut);
        setbut = findViewById(R.id.settingBut);
        btnFilterFood = findViewById((R.id.btnFilterFood));
        btnFilterTrees = findViewById((R.id.btnFilterTrees));
        btnFilterAnimals = findViewById((R.id.btnFilterAnimals));
        btnFilterAll = findViewById((R.id.btnFilterAll));

        DatabaseReference reference = database.getReference().child("events");

        eventList = new ArrayList<>();
        filteredList = new ArrayList<>();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("Firebase", "onDataChange: called");
                eventList.clear(); // Clear the list before adding new data
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Event event = dataSnapshot.getValue(Event.class);
                    if (event != null) {
                        eventList.add(event);
                    } else {
                        Log.w("Firebase", "onDataChange: Event is null");
                    }
                }
                filteredList.clear();
                filteredList.addAll(eventList);
                adapter.notifyDataSetChanged();
                Log.d("Firebase", "onDataChange: eventList size: " + eventList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "onCancelled: " + error.getMessage());
            }
        });

        imglogout = findViewById(R.id.logoutimg);
        imglogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(opportunities.this, R.style.dialoge);
                dialog.setContentView(R.layout.dialog_layout);
                Button no, yes;
                yes = dialog.findViewById(R.id.yesbnt);
                no = dialog.findViewById(R.id.nobnt);
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(opportunities.this, login.class);
                        startActivity(intent);
                        finish();
                    }
                });
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        mainUserRecyclerView = findViewById(R.id.mainUserRecyclerView);
        mainUserRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EventAdapter(opportunities.this, eventList, filteredList);
        mainUserRecyclerView.setAdapter(adapter);

        setbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(opportunities.this, setting.class);
                startActivity(intent);
            }
        });

        chatBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(opportunities.this, MainActivity.class);
                startActivity(intent);
            }
        });

        if (auth.getCurrentUser() == null) {
            Intent intent = new Intent(opportunities.this, login.class);
            startActivity(intent);
        }

        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("Search", newText);
                adapter.filter(newText);
                return true;
            }
        });

        btnFilterAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.filterByCategory("All");
            }
        });

        btnFilterFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.filterByCategory("Food");
            }
        });

        btnFilterTrees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.filterByCategory("Community");
            }
        });

        btnFilterAnimals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.filterByCategory("Animals");
            }
        });
    }
}
