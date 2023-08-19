package com.example.android.smidge;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class RequestFormat {
    @SerializedName("index")
    private String index= "null";

    @SerializedName("values")
    private String[][] vals= new String[][]{new String[]{"1", "2"}};
}
