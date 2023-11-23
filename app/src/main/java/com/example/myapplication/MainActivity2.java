package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

public class MainActivity2 extends AppCompatActivity {

    private RadioGroup radioGroup;
    private EditText StudentNumber,LastName,FirstName;
    private RadioButton selectedRadioButton;
    private boolean writeToDatabase;
    private Button SubmitB;
    private Spinner Courses_Spin;
    private String selectedDivision;
    private static final int PERMISSION_REQUEST_STORAGE = 1000;

    private void checkForPermissions() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_STORAGE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        radioGroup = findViewById(R.id.radioGroup);
        SubmitB = findViewById(R.id.SubmitB);
        StudentNumber = findViewById(R.id.StudentNumber);
        LastName = findViewById(R.id.LastName);
        FirstName = findViewById(R.id.FirstName);
        Courses_Spin = findViewById(R.id.Courses_Spin);

        Intent intent = getIntent();
        writeToDatabase = intent.getBooleanExtra("writeToDatabase", false);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.division_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Courses_Spin.setAdapter(adapter);
        Courses_Spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDivision = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedDivision = "";
            }
        });

        checkForPermissions();

        SubmitB.setOnClickListener(view -> {
            String studentNum = StudentNumber.getText().toString();
            String lastName = LastName.getText().toString();
            String firstName = FirstName.getText().toString();

            if (!isValidStudent(studentNum, firstName, lastName)) {
                return;
            }
            int radioId = radioGroup.getCheckedRadioButtonId();
            selectedRadioButton = findViewById(radioId);
            if (selectedRadioButton == null) {
                Toast.makeText(MainActivity2.this, "Please select your gender", Toast.LENGTH_SHORT).show();
                return;
            }

            String gender = selectedRadioButton.getText().toString();
            String data = studentNum + "," + lastName + "," + firstName + "," + gender + "," + selectedDivision + "\n";

            if (writeToDatabase) {
                saveDataToFirebase(studentNum, lastName, firstName, gender, selectedDivision);
            } else {
                writeToExternalFile(data);
            }

            finish();
        });


    }

        public void checkButton(View view) {
            int radioId = radioGroup.getCheckedRadioButtonId();
            selectedRadioButton = findViewById(radioId);
        }

        //Check if the data is enter correctly/otherwise display a toast message
    public boolean isValidStudent(String studentNumber, String firstName, String lastName) {
        // Check if the student number is exactly 8 digits
        if (studentNumber == null || studentNumber.length() != 8 || !studentNumber.matches("\\d+")) {
            Toast.makeText(this, "Please enter a valid 8-digit student number.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check if the first name is entered
        if (firstName == null || firstName.trim().isEmpty()) {
            Toast.makeText(this, "Please enter your first name.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check if the last name is entered
        if (lastName == null || lastName.trim().isEmpty()) {
            Toast.makeText(this, "Please enter your last name.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    //write data in the cellphone storage, make a data.txt file to record the student data
    private void writeToExternalFile(String data) {
        // App-specific external storage directory which doesn't need WRITE_EXTERNAL_STORAGE permission
        File file = new File(getExternalFilesDir(null), "data.txt");

        try {
            boolean fileCreated = file.exists() || file.createNewFile();
            if (fileCreated) {
                try (FileOutputStream fos = new FileOutputStream(file, true);
                     OutputStreamWriter writer = new OutputStreamWriter(fos)) {
                    writer.append(data);
                    Toast.makeText(this, "Data saved to external file", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Error creating new file", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Log.e("MainActivity2", "Error saving data", e);
            Toast.makeText(this, "Error saving data", Toast.LENGTH_SHORT).show();
        }
    }
    //Store data in Firebase when Database check box is been checked
    private void saveDataToFirebase(String studentNumber, String lastName, String firstName, String gender, String division) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("students");
        String id = databaseReference.push().getKey();

        if (id == null) {
            Toast.makeText(MainActivity2.this, "Failed to generate Firebase entry key", Toast.LENGTH_SHORT).show();
            return;
        }

        Student student = new Student(studentNumber, firstName, lastName, gender, division);
        databaseReference.child(id).setValue(student)
                .addOnSuccessListener(aVoid -> Toast.makeText(MainActivity2.this, "Data saved to Firebase", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(MainActivity2.this, "Failed to save data to Firebase", Toast.LENGTH_SHORT).show());
    }

}
//Student class to store data of each student
class Student {
    public String studentNumber, name, gender, division;
    public Student() {
    }

    public Student(String studentNumber, String firstName, String lastName, String gender, String division) {
        this.studentNumber = studentNumber;
        this.name = firstName + " " + lastName;
        this.gender = gender;
        this.division = division;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("studentNumber", studentNumber);
        result.put("name", name);
        result.put("gender", gender);
        result.put("division", division);

        return result;
    }
}