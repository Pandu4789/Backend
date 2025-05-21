package com.example.myapp;

public class PoojaItemsDto {

    private Long eventId;
    private String itemName;
    private int quantity;
    private double unitPrice;

    // ✅ Default constructor
    public PoojaItemsDto() {}

    // ✅ Parameterized constructor
    public PoojaItemsDto(Long eventId, String itemName, int quantity, double unitPrice) {
        this.eventId = eventId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    // ✅ Getters
    public Long getEventId() {
        return eventId;
    }

    public String getItemName() {
        return itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    // ✅ Setters (optional if using only for receiving data)
    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }
}
