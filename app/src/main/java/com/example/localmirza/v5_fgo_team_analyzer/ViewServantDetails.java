package com.example.localmirza.v5_fgo_team_analyzer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.localmirza.v5_fgo_team_analyzer.model.Servant;
import com.example.localmirza.v5_fgo_team_analyzer.model.Skill;

import static com.example.localmirza.v5_fgo_team_analyzer.MainActivity.listOfServants;

public class ViewServantDetails extends Activity implements PopupMenu.OnMenuItemClickListener {

    ImageView servantPicture, servantSkill1Picture, servantSkill2Picture, servantSkill3Picture;
    TextView servantName, servantHealth, servantAttack, servantCost;
    Button btnChooseServant, btnBack;
    String currentSkillViewDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_servant_details);


        servantPicture = findViewById(R.id.servant_picture);
        servantName = findViewById(R.id.servant_name);
        servantHealth = findViewById(R.id.servant_health);
        servantAttack = findViewById(R.id.servant_attack);
        servantCost = findViewById(R.id.servant_cost);
        servantSkill1Picture = findViewById(R.id.servant_details_skill1);
        servantSkill2Picture = findViewById(R.id.servant_details_skill2);
        servantSkill3Picture = findViewById(R.id.servant_details_skill3);
        btnChooseServant = findViewById(R.id.btn_choose_servant);
        btnBack = findViewById(R.id.btn_back_servant_details);



        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseServantForParty(false);
            }
        });

        setServantDetails();
    }

    /**
     * grabs and displays all relevant information belonging to the specific servant clicked on by
     * user. This includes the servant image, the 3 skills images, String name, double attack, health
     * cost and level
     */
    private void setServantDetails(){
        Intent intent = getIntent();
        String source = intent.getStringExtra("source");

        if(source.equals("only_view")) {//DONT show the choose servant btn if user just viewing
            btnChooseServant.setVisibility(View.GONE);
        }
        else if(source.equals("changing_party")){//show the choose servant btn if user wants to pick
            btnChooseServant.setVisibility(View.VISIBLE);
        }

        int servantNumber = intent.getIntExtra("servantNumber", 0) - 1;

        Servant tempServant = listOfServants.get(servantNumber);

        Drawable servantPictureDrawable = getResources().getDrawable(getResources()
                .getIdentifier("servant_" + (servantNumber+1), "drawable", getPackageName()));
        servantPicture.setImageDrawable(servantPictureDrawable);

        Drawable skillImage1 = getResources().getDrawable(getResources()
                .getIdentifier("servant_" + (servantNumber+1) +"_skill1", "drawable", getPackageName()));
        Drawable skillImage2 = getResources().getDrawable(getResources()
                .getIdentifier("servant_" + (servantNumber+1) +"_skill2", "drawable", getPackageName()));
        Drawable skillImage3 = getResources().getDrawable(getResources()
                .getIdentifier("servant_" + (servantNumber+1) +"_skill3", "drawable", getPackageName()));

        servantSkill1Picture.setImageDrawable(skillImage1);
        servantSkill2Picture.setImageDrawable(skillImage2);
        servantSkill3Picture.setImageDrawable(skillImage3);

        setSkillDescription(servantNumber, 0, servantSkill1Picture);
        setSkillDescription(servantNumber, 1, servantSkill2Picture);
        setSkillDescription(servantNumber, 2, servantSkill3Picture);

        btnChooseServant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseServantForParty(true);
            }
        });

        servantName.setText(tempServant.getName());
        servantHealth.setText("Health: " + tempServant.getHealth().toString());
        servantAttack.setText("Attack: " + tempServant.getAttack().toString());
        servantCost.setText("Cost: " + tempServant.getCost().toString());

    }

    /**
     * If the user picks this servant, then go back to ViewChareecters activity, (which will then
     * automatically redirect user to NewFormation to add their servant to their formation.
     * @param chooseServant
     */
    private void chooseServantForParty(boolean chooseServant) {
        Intent intent = getIntent();
        setResult(RESULT_OK, intent);
        intent.putExtra("choosingServant", chooseServant);
        finish();
    }
    ////////////////////////////////////////SKILL DETAILS/////////////////////////////////////////////

    /**
     * The details for the skills of the chosen servant is shown here using a popup
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
     * the popup displays the name, effects and values of the skill clicked on by the user
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

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }
}
