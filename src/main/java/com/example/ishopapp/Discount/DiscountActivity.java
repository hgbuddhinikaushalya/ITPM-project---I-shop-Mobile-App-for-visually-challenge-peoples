package com.example.ishopapp.Discount;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ishopapp.Location.LocationActivity;
import com.example.ishopapp.MainActivity;
import com.example.ishopapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class DiscountActivity extends AppCompatActivity {

    Button add, search, update, delete, clear;
    TextView currentP, newP, name;
    EditText id, discount;
    DatabaseReference dbRef1, dbRef2;

    float publicdiscount;

    private String TId;
    private float TDiscount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discount);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        add = findViewById(R.id.add);
        search = findViewById(R.id.search);
        update = findViewById(R.id.update);
        delete = findViewById(R.id.delete);
        clear = findViewById(R.id.clear);

        currentP = findViewById(R.id.currentP);
        newP = findViewById(R.id.newP);

        id = findViewById(R.id.id);
        name = findViewById(R.id.name);
        discount = findViewById(R.id.discount);

        dbRef1 = FirebaseDatabase.getInstance().getReference("Discount");
        dbRef2 = FirebaseDatabase.getInstance().getReference("Product");

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDataFromTxtFields();
                saveData(TId, TDiscount);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchData();

            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getDataFromTxtFields();
                updateData(TId, TDiscount);

            }
        });


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDataFromTxtFields();
                deleteData(TId);

            }
        });


        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearData();
            }
        });

    }


    private void saveData(String Id, float Discount) {
        if (Id.isEmpty() || Discount == 0) {
            Toast.makeText(getApplicationContext(), "Fill all fields correctly", Toast.LENGTH_SHORT).show();
        } else {

            dbRef2.orderByChild("id").equalTo(Id).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            String namef = dataSnapshot.child("name").getValue(String.class);

                            // Item with the same Id already exists
                            dbRef1.orderByChild("id").equalTo(Id).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                @Override
                                public void onSuccess(DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        // Item with the same Id already exists
                                        Toast.makeText(getApplicationContext(), "Item already exists", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Item does not exist, proceed with insertion
                                        Discount discount = new Discount(Id, Discount, namef);


                                        dbRef1.child(Id).setValue(discount).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // Insertion successful
                                                Toast.makeText(getApplicationContext(), "Item successfully added", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Insertion failed
                                                Toast.makeText(getApplicationContext(), "Insert failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Query failed
                                    Toast.makeText(getApplicationContext(), "Query failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Item does not exist", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }


    private void fetchData() {


        String searchID = id.getText().toString().trim();
        if (searchID.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Enter the Item ID", Toast.LENGTH_SHORT).show();
        } else {
            dbRef1.orderByChild("id").equalTo(searchID).get().addOnSuccessListener(snapshot -> {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String idf = dataSnapshot.child("id").getValue(String.class);
                        String namef = dataSnapshot.child("name").getValue(String.class);
                        float discountf = dataSnapshot.child("discount").getValue(float.class);

                        publicdiscount = discountf;

                        id.setText(idf);
                        name.setText(namef);
                        discount.setText(String.valueOf(discountf));

                        Toast.makeText(getApplicationContext(), "Successfully fetched data", Toast.LENGTH_SHORT).show();


                        //get prices
                        dbRef2.orderByChild("id").equalTo(searchID).get().addOnSuccessListener(snapshot2 -> {
                            if (snapshot2.exists()) {
                                for (DataSnapshot dataSnapshot2 : snapshot2.getChildren()) {

                                    float pricef = dataSnapshot2.child("price").getValue(float.class);


                                    currentP.setText("Unit Price (Rs.): " + pricef);

                                    float dis = pricef - (pricef * (publicdiscount / 100));

                                    newP.setText("New Price (Rs.): " + String.format("%.2f", dis));

                                    Toast.makeText(getApplicationContext(), "Successfully fetched data", Toast.LENGTH_SHORT).show();

                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Item does not exist: " + searchID, Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(err -> Toast.makeText(getApplicationContext(), "Fetch failed: " + err, Toast.LENGTH_SHORT).show());


                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Item does not exist: " + searchID, Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(err -> Toast.makeText(getApplicationContext(), "Fetch failed: " + err, Toast.LENGTH_SHORT).show());

        }
    }

    private void updateData(String Id, float Discount) {
        Map<String, Object> discount = new HashMap<>();
        discount.put("id", Id);

        discount.put("discount", Discount);

        dbRef2.orderByChild("id").equalTo(Id).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    dbRef1.orderByChild("id").equalTo(Id).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        // @Override
                        public void onSuccess(DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                dbRef1.child(Id).updateChildren(discount).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    //  @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getApplicationContext(), "Data Updated", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "Data Update Failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Toast.makeText(getApplicationContext(), "ID does not exist", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Update failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Item does not exist", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void deleteData(String delProduct) {
        dbRef1.orderByChild("id").equalTo(delProduct).get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure you want to Delete?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dbRef1.child(delProduct).removeValue().addOnSuccessListener(aVoid -> Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Delete failed", Toast.LENGTH_SHORT).show());

                        // finish(); // Close the activity
                        clearData();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Dismiss the dialog box
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

            } else {
                Toast.makeText(getApplicationContext(), "Item does not exist", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void getDataFromTxtFields() {
        TId = (id.getText().toString());
        TDiscount = Float.parseFloat(discount.getText().toString());

        if (TDiscount > 100) {
            TDiscount = 100;
            this.discount.setText("100");
        }

    }

    private void clearData() {
        id.setText("");
        name.setText("");
        discount.setText("0");
        currentP.setText("");
        newP.setText("");

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(DiscountActivity.this, MainActivity.class);
        startActivity(intent);
    }
}