package comp4905.carleton.cookingapplication.Model;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Menu extends RealmObject {
    @PrimaryKey
    @Required
    private String _id;

    @Required
    private String _partition;
    private String title;
    private String author;
    private String calender;
    private String introduction;
    @Required
    private RealmList<String> ingredient;
    @Required
    private RealmList<String> process;


    // Standard getters & setters
    public String get_id() { return _id; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getCalender() { return calender; }
    public void setCalender(String calender) { this.calender = calender; }
    public RealmList<String> getIngredient() { return ingredient; }
    public void setIngredient(RealmList<String> ingredient) { this.ingredient = ingredient; }
    public RealmList<String> getProcess() { return process; }
    public void setProcess(RealmList<String> process) { this.process = process; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }


    public void updateMenu(Menu menu){
        title = menu.getTitle();
        author = menu.getAuthor();
        calender = menu.getCalender();
        introduction = menu.getIntroduction();
        ingredient = menu.getIngredient();
        process = menu.getProcess();
    }

    public Document toDoc(){
        Document document = new Document();
        document.append("_id",_id);
        document.append("title",title);
        document.append("author",author);
        document.append("calendar",calender);
        document.append("introduction",introduction);
        document.append("ingredient",ingredient);
        document.append("process",process);
        return document;
    }

    public String toString(){
        return String.format("{_id: %d, title: %s, author: %s, calendar: %s,ingredient: %s,process: %s",_id,title,author,calender.toString(),ingredient.toString(),process.toString());
    }

    public String get_partition() {
        return _partition;
    }
    public void set_partition(String p) {
         _partition=p;
    }
    public String getIntroduction() {
        return introduction;
    }
    public String setIntroduction(String i) {
        return introduction = i;
    }
}

