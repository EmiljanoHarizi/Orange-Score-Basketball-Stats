package com.example.orangescore;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Admin_Start_Activity extends AppCompatActivity {
    FloatingActionButton add_teams;

    RecyclerView match_recyclerView;
    ArrayList<Match> match_ArrayList;
    MatchRecyclerViewAdapter match_Adapter;
    FirebaseFirestore DB;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Match Data.....");
        progressDialog.show();

        add_teams = findViewById(R.id.add_teams_btn);

        DB = FirebaseFirestore.getInstance();

        match_recyclerView = findViewById(R.id.matches_RecyclerView);
        match_recyclerView.setHasFixedSize(true);
        match_recyclerView.setLayoutManager(new LinearLayoutManager(this));

        match_ArrayList = new ArrayList<Match>();
        match_Adapter = new MatchRecyclerViewAdapter(Admin_Start_Activity.this, match_ArrayList, new MatchRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(Match details) {
                startActivity(new Intent(getApplicationContext(), R1_Activity.class));
                finish();
            }
        });

        match_recyclerView.setAdapter(match_Adapter);

        add_teams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LeagueStartActivity.class));
                finish();
            }
        });

        EventChangeListener();
    }

    private void EventChangeListener() {
        DB.collection("matches").orderBy("matchDate", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Log.e("Firestore error", error.getMessage());
                    return;
                }
                for (DocumentChange dc: value.getDocumentChanges()) {
                    if (dc.getType() == DocumentChange.Type.ADDED) {
                        match_ArrayList.add(dc.getDocument().toObject(Match.class));
                    }
                    match_Adapter.notifyDataSetChanged();
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
            }
        });
    }
}