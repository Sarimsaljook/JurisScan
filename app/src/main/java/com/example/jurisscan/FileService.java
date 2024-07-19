package com.example.jurisscan;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface FileService {
    @GET("get_user_files/{user_id}/")
    Call<FileResponse> getUserFiles(@Path("user_id") String userId);
}

