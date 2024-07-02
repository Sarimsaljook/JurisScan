package com.example.jurisscan;

public class ChatbotResponse {
    private String language;
    private String freeform_text;
    private String knowledge_base;
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getFreeformText() {
        return freeform_text;
    }

    public void setFreeformText(String freeform_text) {
        this.freeform_text = freeform_text;
    }

    public String getKnowledgeBase() {
        return knowledge_base;
    }

    public void setKnowledgeBase(String knowledge_base) {
        this.knowledge_base = knowledge_base;
    }
}



