package com.example.ishopapp.ProductManagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class ProductManagement extends AppCompatActivity {

    private DatabaseReference dbRef;

    private int TQuantity = 0;
    private String TId,TName, TBrand ,TCategory;
    private float TPrice = 0;

    private EditText id_txt,name_txt,quantity_txt, brand_txt, category_txt,price_txt,search_txt;
    private Button Search,Add,Update,Delete,Clear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_management);

        dbRef = FirebaseDatabase.getInstance().getReference("Product");

        id_txt = findViewById(R.id.idtxt);
        name_txt = findViewById(R.id.nametxt);
        quantity_txt = findViewById(R.id.quentitytxt);
        brand_txt = findViewById(R.id.brandtxt);
        category_txt = findViewById(R.id.categorytxt);
        price_txt = findViewById(R.id.pricetxt);
        search_txt = findViewById(R.id.searchtxt);

        quantity_txt.setText("0");
        price_txt.setText("0");

        Search = findViewById(R.id.search);
        Add = findViewById(R.id.add);
        Update = findViewById(R.id.update);
        Delete = findViewById(R.id.delete);
        Clear = findViewById(R.id.clear);



        Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchData();
            }
        });

        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDataFromTxtFields();
                saveData(TId,  TName,  TQuantity,  TPrice, TBrand, TCategory);

            }
        });

        Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDataFromTxtFields();
                updateData(TId,  TName,  TQuantity,  TPrice, TBrand, TCategory);

            }
        });

        Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDataFromTxtFields();
                deleteData(TId);

            }
        });

        Clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearData();
                Toast.makeText(getApplicationContext(),"Data cleared",Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void saveData(String Id, String Name, int Quantity, float Price, String Brand, String Category) {
        if (Id.isEmpty() || Name.isEmpty() || Brand.isEmpty() || Category.isEmpty() || Quantity <= 0 || Price <= 0) {
            Toast.makeText(getApplicationContext(), "Fill all fields correctly", Toast.LENGTH_SHORT).show();
        } else {
            dbRef.orderByChild("id").equalTo(Id).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Item with the same Id already exists
                        Toast.makeText(getApplicationContext(), "Item already exists", Toast.LENGTH_SHORT).show();
                    } else {
                        // Item does not exist, proceed with insertion
                        Product product = new Product(Id, Name, Quantity, Price, Brand, Category);
                        dbRef.child(Id).setValue(product).addOnSuccessListener(new OnSuccessListener<Void>() {
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
    }


    private void fetchData() {
        String searchID = search_txt.getText().toString().trim();
        if (searchID.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Enter the Item ID", Toast.LENGTH_SHORT).show();
        } else {
            dbRef.orderByChild("id").equalTo(searchID).get().addOnSuccessListener(snapshot -> {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String idf = dataSnapshot.child("id").getValue(String.class);
                        String namef = dataSnapshot.child("name").getValue(String.class);
                        int quantityf = dataSnapshot.child("quantity").getValue(Integer.class);
                        float pricef = dataSnapshot.child("price").getValue(float.class);
                        String brandf = dataSnapshot.child("brand").getValue(String.class);
                        String categoryf = dataSnapshot.child("category").getValue(String.class);


                      //  if (idf != null && namef != null && quantityf != null && pricef != null && brandf != null && categoryf != null) {
                            id_txt.setText(idf);
                            name_txt.setText(namef);
                            quantity_txt.setText(String.valueOf(quantityf));
                            price_txt.setText(String.valueOf(pricef));
                            brand_txt.setText(brandf);
                            category_txt.setText(categoryf);

                            Toast.makeText(getApplicationContext(), "Successfully fetched data", Toast.LENGTH_SHORT).show();
                        //} else {
                           // Toast.makeText(getApplicationContext(), "Data is incomplete", Toast.LENGTH_SHORT).show();
                        //}
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Item does not exist: " + searchID, Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(err -> Toast.makeText(getApplicationContext(), "Fetch failed: " + err, Toast.LENGTH_SHORT).show());
        }
    }

    private void updateData(String Id, String Name, int Quantity, float Price, String Brand, String Category) {
        Map<String, Object> product = new HashMap<>();
        product.put("id", Id);
        product.put("name", Name);
        product.put("quantity", Quantity);
        product.put("price", Price);
        product.put("brand", Brand);
        product.put("category", Category);

        dbRef.orderByChild("id").equalTo(Id).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            // @Override
            public void onSuccess(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    dbRef.child(Id).updateChildren(product).addOnSuccessListener(new OnSuccessListener<Void>() {
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

    }

    private void deleteData(String delProduct) {
        dbRef.orderByChild("id").equalTo(delProduct).get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure you want to Delete?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dbRef.child(delProduct).removeValue()
                                        .addOnSuccessListener(aVoid -> Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show())
                                        .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Delete failed", Toast.LENGTH_SHORT).show());

                               // finish(); // Close the activity
                                clearData();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
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
        TId = (id_txt.getText().toString());
        TName = name_txt.getText().toString();
        TQuantity = Integer.parseInt(quantity_txt.getText().toString());
        TPrice = Float.parseFloat(price_txt.getText().toString());
        TBrand = brand_txt.getText().toString();
        TCategory = category_txt.getText().toString();
    }

    private void clearData() {
        id_txt.setText("");
        name_txt.setText("");
        price_txt.setText("0");
        quantity_txt.setText("0");
        brand_txt.setText("");
        category_txt.setText("");
        search_txt.setText("");

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ProductManagement.this, MainActivity.class);
        startActivity(intent);
    }
}