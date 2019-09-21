package com.example.localmirza.v5_fgo_team_analyzer;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.localmirza.v5_fgo_team_analyzer.model.CraftEssence;
import com.example.localmirza.v5_fgo_team_analyzer.model.Formation;
import com.example.localmirza.v5_fgo_team_analyzer.model.Score;
import com.example.localmirza.v5_fgo_team_analyzer.model.Servant;
import com.example.localmirza.v5_fgo_team_analyzer.model.Skill;


import java.text.Format;
import java.util.ArrayList;

import static com.example.localmirza.v5_fgo_team_analyzer.LoadFormation.listOfFormations;
import static com.example.localmirza.v5_fgo_team_analyzer.MainActivity.MAIN_ACTIVITY;
import static com.example.localmirza.v5_fgo_team_analyzer.MainActivity.NEW_INFORMATION;
import static com.example.localmirza.v5_fgo_team_analyzer.MainActivity.listOfCraftEssences;
import static com.example.localmirza.v5_fgo_team_analyzer.MainActivity.listOfServants;

public class NewFormation extends Activity implements PopupMenu.OnMenuItemClickListener {

    public static final int SCORE_ACTIVITY = 1, SAVE_ACTIVITY = 2,
            CARDS_ACTIVITY = 4, CRAFT_ESSENCE_ACTIVITY = 6;

    EditText edtPartyName;
    Button btnScore, btnSave, btnLoad, btnCards;
    ImageView servantOnePicture, servantTwoPicture, servantThreePicture, servantOneCraftEssencePicture,
            servantTwoCraftEssencePicture, servantThreeCraftEssencePicture;
    public Formation currentFormation;
    String currentSkillViewDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_formation);

        edtPartyName = findViewById(R.id.edt_party_name);
        btnScore = findViewById(R.id.btn_score);
        btnSave = findViewById(R.id.btn_save);
        btnLoad = findViewById(R.id.btn_load);
        btnCards = findViewById(R.id.btn_available_cards);
        servantOnePicture = findViewById(R.id.servant_1_picture);
        servantTwoPicture = findViewById(R.id.servant_2_picture);
        servantThreePicture = findViewById(R.id.servant_3_picture);
        servantOneCraftEssencePicture = findViewById(R.id.servant1_ce);
        servantTwoCraftEssencePicture = findViewById(R.id.servant2_ce);
        servantThreeCraftEssencePicture = findViewById(R.id.servant3_ce);

        makeNewFormation();//makes the first formation, if the user first comes in here as a base
        initialFormation();//adds sample data to the first formation

        btnScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchScoreActivity();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchSaveActivity();
            }
        });
        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchLoadActivity();
            }
        });
        btnCards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchViewCharectersActivity("only_view");
            }
        });

        servantOnePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onServentClick(1, "changing_party");
            }
        });
        servantTwoPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onServentClick(2, "changing_party");
            }
        });
        servantThreePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onServentClick(3, "changing_party");
            }
        });
        servantOneCraftEssencePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCraftEssenceClick(1, "changing_craft_essence");
            }
        });
        servantTwoCraftEssencePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCraftEssenceClick(2, "changing_craft_essence");
            }
        });
        servantThreeCraftEssencePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCraftEssenceClick(3, "changing_craft_essence");
            }
        });
    }

    /**
     * goes to the score activity, after attaching the currentFormation to the intent
     */
    private void launchScoreActivity(){
        if(edtPartyName.getText().toString().isEmpty()) {
            edtPartyName.setError("Please enter the name of the party!");
        }
        else{
            Intent intent = new Intent(getApplicationContext(), ScoreActivity.class);
            intent = setupFormationToSend(intent);
            intent.putExtra("RESULT_CODE", SCORE_ACTIVITY);
            startActivityForResult(intent, SCORE_ACTIVITY);
        }
    }

    /**
     * Goes to the Save/Load Activity after attaching the current formation to the intent
     */
    private void launchLoadActivity() {
        Intent intent = new Intent(getApplicationContext(), LoadFormation.class);
        intent.putExtra("RESULT_CODE", NEW_INFORMATION);
        intent.putExtra("purpose", "loadingFormation");
        startActivityForResult(intent, SAVE_ACTIVITY);
    }

    /**
     * If user enters a partyName and it is not duplicate, then the formation is created based on user
     * choice, attached to the intent and sent to same LoadFormaton Activity
     */
    private void launchSaveActivity(){//saving setup as a new formation
        String partyName = edtPartyName.getText().toString();

        if(partyName.isEmpty()) {
            edtPartyName.setError("Please enter the name of the party!");
        }
        else if(checkDuplicatePartyName(partyName)){
            edtPartyName.setError("Please change the party name,\n duplicate save file name found");
        }
        else{
            currentFormation.setPartyName(partyName);
            Intent intent = new Intent(getApplicationContext(), LoadFormation.class);
            intent.putExtra("purpose", "savingFormation");
            intent.putExtra("RESULT_CODE", NEW_INFORMATION);
            intent = setupFormationToSend(intent);
            startActivityForResult(intent, SAVE_ACTIVITY);
        }
    }

    /**
     * Used to check if user entered duplicate party name
     * @param formationName
     * @return
     */
    private boolean checkDuplicatePartyName(String formationName){
        for(int i = 0; i < listOfFormations.size(); i++){
            if(listOfFormations.get(i).getPartyName().equals(formationName)){
                return true;
            }
        }
        return false;
    }

    private void launchViewCharectersActivity(String source){
        Intent intent = new Intent(getApplicationContext(), ViewCharecters.class);
        intent.putExtra("source", source);
        startActivityForResult(intent, CARDS_ACTIVITY);
    }

    private void onServentClick(int pos, String source){
        Intent intent = new Intent(getApplicationContext(), ViewCharecters.class);
        intent.putExtra("position", pos);
        intent.putExtra("source", source);
        startActivityForResult(intent, CARDS_ACTIVITY);
    }
    private void onCraftEssenceClick(int pos, String source){
        Intent intent = new Intent(getApplicationContext(), ViewCraftEssences.class);
        intent.putExtra("position", pos);
        intent.putExtra("source", source);
        startActivityForResult(intent, CRAFT_ESSENCE_ACTIVITY);
    }
    /////////////////////////////////////////////ON ACTIVITY RERSULT //////////////////////////////

    /**
     * If coming from save/load activity, set the currentformation to the formation that is received
     * if coming from cards_activity, a new servant has been chosen, update the UI and formation
     * if coming from craftEssence activity, a new craft essence has been chosen, update UI and the
     * formation
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==SAVE_ACTIVITY){
            if(resultCode==RESULT_OK){
                String source = data.getStringExtra("source");
                Formation loadedFormation = data.getParcelableExtra("formationToSave");
                if(source.equals("loading_formation")){
                    currentFormation = loadedFormation;
                }
            }
        }
        if(requestCode==CARDS_ACTIVITY){
            if(resultCode==RESULT_OK){
                String source = data.getStringExtra("source");

                if(source.equals("changing_party")){
                    int servantPosition = data.getIntExtra("position", -1)-1;
                    int servantNumber = data.getIntExtra("servantNumber", -1)-1;
                    currentFormation.formationServantsList.remove(servantPosition);
                    currentFormation.formationServantsList.add(servantPosition, listOfServants.get(servantNumber));
                    currentFormation.servantPositions[servantPosition] = servantNumber;
                    changeFormationImages(servantPosition, servantNumber);
                }
            }
        }
        if(requestCode==CRAFT_ESSENCE_ACTIVITY){
            if(resultCode==RESULT_OK){
                String source = data.getStringExtra("source");
                if(source.equals("changing_craft_essence")){
                    int ceNumber = data.getIntExtra("ceNumber", 1);
                    int servantPosition = data.getIntExtra("position", -1)-1;
                    currentFormation.craftEssencePositions[servantPosition] = ceNumber;
                    changeCEImages(servantPosition, ceNumber);
                }
            }
        }
    }
    /////////////////////////////////////CRAFT ESSENCE IMAGES /////////////////////////////////////////////////////////////////////////////
    /**
     * changes the craft essence stored in the layout to the new one chosen by the user
     * @param servantPosition
     * @param ceNumber
     */
    private void changeCEImages(int servantPosition, int ceNumber){
        LinearLayout mainFormation = findViewById(R.id.main_formation);

        LinearLayout innerFormation = findViewById(mainFormation.getChildAt(servantPosition).getId());//wh5ch servant layout to focus on

        ImageView ceImage = findViewById(innerFormation.getChildAt(2).getId());//ce image

        ceImage.setImageResource(getResources().getIdentifier("ce_" + ceNumber,
                "drawable", getPackageName()));
    }
    /////////////////////////////////////CRAFT ESSENCE IMAGES /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////SERVANT IMAGES ///////////////////////////////////////////////////////////////////////////////////

    /**
     * Change the servant images and skill images of the specific servant based on servantPosition
     * (1, 2 or 3) and the servantNumber (the specific servant from the list of Servants).
     * @param servantPosition
     * @param servantNumber
     */
    private void changeFormationImages(final int servantPosition, int servantNumber){

        LinearLayout mainFormation = findViewById(R.id.main_formation);

        LinearLayout innerFormation = findViewById(mainFormation.getChildAt(servantPosition).getId());//which servant layout to fous on

        ImageView servantImage = findViewById(innerFormation.getChildAt(0).getId());//servant image

        LinearLayout skillFormation = findViewById(innerFormation.getChildAt(1).getId());//layout storing skills
        ImageView skill_1_image = findViewById(skillFormation.getChildAt(0).getId());//skill 1 image
        ImageView skill_2_image = findViewById(skillFormation.getChildAt(1).getId());//skill 2 image
        ImageView skill_3_image = findViewById(skillFormation.getChildAt(2).getId());// skill 3 image

        setSkillDescription(servantNumber, 0, skill_1_image);
        setSkillDescription(servantNumber, 1, skill_2_image);
        setSkillDescription(servantNumber, 2, skill_3_image);

        LinearLayout textFormation = findViewById(innerFormation.getChildAt(3).getId());//layout storing servant data

        TextView servant_level =  findViewById(textFormation.getChildAt(0).getId());//servant_level
        TextView servant_health = findViewById(textFormation.getChildAt(1).getId());//servant_health
        TextView servant_attack = findViewById(textFormation.getChildAt(2).getId());//servant_attack
        TextView servant_cost = findViewById(textFormation.getChildAt(3).getId());//servant_cost

        servant_level.setTextColor(getResources().getColor(R.color.color_white));
        servant_attack.setTextColor(getResources().getColor(R.color.color_white));
        servant_health.setTextColor(getResources().getColor(R.color.color_white));
        servant_cost.setTextColor(getResources().getColor(R.color.color_white));

        servant_level.setText("Name: "+ listOfServants.get(servantNumber).getName());
        servant_attack.setText("Attack: " + listOfServants.get(servantNumber).getAttack().toString());
        servant_health.setText("Health: " + listOfServants.get(servantNumber).getHealth().toString());
        servant_cost.setText("Cost: " + listOfServants.get(servantNumber).getCost().toString());

        String currentPackage = getPackageName();

        servantNumber += 1;//images start from "1" not "0"

        servantImage.setImageResource(getResources()
                .getIdentifier("servant_"+servantNumber, "drawable", currentPackage));

            skill_1_image.setImageResource(getResources()
                    .getIdentifier("servant_"+servantNumber+"_skill1", "drawable", currentPackage));
            skill_2_image.setImageResource(getResources()
                    .getIdentifier("servant_"+servantNumber+"_skill2", "drawable", currentPackage));
            skill_3_image.setImageResource(getResources()
                    .getIdentifier("servant_"+servantNumber+"_skill3", "drawable", currentPackage));
    }

    /**
     * creates a new formation when the new formation is first clicked from mainactivity
     */
    private void makeNewFormation(){
        ArrayList<Servant> newPartyServants = new ArrayList<>();
        Score score = new Score();
        int[] servantPositions = {0,1,2};
        int[] cePostions = {0,1,2};
        for(int i = 0; i < 3; i++){
            newPartyServants.add(listOfServants.get(i));
        }

        currentFormation = new Formation(edtPartyName.getText().toString(), newPartyServants, servantPositions, cePostions, score);
        currentFormation.analyzeFormation();
    }

    /**
     * Fills in default info. for the default formation shown to the user upon launch
     */
    private void initialFormation(){
        Intent intent = getIntent();
        String source = intent.getStringExtra("source");
        if(source.equals("loading_formation")){
            loadFormationData(intent);
        }
        for(int i =0; i < 3; i++){
            changeFormationImages(i, currentFormation.getServantPositions()[i]);
            changeCEImages(i, currentFormation.getCraftEssencePositions()[i]);
        }
    }

    /**
     * Used to update the specific Servant and CraftEssence Numbers in the current formation object
     * @param servantPositionChanges
     * @param cePositionChanges
     */
    private void updateCurrentFormationServantAndCEPositions(int[] servantPositionChanges, int[] cePositionChanges){
        for(int i = 0; i < 3; i++){
            currentFormation.servantPositions[i]=servantPositionChanges[i];
            currentFormation.craftEssencePositions[i]=cePositionChanges[i];
        }
    }
    /////////////////////SKILL DESCRIPTION POPUP////////////////////////////////////////////////////

    /**
     * sets the description of the skill according to the skillnumber and the image of the skill.
     * the descriptions are stored in the skill objects stored in the formation.
     * @param position
     * @param skillNumber
     * @param skillImage
     */
    public void setSkillDescription(final int position, final int skillNumber, ImageView skillImage){
        skillImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Skill skill = listOfServants.get(position).getSkillList().get(skillNumber);
                currentSkillViewDescription = skill.getName();
                showPopup (v, skill);
            }
        });
    }

    /**
     * The popup which is shown when the user clicks on a specific skill, contains the details of
     * the skill including name, effects and the values of the effects, stored in the skill Object
     * @param v
     * @param skill
     */
    public void showPopup (View v, Skill skill){
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.popup_skill_menu);
        StringBuilder builder = new StringBuilder();

        builder.append(currentSkillViewDescription).append("\n");

        for(int i = 0; i < skill.getEffects().size(); i++){
            popup.getMenu().getItem(i+1).setVisible(true);
            popup.getMenu().getItem(i+1).setTitle(skill.getEffects().get(i) +
                    " : " + skill.getValues().get(i));
        }
        popup.getMenu().getItem(0).setTitle(builder);
        popup.show();
    }
    /////////////////////SKILL DESCRIPTION POPUP////////////////////////////////////////////////////
    /////////////////////LOADING FORMATION DATA/////////////////////////////////////////////////////
    /**
     * Used to create the formation object using the intent that was passed in coming from another activity
     * such as save/load or score
     * @param intent
     */
    public void loadFormationData(Intent intent){
        ArrayList<Servant> tempServantList = intent.getParcelableArrayListExtra("formationToSave.formationServantsList");
        int[] tempCEPositions = intent.getIntArrayExtra("formationToSave.craftEssencePositions");
        int[] tempServantPositions = intent.getIntArrayExtra("formationToSave.servantPositions");
        String tempPartyName = intent.getStringExtra("formationToSave.partyName");
        ArrayList<? extends Skill> servant1SkillList = intent.getParcelableArrayListExtra("formationToSave.skillList1");
        ArrayList<? extends Skill> servant2SkillList = intent.getParcelableArrayListExtra("formationToSave.skillList2");
        ArrayList<? extends Skill> servant3SkillList = intent.getParcelableArrayListExtra("formationToSave.skillList3");
        tempServantList.get(0).setSkillList((ArrayList<Skill>) servant1SkillList);
        tempServantList.get(1).setSkillList((ArrayList<Skill>) servant2SkillList);
        tempServantList.get(2).setSkillList((ArrayList<Skill>) servant3SkillList);
        edtPartyName.setText(tempPartyName);
        currentFormation.formationServantsList.clear();
        currentFormation.formationServantsList.addAll(tempServantList);
        updateCurrentFormationServantAndCEPositions(tempServantPositions, tempCEPositions);
    }

    /**
     * creates the formation data to be added to the intent in order to go to either ScoreActivity or
     * LoadFormation Activity
     * @param intent
     * @return
     */
    public Intent setupFormationToSend(Intent intent){
        currentFormation.analyzeFormation();
        intent.putExtra("formationToSave.formationServantsList", currentFormation.formationServantsList); // using the (String name, Parcelable value) overload!
        intent.putExtra("formationToSave.craftEssencePositions", currentFormation.craftEssencePositions); // using the (String name, Parcelable value) overload!
        intent.putExtra("formationToSave.servantPositions", currentFormation.servantPositions); // using the (String name, Parcelable value) overload!
        intent.putExtra("formationToSave.partyName", currentFormation.getPartyName()); // using the (String name, Parcelable value) overload!
        intent.putExtra("formationToSave.score", currentFormation.getScore()); // using the (String name, Parcelable value) overload!
        intent.putExtra("formationToSave.skillList1", currentFormation.formationServantsList.get(0).getSkillList());
        intent.putExtra("formationToSave.skillList2", currentFormation.formationServantsList.get(1).getSkillList());
        intent.putExtra("formationToSave.skillList3", currentFormation.formationServantsList.get(2).getSkillList());
        return intent;
    }
    /////////////////////LOADING FORMATION DATA/////////////////////////////////////////////////////
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }
}
