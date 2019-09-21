package com.example.localmirza.v5_fgo_team_analyzer.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Score implements Parcelable {

    private int overallScore;
    private String formationAdvantages;
    private String formationDisadvantages;
    private String formationRecommendations;

    /**
     * This class stores all data pertaining to evaluating a formation including it's overall score,
     * the advantages, disadvantages and recommendations
     * @param overallScore the int total score of the formation
     * @param formationAdvantages
     * @param formationDisadvantages
     * @param formationRecommendations
     */
    public Score(int overallScore, String formationAdvantages, String formationDisadvantages, String formationRecommendations) {
        this.overallScore = overallScore;
        this.formationAdvantages = formationAdvantages;
        this.formationDisadvantages = formationDisadvantages;
        this.formationRecommendations = formationRecommendations;
    }

    public Score(){

    }

    protected Score(Parcel in) {
        overallScore = in.readInt();
        formationAdvantages = in.readString();
        formationDisadvantages = in.readString();
        formationRecommendations = in.readString();
    }

    public static final Creator<Score> CREATOR = new Creator<Score>() {
        @Override
        public Score createFromParcel(Parcel in) {
            return new Score(in);
        }

        @Override
        public Score[] newArray(int size) {
            return new Score[size];
        }
    };

    public int getOverallScore() {
        return overallScore;
    }

    public String getFormationAdvantages() {
        return formationAdvantages;
    }

    public String getFormationDisadvantages() {
        return formationDisadvantages;
    }

    public String getFormationRecommendations() {
        return formationRecommendations;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(overallScore);
        parcel.writeString(formationAdvantages);
        parcel.writeString(formationDisadvantages);
        parcel.writeString(formationRecommendations);
    }
}
