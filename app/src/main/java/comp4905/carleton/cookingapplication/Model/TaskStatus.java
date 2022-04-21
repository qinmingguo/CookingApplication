package comp4905.carleton.cookingapplication.Model;

public enum TaskStatus {
    Open("Open"),
    InProgress("In Progress"),
    Complete("Complete");
    String displayName;
    TaskStatus(String displayName) {
        this.displayName = displayName;
    }
}