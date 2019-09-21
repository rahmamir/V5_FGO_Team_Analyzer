package com.example.localmirza.v5_fgo_team_analyzer.model;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;

/**
 * This class is used to store all data pertaining to a charecter or "Servant", including an
 * ArrayList<Skill></Skill>, it's name, attack, health, cost, starAbsorption for critting,
 * the health revived when killed with guts, star generation, the np charge per attack, the npCharge
 * gained when hit, the total defense stat, and it's 3 boolean states (guts for special revive on death,
 *  aliveState to see if servant is alive and evadeState to see if servant dodged an attack
 */
public class Servant implements Parcelable {
    private Double attack, health, cost, starAbsorption, gutsReviveHealth;
    private Double starGeneration, npChargeAttack, npChargeDefense, defense;
    private String name;
    boolean gutsStatus, aliveState, evadeState;

    private ArrayList<Skill> skillList;

    public Servant(){
        skillList = new ArrayList<>();
        gutsStatus = false;
        aliveState = true;
        defense = 0.0;
    }

    protected Servant(Parcel in) {
        if (in.readByte() == 0) {
            attack = null;
        } else {
            attack = in.readDouble();
        }
        if (in.readByte() == 0) {
            health = null;
        } else {
            health = in.readDouble();
        }
        if (in.readByte() == 0) {
            cost = null;
        } else {
            cost = in.readDouble();
        }
        if (in.readByte() == 0) {
            starAbsorption = null;
        } else {
            starAbsorption = in.readDouble();
        }
        if (in.readByte() == 0) {
            gutsReviveHealth = null;
        } else {
            gutsReviveHealth = in.readDouble();
        }
        if (in.readByte() == 0) {
            starGeneration = null;
        } else {
            starGeneration = in.readDouble();
        }
        if (in.readByte() == 0) {
            npChargeAttack = null;
        } else {
            npChargeAttack = in.readDouble();
        }
        if (in.readByte() == 0) {
            npChargeDefense = null;
        } else {
            npChargeDefense = in.readDouble();
        }
        if (in.readByte() == 0) {
            defense = null;
        } else {
            defense = in.readDouble();
        }
        name = in.readString();
        gutsStatus = in.readByte() != 0;
        aliveState = in.readByte() != 0;
        evadeState = in.readByte() != 0;
    }

    public static final Creator<Servant> CREATOR = new Creator<Servant>() {
        @Override
        public Servant createFromParcel(Parcel in) {
            return new Servant(in);
        }

        @Override
        public Servant[] newArray(int size) {
            return new Servant[size];
        }
    };

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public void StateChange(String change, Double changeAmount){
        if(change.equals("guts")){
            gutsStatus = true;
            gutsReviveHealth = changeAmount;
        }
        else if(change.equals("defense")){
            defense = changeAmount;
        }
        else if(change.equals("evade")){
            evadeState = true;
        }
        else if(change.equals("heal")){
            health += changeAmount;
        }

    }

    public void setAttack(Double attack) {
        this.attack = attack;
    }

    public void setHealth(Double health) {
        this.health = health;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public void setStarAbsorption(Double starAbsorption) {
        this.starAbsorption = starAbsorption;
    }

    public void setStarGeneration(double starGeneration) {
        this.starGeneration = starGeneration;
    }

    public void setNpChargeAttack(double npChargeAttack) {
        this.npChargeAttack = npChargeAttack;
    }

    public void setNpChargeDefense(double npChargeDefense) {
        this.npChargeDefense = npChargeDefense;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSkillList(ArrayList<Skill> skillList) {
        this.skillList = skillList;
    }

    public Double getAttack() {
        return attack;
    }

    public Double getHealth() {
        return health;
    }

    public Double getCost() {
        return cost;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Skill> getSkillList() {
        return skillList;
    }

    public Double getStarAbsorption() {
        return starAbsorption;
    }

    public Double getStarGeneration() {
        return starGeneration;
    }

    public Double getNpChargeAttack() {
        return npChargeAttack;
    }

    public Double getNpChargeDefense() {
        return npChargeDefense;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        if (attack == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(attack);
        }
        if (health == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(health);
        }
        if (cost == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(cost);
        }
        if (starAbsorption == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(starAbsorption);
        }
        if (gutsReviveHealth == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(gutsReviveHealth);
        }
        if (starGeneration == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(starGeneration);
        }
        if (npChargeAttack == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(npChargeAttack);
        }
        if (npChargeDefense == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(npChargeDefense);
        }
        if (defense == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(defense);
        }
        parcel.writeString(name);
        parcel.writeByte((byte) (gutsStatus ? 1 : 0));
        parcel.writeByte((byte) (aliveState ? 1 : 0));
        parcel.writeByte((byte) (evadeState ? 1 : 0));
    }

    public Double getDefense() {
        return defense;
    }

    public void setDefense(Double defense) {
        this.defense = defense;
    }
}
