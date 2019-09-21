package com.example.localmirza.v5_fgo_team_analyzer.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class is used to store all data pertaining to a specific skill, including the name of the
 * skill, the cooldown duration of it;s activiation, the list of effects and the list of values for
 * those effects.
 */
public class Skill implements Serializable {
    private String name;
    private int cooldown;
    private ArrayList<String> effects;
    private ArrayList<Double> values;

    public Skill(){
        effects = new ArrayList<>();
        values = new ArrayList<>();

    }

    public Skill(String name, int cooldown, ArrayList<String> effects, ArrayList<Double> values) {
        this.name = name;
        this.cooldown = cooldown;
        this.effects = effects;
        this.values = values;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public void setEffects(ArrayList<String> effects) {
        this.effects = effects;
    }

    public void setValues(ArrayList<Double> values) {
        this.values = values;
    }

    public ArrayList<String> getEffects() {
        return effects;
    }

    public ArrayList<Double> getValues() {
        return values;
    }

    public String getName() {
        return name;
    }

    public int getCooldown() {
        return cooldown;
    }

}
