package com.example.crimecheck;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionHelper
{
    static String ip = "ngknn.ru", port = "1433",
            database = "Zimenkov_CrimeCheck", userName = "33П", userPassword = "12357";

    public static Connection Connection()
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        try
        {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            String ConnectionURL = "jdbc:jtds:sqlserver://" + ip + ":" + port + ";" + "databasename=" + database + ";user=" + userName + ";password=" + userPassword + ";";
            connection = DriverManager.getConnection(ConnectionURL);


        }
        catch (Exception ex)
        {
            Log.e("Error", ex.getMessage());
        }
        return connection;
    }
}
