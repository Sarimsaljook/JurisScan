package com.example.jurisscan;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class DocumentView extends AppCompatActivity {
    private PDFView pdfView;
    private File file;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private String pdfName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_view);

        pdfView = findViewById(R.id.pdfView);

        mAuth = FirebaseAuth.getInstance();

        String pdfPath = getIntent().getStringExtra("pdfPath");
        pdfName = getIntent().getStringExtra("pdfName");
        if (pdfPath != null) {
            file = new File(pdfPath);
            displayPdf(file);
        }

        // Initialize ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving Your Work...");
        progressDialog.setCancelable(false);
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_doc_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Handle action bar item clicks here
        if (id == R.id.action_forward) {
            // Handle the forward button click
            // Add your forward action logic here
            uploadPdf();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void uploadPdf() {
        new UploadPdfTask().execute(file);
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
            File pdfFile = files[0];
            String userId = mAuth.getUid(); // Firebase UID

            OkHttpClient client = new OkHttpClient();

            RequestBody fileBody = RequestBody.create(MediaType.parse("application/pdf"), pdfFile);
            assert userId != null;
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("user_id", userId)
                    .addFormDataPart("file", pdfName, fileBody)
                    .build();

            Request request = new Request.Builder()
                    .url("http://10.0.2.2:8000/upload/")
                    .post(requestBody)
                    .build();

            try (Response response = client.newCall(request).execute()) {
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