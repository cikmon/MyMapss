package com.example.cik.mymapss;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //FloatingActionButton map = (FloatingActionButton) findViewById(R.id.fab);

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


    public void onClick12(View view) {
        Toast toast = Toast.makeText(this, "Hello Android 7",Toast.LENGTH_LONG);
        toast.show();
    }
}

