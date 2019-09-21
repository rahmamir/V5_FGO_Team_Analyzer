package com.example.localmirza.v5_fgo_team_analyzer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.localmirza.v5_fgo_team_analyzer.model.Formation;
import com.example.localmirza.v5_fgo_team_analyzer.model.Score;
import com.example.localmirza.v5_fgo_team_analyzer.model.Servant;
import com.example.localmirza.v5_fgo_team_analyzer.model.Skill;

import java.util.ArrayList;

import static com.example.localmirza.v5_fgo_team_analyzer.MainActivity.listOfServants;

public class ScoreActivity extends Activity {

    TextView txtAdvantages, txtDisadvantages, txtRecommendations, txtScore,
            servantOneName, servantTwoName, servantThreeName, txtScoreDetails;
    ImageView servantOneImage, servantTwoImage, servantThreeImage;
    Button btnScoreBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        txtAdvantages = findViewById(R.id.txt_formation_advantages);
        txtDisadvantages = findViewById(R.id.txt_formation_disadvantages);
        txtRecommendations = findViewById(R.id.txt_formation_tips_recommendations);
        servantOneName = findViewById(R.id.txt_formation_servant_1_name);
        servantTwoName = findViewById(R.id.txt_formation_servant_2_name);
        servantThreeName = findViewById(R.id.txt_formation_servant_3_name);
        servantOneImage = findViewById(R.id.img_formation_servant_1);
        servantTwoImage = findViewById(R.id.img_formation_servant_2);
        servantThreeImage = findViewById(R.id.img_formation_servant_3);
        txtScore = findViewById(R.id.txt_formation_score);
        txtScoreDetails = findViewById(R.id.formation_score_details);
        btnScoreBack = findViewById(R.id.btn_score_back);

        btnScoreBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        setScoreInfo();
    }

    /**
     * Coming from the NewFormation activity, the formation that is passed into the intent is first grabbed
     * and stored in a variable. Next, the appropriate UI elements are updated according to the data
     * in the formation, this includes Servant picture, Servant Name, Formation advantages, disadvantages
     * recommendations and score.
     */
    private void setScoreInfo(){
        String currentPackage = getPackageName();
        Intent intent = getIntent();

        ArrayList<Servant> tempServantList = intent.getParcelableArrayListExtra("formationToSave.formationServantsList");
        int[] tempCEPositions = intent.getIntArrayExtra("formationToSave.craftEssencePositions");
        int[] tempServantPositions = intent.getIntArrayExtra("formationToSave.servantPositions");
        String tempPartyName = intent.getStringExtra("formationToSave.partyName");
        Score tempScore = intent.getParcelableExtra("formationToSave.score");
        ArrayList<? extends Skill> servant1SkillList = intent.getParcelableArrayListExtra("formationToSave.skillList1");
        ArrayList<? extends Skill> servant2SkillList = intent.getParcelableArrayListExtra("formationToSave.skillList2");
        ArrayList<? extends Skill> servant3SkillList = intent.getParcelableArrayListExtra("formationToSave.skillList3");
        tempServantList.get(0).setSkillList((ArrayList<Skill>) servant1SkillList);
        tempServantList.get(1).setSkillList((ArrayList<Skill>) servant2SkillList);
        tempServantList.get(2).setSkillList((ArrayList<Skill>) servant3SkillList);

        Formation newFormationToSave = new Formation(tempPartyName, tempServantList, tempServantPositions, tempCEPositions, tempScore);

        newFormationToSave.analyzeFormation();

        Score scoreAfterAnalysis = new Score(newFormationToSave.getScore().getOverallScore(),
                newFormationToSave.getScore().getFormationAdvantages(),
                newFormationToSave.getScore().getFormationDisadvantages(),
                newFormationToSave.getScore().getFormationRecommendations());

        txtScore.setText(String.valueOf(scoreAfterAnalysis.getOverallScore()));

        String partyAdvantages = scoreAfterAnalysis.getFormationAdvantages(),
                partyDisadvantages = scoreAfterAnalysis.getFormationDisadvantages(),
                partyRecommendations = scoreAfterAnalysis.getFormationRecommendations();

        if(partyDisadvantages.equals("")){//if no disadvantages, perfect team.
            partyDisadvantages = "None, perfect team\n";
            partyRecommendations = "No Recommendations\n";
        }

        txtAdvantages.setText(partyAdvantages);
        txtDisadvantages.setText(partyDisadvantages);
        txtRecommendations.setText(partyRecommendations);
        txtScore.setText(String.valueOf(scoreAfterAnalysis.getOverallScore()));
        txtScoreDetails.setText("Score:" + String.valueOf(scoreAfterAnalysis.getOverallScore()) + "/ 7");

        Servant servantOne = listOfServants.get(newFormationToSave.getServantPositions()[0]),
                servantTwo = listOfServants.get(newFormationToSave.getServantPositions()[1]),
                servantThree = listOfServants.get(newFormationToSave.getServantPositions()[2]);

        servantOneName.setText(servantOne.getName());
        servantTwoName.setText(servantTwo.getName());
        servantThreeName.setText(servantThree.getName());

        String servantOnePosition = String.valueOf(newFormationToSave.getServantPositions()[0]+1);
        String servantTwoPosition = String.valueOf(newFormationToSave.getServantPositions()[1]+1);
        String servantThreePosition = String.valueOf(newFormationToSave.getServantPositions()[2]+1);

        servantOneImage.setImageResource(getResources()
                .getIdentifier("servant_"+servantOnePosition+"_small",
                        "drawable", currentPackage));

        servantTwoImage.setImageResource(getResources()
                .getIdentifier("servant_"+(servantTwoPosition)+"_small",
                        "drawable", currentPackage));

        servantThreeImage.setImageResource(getResources()
                .getIdentifier("servant_"+(servantThreePosition)+"_small",
                        "drawable", currentPackage));
    }
}
