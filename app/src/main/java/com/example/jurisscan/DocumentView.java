package com.example.jurisscan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class DocumentView extends AppCompatActivity {
    private PDFView pdfView;
    private File file;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private String pdfName;
    private ApiService apiService;

    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private final List<ChatMessage> messageList = new ArrayList<>();
    private String knowledgeBase;
    private String pdfPath;
    private String sourceActivity;
    private TransferUtility transferUtility;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_view);

        pdfView = findViewById(R.id.pdfView);

        mAuth = FirebaseAuth.getInstance();

        pdfPath = getIntent().getStringExtra("pdfPath");
        pdfName = getIntent().getStringExtra("pdfName");
        if (pdfPath != null) {
            file = new File(pdfPath);
            displayPdf(file);
        }

        // Initialize ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving Your Work...");
        progressDialog.setCancelable(false);

        sourceActivity = getIntent().getStringExtra("source_activity");

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        // Initialize Retrofit
        String BASE_URL = "https://013c-70-18-228-97.ngrok-free.app/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        // Initialize Chat UI
        // Chat UI variables
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);

        System.out.println(file.getName());

        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                // Respond when the drawer's position changes
                float alpha = 1 - slideOffset; // Decrease alpha as drawer slides in
                // Apply alpha to the main content view (e.g., pdfView)
                pdfView.setAlpha(alpha);
            }
        });

        recyclerView = findViewById(R.id.drawer_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        chatAdapter = new ChatAdapter(this, messageList);
        recyclerView.setAdapter(chatAdapter);

        uploadPdfForKnowledgeBase(file);

        EditText userMessage = findViewById(R.id.editText_message);
        FloatingActionButton sendButton = findViewById(R.id.fab_send);

        sendButton.setOnClickListener(view -> {
            sendChatMessage(userMessage.getText().toString());
            userMessage.setText("");
        });

        String accessKey = "AKIA4MY5LTISDCDAGV4K";
        String secretKey = "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";
        String region = "us-east-1"; // AWS region

        // Create AWS credentials
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);

        // Create an AmazonS3 client
       AmazonS3 s3Client = new AmazonS3Client(awsCredentials, Region.getRegion(region));
       transferUtility = TransferUtility.builder().context(getApplicationContext())
                .s3Client(s3Client).build();

        TransferNetworkLossHandler.getInstance(getApplicationContext());
    }

    private void uploadPdfForKnowledgeBase(File file) {
        if (file != null && file.exists()) {
            MultipartBody.Part filePart = prepareFilePart("file", file);
            apiService.uploadPdf(filePart).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull retrofit2.Response<ResponseBody> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            String responseString = response.body().string();
                            // Parse the response JSON to get the knowledge base
                            JSONObject jsonResponse = new JSONObject(responseString);
                            knowledgeBase = jsonResponse.getString("knowledge_base");
                            Toast.makeText(DocumentView.this, "Knowledge base created", Toast.LENGTH_SHORT).show();
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(DocumentView.this, "Failed to parse knowledge base", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(DocumentView.this, "Knowledge base creation failed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    Toast.makeText(DocumentView.this, "Knowledge base creation failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "PDF file not found", Toast.LENGTH_SHORT).show();
        }
    }


    private MultipartBody.Part prepareFilePart(String partName, File file) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("application/pdf"), file);
        return MultipartBody.Part.createFormData(partName, file.getName() + ".pdf", requestFile);
    }


    private void sendChatMessage(String message) {
        // Add user message to RecyclerView
        messageList.add(new ChatMessage(message, true));
        chatAdapter.notifyItemInserted(messageList.size() - 1);
        recyclerView.scrollToPosition(messageList.size() - 1);

        // Make API call to send message and receive response
        QueryRequest queryRequest = new QueryRequest("English", message, knowledgeBase);
        apiService.askQuestion(queryRequest).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseString = response.body().string();
                        JSONObject jsonResponse = new JSONObject(responseString);
                        JSONObject responseObject = jsonResponse.getJSONObject("response");
                        String botResponse = responseObject.getString("text");

                        System.out.println(botResponse);

                        // Add bot response to RecyclerView after processing
                       messageList.add(new ChatMessage(botResponse, false));
                       chatAdapter.notifyItemInserted(messageList.size() - 1);
                       recyclerView.scrollToPosition(messageList.size() - 1);

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(DocumentView.this, "Failed to parse response", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DocumentView.this, "Failed to get response from server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(DocumentView.this, "Network error, please try again later", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void displayPdf(File file) {
        pdfView.fromFile(file)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .defaultPage(0)
                .load();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(sourceActivity.equals("PastUploadsList")) {
            getMenuInflater().inflate(R.menu.menu_doc_view_alt, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_doc_view, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Handle action bar item clicks here
        if (id == R.id.action_forward) {
            // Handle the forward button click
            uploadPdf();
            return true;
        } else if (id == R.id.action_back) {
            startActivity(new Intent(DocumentView.this, MainActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    private void uploadPdf() {
        new UploadPdfTask().execute(file);
    }

    private void uploadToS3(File file) {
        // Upload the file to S3
        TransferObserver uploadObserver = transferUtility.upload(
                "jurisscan-knowlegebase-storage", // The S3 bucket to upload to
                file.getName(),         // The key for the uploaded object
                file                // The file where the data to upload exists
        );

        System.out.println(uploadObserver.getBucket());

        uploadObserver.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (state == TransferState.COMPLETED) {
                    // Handle successful upload
                    System.out.println("Upload To S3 Successful!");
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
            }

            @Override
            public void onError(int id, Exception ex) {
                // Handle errors
                Toast.makeText(DocumentView.this, "Upload to S3 failed", Toast.LENGTH_SHORT).show();
                ex.printStackTrace();
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class UploadPdfTask extends AsyncTask<File, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }
        @Override
        protected Boolean doInBackground(File... files) {

            uploadToS3(file);

            File pdfFile = files[0];
            String userId = mAuth.getUid(); // Firebase UID

            OkHttpClient client = new OkHttpClient();

            RequestBody fileBody = RequestBody.create(MediaType.parse("application/pdf"), pdfFile);
            assert userId != null;
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("user_id", userId)
                    .addFormDataPart("file_path", pdfPath)
                    .addFormDataPart("file", pdfName, fileBody)
                    .build();

            Request request = new Request.Builder()
                    .url("https://jurisscanapi.loca.lt/upload/")
                    .post(requestBody)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                System.out.println(response);
                return response.isSuccessful();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        @Override
        protected void onPostExecute(Boolean success) {
            progressDialog.dismiss();
            if (success) {
                startActivity(new Intent(DocumentView.this, MainActivity.class));
                Toast.makeText(DocumentView.this, "Upload successful", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(DocumentView.this, "Upload failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}