package com.mrmi.schoolhelper;

import com.google.gson.annotations.SerializedName;

import java.util.Calendar;

public class CustomAssignment
{
    @SerializedName("Name")
    public String assignmentName;

    @SerializedName("Due date String")
    public String dueDateString;

    //Constructor
    public CustomAssignment(String name, String dueString)
    {
        assignmentName=name;
        dueDateString=dueString;
    }

    //Calculates remaining time until date due using dateDueString and the current date and returns the result as a string
    public long calculateRemainingTime()
    {
        Calendar currentDate = Calendar.getInstance(); //Current date
        Calendar dueDateCalendar = Calendar.getInstance();
        String[] items1 = dueDateString.split("/"); //Split the dateDueString into 3 parts (month, day, year)

        //Set dueDateCalendar's day, month and year properties to the ones from the dateDueString
        String day = items1[0], month = items1[1], year = items1[2];
        dueDateCalendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
        dueDateCalendar.set(Calendar.MONTH, Integer.parseInt(month)-1);
        dueDateCalendar.set(Calendar.YEAR, Integer.parseInt(year));

        //Return the difference in milliseconds of the two calendars converted to days (by dividing by 60*60*24*1000) as a string
        return ((dueDateCalendar.getTimeInMillis()-currentDate.getTimeInMillis())/86400000);
    }
}
