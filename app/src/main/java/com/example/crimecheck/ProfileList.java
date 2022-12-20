package com.example.crimecheck;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProfileList {
    Connection connection;
    String ConnectionResult = "";
    Boolean isSuccess = false;
    List<Profile> data;
    public List<Profile> getList(){
        data = new ArrayList<Profile>();
        try {
            ConnectionHelper connectionHelper = new ConnectionHelper();
            connection = ConnectionHelper.Connection();
            if(connection != null){
                String query = "Select * From Crimes";
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                while(resultSet.next()){
                    data.add(new Profile(resultSet.getInt("Id"),
                            resultSet.getString("Location"),
                            resultSet.getString("CrimeType"),
                            resultSet.getString("Description"),
                            resultSet.getTimestamp("DateTime")));
                }
                ConnectionResult = "Success";
                isSuccess = true;
                Log.e(ConnectionResult, "");
                connection.close();
            }
            else{
                ConnectionResult = "Failed";
                Log.e(ConnectionResult,"");
                connection.close();
            }
        }
        catch (Exception ex){
                ex.printStackTrace();
        }
        return data;
    }
}
