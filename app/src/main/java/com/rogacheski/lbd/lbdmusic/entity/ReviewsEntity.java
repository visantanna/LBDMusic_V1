package com.rogacheski.lbd.lbdmusic.entity;

import java.util.Date;

/**
 * Created by vis_a on 28-Apr-17.
 */

public class ReviewsEntity {
    float iGrade;
    String description;
    Date dData_Review;
    String sEvaluators_Name;
    String sEvaluatorsImage;

    public String getsEvaluatorsImage() {
        return sEvaluatorsImage;
    }

    public void setsEvaluatorsImage(String sEvaluatorsImage) {
        this.sEvaluatorsImage = sEvaluatorsImage;
    }


    public float getiGrade() {
        return iGrade;
    }

    public void setiGrade(float iGrade) {
        this.iGrade = iGrade;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getdData_Review() {
        return dData_Review;
    }

    public void setdData_Review(Date dData_Review) {
        this.dData_Review = dData_Review;
    }

    public String getsEvaluators_Name() {
        return sEvaluators_Name;
    }

    public void setsEvaluators_Name(String sEvaluators_Name) {
        this.sEvaluators_Name = sEvaluators_Name;
    }
}
