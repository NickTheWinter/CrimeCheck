package com.example.crimecheck;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class checkCrime extends AppCompatActivity {
    private int CurrentID;
    private TextView crimeTypes;
    private TextView crimeDef;
    private TextView dateTime;
    private Button backButton;
    private ConnectionHelper connectionHelper;
    private Connection connection;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_crime);
        CurrentID = MainActivity.CurrentId;
        crimeTypes = findViewById(R.id.CrimeTypes);
        crimeDef = findViewById(R.id.CrimeDef);
        dateTime = findViewById(R.id.DateTime);
        backButton = findViewById(R.id.BackButton);
        InitListners();
        UploadData();
    }

    private void UploadData() {
        try {
            connectionHelper = new ConnectionHelper();
            connection = ConnectionHelper.Connection();
            String query = "Select * from Crimes where Id = " + CurrentID;
            if(connection != null)
            {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                resultSet.next();
                crimeTypes.setText(resultSet.getString("CrimeType"));
                crimeDef.setText(resultSet.getString("Description"));
                dateTime.setText(resultSet.getString("DateTime"));
                connection.close();
            }
        }
        catch (Exception ex)
        {
            Toast.makeText(this, "Failed to upload data", Toast.LENGTH_SHORT).show();
        }
    }

    private void InitListners(){
        backButton.setOnClickListener(v -> {
            this.finish();
        });
    }

}
