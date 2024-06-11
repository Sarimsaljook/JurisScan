package com.example.jurisscan;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

public class DocumentView extends AppCompatActivity {
    private PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_view);

        pdfView = findViewById(R.id.pdfView);

        String pdfPath = getIntent().getStringExtra("pdfPath");
        if (pdfPath != null) {
            File file = new File(pdfPath);
            displayPdf(file);
        }
    }

    private void displayPdf(File file) {
        pdfView.fromFile(file)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .defaultPage(0)
                .load();
    }
}