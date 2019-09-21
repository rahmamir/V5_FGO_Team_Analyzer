package com.example.localmirza.v5_fgo_team_analyzer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.localmirza.v5_fgo_team_analyzer.model.CraftEssence;
import com.example.localmirza.v5_fgo_team_analyzer.model.Skill;

import static com.example.localmirza.v5_fgo_team_analyzer.MainActivity.listOfCraftEssences;
import static com.example.localmirza.v5_fgo_team_analyzer.MainActivity.listOfServants;
import static com.example.localmirza.v5_fgo_team_analyzer.ViewCharecters.VIEW_SERVANT_DETAILS;

public class ViewCraftEssences extends Activity implements PopupMenu.OnMenuItemClickListener {

    Button btnViewCharectersBack;
    String currentCraftEssenceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_craft_essences);

        btnViewCharectersBack = findViewById(R.id.btn_back_view_craft_essences);

        btnViewCharectersBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });
        DisplayCraftEssences();
    }

    /**
     * Displays the data for each Craft Essence available to pick for the user, shows the name and a
     * button to click if the user wished to choose it for their formation.
     */
    public void DisplayCraftEssences(){
        LinearLayout servantList = findViewById(R.id.craft_essences_list_view);
        for(int i = 0; i < listOfCraftEssences.size(); i++){
            LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout child = (LinearLayout) inflater.inflate(R.layout.servant_small_preview, null);

            final ImageView craftEssencePicture = (ImageView) child.getChildAt(0);
            TextView craftEssenceName = (TextView) child.getChildAt(1);

            Drawable drawable = getResources().getDrawable(getResources()
                    .getIdentifier("ce_" + (i+1), "drawable", getPackageName()));
            craftEssencePicture.setImageDrawable(drawable);

            craftEssenceName.setText(listOfCraftEssences.get(i).getCraftEssenceName());

            final int craftEssenceNumber = i;

            craftEssencePicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewCraftEssenceDetails(craftEssenceNumber, craftEssencePicture);
                }
            });

            TextView btnChooseCE = new TextView(this);
            btnChooseCE.setLayoutParams(child.getChildAt(1).getLayoutParams());
            btnChooseCE.setText("CHOOSE");
            btnChooseCE.setBackground(getResources().getDrawable(R.drawable.colors));
            btnChooseCE.setTextSize(10);
            btnChooseCE.setTextColor(getResources().getColor(R.color.color_white));
            btnChooseCE.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            final int ceNumber = i+1;

            btnChooseCE.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    chooseCraftEssence(ceNumber);
                }
            });

            child.addView(btnChooseCE);

            servantList.addView(child, i);
        }
    }

    /**
     * If the user chooses a craft essence, attach the position of the CraftEssence to the intent
     * and then go back
     * @param ceNumber
     */
    private void chooseCraftEssence(int ceNumber){
        Intent intent = getIntent();
        intent.putExtra("ceNumber", ceNumber);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * If the user wants to see the detailed effects of the craft essence, they can click on it to
     * view the details
     * @param position
     * @param skillImage
     */
    public void viewCraftEssenceDetails(final int position, ImageView skillImage){
        skillImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CraftEssence ce = listOfCraftEssences.get(position);
                currentCraftEssenceName = ce.getCraftEssenceName();
                showPopup (v, ce);
            }
        });
    }

    /**
     * The popup shows the CraftEssence information including name, description and effect value
     * @param v
     * @param ce
     */
    public void showPopup (View v, CraftEssence ce){
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.popup_skill_menu);
        String ceName, ceDescription;

        ceName = currentCraftEssenceName;
        ceDescription = ce.getCraftEssenseEffect();

        popup.getMenu().getItem(0).setTitle(ceName);
        popup.getMenu().getItem(1).setVisible(true);
        popup.getMenu().getItem(1).setTitle(ceDescription);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }
}
