package com.example.jurisscan;

// Query.java
public class QueryRequest {
    private String language;
    private String freeform_text;
    private String knowledge_base;

    public QueryRequest(String language, String freeform_text, String knowledge_base) {
        this.language = language;
        this.freeform_text = freeform_text;
        this.knowledge_base = knowledge_base;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getFreeform_text() {
        return freeform_text;
    }

    public void setFreeform_text(String freeform_text) {
        this.freeform_text = freeform_text;
    }

    public String getKnowledge_base() {
        return knowledge_base;
    }

    public void setKnowledge_base(String knowledge_base) {
        this.knowledge_base = knowledge_base;
    }
}
