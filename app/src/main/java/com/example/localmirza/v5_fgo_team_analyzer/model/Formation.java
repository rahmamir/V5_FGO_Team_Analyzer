package com.example.localmirza.v5_fgo_team_analyzer.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Formation implements Parcelable{

    private String partyName;
    public ArrayList<Servant> formationServantsList;
    public int[] servantPositions, craftEssencePositions;
    private static Score score;
    private String[] allAdvantages, allDisAdvantages, allRecommendations;
    ArrayList<Integer> formationAdvantages;
    ArrayList<String> allAvailableServantSkills;
    String partyAdvantages, partyDisadvantages, partyRecommendations;
    Boolean[] selectAdvantages;

    /**
     * The constructor for the Formation involves all of the necessary information to create a Formation
     * object, this includes the partyName, the list of 3 Servants creating the formation,
     * the int[] of Servant and Craft Essence positions, and the Score object to store the score
     * @param partyName
     * @param formationServantsList
     * @param servantPositions
     * @param craftEssencePositions
     * @param score
     */
    public Formation(String partyName, ArrayList<Servant> formationServantsList,
                     int[] servantPositions, int[] craftEssencePositions, Score score) {
        this.partyName = partyName;
        this.formationServantsList = formationServantsList;
        this.servantPositions = servantPositions;
        this.craftEssencePositions = craftEssencePositions;
        this.score = score;
        formationAdvantages = new ArrayList<>();
        allAvailableServantSkills = new ArrayList<>();
        allAdvantages = new String[7];
        allDisAdvantages = new String[7];
        allRecommendations = new String[7];
        partyAdvantages = "";
        partyDisadvantages = "";
        partyRecommendations = "";
        populateFormationAnalysis();
        //analyzeFormation();
    }

    protected Formation(Parcel in) {
        partyName = in.readString();
        servantPositions = in.createIntArray();
        craftEssencePositions = in.createIntArray();
        allAdvantages = in.createStringArray();
        allDisAdvantages = in.createStringArray();
        allRecommendations = in.createStringArray();
        allAvailableServantSkills = in.createStringArrayList();
    }

    public static final Creator<Formation> CREATOR = new Creator<Formation>() {
        @Override
        public Formation createFromParcel(Parcel in) {
            return new Formation(in);
        }

        @Override
        public Formation[] newArray(int size) {
            return new Formation[size];
        }
    };

    public int[] getCraftEssencePositions() {
        return craftEssencePositions;
    }

    public String getPartyName() {
        return partyName;
    }

    public ArrayList<Servant> getFormationServantsList() {
        return formationServantsList;
    }

    public int[] getServantPositions() {
        return servantPositions;
    }

    public Score getScore(){
        return score;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }

    /**
     * The below method analyzes the formation and determines the score of the formation. This is done
     * by looping through each of the 3 Skills of each of the 3 Servants, and adding the effects of each
     * skill into a list of effects
     * There is already a predefined list of total advantages, disadvantages and recommendations
     * The effects list from above is detected in a loop, and the party is awarded certain advantages,
     * depending upon what skills the party possesses.
     */
    public void analyzeFormation(){

        for(int i = 0; i < 3; i++){//3 servants
            for(int j = 0; j < 3; j++){//3 skills
                Skill tempSkill = formationServantsList.get(i).getSkillList().get(j);
                for(int k=0; k < tempSkill.getEffects().size(); k++){//effects of each skill
                    allAvailableServantSkills.add(tempSkill.getEffects().get(k));
                }
            }
        }
        selectAdvantages = new Boolean[7];
        for(int i = 0; i < 7; i++){
            selectAdvantages[i]=false;
        }

        for(int i = 0; i < allAvailableServantSkills.size(); i++){
            addSkillAdvantage(allAvailableServantSkills.get(i));
        }

        int advantageCount = 0;
        StringBuilder tempPartyAdvantages, tempPartyDisadvantages, tempPartyRecommendations;
        tempPartyAdvantages = new StringBuilder();
        tempPartyDisadvantages = new StringBuilder();
        tempPartyRecommendations = new StringBuilder();

        for(int i = 0; i < 7; i ++){
            if(selectAdvantages[i]){
                Log.d("TAGFORMATION", allAdvantages[i]);
                tempPartyAdvantages.append(allAdvantages[i]);
                advantageCount++;
            }
            else{
                tempPartyDisadvantages.append(allDisAdvantages[i]);
                tempPartyRecommendations.append(allRecommendations[i]);
            }
        }
        partyAdvantages = tempPartyAdvantages.toString();
        partyDisadvantages = tempPartyDisadvantages.toString();
        partyRecommendations = tempPartyRecommendations.toString();

        Log.d("TAGFORMATION", "advantagecount = " + String.valueOf(advantageCount));
        score = new Score(advantageCount, partyAdvantages, partyDisadvantages,
                partyRecommendations);
    }

    /**
     * contains the predefined list of all advantages, disadvantages, and recommendations
     */
    private void populateFormationAnalysis() {
        allAdvantages[0]="Able to ignore enemy NP Attacks\n";
        allAdvantages[1]="Has healing capacity\n";
        allAdvantages[2]="Capable of NP Spamming\n";
        allAdvantages[3]="Contains some crit support\n";
        allAdvantages[4]="Enables strong attacker \n";
        allAdvantages[5]="Provides strong team buffs, very useful\n";
        allAdvantages[6]="Able to delay enemy NP, buys a turn or two\n";
        allDisAdvantages[0]="Vulnerable to enemy NPs\n";
        allDisAdvantages[1]="No Healing available\n";
        allDisAdvantages[2]="Long wait time during NPs\n";
        allDisAdvantages[3]="No crit support thus lower damage\n";
        allDisAdvantages[4]="No strong attacker available \n";
        allDisAdvantages[5]="No Team buffs, unreliable damage \n";
        allDisAdvantages[6]="Unable to control enemy NPs, faster enemies will NP spam\n";
        allRecommendations[0]="Focus on bursting the enemy down with crits or NPs\n";
        allRecommendations[1]="Use defensive buffs or utility options such as stun and NP seal\n";
        allRecommendations[2]="Use CEs which boost NP gain\n";
        allRecommendations[3]="Buff-stack a single attacker to make up for lower damage\n";
        allRecommendations[4]="Go for the long game, outlast the enemy and slowly chip them down\n";
        allRecommendations[5]="Save buffs until attacker has all cards, then triple buff and fire\n";
        allRecommendations[6]="Either use NP ignore skills or burst them down before they can fires\n";
    }

    /**
     * Checks each available skill and gives certain advantages based on the skills available.
     * @param currentSkill
     */
    private void addSkillAdvantage(String currentSkill){
        if(currentSkill.equals("Guts") || currentSkill.equals("Evade")){
            selectAdvantages[0]=true;
        }
        else if(currentSkill.equals("Heal")){
            selectAdvantages[1]=true;
        }
        else if(currentSkill.equals("NP Charge")){
            selectAdvantages[2]=true;
        }
        else if(currentSkill.equals("Crit Damage") || currentSkill.equals("Crit Gather")
                || currentSkill.equals("Crit Up")){
            selectAdvantages[3]=true;
        }
        else if(currentSkill.equals("Attack Up") || currentSkill.equals("Arts Up")
                || currentSkill.equals("Buster Up")){
            selectAdvantages[4]=true;;
        }
        else if(currentSkill.equals("All Attack Up") || currentSkill.equals("All Defense Up")){
            selectAdvantages[5]=true;
        }
        else if(currentSkill.equals("Seal Enemy NP ") || currentSkill.equals("Lower Enemy NP")
                || currentSkill.equals("Lower Enemy NP Strength") || currentSkill.equals("Stun")){
            selectAdvantages[6]=true;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(partyName);
        parcel.writeIntArray(servantPositions);
        parcel.writeIntArray(craftEssencePositions);
        parcel.writeStringArray(allAdvantages);
        parcel.writeStringArray(allDisAdvantages);
        parcel.writeStringArray(allRecommendations);
        parcel.writeStringList(allAvailableServantSkills);
    }


}
