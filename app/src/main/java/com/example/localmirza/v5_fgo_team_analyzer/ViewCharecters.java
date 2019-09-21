package com.example.localmirza.v5_fgo_team_analyzer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import static com.example.localmirza.v5_fgo_team_analyzer.MainActivity.listOfServants;

public class ViewCharecters extends Activity {

    public static final int VIEW_SERVANT_DETAILS = 6;
    Button btnViewCharectersBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_charecters);

        btnViewCharectersBack = findViewById(R.id.btn_back_view_charecters);

        btnViewCharectersBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });
        DisplayServants();
    }

    /**
     * displays the the information for each servant in the List XML element, including their name
     * and picture. The layout 'servant_small_preview' is used as a base for how the servants are
     * presented.
     */
    public void DisplayServants(){
        LinearLayout servantList = findViewById(R.id.servants_list_view);

        for(int i = 0; i < listOfServants.size(); i++){
            LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout child = (LinearLayout) inflater.inflate(R.layout.servant_small_preview, null);

            ImageView servantPicture = (ImageView) child.getChildAt(0);
            TextView servantName = (TextView) child.getChildAt(1);

            Drawable drawable = getResources().getDrawable(getResources()
                    .getIdentifier("servant_" + (i+1) + "_small", "drawable", getPackageName()));
            servantPicture.setImageDrawable(drawable);

            servantName.setText(listOfServants.get(i).getName());

            final int servantNumber = i + 1;

            servantPicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewServantDetails(servantNumber);
                }
            });

            servantList.addView(child, i);

        }
    }

    /**
     * links to another activity which will show further details of the servant the user chooses,
     * passes in the servantNumber in the listOfServants, into the intent.
     * @param servantNumber
     */
    private void viewServantDetails(int servantNumber){
        Intent intent = getIntent();
        intent.setClass(getApplicationContext(), ViewServantDetails.class);
        intent.putExtra("servantNumber", servantNumber);
        startActivityForResult(intent, VIEW_SERVANT_DETAILS);
    }

    /**
     * If coming back from ViewServantDetails Activity, immedieatly go to NewFormation as a new servant
     * has been chosen, and it must be added to the formation in the NewFormation class. Otherwise,
     * do nothing else as the user is just viewing the servants.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==VIEW_SERVANT_DETAILS){
            if(resultCode==RESULT_OK){
                setResult(RESULT_OK, data);
                String source = data.getStringExtra("source");
                boolean choosingServant = data.getBooleanExtra("choosingServant", true);

                if(choosingServant && source.equals("changing_party")){
                    finish();
                }
            }
        }
    }
}