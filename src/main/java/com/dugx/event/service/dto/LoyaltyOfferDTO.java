package com.dugx.event.service.dto;

/** Mot uu dai co the doi bang diem Lotusmiles. */
public class LoyaltyOfferDTO {

    private String id;

    private String label;

    private String description;

    private int pointCost;

    public LoyaltyOfferDTO(String id, String label, String description, int pointCost) {
        this.id = id;
        this.label = label;
        this.description = description;
        this.pointCost = pointCost;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }

    public int getPointCost() {
        return pointCost;
    }
}
