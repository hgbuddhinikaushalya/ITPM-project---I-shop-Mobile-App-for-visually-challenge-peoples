package com.example.ishopapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.ishopapp.Discount.DiscountActivity;
import com.example.ishopapp.Location.LocationActivity;
import com.example.ishopapp.ProductManagement.ProductManagement;
import com.example.ishopapp.ShoppingCart.ShoppingCart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Button product,discount,cart,map,contribution;
    //EditText speechText;
    ImageView speak;

    TextToSpeech textToSpeech;

    ArrayList<String> array;

    private static final int RECOGNIZER_RESULT =1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        product= findViewById(R.id.button);
        discount= findViewById(R.id.discount);
        cart= findViewById(R.id.cart);
        map= findViewById(R.id.map);
        contribution= findViewById(R.id.contribution);
        speak = findViewById(R.id.hand);

        array = new ArrayList<>();

        array.addAll(Arrays.asList("say, the shopping cart to open you cart","say, supermarket map to get directions"));
       // speechText = findViewById(R.id.text);

        //Speak();
        TextToSpeech("Hi, Please tap the screen and order");

        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Speak();
                TextToSpeech("what do you want to open");
            }
        });

        product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProductManagement.class);
                startActivity(intent);
                finish();

            }
        });

        discount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DiscountActivity.class);
                startActivity(intent);
                finish();
            }
        });

        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ShoppingCart.class);
                startActivity(intent);
                finish();

            }
        });

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LocationActivity.class);
                startActivity(intent);
                finish();

            }
        });

        contribution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ContributionActivity.class);
                startActivity(intent);
                finish();

            }
        });
    }

    private void openUrl(String url) {

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == RECOGNIZER_RESULT && resultCode == RESULT_OK){
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String str = matches.get(0);
            //speechText.setText(str);

//            if(str.equals("open Google")){
//                openUrl("https://www.google.com");
//            }

            if(str.equals("the shopping cart")){
                Intent intent = new Intent(MainActivity.this, ShoppingCart.class);
                startActivity(intent);
                finish();
            }

            if(str.equals("supermarket map")){
                Intent intent = new Intent(MainActivity.this, LocationActivity.class);
                startActivity(intent);
                finish();
            }

            if(str.equals("exit")){
                System.exit(0);

            }

            if(str.equals("read instructions")){
                new android.os.Handler().postDelayed(new Runnable() {
                    int index = 0;

                    @Override
                    public void run() {
                        if (index < array.size()) {
                            TextToSpeech(array.get(index));
                            index++;
                            // Schedule the next iteration with a delay of 1000 milliseconds
                            new android.os.Handler().postDelayed(this, 1000L);
                        }
                    }
                }, 1000L); // Delay the initial execution by 1000 milliseconds


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

    public void TextToSpeech(String str){

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i==TextToSpeech.SUCCESS){
                    textToSpeech.setLanguage(Locale.UK);
                    textToSpeech.setSpeechRate(1.0f);
                    textToSpeech.speak(str,TextToSpeech.QUEUE_ADD,null);
                }
            }
        });

    }



}

