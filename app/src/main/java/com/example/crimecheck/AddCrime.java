package com.example.crimecheck;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.location.Location;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;

public class AddCrime extends AppCompatActivity {
    private Button addButton;
    private Button backButton;
    private Spinner crimeTypes;
    private EditText definitionText;
    private TextView leftWordCount;
    private int wordCount;
    private ConnectionHelper connectionHelper;
    private Connection connection;
    private Location location;
    private String currentLocation;
    private String crimeType;
    private String description;
    private Timestamp dateTime;
    private View v;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_crime);
        Bundle arguments = getIntent().getExtras();
        try {
            currentLocation = arguments.get("location").toString();
        }
        catch (NullPointerException ex){
            Toast.makeText(this,"Cannot find location. Make a move",Toast.LENGTH_SHORT);
        }

        addButton = findViewById(R.id.AddButton);
        backButton = findViewById(R.id.BackButton);
        crimeTypes = findViewById(R.id.CrimeTypes);
        definitionText = findViewById(R.id.CrimeDef);
        leftWordCount = findViewById(R.id.LeftWordCount);

        java.util.Date date = new java.util.Date();
        dateTime = new Timestamp(date.getTime());
        InitListners();
        InitSpinner();
    }
    private void PostData()
    {
        try {
            connectionHelper = new ConnectionHelper();
            connection = ConnectionHelper.Connection();
            String query = "Insert Into Crimes(Location, CrimeType,Description,DateTime) Values('"+ currentLocation
                    + "', '"+crimeType+"', '"+description+"', '"+dateTime+"')";
            if(connection != null)
            {
                Statement statement = connection.createStatement();
                statement.execute(query);
                connection.close();
                Toast.makeText(this, "Data was uploaded data", Toast.LENGTH_SHORT).show();
                ExitActivity(v);
            }
        }
        catch (Exception ex)
        {
            Toast.makeText(this, "Failed to upload data", Toast.LENGTH_SHORT).show();
        }
    }

    private void InitSpinner()
    {
        ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(this, R.array.crime_types_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        crimeTypes.setAdapter(adapter);
    }
    private void ExitActivity(View v){
        this.finish();
    }
    private void InitListners(){
        addButton.setOnClickListener(v -> {
                crimeType = crimeTypes.getSelectedItem().toString();
                description = definitionText.getText().toString();
                PostData();
        });
        backButton.setOnClickListener(v -> {
            ExitActivity(v);
        });
        definitionText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                wordCount = definitionText.getText().length();
                leftWordCount.setText(String.valueOf(200 - wordCount));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    
}
