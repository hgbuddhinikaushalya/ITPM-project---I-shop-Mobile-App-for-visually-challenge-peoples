package com.example.ishopapp;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ishopapp.ShoppingCart.ShoppingCart;

import java.util.ArrayList;

public class Locations extends AppCompatActivity {

    Button map ;
    private static final int RECOGNIZER_RESULT =1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_locations);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;



        });

        map = findViewById(R.id.maps);

        Speak();

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openApp();
            }
        });



    }

    public void openApp(){
        Intent intent = getPackageManager().getLaunchIntentForPackage("com.google.android.apps.maps");

        if(intent != null){
            startActivity(intent);
        }else{
            Toast.makeText(Locations.this,"There is no app",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == RECOGNIZER_RESULT && resultCode == RESULT_OK){
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String str = matches.get(0).toString();
            //speechText.setText(str);

//            if(str.equals("open Google")){
//                openUrl("https://www.google.com");
//            }

            if(str.equals("go to map")){
                openApp();
            }

            if(str.equals("exit")){
                System.exit(0);

            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void Speak(){
        Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Speech to text");
        startActivityForResult(speechIntent,RECOGNIZER_RESULT);
    }

}