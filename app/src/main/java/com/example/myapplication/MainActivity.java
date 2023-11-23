package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private Button read, write;
    private CheckBox checkBox;
    private DatabaseReference root; // Global variable for database reference

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkBox = findViewById(R.id.checkBox);
        write = findViewById(R.id.write);
        read = findViewById(R.id.read);

        // Initialize Firebase Database reference
        root = FirebaseDatabase.getInstance().getReference();

        write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean writeToDatabase = checkBox.isChecked();

                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                intent.putExtra("writeToDatabase", writeToDatabase);
                startActivity(intent);
            }
        });

        read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the Read Data screen
                Intent intent = new Intent(MainActivity.this, MainActivity3.class);
                boolean writeToDatabase = checkBox.isChecked();
                intent.putExtra("writeToDatabase", writeToDatabase); // Pass the flag to MainActivity3
                startActivity(intent);
            }
        });
    }

}


