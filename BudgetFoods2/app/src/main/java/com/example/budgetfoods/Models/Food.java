package com.example.budgetfoods.Models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "my_cart")
public class Food {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String foodname;
    private String description;
    private String restaurant;
    private String price;
    private String fId;
    private String timestamp;
    private String Uid;
    private String discount;
    private String discountdescription;
    private String foodimage;
    private int quantity;
    private int total;

    public Food(String foodname, String description, String restaurant, String price, String fId, String timestamp, String Uid, String discount, String discountdescription, String foodimage) {
        this.foodname = foodname;
        this.description = description;
        this.restaurant = restaurant;
        this.price = price;
        this.fId = fId;
        this.timestamp = timestamp;
        this.Uid = Uid;
        this.discount = discount;
        this.discountdescription = discountdescription;
        this.foodimage = foodimage;
        this.quantity = 1;
        this.total = recalculateTotal(); // Calculate total based on initial quantity
    }

    public Food() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFId() {
        return fId;
    }

    public void setFId(String fId) {
        this.fId = fId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getFoodname() {
        return foodname;
    }

    public void setFoodname(String foodname) {
        this.foodname = foodname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(String restaurant) {
        this.restaurant = restaurant;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
        //  recalculateTotal(); // Recalculate total when discount changes
    }

    public String getDiscountdescription() {
        return discountdescription;
    }

    public void setDiscountdescription(String discountdescription) {
        this.discountdescription = discountdescription;
    }

    public String getFoodimage() {
        return foodimage;
    }

    public void setFoodimage(String foodimage) {
        this.foodimage = foodimage;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        recalculateTotal(); // Recalculate total when quantity changes
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotal() {
        return total;
    }

    private int recalculateTotal() {
        int originalPrice = Integer.parseInt(price);
        if (discount != null && !discount.isEmpty() && discountdescription != null && !discountdescription.isEmpty()) {
            int discountValue = Integer.parseInt(discount);
            if (discountValue > 0 && discountdescription.contains("%")) {
                double discountPercentage = discountValue / 100.0;
                double newPrice = originalPrice * (1 - discountPercentage);
                total = (int) (newPrice * quantity);
                return total;
            }
        }

        total = originalPrice * quantity;
        return total;
    }
}
