package com.example.balkarrana.assignment4;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

public class TeamInformation extends AppCompatActivity {

    Button exit, upload, delete, update;
    EditText cityField, nameField, mvpField;
    Spinner sportField;
    ImageView img;
    Uri imgUri;
    String city, selectedImagePath;
    SQLiteDatabase database;

    private int teamId;

    private static final int SELECT_PICTURE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_information);

        // grab all fields
        cityField = findViewById(R.id.city);
        nameField = findViewById(R.id.teamName);
        sportField = findViewById(R.id.sport);
        mvpField = findViewById(R.id.mvp);
        img = findViewById(R.id.uploadImage);

        // Buttons
        exit = findViewById(R.id.exit);
        upload = findViewById(R.id.upload);
        delete = findViewById(R.id.delete);
        update = findViewById(R.id.update);

        // grab intent to get city
        Intent intent = getIntent();
        city = intent.getStringExtra("city");

        // open database
        database = openOrCreateDatabase("teams", 0, null);

        loadInformation(city);

        upload.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
            }
        });

        update.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                String cityTxt = cityField.getText().toString(), nameTxt = nameField.getText().toString(),
                        sportTxt = sportField.getSelectedItem().toString(), mvpTxt = mvpField.getText().toString();

                if(cityTxt.equals("")){
                    AlertDialog.Builder build = new AlertDialog.Builder(TeamInformation.this);

                    build.setNeutralButton("CLOSE", null);
                    build.setMessage("City must have a value.");
                    build.setTitle("Incorrect Information");

                    build.show();
                }else{

                    ContentValues cv = new ContentValues();
                    cv.put("city", cityTxt);
                    cv.put("name", nameTxt);
                    cv.put("sport", sportTxt);
                    cv.put("mvp", mvpTxt);

                    cv.put("imageURI", imgUri.toString());

                    database.update("teams", cv, "teamId=" + teamId, null);
                }
            }
        });

        delete.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                SQLiteDatabase database = openOrCreateDatabase("teams", 0, null);
                database.delete("teams", "teamId=" + teamId, null);
                database.close();

                Intent intent = new Intent(v.getContext(), MainActivity.class);
                startActivity(intent);
            }
        });


        // exit app
        exit.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }


    void loadInformation(String city){
        SQLiteDatabase database = openOrCreateDatabase("teams", 0, null);

        Cursor cursor = database.rawQuery("SELECT * FROM teams WHERE city ='" + city + "'", null);

        while(cursor.moveToNext()){
            teamId = cursor.getInt(cursor.getColumnIndex("teamId"));
            System.out.println("team id: " + teamId);

            // need spinner adapter
            ArrayAdapter spinnerAdapter = (ArrayAdapter) sportField.getAdapter();

            cityField.setText(cursor.getString(cursor.getColumnIndex("city")));
            nameField.setText(cursor.getString(cursor.getColumnIndex("name")));

            int spinnerPosition = spinnerAdapter.getPosition(cursor.getString(cursor.getColumnIndex("sport")));
            sportField.setSelection(spinnerPosition);

            mvpField.setText(cursor.getString(cursor.getColumnIndex("mvp")));

            String imgUriString = cursor.getString(cursor.getColumnIndex("imageURI"));
            imgUri = Uri.parse(imgUriString);

            img.setImageURI(imgUri);

        }

        database.close();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                selectedImagePath = getPath(selectedImageUri);
                img.setImageURI(selectedImageUri);
                imgUri = selectedImageUri;
            }
        }
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
