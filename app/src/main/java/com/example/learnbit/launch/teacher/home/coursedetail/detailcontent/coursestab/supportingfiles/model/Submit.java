package com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.coursestab.supportingfiles.model;

public class Submit {

    private String submitFileName;
    private String submitFileUrl;
    private String userUid;

    public Submit(String submitFileName, String submitFileUrl, String userUid) {
        this.submitFileName = submitFileName;
        this.submitFileUrl = submitFileUrl;
        this.userUid = userUid;
    }

    public Submit() {
    }

    public String getSubmitFileName() {
        return submitFileName;
    }

    public void setSubmitFileName(String submitFileName) {
        this.submitFileName = submitFileName;
    }

    public String getSubmitFileUrl() {
        return submitFileUrl;
    }

    public void setSubmitFileUrl(String submitFileUrl) {
        this.submitFileUrl = submitFileUrl;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }
}
