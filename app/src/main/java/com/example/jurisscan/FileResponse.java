package com.example.jurisscan;

import java.util.List;

public class FileResponse {
    private List<File> files;

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    public static class File {
        private String file_name;
        private String file_path;
        private String file_content; // Base64 encoded file content

        // Getters and Setters
        public String getFileName() {
            return file_name;
        }

        public void setFileName(String file_name) {
            this.file_name = file_name;
        }

        public String getFilePath() {
            return file_path;
        }

        public void setFilePath(String file_path) {
            this.file_path = file_path;
        }

        public String getFileContent() {
            return file_content;
        }

        public void setFileContent(String file_content) {
            this.file_content = file_content;
        }
    }
}

