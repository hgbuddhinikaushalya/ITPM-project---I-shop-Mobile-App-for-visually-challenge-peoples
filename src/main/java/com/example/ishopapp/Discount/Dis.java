package com.example.ishopapp.Discount;

public class Dis {
    private String Id;
    private String Name;
    private float discount;


    public Dis(String Id, String Name, float discount) {
        this.Id = Id;
        this.Name = Name;
        this.discount = discount;


    }

    public String getId() {
        return Id;
    }
    public void setId(String Id) {
        this.Id = Id;
    }

    public String getName() {
        return Name;
    }
    public void setName(String Name) {
        this.Name = Name;
    }

    public float getDiscount() {
        return discount;
    }
    public void setDiscount(float discount) {
        this.discount = discount;
    }



}

