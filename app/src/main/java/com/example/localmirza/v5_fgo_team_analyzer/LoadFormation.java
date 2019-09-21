package com.example.localmirza.v5_fgo_team_analyzer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.localmirza.v5_fgo_team_analyzer.model.Formation;
import com.example.localmirza.v5_fgo_team_analyzer.model.Score;
import com.example.localmirza.v5_fgo_team_analyzer.model.Servant;
import com.example.localmirza.v5_fgo_team_analyzer.model.Skill;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class LoadFormation extends Activity {

    public static ArrayList<Formation> listOfFormations = new ArrayList<>();
    private static final String FILE_NAME = "file_formations_list.txt";
    private static int READ_FILE = 0;
    Bundle savedState;
    ListView listview;
    Button btnBack;
    JSONArray jsonFormationArray;

    /**
     * handles the reading of the JSON file and loads formations that were previously added to the
     * file. If the user is coming to save a new formation, this new formation is added to the list
     * and written to the file.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        savedState = savedInstanceState;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_formation);

        listview = findViewById(R.id.save_files_list);
        btnBack = findViewById(R.id.btn_back_load_formation);

        Intent intent = getIntent();
        int resultCode = intent.getIntExtra("RESULT_CODE", -1);
        String purpose = intent.getStringExtra("purpose");

        if(READ_FILE==0){
            read();
            READ_FILE=1;
        }

        if(resultCode!=0 && purpose.equals("savingFormation")) {
            saveFormation();
        }
        if(listOfFormations.size()>0) {
            listview.setAdapter(new charecterListAdapter(this, listOfFormations));
        }
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnBackNewFormation(RESULT_CANCELED);
            }
        });
    }

    /**
     * save formation to file as a jsonObject and save the Formation itself to the list of Formations
     * Also calls loadFormationData() to attach the data to the formation being loaded on the screen
     * */
    private void saveFormation() {
        Intent intent = getIntent();
        Formation newFormationToSave = loadFormationData(intent);
        listOfFormations.add(newFormationToSave);
        storeJSONObjectInFile(newFormationToSave);
    }

    class charecterListAdapter extends BaseAdapter {
        Context context;
        ArrayList<Formation> data;
        private LayoutInflater inflater = null;

        public charecterListAdapter(Context context, ArrayList<Formation> data) {
            // TODO Auto-generated constructor stub
            this.context = context;
            this.data = data;
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        /**
         * grabs the data for each formation object and attaches them to appropriate layout pieces,
         * specifically the Servant images, and the party name, the layout used is called
         * "save_file_preview.xml"
         * @param position
         * @param convertView
         * @param parent
         * @return
         */
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            View vi = convertView;
            if (vi == null)
                vi = inflater.inflate(R.layout.save_file_preview, null);

            ImageView servantOneImage, servantTwoImage, servantThreeImage;
            TextView partyName;
            Button btnLoad, btnDelete;

            partyName = vi.findViewById(R.id.save_formation1_name);
            servantOneImage = vi.findViewById(R.id.save_formation1_servant1_image);
            servantTwoImage = vi.findViewById(R.id.save_formation1_servant2_image);
            servantThreeImage = vi.findViewById(R.id.save_formation1_servant3_image);
            btnLoad = vi.findViewById(R.id.save_formation1_load);
            btnDelete = vi.findViewById(R.id.save_formation1_delete);

            setImage(servantOneImage, data.get(position).getServantPositions()[0]-1);
            setImage(servantTwoImage, data.get(position).getServantPositions()[1]-1);
            setImage(servantThreeImage, data.get(position).getServantPositions()[2]-1);

            partyName.setText(data.get(position).getPartyName());

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listOfFormations.remove(position);
                }
            });
            btnLoad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadFormationDataToNewFormation(position);
                }
            });

            return vi;
        }
    }

    /**
     * sets up the intent that will go back to NewFormation class, with the specific formation that
     * is selected by th user. The formation object is first created using setupFormationToSend()
     * then, it is added to the intent tot be sent
     * @param formationPosition
     */
    private void loadFormationDataToNewFormation(int formationPosition) {
        Formation currentFormation = listOfFormations.get(formationPosition);

        Intent intent = getIntent();
        setResult(RESULT_OK, intent);
        intent.putExtra("source", "loading_formation");
        intent = setupFormationToSend(intent, currentFormation);
        intent.setClass(getApplicationContext(), NewFormation.class);
        startActivityForResult(intent, RESULT_OK);
    }

    /**
     * on the layout, it sets thhe image of the corresponding servant that is in the formation.
     * This is called in the adapter for the listview above, for each servant object in question
     * @param servantImage
     * @param servantPosition
     */
    public void setImage(ImageView servantImage, int servantPosition){
        Drawable drawable = getResources().getDrawable(getResources()
                .getIdentifier("servant_" + (servantPosition+2) + "_small", "drawable", getPackageName()));
        servantImage.setImageDrawable(drawable);
    }

    /**
     * takes user back to the previous activity, he/she was on, which is the always
     * NewFormation activity.
     * @param resultCode
     */
    private void btnBackNewFormation(int resultCode){
        Intent intent = getIntent();
        setResult(resultCode, intent);
        finish();
    }

    /**
     * sets up the data inside the formation object,
     * this incudes the partyName, score, list of skills,
     * servant and craft essence positions
     * @param intent
     * @param currentFormation
     * @return
     */
    public Intent setupFormationToSend(Intent intent, Formation currentFormation){
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

    /**
     * Similar to above, but this will be used to load the Formation data in order to make the Formation
     * object, that was received from the intent. This is called when the user is saving their custom
     * formation.
     * @param intent
     * @return
     */
    public Formation loadFormationData(Intent intent){
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
        return newFormationToSave;
    }

    ////////////////////////////////////////////STORE IN JSON FILE /////////////////////////////////////////////
    /**
     * Parsed the JSON objects and arrays
     * partyName is a single JSON OBject string
     * formationServantList is a JSONArray of 3 Servant JSONObjects
     * Each Servant JSONObject has a long list of string properties such as name and attack, and
     * health.
     * Each Servant also has a list of 3 Skill JSONObjects
     * servantPostion are stored in int[]
     * craftEssencePosition stored in int[]
     * @param formation
     */
    private void storeJSONObjectInFile(Formation formation) {
        jsonFormationArray = new JSONArray();
        try {
            JSONObject jsonFormationName= new JSONObject();
            jsonFormationName.put( "party-name", formation.getPartyName());


            JSONObject jsonScore = new JSONObject();
            jsonScore.put("overall-score", formation.getScore().getOverallScore());
            jsonScore.put("formation-advantages", formation.getScore().getFormationAdvantages());
            jsonScore.put("formation-disadvantages", formation.getScore().getFormationDisadvantages());
            jsonScore.put("formation-recommandations", formation.getScore().getFormationRecommendations());

            JSONObject jsonScorePrimary = new JSONObject();
            jsonScorePrimary.put("score", jsonScore);

            JSONArray jsonServantList = new JSONArray();

            for(int i = 0; i < 3; i++){//list of servants
                JSONObject jsonServant = new JSONObject();

                Servant tempServant  = formation.formationServantsList.get(i);

                jsonServant.put("name", tempServant.getName());
                jsonServant.put("attack", tempServant.getAttack());
                jsonServant.put("health", tempServant.getHealth());
                jsonServant.put("cost", tempServant.getCost());
                jsonServant.put("defence", tempServant.getDefense());
                jsonServant.put("star-absorption", tempServant.getStarAbsorption());
                jsonServant.put("star-generation", tempServant.getStarGeneration());
                jsonServant.put("np-charge-attack", tempServant.getNpChargeAttack());
                jsonServant.put("np-charge-defence", tempServant.getNpChargeDefense());

                JSONArray jsonSkillList = new JSONArray();
                for(int j = 0; j < 3; j++){//list of skills
                    JSONObject skill = new JSONObject();
                    skill.put("skill-"+j+"-name", tempServant.getSkillList().get(j).getName());
                    skill.put("skill-"+j+"-cooldown", tempServant.getSkillList().get(j).getCooldown());

                    JSONArray effects = new JSONArray();
                    ArrayList<String> effectsList = tempServant.getSkillList().get(j).getEffects();

                    for(int k = 0; k < effectsList.size(); k++){//list of effects
                        effects.put( effectsList.get(k));
                    }
                    skill.put("effects-list", effects);

                    JSONArray values = new JSONArray();
                    ArrayList<Double> valuesList = tempServant.getSkillList().get(j).getValues();

                    for(int l = 0; l < valuesList.size(); l++){//list of values for the effects
                        values.put(valuesList.get(l));
                    }
                    skill.put("values-list", values);
                    jsonSkillList.put(j, skill);
                }
                jsonServant.put("skill-list", jsonSkillList);
                jsonServantList.put(i, jsonServant);
            }

            JSONArray jsonServantPositions = new JSONArray();
            for(int i = 0; i < 3; i++){
                jsonServantPositions.put(i, formation.getServantPositions()[i]);
            }

            JSONArray jsonCraftEssencePositions = new JSONArray();
            for(int i = 0; i < 3; i++){
                jsonCraftEssencePositions.put(i, formation.getCraftEssencePositions()[i]);
            }

            jsonFormationArray.put(0,jsonFormationName);
            jsonFormationArray.put(1,jsonScorePrimary);
            jsonFormationArray.put(2,jsonServantList);
            jsonFormationArray.put(3,jsonServantPositions);
            jsonFormationArray.put(4,jsonCraftEssencePositions);

            addFormationToFile(jsonFormationArray.toString());
        } catch (JSONException e) {
            Log.d("TAGJSON", e.toString());
            e.printStackTrace();
        }
    }

    /**
     * Writes the JSONArray Formation to the internal file
     * @param toFile
     */
    private void addFormationToFile(String toFile){
        FileOutputStream fos;

        try {
            fos = openFileOutput(FILE_NAME, MODE_APPEND);
        } catch (FileNotFoundException e) {
            return;
        }
        Log.d("loadFormationMIRZA", "writingToFile");
        PrintWriter writer = new PrintWriter(fos);
        writer.println(toFile);
        writer.close();
    }

    /**
     * starts reads the file of formations
     */
    private void read(){
        Log.d("TAG", "attempting to read");
        FileInputStream fis;
        try {
            fis = openFileInput(FILE_NAME);
        } catch (FileNotFoundException e) {
            return;
        }
        readFile(fis);
    }

    /**
     * uses a scanner to read the file, and send each line (each formation) to a JSONParser
     * @param fis
     */
    private void readFile(FileInputStream fis){
        Scanner scanner = new Scanner(fis);

        while(scanner.hasNextLine()){
                parseJSON(scanner.nextLine());//each line is a formation
        }
    }

    /**
     * Parsed the JSON objects and arrays
     * partyName is a single JSON OBject string
     * formationServantList is a JSONArray of 3 Servant JSONObjects
     * Each Servant JSONObject has a long list of string properties such as name and attack, and
     * health.
     * Each Servant also has a list of 3 Skill JSONObjects
     * servantPostion are stored in int[]
     * craftEssencePosition stored in int[]
     * @param jsonString
     */
    private void parseJSON(String jsonString) {
        Formation newFormationToReadIn;

        try {
            //JSONArray formationArray = root.getJSONArray("formation-list");
            JSONArray formationArray = new JSONArray(jsonString);

            JSONObject partyName = formationArray.getJSONObject(0);// singleFormation.getString("party-name");

            JSONObject singleFormation = formationArray.getJSONObject(1);
            JSONObject scores = singleFormation.getJSONObject("score");

            Score newScoreToAdd = new Score(scores.getInt("overall-score"), scores.getString("formation-advantages"),
                    scores.getString("formation-disadvantages"), scores.getString("formation-recommandations"));

            JSONArray JSONformationServantList = formationArray.getJSONArray(2);

            ArrayList<Servant> formationServantsList = new ArrayList<>();

            for(int i = 0; i < 3; i++){
                Servant servant = grabServantData(JSONformationServantList, i);
                formationServantsList.add(servant);
            }

            JSONArray servantPosition = formationArray.getJSONArray(3);
            JSONArray craftEssencePos = formationArray.getJSONArray(4);
            int[] servantPositions = new int[3];
            int[] craftEssencePositions = new int[3];

            for (int k = 0; k < 3; k++) {
                int formServantPosition = servantPosition.getInt(k);
                int formCraftPosition = craftEssencePos.getInt(k);
                servantPositions[k] = formServantPosition;
                craftEssencePositions[k] = formCraftPosition;
            }

            newFormationToReadIn = new Formation(partyName.getString("party-name"), formationServantsList, servantPositions,
                    craftEssencePositions, newScoreToAdd);

            listOfFormations.add(newFormationToReadIn);

        } catch (JSONException e) {

        }
    }

    /**
     * Accepts a JSONArray of Servants, and returns a Servant object, used in creating the Formation object
     * when reading from the file. The formation object will later be added to the list of Formations
     * @param formationServantList
     * @param position
     * @return
     */
    private Servant grabServantData(JSONArray formationServantList, int position){
            Servant newServantToAdd = new Servant();

            ArrayList<Skill> listOfAllServantSkills = new ArrayList<>();

            try {
                JSONObject formServantList = formationServantList.getJSONObject(position);

                newServantToAdd.setName(formServantList.getString("name"));
                newServantToAdd.setAttack(formServantList.getDouble("attack"));
                newServantToAdd.setHealth(formServantList.getDouble("health"));
                newServantToAdd.setCost(formServantList.getDouble("cost"));
                newServantToAdd.setDefense(formServantList.getDouble("defence"));
                newServantToAdd.setStarAbsorption(formServantList.getDouble("star-absorption"));
                newServantToAdd.setStarGeneration(formServantList.getDouble("star-generation"));
                newServantToAdd.setNpChargeAttack(formServantList.getDouble("np-charge-attack"));
                newServantToAdd.setNpChargeDefense(formServantList.getInt("np-charge-defence"));

                JSONArray listOfSkills = formServantList.getJSONArray("skill-list");
                for(int i = 0; i < 3; i++){

                    JSONObject skill = (JSONObject) listOfSkills.get(i);

                    ArrayList<String> listOfEffects = new ArrayList<>();
                    ArrayList<Double> listOfValues = new ArrayList<>();

                    String name = skill.getString("skill-"+i+"-name");
                    int cooldown = skill.getInt("skill-"+i+"-cooldown");

                    for(int k = 0; k < skill.getJSONArray("effects-list").length(); k++){
                        listOfEffects.add(skill.getJSONArray("effects-list").getString(k));
                    }

                    for(int l = 0; l < skill.getJSONArray("values-list").length(); l++){
                        listOfValues.add(skill.getJSONArray("values-list").getDouble(l));
                    }

                    Skill skillToAdd = new Skill(name, cooldown, listOfEffects, listOfValues);
                    listOfAllServantSkills.add(skillToAdd);
                }
            }
            catch (Exception e){
                Log.d("TAGSERVANT", e.toString());
            }

            newServantToAdd.setSkillList(listOfAllServantSkills);
            return newServantToAdd;
    }
}
