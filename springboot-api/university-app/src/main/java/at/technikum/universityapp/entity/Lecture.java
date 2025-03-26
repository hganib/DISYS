package at.technikum.universityapp.entity;

public class Lecture {

    private String name;

    private String author;

    private int ects;

    public Lecture(String name, String author, int ects) {
        this.name = name;
        this.author = author;
        this.ects = ects;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getEcts() {
        return ects;
    }

    public void setEcts(int ects) {
        this.ects = ects;
    }
}
