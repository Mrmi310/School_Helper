package com.mrmi.schoolhelper;

import android.view.View;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class CustomSubject
{
    @SerializedName("Name")
    public String subjectName;

    @SerializedName("Average")
    public int averageGrade;

    @SerializedName("Grades")
    public ArrayList<Integer> gradeList;

    //Constructor
    public CustomSubject(String name, int average, ArrayList<Integer> grades)
    {
        subjectName=name;
        averageGrade=average;
        gradeList=grades;
    }

    //Calculates average grade
    public double calculateAverageGrade()
    {
        if(gradeList.size()==0)
            return 0;
        double sum=0;
        for(int grade : gradeList)
            sum+=grade;
        return sum/gradeList.size();
    }
}
