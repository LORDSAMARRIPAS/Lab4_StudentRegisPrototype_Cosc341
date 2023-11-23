package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity3 extends AppCompatActivity {
    private List<Student> studentRecords; //
    private int currentIndex;
    private TextView stuNum1, Lastname1, Gender2, Division1;
    private Button previous, next, Mainscreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        // Initialize UI components
        previous = findViewById(R.id.previous);
        next = findViewById(R.id.next);
        Mainscreen = findViewById(R.id.Mainscreen);
        stuNum1 = findViewById(R.id.stuNum1);
        Lastname1 = findViewById(R.id.Lastname1);
        Gender2 = findViewById(R.id.Gender2);
        Division1 = findViewById(R.id.Division1);

        studentRecords = new ArrayList<>();
        currentIndex = 0;

        // Fetch the data and populate the list
        CatchData();

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (studentRecords.isEmpty()) {
                    showToastNoData();
                    return;
                }
                currentIndex = (currentIndex + 1) % studentRecords.size();
                displayStudentInfo(studentRecords.get(currentIndex));
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (studentRecords.isEmpty()) {
                    showToastNoData();
                    return;
                }
                currentIndex = (currentIndex - 1 + studentRecords.size()) % studentRecords.size();
                displayStudentInfo(studentRecords.get(currentIndex));
            }
        });

        Mainscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void fetchDataFromFirebase() {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://myproj1-2766a-default-rtdb.firebaseio.com/students");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                studentRecords.clear();

                Log.d("MainActivity3", "Data snapshot: " + dataSnapshot.toString());

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        Student student = snapshot.getValue(Student.class);
                        if (student != null) {
                            studentRecords.add(student);
                        } else {
                            Log.e("MainActivity3", "Null student object received");
                        }
                    } catch (Exception e) {
                        Log.e("MainActivity3", "Error parsing student data", e);
                    }
                }

                if (!studentRecords.isEmpty()) {
                    displayStudentInfo(studentRecords.get(0));
                } else {
                    Log.d("MainActivity3", "No students found in Firebase");
                    showToastNoData();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("MainActivity3", "Database error", databaseError.toException());
                showToastNoData();
            }
        });
    }


    private void CatchData() {
        boolean writeToDatabase = getIntent().getBooleanExtra("writeToDatabase", false);
        if (writeToDatabase) {
            // Fetch data from Firebase
            fetchDataFromFirebase();
        } else {
            // Read data from a local file
            readDatafromFile();
        }
    }



    private void displayStudentInfo(Student student) {
        stuNum1.setText(student.studentNumber);
        Lastname1.setText(student.name);
        Gender2.setText(student.gender);
        Division1.setText(student.division);
    }

    private void showToastNoData() {
        Toast.makeText(this, "No records available.", Toast.LENGTH_SHORT).show();
    }

    private void readDatafromFile() {
        File file = new File(getExternalFilesDir(null), "data.txt"); // Adjust the file path and name

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Parse the line into a Student object
                Student student = parseLineToStudent(line);
                studentRecords.add(student);
            }
            if (!studentRecords.isEmpty()) {
                displayStudentInfo(studentRecords.get(0));
            }
        } catch (IOException e) {
            Log.e("MainActivity3", "Error reading file", e);
            showToastNoData();
        }
    }

    private Student parseLineToStudent(String line) {
        // Split the line based on commas
        String[] parts = line.split(",");

        String studentNumber = parts[0].trim();
        String lastName = parts[1].trim();
        String firstName = parts[2].trim();
        String gender = parts[3].trim();
        String division = parts[4].trim();

        return new Student(studentNumber, lastName, firstName, gender, division);
    }



}

