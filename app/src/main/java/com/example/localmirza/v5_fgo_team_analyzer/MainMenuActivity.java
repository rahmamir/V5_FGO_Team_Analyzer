package com.example.localmirza.v5_fgo_team_analyzer;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Parcel;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainMenuActivity extends Activity {

    MediaPlayer aimerBrave, aimerHana, aimerStar;
    int previousSong = -1;
    Button playBrave, playHana, playStar;
    SeekBar sbVol;
    TextView txtVol;
    AudioManager manager;
    Spinner spnWallpaper;

    int min = 0;
    int max = 100;
    int current = 13;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_menu);
        playBrave = findViewById(R.id.btn_song_1);
        playHana = findViewById(R.id.btn_song_2);
        playStar = findViewById(R.id.btn_song_3);
        sbVol = findViewById(R.id.sb_volume);
        txtVol = findViewById(R.id.txt_volume);
        spnWallpaper = findViewById(R.id.spn_wallpaper);
        /**
         * grabs each mp3 file to be used for the music choice, and stores them in MediaPlayer objects
         * NOTE = this only works for API 26, not other APIs
         */
        final AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

        manager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int volMax = manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int proVol = manager.getStreamVolume(AudioManager.STREAM_MUSIC);
        sbVol.setMax(volMax);
        sbVol.setProgress(proVol);

        int resID=getResources().getIdentifier("aimer_brave_shine", "raw", getPackageName());
        aimerBrave=MediaPlayer.create(this,resID);

        resID=getResources().getIdentifier("aimer_hana", "raw", getPackageName());
        aimerHana=MediaPlayer.create(this,resID);

        resID=getResources().getIdentifier("aimer_star", "raw", getPackageName());
        aimerStar=MediaPlayer.create(this,resID);


        sbVol.setMax(max - min);
        sbVol.setProgress(current - min);
        txtVol.setText("" + current);

        playBrave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAllExcept(0);
                songSelection("Brave Shine");
            }
        });

        playHana.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAllExcept(1);
                songSelection("Hana No Uta");
            }


        });

        playStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAllExcept(2);
                songSelection("Last Stardust");
            }
        });


        /**
         * If the pprogress spinner is changed, update the volume accordingly.
         */
        sbVol.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                current = progress + min;
                txtVol.setText("" + current);
                audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {


            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        /**
         * If a background is selected from the list of backgrounds available, the background in the
         * main screen is changed into the picture selected
         */
        spnWallpaper.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Wallpaper wallpaper = new Wallpaper(parent.getAdapter().getItem(position).toString());

                String format = "Position is %d, and Value is %s";

                Toast.makeText(getApplicationContext(), String.format(format, position, wallpaper),
                        Toast.LENGTH_LONG).show();

                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View vi = inflater.inflate(R.layout.activity_main, null); //log.xml is your file.
                LinearLayout mainPageLinearLayout = vi.findViewById(R.id.layout_main_activity);
                if(position==0){
                    mainPageLinearLayout.setBackground(getResources().getDrawable(R.drawable.general_background));
                }
                else {
                    Drawable drawable = getResources().getDrawable(getResources().
                            getIdentifier("fate_background_"+position, "drawable",
                                    getPackageName()));

                    mainPageLinearLayout.setBackground(drawable);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });
    }

    /**
     * This is called every time the user makes a different song selection. The previous song playing
     * if any, is paused, so that the new song can be played.
     * @param player
     */
    private void stopAllExcept(int player){
        if(previousSong==-1){
            previousSong=player;
        }
        else{
            if(previousSong==0){
                aimerBrave.pause();
            }
            else if(previousSong==1){
                aimerHana.pause();
            }
            else if(previousSong==2){
                aimerStar.pause();
            }
        }

        if(player==0){
            aimerBrave.start();
        }
        else if(player==1){
            aimerHana.start();
        }
        else{
            aimerStar.start();
        }
        previousSong = player;
    }

    /**
     * Displays which song the user has selected. Also contains the adapter being set up for the
     * list of background the user can pick.
     * @param songName
     */
    private void songSelection(String songName){

        Toast.makeText(this, "Song Chosen : " + songName, Toast.LENGTH_LONG).show();

    String[] wallpaper = getResources().getStringArray(R.array.available_backgrounds);

    ArrayAdapter<String> wallAdapter = new ArrayAdapter<>(this,
            android.R.layout.simple_list_item_1, wallpaper);

    wallAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    spnWallpaper.setAdapter(wallAdapter);

    }

}



