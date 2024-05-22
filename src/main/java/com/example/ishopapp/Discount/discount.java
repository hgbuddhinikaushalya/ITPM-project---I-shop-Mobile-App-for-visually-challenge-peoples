package com.example.ishopapp.Discount;

public class Discount {
    private String Id,name;
    private float Discount;


    public Discount(String Id, float Discount,String name) {
        this.Id = Id;
        this.Discount = Discount;
        this.name = name;

    }

    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }

    public float getDiscount() {
        return Discount;
    }

    public void setDiscount(float Discount) {
        this.Discount = Discount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}


