package com.example.orangescore;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class R1_Activity extends AppCompatActivity {
    RecyclerView home_recyclerView, away_recyclerView;
    ArrayList<Player> homePlayers_List, awayPlayers_List;
    TeamHomeRecyclerViewAdapter home_Adapter;
    TeamAwayRecyclerViewAdapter away_Adapter;
    FirebaseFirestore DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_r1);

        DB = FirebaseFirestore.getInstance();

        /** Populating recycler view that shows the players of home team */
        home_recyclerView = findViewById(R.id.players_home_recyclerView);
        home_recyclerView.setHasFixedSize(true);
        home_recyclerView.setLayoutManager(new LinearLayoutManager(this));

        homePlayers_List = new ArrayList<Player>();
        home_Adapter = new TeamHomeRecyclerViewAdapter(R1_Activity.this, homePlayers_List);

        home_recyclerView.setAdapter(home_Adapter);

        /** Populating recycler view that shows the players of away team */
        away_recyclerView = findViewById(R.id.players_away_recyclerView);
        away_recyclerView.setHasFixedSize(true);
        away_recyclerView.setLayoutManager(new LinearLayoutManager(this));

        awayPlayers_List = new ArrayList<Player>();
        away_Adapter = new TeamAwayRecyclerViewAdapter(R1_Activity.this, awayPlayers_List);

        away_recyclerView.setAdapter(away_Adapter);

        EventChangeListenerHome();
        EventChangeListenerAway();
    }

    private void EventChangeListenerAway() {
        DB.collection("players").orderBy("player_name", Query.Direction.ASCENDING).whereEqualTo("player_team", "Olympiacos B.C.").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore error", error.getMessage());
                    return;
                }
                for (DocumentChange homedc: value.getDocumentChanges()) {
                    if (homedc. getType() == DocumentChange.Type.ADDED) {
                        awayPlayers_List.add(homedc.getDocument().toObject(Player.class));
                    }
                    away_Adapter.notifyDataSetChanged();
                }
            }

        });
    }

    private void EventChangeListenerHome() {
        DB.collection("players").orderBy("player_name", Query.Direction.ASCENDING).whereEqualTo("player_team", "AEK B.C.").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore error", error.getMessage());
                    return;
                }
                for (DocumentChange homedc: value.getDocumentChanges()) {
                    if (homedc. getType() == DocumentChange.Type.ADDED) {
                        homePlayers_List.add(homedc.getDocument().toObject(Player.class));
                    }
                    home_Adapter.notifyDataSetChanged();
                }
            }
        });
    }


}