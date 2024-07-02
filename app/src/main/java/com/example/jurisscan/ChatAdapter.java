package com.example.jurisscan;
// ChatAdapter.java
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<ChatMessage> messages;

    public ChatAdapter(List<ChatMessage> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) { // User message layout
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_message, parent, false);
            return new UserMessageViewHolder(view);
        } else { // AI message layout
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ai_message, parent, false);
            return new AiMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        if (message.isUser()) {
            ((UserMessageViewHolder) holder).bind(message);
        } else {
            ((AiMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).isUser() ? 0 : 1;
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    // ViewHolder for user messages
    private static class UserMessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView messageTextView;

        public UserMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
        }

        public void bind(ChatMessage message) {
            messageTextView.setText(message.getMessage());
        }
    }

    // ViewHolder for AI messages
    private static class AiMessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView messageTextView;

        public AiMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
        }

        public void bind(ChatMessage message) {
            messageTextView.setText(message.getMessage());
        }
    }
}

