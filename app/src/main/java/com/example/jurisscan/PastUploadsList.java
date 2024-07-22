package com.example.jurisscan;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PastUploadsList extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FileAdapter fileAdapter;
    private FileService fileService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_uploads_list);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fileService = RetrofitClient.getClient("https://jurisscanapi.loca.lt/").create(FileService.class);

        fetchUserFiles();
    }

    private void fetchUserFiles() {
        String userId = FirebaseAuth.getInstance().getUid();

        fileService.getUserFiles(userId).enqueue(new Callback<FileResponse>() {
            @Override
            public void onResponse(Call<FileResponse> call, Response<FileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<FileResponse.File> files = response.body().getFiles();
                    fileAdapter = new FileAdapter(PastUploadsList.this, files);
                    recyclerView.setAdapter(fileAdapter);
                } else {
                    Toast.makeText(PastUploadsList.this, "Error fetching files", Toast.LENGTH_SHORT).show();
                    System.out.println(response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<FileResponse> call, Throwable t) {
                Toast.makeText(PastUploadsList.this, "Network error", Toast.LENGTH_SHORT).show();
                System.out.println(t.getMessage());
            }
        });
    }
}
