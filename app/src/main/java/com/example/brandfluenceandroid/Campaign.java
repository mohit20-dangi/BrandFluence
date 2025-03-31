package com.example.brandfluenceandroid;

import java.io.Serializable; // 🔥 Important
import java.util.ArrayList;
import java.util.List;

public class Campaign implements Serializable { // 🔥 Make Serializable
    private String title;
    private String description;
    private String platforms;
    private double budget;
    private String deadline;
    private String brandId;
    private List<String> interestedInfluencers;
    private List<String> approvedInfluencers;
    private String campaignId;
    private String brandName;

    public Campaign() {}

    public Campaign(String title, String description, String platform, double budget, String deadline, String brandId, String campaignId) {
        this.title = title;
        this.description = description;
        this.platforms = platform;
        this.budget = budget;
        this.deadline = deadline;
        this.brandId = brandId;
        this.campaignId = campaignId;
    }

    public Campaign(String campaignId, String title, String description, String deadline, String brandId) {
        this.campaignId = campaignId;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.brandId = brandId;
    }

    public String getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(String campaignId) {
        this.campaignId = campaignId;
    }

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public List<String> getInterestedInfluencers() {
        return interestedInfluencers;
    }

    public void setInterestedInfluencers(List<String> interestedInfluencers) {
        this.interestedInfluencers = interestedInfluencers;
    }

    public List<String> getApprovedInfluencers() {
        return approvedInfluencers != null ? approvedInfluencers : new ArrayList<>();
    }

    public void setApprovedInfluencers(List<String> approvedInfluencers) {
        this.approvedInfluencers = approvedInfluencers;
    }

    public String getPlatforms() {
        return platforms;
    }

    public double getBudget() {
        return budget;
    }
    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }
}
