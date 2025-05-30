package es.studium.tusservi.ui.calendar;

public class Task {
    private long id;
    private long date; // timestamp solo año-mes-día
    private String description;
    private boolean done;
    private String startTime; // "08:00"
    private String endTime;   // "09:00"

    public Task(long id, long date, String description, boolean done, String startTime, String endTime) {
        this.id = id;
        this.date = date;
        this.description = description;
        this.done = done;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // getters y setters normales para todo

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getDate() { return date; }
    public void setDate(long date) { this.date = date; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isDone() { return done; }
    public void setDone(boolean done) { this.done = done; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
}
