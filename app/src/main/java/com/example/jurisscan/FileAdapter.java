package com.example.jurisscan;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {
    private List<FileResponse.File> fileList;
    private Context context;

    public FileAdapter(Context context, List<FileResponse.File> fileList) {
        this.context = context;
        this.fileList = fileList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.file_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FileResponse.File fileItem = fileList.get(position);
        holder.fileNameTextView.setText(fileItem.getFileName());
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DocumentView.class);
            intent.putExtra("pdfPath", fileItem.getFilePath());
            intent.putExtra("pdfName", fileItem.getFileName());
            intent.putExtra("source_activity", "PastUploadsList");
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView fileNameTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fileNameTextView = itemView.findViewById(R.id.file_name_text_view);
        }
    }
}

