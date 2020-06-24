package com.mrmi.schoolhelper;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Homework extends AppCompatActivity
{
    Dialog popupDialog; //Used for adding a new assignment to the scrollview
    ArrayList<CustomAssignment> assignmentList; //Used for storing assignment views in user preferences

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homework);

        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED); //Lock activity into portrait mode
        popupDialog = new Dialog(this);

        loadAssignments();
        displayAssignments();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        displayAssignments();
    }

    //Calls save and display subject functions for a new assignments created using the ADD ASSIGNMENT button
    public void addAssignment(View v)
    {
        //Pop-up add_data_popup layout which allows the user to input a subject name and cancel/add it
        popupDialog.setContentView(R.layout.add_assignment_popup);

        //Buttons, edit text and calendar in the add_assignment_popup
        Button cancelButton = popupDialog.findViewById(R.id.cancelButton), addButton = popupDialog.findViewById(R.id.addButton);
        final EditText newAssignmentNameText = popupDialog.findViewById(R.id.assignmentEditText);
        final CalendarView calendarView = popupDialog.findViewById(R.id.calendarView);

        //String of the date due to be added to the new CustomAssignment
        final String[] dds = new String[1];
        dds[0]="";

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener()
        {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth)
            {
                dds[0]="";
                ++month; //Increase the month value by 1 because the months begin from 0 in CalendarViews
                System.out.println("[MRMI]: CHANGED DATE");
                //Create the dayDueString: add zeroes if needed (if the month or day) ints are < 10 for more convenient splicing.
                if(dayOfMonth<10)
                    dds[0]+="0";
                dds[0]+=(dayOfMonth+"/");
                if(month<10)
                    dds[0]+="0";
                dds[0]+=(month+"/"+year);
            }
        });
        //Dismiss popup on cancelButton click
        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                popupDialog.dismiss();
            }
        });

        //Save current assignments in shared preferences and add display assignments again
        addButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(dds[0]!="")
                {
                    saveAssignment(newAssignmentNameText.getText().toString(), dds[0]);
                    displayAssignments();
                    popupDialog.dismiss();
                }
            }
        });

        popupDialog.show();
    }

    //Creates a CustomAssignment, adds it to the assignmentList and saves the list in shared preferences as a json string using Google's gson API
    private void saveAssignment(String assignmentName, String dueDateString)
    {
        try
        {
            System.out.println("[MRMI]: Adding assignment: " + assignmentName);

            //Create CustomAssignment and add it to the assignmentList
            CustomAssignment assignment = new CustomAssignment(assignmentName, dueDateString);
            assignmentList.add(assignment);

            //Schedule notification for newly added assignment
            scheduleAssignmentNotification(assignment);


            //Save the edited assignmentList
            Gson gson = new Gson();
            String json = gson.toJson(assignmentList);
            getSharedPreferences("shared preferences", MODE_PRIVATE).edit().putString("Assignment list", json).apply();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    //Loads assignment Views stored in shared preferences using google's gson API into assignmentList
    private void loadAssignments()
    {
        Gson gson = new Gson();
        String json = getSharedPreferences("shared preferences", MODE_PRIVATE).getString("Assignment list", null);

        //Get assignment list from shared preferences
        Type type = new TypeToken<ArrayList<CustomAssignment>>() {}.getType();
        assignmentList = gson.fromJson(json, type);

        //Creates a new assignment list if none exists in shared preferences
        if (assignmentList == null)
        {
            assignmentList = new ArrayList<>();
            System.out.println("[MRMI]: SET SUBJECTARRAYLIST TO NEW ARRAYLIST");
        }
    }

    //Displays saved assignments as inflated views in the assignmentListLayout LinearLayout
    private void displayAssignments()
    {
        LinearLayout assignmentListLayout = findViewById(R.id.assignmentListLayout);
        assignmentListLayout.removeAllViews(); //Clears all views to prevent duplication when displaying new ones

        int assignmentListSize = assignmentList.size();

        //Loop through all assignments in assignment list stored in shared preferences
        for (int i = 0; i < assignmentListSize; ++i)
        {
            CustomAssignment assignment = assignmentList.get(i);
            System.out.println("[MRMI]: ADDING ASSIGNMENT TO ASSIGNMENT LIST VIEW");

            LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View assignmentView = inflater.inflate(R.layout.assignment_view, null);
            //Text Views of the current assignment's view's layout
            TextView assignmentNameTextView = assignmentView.findViewById(R.id.assignmentNameText), dueDateText = assignmentView.findViewById(R.id.dueDateText), remainingTimeText = assignmentView.findViewById(R.id.remainingTimeText);
            assignmentNameTextView.setText(assignment.assignmentName); //Set the subject name
            dueDateText.setText("DATE DUE: " + assignment.dueDateString); //Display grades from current CustomSubject's gradeList
            remainingTimeText.setText("REMAINING TIME: " + assignment.calculateRemainingTime() + " days."); //Display time left until assignment's date due

            //Button which deletes the current assignment
            Button deleteAssignmentButton = assignmentView.findViewById(R.id.deleteAssignmentButton);

            //Call delete assignment function for assignment at (final) index i in assignmentList
            final int finalI = i;
            deleteAssignmentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteAssignment(finalI);
                }
            });

            assignmentListLayout.addView(assignmentView); //Add the view to the LinearLayout subjectListLayou
        }
    }

    //Deletes assignment from assignmentList and rewrites the list in user preferences
    private void deleteAssignment(int targetAssignmentIndex)
    {
        //Remove the customAssignment in assignmentList at index targetAssignmentIndex and save the assignmentList in shared preferences
        assignmentList.remove(targetAssignmentIndex);
        Gson gson = new Gson();
        String json = gson.toJson(assignmentList);
        getSharedPreferences("shared preferences", MODE_PRIVATE).edit().putString("Assignment list", json).apply();

        //Display the assignments again
        displayAssignments();
    }

    //Schedule assignment notification for 2 days before the assignment's due date
    private void scheduleAssignmentNotification(CustomAssignment assignment)
    {
        System.out.println("[MRMI]: ENTERED SCHEDULE ASSIGNMENT");

        Intent myIntent = new Intent(Homework.this, NotificationReceiver.class);
        int uniqueID = (int) System.currentTimeMillis();
        myIntent.putExtra("Assignment name", assignment.assignmentName);
        //Launch AlarmNotificationReceiver using a pending intent with a unique ID to allow multiple notifications with one alarm manager
        PendingIntent pendingIntent = PendingIntent.getBroadcast(Homework.this, uniqueID, myIntent, 0);

        //Schedule alarm to fire 2 days before the due date of the current assignment
        System.out.println("[MRMI]: Scheduled notification for " + (assignment.calculateRemainingTime()-2) +" days.");
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+(assignment.calculateRemainingTime()-2)*86400000, pendingIntent);

    }
}
