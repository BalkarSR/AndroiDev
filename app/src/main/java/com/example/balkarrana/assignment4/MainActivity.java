package com.example.balkarrana.assignment4;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button addTeam, exit;
    SQLiteDatabase database;
    ListView teams;
    ArrayList<String> teamInfo = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addTeam = findViewById(R.id.addTeam);
        exit = findViewById(R.id.exit);
        teams = findViewById(R.id.teams);

        // create database
        database = openOrCreateDatabase("teams", 0, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS teams(teamId INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1, city varchar, name varchar, sport varchar, mvp varchar, imageURI varchar)");


        // get all teams currently in database
        populateTeams();
        teams.setOnItemClickListener(new TeamListener());

        // start add team activity
        addTeam.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(v.getContext(), AddTeam.class);
                startActivity(intent);
            }
        });

        // exit app
        exit.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                database.close();
                moveTaskToBack(true);
            }
        });
    }

    class TeamListener implements AdapterView.OnItemClickListener {
        TeamListener(){}

        // long is included to make implementation happy
        public void onItemClick(AdapterView<?> adaptView, View view, int i, long l){
            Intent intent = new Intent(MainActivity.this, TeamInformation.class);
            intent.putExtra("city", teamInfo.get(i));
            startActivity(intent);
        }
    }

    void populateTeams(){
        Cursor cursor = database.rawQuery("SELECT * FROM teams", null);
        String info;

        while(cursor.moveToNext()){
            teamInfo.add(cursor.getString(cursor.getColumnIndex("city")).concat(" ").concat(cursor.getString(cursor.getColumnIndex("name"))));
        }

        ArrayAdapter<String> teamList = new ArrayAdapter<>(getApplicationContext(), R.layout.centerlist, teamInfo);
        teams.setAdapter(teamList);
    }
}
