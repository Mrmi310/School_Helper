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
        int subjectCount=0, customSubjectListSize=customSubjectList.size();
        //Loop through all subjects from subject list stored in shared preferences
        for(int i=0; i<customSubjectListSize; ++i)
        {
            CustomSubject subject = customSubjectList.get(i);
            System.out.println("[MRMI]: ADDING SUBJECT TO SUBJECT LIST VIEW");

            LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View subjectView = inflater.inflate(R.layout.subject_grades, null);

            //Text Views of the current subject's view's layout
            TextView subjectNameTextView = subjectView.findViewById(R.id.subjectName), gradeListTextView = subjectView.findViewById(R.id.gradeList), averageGradeTextView = subjectView.findViewById(R.id.subjectAverageGrade);
            subjectNameTextView.setText(subject.subjectName); //Set the subject name
            gradeListTextView.setText("Grades: " + Arrays.toString(subject.gradeList.toArray())); //Display grades from current CustomSubject's gradeList

            //Buttons for adding grades and deleting the subject
            Button deleteSubjectButton = subjectView.findViewById(R.id.deleteSubjectButton), addGradeButton= subjectView.findViewById(R.id.addGradeButton);

            //Call delete subject function for subject at (final) index i in customSubjectList
            final int finalI = i;
            deleteSubjectButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    deleteSubject(finalI);
                }
            });

            //Call addGrade function for subject at (final) index i in customSubjectList
            addGradeButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    activateAddGradePopup(finalI);
                }
            });

            //Check if the average grade can't be calculated (no grades added)
            if(subject.calculateAverageGrade()==0)
                averageGradeTextView.setText("Average grade unavailable");
            else //Display average grade of current subject and add current average grade rounded up (if its decimal point is >=0.5) to the total average grade
            {
                double averageGrade = subject.calculateAverageGrade();
                averageGradeTextView.setText("Average grade: " + String.format("%.2f", averageGrade));
                subjectCount++;
                int averageGradeInt = (int) averageGrade;
                if(averageGrade-averageGradeInt>=0.5)
                    totalAverageGrade+=averageGradeInt+1;
                else
                    totalAverageGrade+=averageGradeInt;
            }

            subjectListLayout.addView(subjectView); //Add the view to the LinearLayout subjectListLayout
        }

        //Display total average grade rounded to 2 decimal places (if possible)
        if(subjectCount>0)
            totalAverageGradeTextView.setText("Total average grade: " + String.format("%.2f", totalAverageGrade/subjectCount));
        else
            totalAverageGradeTextView.setText("Total average grade unavailable.");
    }

    //Creates a CustomSubject, adds it to the customSubjectList and saves the list in shared preferences as a json string using Google's gson API
    private void saveSubject(String subjectName)
    {
        try
        {
            System.out.print("[MRMI]: Adding subject: " + subjectName);
            //Create custom subject and add it to the list
            customSubjectList.add(new CustomSubject(subjectName, 0, new ArrayList<Short>()));
            Gson gson = new Gson();
            String json = gson.toJson(customSubjectList);
            getSharedPreferences("shared preferences", MODE_PRIVATE).edit().putString("Subject list", json).apply();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    //Adds a grade to the current subject
    public void activateAddGradePopup(final int targetSubjectIndex)
    {
        //Pop-up add_data_popup layout which allows the user to input a subject name and cancel/add it
        popupDialog.setContentView(R.layout.add_grade_popup);

        //Buttons and edit text in the add_data_popup
        Button cancelButton = popupDialog.findViewById(R.id.cancelButton);

        //Dismiss popup on cancelButton click
        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                popupDialog.dismiss();
            }
        });

        //TODO: MAKE GRADE SELECTION MORE EFFICENT WITH EVERY GRADE BUTTON USING 1 FUNCTION TO RETURN THEIR VALUE?
        //Changes selectedGrade depending on which grade button is pressed
        final short[] selectedGrade = new short[1]; //Grade the user selected in the add_grade_popup
        Button oneButton = popupDialog.findViewById(R.id.oneButton),
                twoButton = popupDialog.findViewById(R.id.twoButton),
                threeButton = popupDialog.findViewById(R.id.threeButton),
                fourButton = popupDialog.findViewById(R.id.fourButton),
                fiveButton = popupDialog.findViewById(R.id.fiveButton);

        oneButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                selectedGrade[0] = 1;
                System.out.println("[MRMI]: SELECTED GRADE: " + 1);
                //Add selected grade to the currently selected subject which called this function
                customSubjectList.get(targetSubjectIndex).gradeList.add(selectedGrade[0]);
                //Save customSubjectList with added grade in currently selected subject in shared preferences using gson
                Gson gson = new Gson();
                String json = gson.toJson(customSubjectList);
                getSharedPreferences("shared preferences", MODE_PRIVATE).edit().putString("Subject list", json).apply();
                displaySubjects();
                popupDialog.dismiss();
            }
        });
        twoButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                selectedGrade[0] = 2;
                System.out.println("[MRMI]: SELECTED GRADE: " + 2);
                //Add selected grade to the currently selected subject which called this function
                customSubjectList.get(targetSubjectIndex).gradeList.add(selectedGrade[0]);
                //Save customSubjectList with added grade in currently selected subject in shared preferences using gson
                Gson gson = new Gson();
                String json = gson.toJson(customSubjectList);
                getSharedPreferences("shared preferences", MODE_PRIVATE).edit().putString("Subject list", json).apply();
                displaySubjects();
                popupDialog.dismiss();
            }
        });
        threeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                selectedGrade[0] = 3;
                System.out.println("[MRMI]: SELECTED GRADE: " + 3);
                //Add selected grade to the currently selected subject which called this function
                customSubjectList.get(targetSubjectIndex).gradeList.add(selectedGrade[0]);
                //Save customSubjectList with added grade in currently selected subject in shared preferences using gson
                Gson gson = new Gson();
                String json = gson.toJson(customSubjectList);
                getSharedPreferences("shared preferences", MODE_PRIVATE).edit().putString("Subject list", json).apply();
                displaySubjects();
                popupDialog.dismiss();
            }
        });
        fourButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                selectedGrade[0] = 4;
                System.out.println("[MRMI]: SELECTED GRADE: " + 4);
                //Add selected grade to the currently selected subject which called this function
                customSubjectList.get(targetSubjectIndex).gradeList.add(selectedGrade[0]);
                //Save customSubjectList with added grade in currently selected subject in shared preferences using gson
                Gson gson = new Gson();
                String json = gson.toJson(customSubjectList);
                getSharedPreferences("shared preferences", MODE_PRIVATE).edit().putString("Subject list", json).apply();
                displaySubjects();
                popupDialog.dismiss();
            }
        });
        fiveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                selectedGrade[0] = 5;
                System.out.println("[MRMI]: SELECTED GRADE: " + 5);
                //Add selected grade to the currently selected subject which called this function
                customSubjectList.get(targetSubjectIndex).gradeList.add(selectedGrade[0]);
                //Save customSubjectList with added grade in currently selected subject in shared preferences using gson
                Gson gson = new Gson();
                String json = gson.toJson(customSubjectList);
                getSharedPreferences("shared preferences", MODE_PRIVATE).edit().putString("Subject list", json).apply();
                displaySubjects();
                popupDialog.dismiss();
            }
        });

        popupDialog.show();
    }

    //Deletes subject from subject list
    public void deleteSubject(int targetSubjectIndex)
    {
        //Remove the customSubject in customSubjectList at index targetSubjectIndex and save the customSubjectList in user preferences
        customSubjectList.remove(targetSubjectIndex);
        Gson gson = new Gson();
        String json = gson.toJson(customSubjectList);
        getSharedPreferences("shared preferences", MODE_PRIVATE).edit().putString("Subject list", json).apply();

        //Display the subjects again
        displaySubjects();
    }
}
