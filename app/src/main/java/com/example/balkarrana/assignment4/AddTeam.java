package com.example.balkarrana.assignment4;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
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

import java.io.ByteArrayOutputStream;

public class AddTeam extends AppCompatActivity {
    SQLiteDatabase database;

    EditText cityField, nameField, mvpField;
    Spinner sportField;
    Button submit, exit, upload;
    String selectedImagePath;
    ImageView img;
    Uri imgUri;

    private static final int SELECT_PICTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_team);

        // open database
        database = openOrCreateDatabase("teams", 0, null);

        // grab all fields
        cityField = findViewById(R.id.city);
        nameField = findViewById(R.id.teamName);
        sportField = findViewById(R.id.sport);
        mvpField = findViewById(R.id.mvp);

        // buttons
        submit = findViewById(R.id.addTeam);
        exit = findViewById(R.id.exit);
        upload = findViewById(R.id.upload);

        // imageview
        img = findViewById(R.id.uploadImage);

        upload.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
            }
        });


        // add team
        submit.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                String cityTxt = cityField.getText().toString(), nameTxt = nameField.getText().toString(),
                        sportTxt = sportField.getSelectedItem().toString(), mvpTxt = mvpField.getText().toString();


                if(cityTxt.equals("")){
                    AlertDialog.Builder build = new AlertDialog.Builder(AddTeam.this);

                    build.setNeutralButton("CLOSE", null);
                    build.setMessage("City must have a value.");
                    build.setTitle("Incorrect Information");

                    build.show();
                }else{
                    database.execSQL("INSERT INTO teams(city, name, sport, mvp, imageURI) VALUES ('" + cityTxt + "','" + nameTxt + "','" + sportTxt + "','" + mvpTxt + "','" + imgUri.toString() +"');");
                    clearAllFields();
                }
            }
        });


        // finish intent
        exit.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                database.close();
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void clearAllFields(){
        cityField.setText("");
        nameField.setText("");
        mvpField.setText("");
        sportField.setSelection(0);
        img.setImageResource(R.drawable.image_not_found);
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

    // convert from bitmap to byte array
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }
}
