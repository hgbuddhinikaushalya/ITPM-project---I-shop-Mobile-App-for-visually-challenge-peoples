package com.example.ishopapp.Location;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ishopapp.MainActivity;
import com.example.ishopapp.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class LocationActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {

    int REQUEST_CODE = 1;
    IndoorGraph graph;
    IndoorLocation[] IndoorLocationArr;
    TextView pathtv;
    TextToSpeech textToSpeech;
    StringBuilder pathString;
    ArrayList<String> LocationNameArr;

    int previousVal = 0;

    int Maxsize = 27;
    int totalDistance = 0;

    int check = 0;

    private GestureDetector gestureDetector;
    private float x1, x2, y1, y2;
    private static int MIN_DISTANCE = 150;

    int currentLocNum = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_location);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        this.gestureDetector = new GestureDetector(LocationActivity.this, this);

        TextToSpeech("swipe up to get directions.");

        ImageView point = findViewById(R.id.point);
        ImageView imageView = findViewById(R.id.imageView);
        pathtv = findViewById(R.id.pathtv);
       // ImageView tap = findViewById(R.id.tapImage);


        // Create indoor graph and add indoor locations
        graph = new IndoorGraph();

        LocationNameArr = new ArrayList<>();
        ArrayList<String> LocationObjectNameArr = new ArrayList<>();
        ArrayList<Float> LocationX = new ArrayList<>();
        ArrayList<Float> LocationY = new ArrayList<>();

        IndoorLocationArr = new IndoorLocation[Maxsize];

        LocationNameArr.addAll(Arrays.asList("bakery", "meat", "flowers", "drinks", "sea foods", "ATM", "milk", "Frozen Foods",
                "Asian foods", "cashier", "entrance", "exit", "canned goods", "household items", "hair care", "snacks", "pasta", "CD1", "CD2", "CD3", "CD4", "CD5", "O1", "O2", "O3", "O4", "O5"));

//        LocationObjectNameArr.addAll(Arrays.asList("Bakery", "Meat", "Flowers", "Drinks", "Sea_foods", "ATM", "Milk", "Frozen_foods",
//                "Rice", "Cash desk", "Entrance", "Exit", "Canned goods", "Household_items", "Shampoo", "Snacks", "Pasta"));

        LocationX.addAll(Arrays.asList(0.0f, 0.0f, 0.0f, 0.0f, 0.3f, 0.55f, 0.2f, 0.5f, 0.2f, 0.6f, 0.7f, 0.7f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f,
                0.0f, 0.0f, 0.0f, 0.0f, 0.0f));
        LocationY.addAll(Arrays.asList(0.06f, 0.4f, 0.7f, 0.9f, 0.9f, 0.9f, 0.06f, 0.06f, 0.17f, 0.5f, 0.9f, 0.0f, 0.37f, 0.45f, 0.55f, 0.62f, 0.72f, 0.37f, 0.45f, 0.55f, 0.62f, 0.72f,
                0.37f, 0.45f, 0.55f, 0.62f, 0.72f));


        imageView.post(new Runnable() {
            @Override
            public void run() {

                int[] location = new int[2];
                imageView.getLocationOnScreen(location);

                int imageViewX = location[0];
                int imageViewY = location[1];

                int maxX = imageView.getWidth();
                int maxY = imageView.getHeight();
                //  textView.setText(String.valueOf(maxX)+" "+String.valueOf(maxY));

// Calculate the position where you want to mark on the image (example)
                //  int markerX = maxX; // example X coordinate
                //  int markerY = maxY; // example Y coordinate

// Set the position of the marker ImageView

                point.setX((float) (maxX * 0.0));
                point.setY((float) (maxY * 0.7));

                for (int i = 0; i < Maxsize; i++) {

                    // Create indoor graph and add indoor locations
                    IndoorLocationArr[i] = new IndoorLocation(LocationNameArr.get(i), maxX * LocationX.get(i), maxX * LocationY.get(i));

                    // Add nodes to the graph
                    graph.addNode(IndoorLocationArr[i]);

                }

                graph.addEdge(IndoorLocationArr[10], IndoorLocationArr[5], IndoorGraph.calculateDistance(IndoorLocationArr[10], IndoorLocationArr[5]));
                graph.addEdge(IndoorLocationArr[5], IndoorLocationArr[4], IndoorGraph.calculateDistance(IndoorLocationArr[5], IndoorLocationArr[4]));
                graph.addEdge(IndoorLocationArr[4], IndoorLocationArr[3], IndoorGraph.calculateDistance(IndoorLocationArr[4], IndoorLocationArr[3]));
                graph.addEdge(IndoorLocationArr[3], IndoorLocationArr[2], IndoorGraph.calculateDistance(IndoorLocationArr[3], IndoorLocationArr[2]));
                graph.addEdge(IndoorLocationArr[2], IndoorLocationArr[1], IndoorGraph.calculateDistance(IndoorLocationArr[2], IndoorLocationArr[1]));
                graph.addEdge(IndoorLocationArr[1], IndoorLocationArr[0], IndoorGraph.calculateDistance(IndoorLocationArr[1], IndoorLocationArr[0]));
                graph.addEdge(IndoorLocationArr[0], IndoorLocationArr[6], IndoorGraph.calculateDistance(IndoorLocationArr[0], IndoorLocationArr[6]));
                graph.addEdge(IndoorLocationArr[6], IndoorLocationArr[7], IndoorGraph.calculateDistance(IndoorLocationArr[6], IndoorLocationArr[7]));
                graph.addEdge(IndoorLocationArr[7], IndoorLocationArr[11], IndoorGraph.calculateDistance(IndoorLocationArr[7], IndoorLocationArr[11]));
                graph.addEdge(IndoorLocationArr[6], IndoorLocationArr[8], IndoorGraph.calculateDistance(IndoorLocationArr[6], IndoorLocationArr[8]));
                graph.addEdge(IndoorLocationArr[8], IndoorLocationArr[12], IndoorGraph.calculateDistance(IndoorLocationArr[8], IndoorLocationArr[12]));
                graph.addEdge(IndoorLocationArr[7], IndoorLocationArr[17], IndoorGraph.calculateDistance(IndoorLocationArr[7], IndoorLocationArr[9]));
                graph.addEdge(IndoorLocationArr[21], IndoorLocationArr[9], IndoorGraph.calculateDistance(IndoorLocationArr[12], IndoorLocationArr[9]));
                graph.addEdge(IndoorLocationArr[20], IndoorLocationArr[9], IndoorGraph.calculateDistance(IndoorLocationArr[13], IndoorLocationArr[9]));
                graph.addEdge(IndoorLocationArr[19], IndoorLocationArr[9], IndoorGraph.calculateDistance(IndoorLocationArr[14], IndoorLocationArr[9]));
                graph.addEdge(IndoorLocationArr[18], IndoorLocationArr[9], IndoorGraph.calculateDistance(IndoorLocationArr[15], IndoorLocationArr[9]));
                graph.addEdge(IndoorLocationArr[17], IndoorLocationArr[9], IndoorGraph.calculateDistance(IndoorLocationArr[16], IndoorLocationArr[9]));
               // graph.addEdge(IndoorLocationArr[10], IndoorLocationArr[11], IndoorGraph.calculateDistance(IndoorLocationArr[10], IndoorLocationArr[11]));
                graph.addEdge(IndoorLocationArr[4], IndoorLocationArr[16], IndoorGraph.calculateDistance(IndoorLocationArr[4], IndoorLocationArr[3]));

                graph.addEdge(IndoorLocationArr[16], IndoorLocationArr[21], IndoorGraph.calculateDistance(IndoorLocationArr[16], IndoorLocationArr[21]));
                graph.addEdge(IndoorLocationArr[15], IndoorLocationArr[20], IndoorGraph.calculateDistance(IndoorLocationArr[15], IndoorLocationArr[20]));
                graph.addEdge(IndoorLocationArr[14], IndoorLocationArr[19], IndoorGraph.calculateDistance(IndoorLocationArr[14], IndoorLocationArr[19]));
                graph.addEdge(IndoorLocationArr[13], IndoorLocationArr[18], IndoorGraph.calculateDistance(IndoorLocationArr[13], IndoorLocationArr[18]));
                graph.addEdge(IndoorLocationArr[12], IndoorLocationArr[17], IndoorGraph.calculateDistance(IndoorLocationArr[12], IndoorLocationArr[17]));

                graph.addEdge(IndoorLocationArr[16], IndoorLocationArr[26], IndoorGraph.calculateDistance(IndoorLocationArr[16], IndoorLocationArr[26]));
                graph.addEdge(IndoorLocationArr[15], IndoorLocationArr[25], IndoorGraph.calculateDistance(IndoorLocationArr[15], IndoorLocationArr[25]));
                graph.addEdge(IndoorLocationArr[14], IndoorLocationArr[24], IndoorGraph.calculateDistance(IndoorLocationArr[14], IndoorLocationArr[24]));
                graph.addEdge(IndoorLocationArr[13], IndoorLocationArr[23], IndoorGraph.calculateDistance(IndoorLocationArr[13], IndoorLocationArr[23]));
                graph.addEdge(IndoorLocationArr[12], IndoorLocationArr[22], IndoorGraph.calculateDistance(IndoorLocationArr[12], IndoorLocationArr[22]));

                graph.addEdge(IndoorLocationArr[26], IndoorLocationArr[25], IndoorGraph.calculateDistance(IndoorLocationArr[26], IndoorLocationArr[25]));
                graph.addEdge(IndoorLocationArr[25], IndoorLocationArr[24], IndoorGraph.calculateDistance(IndoorLocationArr[25], IndoorLocationArr[24]));
                graph.addEdge(IndoorLocationArr[24], IndoorLocationArr[23], IndoorGraph.calculateDistance(IndoorLocationArr[24], IndoorLocationArr[23]));
                graph.addEdge(IndoorLocationArr[23], IndoorLocationArr[22], IndoorGraph.calculateDistance(IndoorLocationArr[23], IndoorLocationArr[22]));
                graph.addEdge(IndoorLocationArr[3], IndoorLocationArr[22], IndoorGraph.calculateDistance(IndoorLocationArr[3], IndoorLocationArr[22]));
                graph.addEdge(IndoorLocationArr[3], IndoorLocationArr[23], IndoorGraph.calculateDistance(IndoorLocationArr[3], IndoorLocationArr[23]));
                graph.addEdge(IndoorLocationArr[3], IndoorLocationArr[24], IndoorGraph.calculateDistance(IndoorLocationArr[3], IndoorLocationArr[24]));
                graph.addEdge(IndoorLocationArr[3], IndoorLocationArr[25], IndoorGraph.calculateDistance(IndoorLocationArr[3], IndoorLocationArr[25]));

                graph.addEdge(IndoorLocationArr[5], IndoorLocationArr[3], IndoorGraph.calculateDistance(IndoorLocationArr[5], IndoorLocationArr[3]));
                graph.addEdge(IndoorLocationArr[3], IndoorLocationArr[1], IndoorGraph.calculateDistance(IndoorLocationArr[3], IndoorLocationArr[1]));
                graph.addEdge(IndoorLocationArr[3], IndoorLocationArr[0], IndoorGraph.calculateDistance(IndoorLocationArr[3], IndoorLocationArr[0]));
                graph.addEdge(IndoorLocationArr[0], IndoorLocationArr[7], IndoorGraph.calculateDistance(IndoorLocationArr[0], IndoorLocationArr[7]));
                graph.addEdge(IndoorLocationArr[0], IndoorLocationArr[11], IndoorGraph.calculateDistance(IndoorLocationArr[0], IndoorLocationArr[11]));
                graph.addEdge(IndoorLocationArr[6], IndoorLocationArr[11], IndoorGraph.calculateDistance(IndoorLocationArr[6], IndoorLocationArr[11]));

                graph.addEdge(IndoorLocationArr[10], IndoorLocationArr[3], IndoorGraph.calculateDistance(IndoorLocationArr[10], IndoorLocationArr[3]));
                graph.addEdge(IndoorLocationArr[10], IndoorLocationArr[4], IndoorGraph.calculateDistance(IndoorLocationArr[10], IndoorLocationArr[4]));


            }

        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String strr = matches.get(0);

            for (int i = 0; i < Maxsize; i++) {

                if (strr.equals(LocationNameArr.get(i))) {
                    FindShortestPath(currentLocNum, i);
                }


            }

        }

    }

    public void FindShortestPath(int current, int destination) {
        // Find shortest path between two indoor locations
        List<IndoorLocation> path = graph.shortestPath(IndoorLocationArr[current], IndoorLocationArr[destination]);

        // Display the path to the user
        pathString = new StringBuilder();

        // ArrayList<String> pathString2 = new ArrayList<>();

        for (int i = 0; i < path.size() - 1; i++) {

            IndoorLocation currentLocation = path.get(i);
            IndoorLocation nextLocation = path.get(i + 1);

            // Calculate the angle for the edge between currentLocation and nextLocation
            double deltaX = nextLocation.getX() - currentLocation.getX();
            double deltaY = nextLocation.getY() - currentLocation.getY();
            double angleRadians = Math.atan2(deltaY, deltaX);
            double angleDegrees = Math.toDegrees(angleRadians);
            angleDegrees = (angleDegrees + 360) % 360;

            pathString.append(currentLocation.getName()).append(" -> ");

            if (angleDegrees > 0 && angleDegrees <= 45) {
                angleDegrees = 0;
            } else if (angleDegrees > 45 && angleDegrees <= 90) {
                angleDegrees = 90;
            } else if (angleDegrees > 90 && angleDegrees <= 135) {
                angleDegrees = 90;
            } else if (angleDegrees > 135 && angleDegrees <= 180) {
                angleDegrees = 180;
            } else if (angleDegrees > 180 && angleDegrees <= 225) {
                angleDegrees = 180;
            } else if (angleDegrees > 225 && angleDegrees <= 270) {
                angleDegrees = 270;
            } else if (angleDegrees > 270 && angleDegrees <= 315) {
                angleDegrees = 270;
            } else if (angleDegrees > 315 && angleDegrees <= 360) {
                angleDegrees = 0;
            }


            pathString.append(nextLocation.getName()).append(" (Angle: ").append(angleDegrees).append(" degrees)\n");

            // Calculate the distance between currentLocation and nextLocation
            double distance = IndoorGraph.calculateDistance(currentLocation, nextLocation);

            // Format the distance to two decimal places
            DecimalFormat df = new DecimalFormat("#"); // Pattern for two decimal places
            String formattedDistance = df.format(distance);
            float newDistance = (float) (Float.parseFloat(formattedDistance) / 100.0);


            pathString.append(nextLocation.getName()).append(" (Distance: ").append(newDistance).append(" meters)\n");

            // Delay
            double finalAngleDegrees = angleDegrees;

            new android.os.Handler().postDelayed(new Runnable() {
                public void run() {
                    if (finalAngleDegrees == 0) {
                        TextToSpeech("turn right and continue straight " + newDistance + " meters");
                    } else if (finalAngleDegrees == 90) {
                        TextToSpeech("turn back " + newDistance + " meters");
                    } else if (finalAngleDegrees == 180) {
                        TextToSpeech("turn left and continue straight " + newDistance + " meters");
                    } else if (finalAngleDegrees == 270) {
                        TextToSpeech("turn right and continue straight " + newDistance + " meters");
                    }


                }
            }, (6000L * i)); // Delay in milliseconds (proportional to i )

            int x = 0;
            x += 6000L * i;

            current = destination;
            currentLocNum = current;


        }


        //  pathString.append("Destination");

        pathtv.setText("Shortest Path: " + pathString.toString());
        //Toast.makeText(this,"You have arrived",Toast.LENGTH_SHORT);
        //TextToSpeech("You have arrived");

    }

    public void TextToSpeech(String str) {
        // Convert text to speech and speak it
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    textToSpeech.setLanguage(Locale.UK);
                    textToSpeech.setSpeechRate(1.0f);
                    textToSpeech.speak(str, TextToSpeech.QUEUE_ADD, null);
                }
            }
        });
    }

    public void Speak() {
        // Initiate speech recognition
        Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "What do you want to buy?");
        startActivityForResult(speechIntent, 0);

        // TextToSpeech(textTospeech);


    }

    public void getAngle(IndoorLocation v1, IndoorLocation v2) {
        // Get the coordinates of the two vertices connected by the edge
        IndoorLocation vertex1 = v1; // Assuming vertex 1 is at index 10
        IndoorLocation vertex2 = v2;  // Assuming vertex 2 is at index 5

// Calculate the differences in x and y coordinates
        double deltaX = vertex2.getX() - vertex1.getX();
        double deltaY = vertex2.getY() - vertex1.getY();

// Calculate the angle using atan2 (or another appropriate method)
        double angle = Math.atan2(deltaY, deltaX);

// Convert the angle to degrees if needed
        angle = Math.toDegrees(angle);

// Ensure the angle is within [0, 360] range
        angle = (angle + 360) % 360;

        Toast.makeText(this, "Angle of the edge: " + angle, Toast.LENGTH_LONG);
//        System.out.println("Angle of the edge: " + angle);
//        pathtv.setText("Angle of the edge: " + angle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);

        switch (event.getAction()) {
            // Starting to swipe gesture
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                y1 = event.getY();
                break; // Add break statement here

            // Ending swipe gesture
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                y2 = event.getY();

                // Calculate differences
                float valueX = x2 - x1;
                float valueY = y2 - y1;

                if (Math.abs(valueX) > MIN_DISTANCE) {
                    // Horizontal swipe detected
                    if (x2 > x1) {
                        //Toast.makeText(this, "Right is swiped", Toast.LENGTH_SHORT).show();
                    } else {
                        //Toast.makeText(this, "Left is swiped", Toast.LENGTH_SHORT).show();
                    }
                } else if (Math.abs(valueY) > MIN_DISTANCE) {
                    // Vertical swipe detected
                    if (y2 > y1) {
                       // Toast.makeText(this, "Bottom is swiped", Toast.LENGTH_SHORT).show();
                    } else {
                        //Toast.makeText(this, "Top is swiped", Toast.LENGTH_SHORT).show();
                        Speak();
                        TextToSpeech("What do you want to buy?");
                    }
                }
                break; // Add break statement here
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(@NonNull MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(@NonNull MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(@NonNull MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(@NonNull MotionEvent e) {

    }

    @Override
    public boolean onFling(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(LocationActivity.this, MainActivity.class);
        startActivity(intent);
    }

}