package com.example.brandfluenceandroid;

public class Influencer {
    private String influencerId;
    private String name;
    private String gender;
    private String platforms;
    private String followers;
    private String category;
    private String socialMediaLink;
    private String profileImageBase64;


        public Influencer() {
            // Default constructor required for Firestore
        }

        public Influencer(String id, String name, String gender, String platform, String followers, String category, String socialMediaLink, String profileImageBase64) {
            this.influencerId = id;
            this.name = name;
            this.gender = gender;
            this.platforms = platform;
            this.followers = followers;
            this.category = category;
            this.socialMediaLink = socialMediaLink;
            this.profileImageBase64 = profileImageBase64;
        }

    public String getInfluencerId() { return influencerId; }
    public String Id() { return influencerId; }
    public String getName() { return name; }
    public String getGender() { return gender; }
    public String getPlatforms() { return platforms; }
    public String getFollowers() { return followers; }
    public String getCategory() { return category; }
    public String getSocialMediaLink() { return socialMediaLink; }
    public String getProfileImageBase64() { return profileImageBase64; }
    public void setProfileImageBase64(String profileImageBase64) { this.profileImageBase64 = profileImageBase64; }

    public void setInfluencerId(String influencerId) { this.influencerId = influencerId; }



}
