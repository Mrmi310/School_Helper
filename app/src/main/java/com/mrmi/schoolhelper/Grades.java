package com.mrmi.schoolhelper;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Grades extends AppCompatActivity
{
    Dialog popupDialog; //Used for adding a new subject to the scrollview

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grades);

        Button addSubject = findViewById(R.id.gradesButton);

        popupDialog = new Dialog(this);
    }

    public void addSubject(View v)
    {
        //Pop-up add_data_popup layout which allows the user to input a subject name and cancel/add it
        popupDialog.setContentView(R.layout.add_data_popup);

        //Buttons and edit text in the add_data_popup
        Button cancelButton = popupDialog.findViewById(R.id.cancelButton), addButton = popupDialog.findViewById(R.id.addButton);
        EditText subjectName = popupDialog.findViewById(R.id.insertData);

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
                popupDialog.dismiss();
            }
        });

        popupDialog.show();
    }
}
