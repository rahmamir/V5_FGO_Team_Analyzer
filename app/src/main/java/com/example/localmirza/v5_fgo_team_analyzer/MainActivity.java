package com.example.localmirza.v5_fgo_team_analyzer;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.localmirza.v5_fgo_team_analyzer.model.CraftEssence;
import com.example.localmirza.v5_fgo_team_analyzer.model.Servant;
import com.example.localmirza.v5_fgo_team_analyzer.model.Skill;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends Activity {

    ProgressBar pbLoad;
    public static final int MAIN_ACTIVITY = 0, NEW_INFORMATION = 1, LOAD_INFORMATION = 2,
            VIEW_CHARACTERS = 3, MAIN_MENU_TWO=4;

    Button btnNew, btnLoad, btnChar, btnExit;
    ArrayList<String> keywords;
    ArrayList<Double> values;
    public static final int NOTIFICATION_TAPPED = 1;
    Button btnViewAllCharecters;
    public static ArrayList<Servant> listOfServants = new ArrayList<>();
    public static ArrayList<CraftEssence> listOfCraftEssences = new ArrayList<>();
    static int skillCount=0;
    String[] listOfSkills;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listOfSkills = getResources().getStringArray(R.array.skill_list);

        pbLoad = findViewById(R.id.pb_load);
        btnNew = findViewById(R.id.btn_new_info);
        btnLoad = findViewById(R.id.btn_load_info);
        btnChar = findViewById(R.id.btn_view_all_charecters);
        btnExit = findViewById(R.id.btn_exit);
        btnViewAllCharecters = findViewById(R.id.btn_view_all_charecters);

        //////////////////////////////////////////////////////////////////////////////////
        /**
         * implemented alarm manager to set the interval for the notification.
         *
         */
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent notificationIntent = new Intent("DISPLAY_NOTIFICATION");

        PendingIntent broadcast = PendingIntent.getBroadcast
                (this, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                10000, broadcast);//86400000 = 24hrs
        //////////////////////////////////////////////////////////////////////////////

        Intent intent = getIntent();
        //If this is the first time this activity is run, grab the data for the servants online
        if(String.valueOf(intent.getAction()).equals("android.intent.action.MAIN")
                || notificationIntent.getIntExtra("from_notification", 1)==1){
            new gatherData().execute();
            CraftEssence.populateCraftEssenceList(this);
        }

        btnViewAllCharecters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ViewCharecters.class);
                startActivity(intent);
            }
        });

        btnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchNewInformation();
            }
        });

        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchLoadInformation();
            }
        });

        btnChar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchViewCharacters();
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * gathers all Servant data from the website "https://grandorder.wiki/Servant_name"
     */
    public class gatherData extends AsyncTask<Void, Void, Void>{

        String words;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                String[] servantNameListResource = getResources().getStringArray(R.array.servant_list);

                for(int i = 0; i < servantNameListResource.length; i++){
                    Document document = Jsoup.connect("https://grandorder.wiki/"+servantNameListResource[i]).get();
                    words = document.text();
                    getServantData(servantNameListResource[i]);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Grabs the data pertaining to a specific servant
         * @param servantName
         */
        public void getServantData(String servantName){
            boolean basicStats = false;
            ArrayList<Skill> servantSkills = new ArrayList<>();
            Servant newServant = new Servant();

            if(servantName.length() > 20){
                servantName=servantName.substring(0,19);
            }
            newServant.setName(formatName(servantName));
            //Special Case : forces the name with unrecognized charecters to be manually changed
            if(servantName.charAt(0)=='Z') newServant.setName("Zhuge Liang");//name too long for layout

            String[] listWords = words.split(" ");

            for(int i = 0; i < listWords.length; i++){
                if(listWords[i].equals("Attack") && !basicStats){

                    newServant.setAttack(Double.parseDouble(listWords[i+3].replace(",", "")));
                    newServant.setHealth(Double.parseDouble(listWords[i+8].replace(",", "")));
                    newServant.setCost(Double.parseDouble(listWords[i+11]));
                    newServant.setStarAbsorption(Double.parseDouble(listWords[i+14]));
                    newServant.setStarGeneration(Double.parseDouble(listWords[i+17].replace("%", "")));
                    newServant.setNpChargeAttack(Double.parseDouble(listWords[i+21].replace("%", "")));
                    newServant.setNpChargeDefense(Double.parseDouble(listWords[i+25].replace("%", "")));

                    i += 25;
                    basicStats = true;
                }
                if((listWords[i]).equals("Starts")){//skill one
                    servantSkills.add(grabSkillData(listWords));

                }
                if((listWords[i]).equals("Unlocks")){//skill 2
                    if(listWords[i+2].equals("1st")){
                        servantSkills.add(grabSkillData(listWords));;
                    }
                    else {
                        servantSkills.add(grabSkillData(listWords));
                        break;
                    }
                }
            }
            newServant.setSkillList(servantSkills);
            listOfServants.add(newServant);
        }

        /**
         * The name of each servant is formatted to be presentable, no '_' or ' ' charectar in the
         * name string
         * @param servantName
         * @return
         */
        private String formatName(String servantName) {

            String noUnderScores = servantName.replace('_', ' ');
            String noDashes = noUnderScores.replace('-', ' ');

            return noDashes;
        }

        /**
         * specifically grabs the skill information, including the cooldown, the listWords[] contains
         * all the data for the skill including the effects, values and names
         * @param listWords
         * @return
         */
        public Skill grabSkillData(String[] listWords){
            Skill skill;
            int cooldown = 0;

            for(int k=0; k < listWords.length; k++){
                if(listWords[k].equals("Cooldown:")){
                    cooldown = Integer.parseInt(listWords[k+1])-2;
                    break;
                }
            }
            skill = populateSkill(cooldown);
            return skill;
        }

        /**
         * Once all the data has been extracted, close the spinner and make the menu buttons
         * visible.
         * @param aVoid
         */
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            LinearLayout printScreenLayout = findViewById(R.id.layout_print_screen);
            LinearLayout mainMenuLayout = findViewById(R.id.layout_main_menu);
            mainMenuLayout.setVisibility(View.VISIBLE);
            printScreenLayout.setVisibility(View.GONE);
        }
    }


    private void launchNewInformation(){
        Intent intent = new Intent(getApplicationContext(), NewFormation.class);
        intent.putExtra("source", "from_main_menu");
        startActivityForResult(intent, NEW_INFORMATION);
    }

    private void launchLoadInformation(){
        Intent intent = new Intent(getApplicationContext(), LoadFormation.class);
        intent.putExtra("RESULT_CODE", MAIN_ACTIVITY);
        startActivityForResult(intent, LOAD_INFORMATION);
    }

    private void launchViewCharacters(){
        Intent intent = new Intent(getApplicationContext(), ViewCharecters.class);
        intent.putExtra("source", "only_view");
        startActivityForResult(intent, VIEW_CHARACTERS);

    }
    ////////////////////////////////////////////MENU ITEMS////////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.menu_main_2, menu);
        return true;
    }

    /**
     * starts the main_menu activity storing the songs choice and the volume slider.
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id==R.id.action_settings){

            Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
            startActivityForResult(intent, MAIN_MENU_TWO);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        skillCount=0;
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * adds the effects and values for each skill posessed by each servant, these are stored in
     * seperate lists, ArrayList<Double> and ArrayList<String>
     * @param cooldown
     * @return
     */
    public Skill populateSkill(int cooldown){
        Skill skill = new Skill();
        skill.setName(listOfSkills[skillCount]);
        skill.setCooldown(cooldown);

        keywords = new ArrayList<>();
        values = new ArrayList<>();

        if(skillCount==0 || skillCount==3 || skillCount==20){//cu chulainn and cu alter
            implementeffect("Guts", 2500.0);
        }
        if(skillCount==1 || skillCount==19 || skillCount==39){//cu and cu alter
            implementeffect("Evade", 3.0);
        }
        if(skillCount==2 || skillCount==4 || skillCount==10 || skillCount==26){
            implementeffect("Heal", 1500.0);
        }
        if(skillCount==1 || skillCount==3 || skillCount==10 || skillCount==19 || skillCount==25
                || skillCount==39){//leonardo da vinci
            implementeffect("Defense Up", 40.0);
        }
        if(skillCount==34){
            implementeffect("All Defense Up", 20.0);
        }
        if(skillCount==4 || skillCount==5 || skillCount==6 || skillCount==11 || skillCount==23
                || skillCount==33 || skillCount==34 || skillCount==35  || skillCount==37){//lancelot(s), ozymandias
            implementeffect("NP Charge", 1.0);
        }
        if(skillCount==7 || skillCount==15 || skillCount==30 || skillCount==38){
            implementeffect("Crit Gather", 600.0);
        }
        if(skillCount==8 || skillCount==13 || skillCount==15 || skillCount==23
                || skillCount==32  || skillCount==33 || skillCount==40){
            implementeffect("Crit Damage", 50.0);
        }
        if(skillCount==5 || skillCount==6 || skillCount==14 || skillCount==27 || skillCount==31
                || skillCount==40 || skillCount==44){
            implementeffect("Crit Up", 15.0);
        }
        if(skillCount==10 || skillCount==16){//ozymandias
            implementeffect("Attack Up", 40.0);
        }
        if(skillCount==9 || skillCount==16  || skillCount==35 || skillCount==36 || skillCount==42){
            implementeffect("All Attack Up", 20.0);
        }
        if(skillCount==12 || skillCount==26 || skillCount==41){//emiya_assassin
            implementeffect("Arts Up", 35.0);
        }
        if(skillCount==17 || skillCount==22 || skillCount==43){//jalter, karna
            implementeffect("Buster Up", 50.0);
        }
        if(skillCount==18){//cu alter
            implementeffect("Lower Enemy Attack", 20.0);
        }
        if(skillCount==21){//karna
            implementeffect("Seal Enemy NP", 1.0);
        }
        if(skillCount==24){
            implementeffect("Lower Enemy NP", 1.0);
        }
        if(skillCount==28){
            implementeffect("Lower Enemy NP Strength", 1.0);
        }
        if(skillCount==29){
            implementeffect("Stun", 1.0);
        }

        skill.setEffects(keywords);
        skill.setValues(values);
        skillCount++;

        return skill;
    }

    private void implementeffect(String effect, double value){
        keywords.add(effect);
        values.add(value);
    }
}
