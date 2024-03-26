package com.example.myapplicationddd;

public class ReviewUpload {

    public ReviewUpload(String userId,String imageName, String imageUri, String username, String userImage) {
        this.userId = userId;
        this.imageName = imageName;
        this.imageUri = imageUri;
        this.userImage = userImage;
        this.username = username;

    }


    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    private String imageName;
    private String imageUri;
    private String userId;
    private String userImage;
    private String username;


    public ReviewUpload() {
    }

    public String getUserId() {
        return userId;
    }
    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }



}
