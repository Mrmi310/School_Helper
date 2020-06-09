package com.mrmi.schoolhelper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button gradesButton = findViewById(R.id.gradesButton), homeworkButton = findViewById(R.id.homeworkButton);

        //Launch grades activity on grades button click
        gradesButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent gradesIntent = new Intent(MainActivity.this, Grades.class);
                startActivity(gradesIntent);
            }
        });

        //Launch homework activity on homework button click
        homeworkButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent homeworkIntent = new Intent(MainActivity.this, Homework.class);
                startActivity(homeworkIntent);
            }
        });
    }
}
