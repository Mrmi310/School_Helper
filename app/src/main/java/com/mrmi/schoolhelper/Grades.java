package com.mrmi.schoolhelper;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.Inflater;

public class Grades extends AppCompatActivity
{
    Dialog popupDialog; //Used for adding a new subject to the scrollview
    ArrayList<CustomSubject> customSubjectList; //Used for storing subject views in user preferences

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grades);

        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //Lock activity into portrait mode
        popupDialog = new Dialog(this);

        loadSubjects();
        displaySubjects();
    }

    //Calls save and display subject functions for a new subject created using the ADD SUBJECT button
    public void addSubject(View v)
    {
        //Pop-up add_data_popup layout which allows the user to input a subject name and cancel/add it
        popupDialog.setContentView(R.layout.add_data_popup);

        //Buttons and edit text in the add_data_popup
        Button cancelButton = popupDialog.findViewById(R.id.cancelButton), addButton = popupDialog.findViewById(R.id.addButton);
        final EditText newSubjectName = popupDialog.findViewById(R.id.insertData);

        //Dismiss popup on cancelButton click
        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                popupDialog.dismiss();
            }
        });

        //TODO: Add a new subject named subjectName with empty grades and no grade average to the scrollView using the subject_grades layout
        addButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                saveSubject(newSubjectName.getText().toString());
                displaySubjects();
                popupDialog.dismiss();
            }
        });

        popupDialog.show();
    }

    //Loads subject Views stored in shared preferences using google's gson API into customSubjectList
    private void loadSubjects()
    {
        Gson gson = new Gson();
        String json = getSharedPreferences("shared preferences", MODE_PRIVATE).getString("Subject list", null);

        //Get custom subject list from shared preferences
        Type type = new TypeToken<ArrayList<CustomSubject>>() {}.getType();
        customSubjectList = gson.fromJson(json, type);

        //Creates a new custom subject list if none exists in shared preferences
        if (customSubjectList == null)
        {
            customSubjectList = new ArrayList<>();
            System.out.println("[MRMI]: SET SUBJECTARRAYLIST TO NEW ARRAYLIST");
        }
    }

    //Displays saved custom subjects as inflated views in the subjectList LinearLayout
    private void displaySubjects()
    {
        LinearLayout subjectListLayout = findViewById(R.id.subjectList);
        subjectListLayout.removeAllViews(); //Clears all views to prevent duplication when displaying new ones

        TextView totalAverageGradeTextView = findViewById(R.id.averageGradeText);
        double totalAverageGrade=0;
        int subjectCount=0;

        //Loop through all subjects from subject list stored in shared preferences
        for(CustomSubject subject : customSubjectList)
        {
            System.out.println("[MRMI]: ADDING SUBJECT TO SUBJECT LIST VIEW");

            LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View subjectView = inflater.inflate(R.layout.subject_grades, null);

            //Text Views of the current subject's view's layout
            TextView subjectNameTextView = subjectView.findViewById(R.id.subjectName), gradeListTextView = subjectView.findViewById(R.id.gradeList), averageGradeTextView = subjectView.findViewById(R.id.subjectAverageGrade);

            subjectNameTextView.setText(subject.subjectName); //Set the subject name
            gradeListTextView.setText("Grades: " + Arrays.toString(subject.gradeList.toArray())); //Display grades from current CustomSubject's gradeList
            //Check if the average grade can't be calculated (no grades added)
            if(subject.calculateAverageGrade()==0)
                averageGradeTextView.setText("Average grade unavailable");
            else //Display average grade of current subject and add current average grade rounded up (if its decimal point is >=0.5) to the total average grade
            {
                double averageGrade = subject.calculateAverageGrade();
                averageGradeTextView.setText("Average grade: " + averageGrade);
                subjectCount++;
                int averageGradeInt = (int) averageGrade;
                if(averageGrade-averageGradeInt>=0.5)
                    totalAverageGrade+=averageGradeInt+1;
                else
                    totalAverageGrade+=averageGradeInt;
            }

            subjectListLayout.addView(subjectView); //Add the view to the LinearLayout subjectListLayout
            if(subjectCount>0)
                totalAverageGradeTextView.setText("Total average grade: " + String.format("%.2f", totalAverageGrade/subjectCount)); //Display total average grade rounded to 2 decimal places
            else
                totalAverageGradeTextView.setText("Total average grade unavailable.");
        }
    }

    //Creates a CustomSubject, adds it to the customSubjectList and saves the list in shared preferences as a json string using Google's gson API
    private void saveSubject(String subjectName)
    {
        try
        {
            System.out.print("[MRMI]: Adding subject: " + subjectName);
            //Create custom subject and add it to the list
            customSubjectList.add(new CustomSubject(subjectName, 0, new ArrayList<Integer>()));
            Gson gson = new Gson();
            String json = gson.toJson(customSubjectList);
            getSharedPreferences("shared preferences", MODE_PRIVATE).edit().putString("Subject list", json).apply();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
