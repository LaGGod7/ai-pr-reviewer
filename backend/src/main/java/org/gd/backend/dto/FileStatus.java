package org.gd.backend.dto;

public enum  FileStatus {
    ADDED, MODIFIED, REMOVED, RENAMED, OTHER;
    public static FileStatus fromGitHub(String status) {
        if (status == null) {
            return OTHER;
        }

        return switch(status){
            case "added" -> ADDED;
            case "modified" -> MODIFIED;
            case "removed" -> REMOVED;
            case "renamed" -> RENAMED;
            default -> OTHER;
        };
    }
}
