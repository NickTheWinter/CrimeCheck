package com.example.crimecheck;

import java.sql.Timestamp;

public class Profile {
    int ID;
    String location, crimeType, description;
    Timestamp dateTime;
    public Profile(int _id,String _location, String _crimeType, String _description, Timestamp _dateTime){
        ID = _id;
        location = _location;
        crimeType = _crimeType;
        description = _description;
        dateTime = _dateTime;
    }
}
