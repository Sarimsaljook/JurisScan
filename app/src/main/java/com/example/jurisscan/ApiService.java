package com.example.jurisscan;
// ApiService.java
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {

    @Multipart
    @POST("upload_pdf")
    Call<ResponseBody> uploadPdf(
            @Part MultipartBody.Part file
    );

    @POST("ask")
    Call<ResponseBody> askQuestion(
            @Body QueryRequest query
    );
}

