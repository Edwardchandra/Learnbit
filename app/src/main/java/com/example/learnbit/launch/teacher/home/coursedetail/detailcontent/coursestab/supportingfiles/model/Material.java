package com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.coursestab.supportingfiles.model;

public class Material {

    private String materialName;
    private String materialURL;

    public Material(String materialName, String materialURL) {
        this.materialName = materialName;
        this.materialURL = materialURL;
    }

    public Material() {
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getMaterialURL() {
        return materialURL;
    }

    public void setMaterialURL(String materialURL) {
        this.materialURL = materialURL;
    }
}
