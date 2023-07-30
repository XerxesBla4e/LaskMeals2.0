package com.example.budgetfoods.models;

public class FoodModel {
    private String fId;
    private String fName;
    private String fDescription;
    private String fRestaurant;

    private String fPrice;
    private int fQuantity;
    private int fTotal;
    private String fDiscount;
    private String fDiscountDesc;
    private String fTimestamp;
    private String fUid;
    private String fImage;

    public FoodModel() {
    }

    public FoodModel(String fId, String fName, String fDescription, String fRestaurant,String fPrice, int fQuantity, int fTotal, String fDiscount, String fDiscountDesc, String fTimestamp, String fUid, String fImage) {
        this.fId = fId;
        this.fName = fName;
        this.fDescription = fDescription;
        this.fRestaurant = fRestaurant;
        this.fPrice = fPrice;
        this.fQuantity = fQuantity;
        this.fTotal = fTotal;
        this.fDiscount = fDiscount;
        this.fDiscountDesc = fDiscountDesc;
        this.fTimestamp = fTimestamp;
        this.fUid = fUid;
        this.fImage = fImage;
    }

    public String getFRestaurant() {
        return fRestaurant;
    }

    public void setFRestaurant(String fRestaurant) {
        this.fRestaurant = fRestaurant;
    }

    public String getFId() {
        return fId;
    }

    public void setFId(String fId) {
        this.fId = fId;
    }


    public String getFName() {
        return fName;
    }

    public void setFName(String fName) {
        this.fName = fName;
    }

    public String getFDescription() {
        return fDescription;
    }

    public void setFDescription(String fDescription) {
        this.fDescription = fDescription;
    }

    public String getFPrice() {
        return fPrice;
    }

    public void setFPrice(String fPrice) {
        this.fPrice = fPrice;
    }

    public int getFQuantity() {
        return fQuantity;
    }

    public void setFQuantity(int fQuantity) {
        this.fQuantity = fQuantity;
    }

    public int getFTotal() {
        return fTotal;
    }

    public void setFTotal(int fTotal) {
        this.fTotal = fTotal;
    }

    public String getFDiscount() {
        return fDiscount;
    }

    public void setFDiscount(String fDiscount) {
        this.fDiscount = fDiscount;
    }

    public String getFDiscountDesc() {
        return fDiscountDesc;
    }

    public void setFDiscountDesc(String fDiscountDesc) {
        this.fDiscountDesc = fDiscountDesc;
    }

    public String getFTimestamp() {
        return fTimestamp;
    }

    public void setFTimestamp(String fTimestamp) {
        this.fTimestamp = fTimestamp;
    }

    public String getFUid() {
        return fUid;
    }

    public void setFUid(String fUid) {
        this.fUid = fUid;
    }

    public String getFImage() {
        return fImage;
    }

    public void setFImage(String fImage) {
        this.fImage = fImage;
    }
}
