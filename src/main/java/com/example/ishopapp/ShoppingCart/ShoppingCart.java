package com.example.ishopapp.ShoppingCart;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ishopapp.Location.LocationActivity;
import com.example.ishopapp.MainActivity;
import com.example.ishopapp.R;
import com.example.ishopapp.RecyclerView.Adapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class ShoppingCart extends AppCompatActivity {

    private static final int RECOGNIZER_RESULT = 0;
    private static final int ADD_SPEECH_REQUEST_CODE = 1;
    private static final int DELETE_SPEECH_REQUEST_CODE = 2;
    private static final int UPDATE_SPEECH_REQUEST_CODE = 3;
    private static final int SHOW_SPEECH_REQUEST_CODE = 4;
    RecyclerView recyclerView;
    List<String> nameList, eachPrice, quantityList, priceList,discountList;
    Adapter adapter;
    TextToSpeech textToSpeech;
    EditText delET;
    Button delete;
    private DatabaseReference dbRef1, dbRef2, dbRef3;
    private TextView itemName,TotalPricetv;
    private String strr;
    private String namef, nameUpdate, idf, command;
    private float pricef,discountf;

    private int quantityf, quantityUpdate;




    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        dbRef1 = FirebaseDatabase.getInstance().getReference("Cart");
        dbRef2 = FirebaseDatabase.getInstance().getReference("Product");
        dbRef3 = FirebaseDatabase.getInstance().getReference("Discount");


        ImageView tap = findViewById(R.id.tapImage);
        TotalPricetv = findViewById(R.id.totalPrice);

        RefreshRecyclerView();

        TextToSpeech("Welcome to Cart, double tap to set commands");
        //
        tap.setOnTouchListener(new View.OnTouchListener() {
            private static final long DOUBLE_TAP_TIME_DELTA = 300; // Maximum duration between two taps (in milliseconds)
            private long lastClickTime = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int eventAction = event.getAction();
                switch (eventAction) {
                    case MotionEvent.ACTION_DOWN:
                        long clickTime = System.currentTimeMillis();
                        if (clickTime - lastClickTime <= DOUBLE_TAP_TIME_DELTA) {
                            // Detected double tap

                            // addItemWithVoiceCommand();
                            // promptForName("add");
                            Speak(0, "SET COMMAND", "Set your command");
                            Toast.makeText(ShoppingCart.this, "Reacting to Double Tap..", Toast.LENGTH_SHORT).show();
                        }
                        lastClickTime = clickTime;
                        break;
                }
                return true;
            }
        });

        //recycler view
        recyclerView = findViewById(R.id.text_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        nameList = new ArrayList<>();
        eachPrice = new ArrayList<>();
        quantityList = new LinkedList<>();
        priceList = new LinkedList<>();
        discountList = new LinkedList<>();
        adapter = new Adapter(nameList, eachPrice, quantityList, priceList,discountList);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            strr = matches.get(0);

            if (strr.equals("add item")) {
                Speak(ADD_SPEECH_REQUEST_CODE, "ADD ITEM", "What do you want to add?");
            } else if (strr.equals("delete item")) {
                Speak(DELETE_SPEECH_REQUEST_CODE, "DELETE ITEM", "What do you want to delete?");
            } else if (strr.equals("update item")) {
                Speak(UPDATE_SPEECH_REQUEST_CODE, "UPDATE ITEM", "What do you want to update?");
            } else if (strr.equals("show items")) {

                requestCode = SHOW_SPEECH_REQUEST_CODE;

            } else if (strr.equals("what is my total price")) {
                TextToSpeech("Your total is "+String.valueOf(calculateTotal(priceList,discountList))+" and you got "+calculateDiscount(discountList)+" discount.");
            }
        }
        if (requestCode == ADD_SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            strr = matches.get(0);

            // After recognizing the speech, call the appropriate method based on the context
            if (namef == null) {
                // If namef is null, it means we need to prompt for the item name
                namef = strr; // Update namef with the recognized item name
                promptForQuantity(dbRef2, ADD_SPEECH_REQUEST_CODE); // Prompt for the quantity

            } else {
                // If namef is already set, it means we are prompted for the quantity
                quantityf = Integer.parseInt(strr); // Convert the recognized quantity to a number
                saveData(namef, quantityf); // Save the data with the provided item name and quantity
                namef = null;


            }
        }

        if (requestCode == UPDATE_SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            strr = matches.get(0);

            // After recognizing the speech, call the appropriate method based on the context
            if (namef == null) {
                // If namef is null, it means we need to prompt for the item name
                namef = strr; // Update namef with the recognized item name
                promptForQuantity(dbRef1, UPDATE_SPEECH_REQUEST_CODE); // Prompt for the quantity

            } else {
                // If namef is already set, it means we are prompted for the quantity
                quantityf = Integer.parseInt(strr); // Convert the recognized quantity to a number
                updateData(namef, quantityf); // Save the data with the provided item name and quantity
                namef = null;


            }
        }

        if (requestCode == DELETE_SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String recognizedText = matches.get(0);

            // Call the deleteItem method with the recognized item name
            deleteData(recognizedText);


        }

        if (requestCode == SHOW_SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String recognizedText = matches.get(0);

            fetchData();


        }

    }

    private void promptForQuantity(DatabaseReference databaseReference, int code) {

        CheckItemInDb(databaseReference, code);

    }

    public void CheckItemInDb(DatabaseReference databaseReference, int code) {
        databaseReference.orderByChild("name").equalTo(strr).get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String namef = dataSnapshot.child("name").getValue(String.class);

                    //  TextToSpeech("How many " + namef + " do you want?");
                    Speak(code, "ADD ITEM", "How many " + namef + " do you want?"); // Initiate speech recognition

//                    dataList.add(namef);
//                    recyclerView.setAdapter(adapter);
                }
            } else {
                TextToSpeech("Item does not exist");
                namef = null;
            }
        }).addOnFailureListener(err -> Toast.makeText(getApplicationContext(), "Fetch failed: " + err, Toast.LENGTH_SHORT).show());
    }

    public void Speak(int RequestCode, String text, String textTospeech) {
        // Initiate speech recognition
        Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, text);
        startActivityForResult(speechIntent, RequestCode);

        TextToSpeech(textTospeech);


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

    private void saveData(String itemName, int quantity) {
        dbRef2.orderByChild("name").equalTo(itemName).get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String id = dataSnapshot.child("id").getValue(String.class);
                    String name = dataSnapshot.child("name").getValue(String.class);
                    int QtyLeft = dataSnapshot.child("quantity").getValue(Integer.class);
                    float price = dataSnapshot.child("price").getValue(Float.class);
                    float totalPrice = price * quantity;

                    if (QtyLeft > quantity) {

                        //check discounts
                        dbRef3.orderByChild("id").equalTo(id).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                            @Override
                            public void onSuccess(DataSnapshot snapshot) {

                                Cart cart = null;
                                if (snapshot.exists()) {
                                    // Loop through the result (though there should be only one match)
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        float Unitdiscount = dataSnapshot.child("discount").getValue(Float.class);
                                        float discountf = (float) ((Unitdiscount / 100.0) * totalPrice);
                                        cart = new Cart(id, name, quantity, totalPrice, discountf);
                                    }
                                }else{
                                        discountf = 0;
                                     cart = new Cart(id, name, quantity, totalPrice, discountf);
                                }

                                        dbRef1.child(name).setValue(cart).addOnSuccessListener(aVoid -> {
                                            RefreshRecyclerView();
                                            TextToSpeech("Item added. Anything else?");
                                            Toast.makeText(getApplicationContext(), "Added to cart", Toast.LENGTH_SHORT).show();

                                            //add to recycler view
                                            nameList.add(name);
                                            eachPrice.add(String.valueOf(price));
                                            quantityList.add(String.valueOf(quantity));
                                            priceList.add(String.valueOf(totalPrice));
                                            discountList.add(String.valueOf(discountf));

                                            recyclerView.setAdapter(adapter);
                                            //System.out.println(discountList);
                                            //checkDiscounts(name);

//                                            float x = calculateTotal(priceList);
//                                            TotalPricetv.setText(String.valueOf(x));


                                        }).addOnFailureListener(e -> {
                                            TextToSpeech("Failed to add item to cart");
                                            Toast.makeText(getApplicationContext(), "Insert failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });

                                        Map<String, Object> updateProductQty = new HashMap<>();
                                        // updateProductQty.put("name", name);
                                        updateProductQty.put("quantity", (QtyLeft - quantity));

                                        // QtyLeft = QtyLeft-quantity;

                                        dbRef2.child(id).updateChildren(updateProductQty).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // RefreshRecyclerView();
                                                //TextToSpeech("Item Updated");
                                                Toast.makeText(getApplicationContext(), "Item Updated", Toast.LENGTH_SHORT).show();
                                                //nameUpdate = null;

                                            }
                                        });


                                    Toast.makeText(ShoppingCart.this, "Successfully fetched data", Toast.LENGTH_SHORT).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ShoppingCart.this, "Fetch failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });





                        return; // Exit the loop once data is saved
                    } else {
                        TextToSpeech("Sorry, low quantity in stock");
                    }
                }
            } else {
                TextToSpeech("Item does not exist");
            }
        }).addOnFailureListener(err -> {
            TextToSpeech("Failed to retrieve item details");
            Toast.makeText(getApplicationContext(), "Fetch failed: " + err, Toast.LENGTH_SHORT).show();
        });


    }

    private void fetchData() {

        dbRef1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //clear recycler view
                clearItems();
                recyclerView.setAdapter(adapter);

                // Loop through the dataSnapshot to retrieve all data
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Retrieve data as needed
                    String name = snapshot.child("name").getValue(String.class);
                    float price = snapshot.child("price").getValue(Float.class);
                    float discount = snapshot.child("discount").getValue(Float.class);
                    Integer quantity = snapshot.child("quantity").getValue(Integer.class);

                    // Add data to the lists
                    nameList.add(name);
                    eachPrice.add(String.format("%.2f",(price / quantity)));
                    priceList.add(String.format("%.2f",price));
                    quantityList.add(String.valueOf(quantity));
                    discountList.add(String.format("%.2f",discount));

                }


                // Set the adapter to the RecyclerView
                recyclerView.setAdapter(adapter);
                speakAllItems();

                float total = calculateTotal(priceList,discountList);
                float price = calculatePrice(priceList);
                float discount = calculateDiscount(discountList);

                TotalPricetv.setText("Price: "+String.valueOf(price)+" Discount: "+String.valueOf(discount)+" Total: "+String.valueOf(total));


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors or canceled requests
                Log.e("Firebase", "Error retrieving data", databaseError.toException());
            }
        });
    }

    private void RefreshRecyclerView() {

        dbRef1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //clear recycler view
                clearItems();
                recyclerView.setAdapter(adapter);

                // Loop through the dataSnapshot to retrieve all data
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Retrieve data as needed
                    String name = snapshot.child("name").getValue(String.class);
                    float price = snapshot.child("price").getValue(Float.class);
                    float discount = snapshot.child("discount").getValue(Float.class);
                    Integer quantity = snapshot.child("quantity").getValue(Integer.class);

                    // Add data to the lists
                    nameList.add(name);
                    eachPrice.add(String.format("%.2f",(price / quantity)));
                    priceList.add(String.format("%.2f",price));
                    quantityList.add(String.valueOf(quantity));
                    discountList.add(String.format("%.2f",discount));




                }

                float total = calculateTotal(priceList,discountList);
                float price = calculatePrice(priceList);
                float discount = calculateDiscount(discountList);

                TotalPricetv.setText("Price: "+String.valueOf(price)+" Discount: "+String.valueOf(discount)+" Total: "+String.valueOf(total));

                // Set the adapter to the RecyclerView
                recyclerView.setAdapter(adapter);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors or canceled requests
                Log.e("Firebase", "Error retrieving data", databaseError.toException());
            }
        });
    }

    private void updateData(String name, int quantityN) {

        dbRef2.orderByChild("name").equalTo(name).get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String id2 = dataSnapshot.child("id").getValue(String.class);
                    String name2 = dataSnapshot.child("name").getValue(String.class);
                    int QtyLeft = dataSnapshot.child("quantity").getValue(Integer.class);

                    dbRef1.orderByChild("name").equalTo(name).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot snapshot) {


                            if (snapshot.exists()) {

                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    //    String id = dataSnapshot.child("id").getValue(String.class);
                                    String name = dataSnapshot.child("name").getValue(String.class);
                                    float price = dataSnapshot.child("price").getValue(Float.class);
                                    float discount = dataSnapshot.child("discount").getValue(Float.class);
                                    int quantity = dataSnapshot.child("quantity").getValue(Integer.class);

                                    float newPrice = (price / quantity) * Float.parseFloat(String.valueOf(quantityN));
                                    float newDiscount = (discount/quantity)*Float.parseFloat(String.valueOf(quantityN));

                                    int qtySum = QtyLeft + quantity;

                                    if (qtySum > quantityN) {

                                        Map<String, Object> cart = new HashMap<>();
                                        cart.put("name", name);
                                        cart.put("price", newPrice);
                                        cart.put("quantity", quantityN);
                                        cart.put("discount",newDiscount);

                                        Map<String, Object> product = new HashMap<>();
                                        product.put("id", id2);
                                        product.put("quantity", (qtySum - quantityN));

                                        dbRef2.child(id2).updateChildren(product).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // RefreshRecyclerView();
                                                //TextToSpeech("Item Updated");
                                                Toast.makeText(getApplicationContext(), "Item Updated", Toast.LENGTH_SHORT).show();
                                                //nameUpdate = null;

                                            }
                                        });

                                        dbRef1.child(name).updateChildren(cart).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                RefreshRecyclerView();
                                                TextToSpeech("Item Updated");
                                                Toast.makeText(getApplicationContext(), "Item Updated", Toast.LENGTH_SHORT).show();
                                                nameUpdate = null;
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(), "Data Update Failed", Toast.LENGTH_SHORT).show();
                                                nameUpdate = null;
                                            }
                                        });
                                        return;
                                    }else {
                                        TextToSpeech("Sorry, low quantity in stock");
                                    }
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "item does not exist", Toast.LENGTH_SHORT).show();
                                nameUpdate = null;
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Update failed", Toast.LENGTH_SHORT).show();
                            nameUpdate = null;
                        }
                    });
                }
            } else {

            }
        });

    }


    private void deleteData(String delProduct) {
        dbRef1.orderByChild("name").equalTo(delProduct).get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    int quantity = dataSnapshot.child("quantity").getValue(Integer.class);

                    dbRef2.orderByChild("name").equalTo(delProduct).get().addOnSuccessListener(productSnapshot -> {
                        if (productSnapshot.exists()) {
                            for (DataSnapshot productDataSnapshot : productSnapshot.getChildren()) {
                                String id = productDataSnapshot.getKey();
                                int remainQuantity = productDataSnapshot.child("quantity").getValue(Integer.class);
                                int updatedQuantity = quantity + remainQuantity;

                                Map<String, Object> productUpdates = new HashMap<>();
                                productUpdates.put("quantity", updatedQuantity);

                                dbRef2.child(id).updateChildren(productUpdates).addOnSuccessListener(aVoid -> {
                                    dbRef1.child(delProduct).removeValue().addOnSuccessListener(aVoid1 -> {
                                        RefreshRecyclerView();
                                        TextToSpeech("Item deleted");
                                        Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
                                    }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Delete failed", Toast.LENGTH_SHORT).show());
                                });
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Product does not exist", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Failed to fetch product data", Toast.LENGTH_SHORT).show());
                }
            } else {
                Toast.makeText(getApplicationContext(), "Item does not exist in the cart", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Failed to fetch cart data", Toast.LENGTH_SHORT).show());
    }


    public void clearItems() {
        nameList.clear();
        eachPrice.clear();
        quantityList.clear();
        priceList.clear();
        discountList.clear();
    }

    private void speakAllItems() {
        for (int i = 0; i < nameList.size(); i++) {
            final int index = i;
            // Delay each speech by 1 second
            new android.os.Handler().postDelayed(new Runnable() {
                        public void run() {
                            TextToSpeech(quantityList.get(index) + " quantity of " + nameList.get(index));
                        }
                    }, 1000L * i // Delay in milliseconds (1 second multiplied by the index)


            );


        }
    }

    float calculateTotal(List<String> Pricelist,List<String> Discountlist){
        float price = 0, dis = 0;
        for (int i = 0; i < priceList.size(); i++) {
            price += Float.parseFloat(priceList.get(i));
            dis += Float.parseFloat(Discountlist.get(i));
        }
        return Float.parseFloat(String.format("%.3f", (price - dis)));
    }

    float calculatePrice(List<String> Pricelist){
        float price = 0;
        for (int i = 0; i < priceList.size(); i++) {
            price += Float.parseFloat(priceList.get(i));

        }
        return Float.parseFloat(String.format("%.3f", (price)));
    }

    float calculateDiscount(List<String> Discountlist){
        float dis = 0;
        for (int i = 0; i < Discountlist.size(); i++) {
            dis += Float.parseFloat(Discountlist.get(i));

        }
        return Float.parseFloat(String.format("%.3f", (dis)));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ShoppingCart.this, MainActivity.class);
        startActivity(intent);
    }


           /* public int convertWordToNumber(String word) {
                // Create a map to store the mapping of word representations to numbers
                Map<String, Integer> wordToNumberMap = new HashMap<>();
                wordToNumberMap.put("zero", 0);
                wordToNumberMap.put("one", 1);
                wordToNumberMap.put("two", 2);
                wordToNumberMap.put("three", 3);
                wordToNumberMap.put("four", 4);
                wordToNumberMap.put("five", 5);
                wordToNumberMap.put("six", 6);
                wordToNumberMap.put("seven", 7);
                wordToNumberMap.put("eight", 8);
                wordToNumberMap.put("nine", 9);
                wordToNumberMap.put("ten", 10);
                wordToNumberMap.put("eleven", 11);
                wordToNumberMap.put("twelve", 12);
                wordToNumberMap.put("thirteen", 13);
                wordToNumberMap.put("fourteen", 14);
                wordToNumberMap.put("fifteen", 15);
                wordToNumberMap.put("sixteen", 16);
                wordToNumberMap.put("seventeen", 17);
                wordToNumberMap.put("eighteen", 18);
                wordToNumberMap.put("nineteen", 19);
                wordToNumberMap.put("twenty", 20);
                wordToNumberMap.put("thirty", 30);
                wordToNumberMap.put("forty", 40);
                wordToNumberMap.put("fifty", 50);
                wordToNumberMap.put("sixty", 60);
                wordToNumberMap.put("seventy", 70);
                wordToNumberMap.put("eighty", 80);
                wordToNumberMap.put("ninety", 90);

                // Split the input into words
                String[] words = word.split("\\s+");

                int number = 0;
                int temp = 0;
                for (String w : words) {
                    int value = wordToNumberMap.getOrDefault(w, -1);
                    if (value != -1) {
                        temp += value;
                    } else if (w.equals("hundred")) {
                        temp *= 100;
                    } else if (w.equals("thousand")) {
                        number += temp * 1000;
                        temp = 0;
                    } else if (w.equals("million")) {
                        number += temp * 1000000;
                        temp = 0;
                    } else if (w.equals("billion")) {
                        number += temp * 1000000000;
                        temp = 0;
                    }
                }
                number += temp;
                return number;
            }*/
}