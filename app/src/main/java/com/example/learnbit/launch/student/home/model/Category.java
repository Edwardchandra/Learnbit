package com.example.learnbit.launch.student.home.model;

import android.media.Image;
import android.widget.ImageView;

import java.util.ArrayList;

public class Category {

    private int image;
    private String name;
    private String[] subCategoryArray;

    public Category(int image, String name, String[] subCategoryArray) {
        this.image = image;
        this.name = name;
        this.subCategoryArray = subCategoryArray;
    }

    public Category() {
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getSubCategoryArray() {
        return subCategoryArray;
    }

    public void setSubCategoryArray(String[] subCategoryArray) {
        this.subCategoryArray = subCategoryArray;
    }
}
