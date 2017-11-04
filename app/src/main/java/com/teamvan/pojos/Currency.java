package com.teamvan.pojos;

import android.graphics.Bitmap;

/**
 * Created by allan on 30/10/2017.
 */

public class Currency {
    private int id;
    private String name, code, unicode_hex;
    private Bitmap image;

    public Currency() {
    }

    public Currency(int id, String name, String code, String unicode_hex, Bitmap image) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.unicode_hex = unicode_hex;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getUnicode_hex() {
        return unicode_hex;
    }

    public void setUnicode_hex(String unicode_hex) {
        this.unicode_hex = unicode_hex;
    }
}
