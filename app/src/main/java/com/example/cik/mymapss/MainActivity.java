package com.example.cik.mymapss;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
   private FloatingActionButton Fbtn;
    private  static  final int VOICE_RECOGNITION_REQUEST_CODE = 1001;
    private ArrayList<String> strPoluch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Fbtn = (FloatingActionButton) findViewById(R.id.fab);
/////
       /* map.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                Toast toast = Toast.makeText(this, "Hello Android 7",Toast.LENGTH_LONG);
                toast.show();

                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                 //       .setAction("Action", null).show();
            }
        });
*/


}
    public void onClick12(View view){
        CheckVoiceRecognition();
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getClass().getPackage().getName());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Говорите");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        startActivityForResult(intent,VOICE_RECOGNITION_REQUEST_CODE);
    }


    public  void  CheckVoiceRecognition(){
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH),0);
        if (activities.size()==0){
            Fbtn.setEnabled(false);
            Toast.makeText(this,"Voice recognizer not present",Toast.LENGTH_LONG).show();
        }
    }
/*
    public void onClick1(View view) {
        Toast toast = Toast.makeText(this, "Hello Android 7",Toast.LENGTH_LONG);
        toast.show();
    }
    */

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        if (resultCode == RESULT_OK){
            ArrayList<String> textMatchlist = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            if (!textMatchlist.isEmpty()){
                if (textMatchlist.get(0).contains("search")){
                    String searchQuery = textMatchlist.get(0).replace("search"," ");
                    Intent search = new Intent(Intent.ACTION_WEB_SEARCH);
                    search.putExtra(SearchManager.QUERY,searchQuery);
                    startActivity(search);
                }
                else {
                    strPoluch=textMatchlist;
                    Toast.makeText(this, strPoluch.get(0),Toast.LENGTH_LONG).show();
                }
            }
        }
        else if (resultCode == RecognizerIntent.RESULT_AUDIO_ERROR){
            showToastMessage("Audio Error");

        }
        else if ((resultCode == RecognizerIntent.RESULT_CLIENT_ERROR)){
            showToastMessage("Client Error");

        }
        else if (resultCode == RecognizerIntent.RESULT_NETWORK_ERROR){
            showToastMessage("Network Error");
        }
        else if (resultCode == RecognizerIntent.RESULT_NO_MATCH){
            showToastMessage("No Match");
        }
        else if (resultCode == RecognizerIntent.RESULT_SERVER_ERROR){
            showToastMessage("Server Error");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


void  showToastMessage(String message){
    Toast.makeText(this,message,Toast.LENGTH_LONG).show();
}

}

