package com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.coursestab.supportingfiles.model;

public class File {

    private String name;
    private Boolean isUpload = false;

    public File(String name, Boolean isUpload) {
        this.name = name;
        this.isUpload = isUpload;
    }

    public File() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getUpload() {
        return isUpload;
    }

    public void setUpload(Boolean upload) {
        isUpload = upload;
    }
}
