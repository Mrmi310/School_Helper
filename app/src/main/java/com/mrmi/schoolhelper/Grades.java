package com.mrmi.schoolhelper;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.List;

public class Grades extends AppCompatActivity
{
    Dialog popupDialog; //Used for adding a new subject to the scrollview

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grades);

        popupDialog = new Dialog(this);
    }

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
                LinearLayout subjectList = findViewById(R.id.subjectList);
                LayoutInflater inflater =  (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                /*Create new subject using the subject_grades layout and setting its grade list to an empty string, the grade average to none and the subject name
                to the inputted subject name from the dialog popup*/
                View subjectView = inflater.inflate(R.layout.subject_grades, null);
                TextView subjectName = subjectView.findViewById(R.id.subjectName), gradeList = subjectView.findViewById(R.id.gradeList), averageGrade = subjectView.findViewById(R.id.subjectAverageGrade);

                subjectName.setText(newSubjectName.getText().toString());
                gradeList.setText("Grades: ");
                averageGrade.setText("Average grade: ");

                subjectList.addView(subjectView);

                popupDialog.dismiss();
            }
        });

        popupDialog.show();
    }
}
