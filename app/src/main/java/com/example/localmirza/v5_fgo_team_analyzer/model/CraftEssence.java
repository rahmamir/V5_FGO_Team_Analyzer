package com.example.localmirza.v5_fgo_team_analyzer.model;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.Image;

import com.example.localmirza.v5_fgo_team_analyzer.R;

import java.util.ArrayList;

import static com.example.localmirza.v5_fgo_team_analyzer.MainActivity.listOfCraftEssences;

public class CraftEssence {

    private String craftEssenceName, craftEssenseEffect;
    private Drawable craftEssenceImage;

    /**
     * This class stores craft essences, which gives boosts to the available servants
     * It contains the name, effect and drawable image of the craft essence, the eff
     */
    public CraftEssence(String craftEssenceName, String craftEssenseEffect, Drawable craftEssenceImage) {
        this.craftEssenceName = craftEssenceName;
        this.craftEssenseEffect = craftEssenseEffect;
        this.craftEssenceImage = craftEssenceImage;
    }

    public String getCraftEssenceName() {
        return craftEssenceName;
    }

    public String getCraftEssenseEffect() {
        return craftEssenseEffect;
    }

    /**
     * grabs the craft essence drawables and stores them in an array of Craft Essences
     * (Conext) -> void
     * @param context
     */
    public static void populateCraftEssenceList(Context context){
        String packageName = context.getPackageName();
        Resources resources = context.getResources();
        //listOfCraftEssences
        for(int i = 1; i < 18; i++) {
            Drawable drawable = resources.getDrawable(resources
                    .getIdentifier("ce_" + i, "drawable", packageName));
            String[] craftEssenceNamesList = resources.getStringArray(R.array.craft_essence_list);

            String[] craftEssenceEffectsList = resources.getStringArray(R.array.craft_essence_effects_list);

            CraftEssence newCraftEssence = new CraftEssence(craftEssenceNamesList[i-1],
                    craftEssenceEffectsList[i-1], drawable);

            listOfCraftEssences.add(newCraftEssence);
        }

    }
}
