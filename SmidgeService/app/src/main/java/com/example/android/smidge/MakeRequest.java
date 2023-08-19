package com.example.android.smidge;

import android.telecom.Call;

import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface MakeRequest {
    @Headers("Content-Type: application/json")
    @POST("render/")
    Call updateTable(@Body RequestFormat req);

}