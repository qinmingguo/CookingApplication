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
import io.realm.annotations.RealmClass;
import io.realm.annotations.Required;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RealmClass
public class Menu extends RealmObject {
    @PrimaryKey
    @Required
    private String _id;
    @Required
    private String _partition;
    @Required
    private String author_id;
    private String author;
    private String calender;
    @Required
    private String ingredient;
    private String introduction;
    @Required
    private String process;
    private String title;
    // Standard getters & setters
    public String get_id() { return _id; }
    public void set_id(String _id) { this._id = _id; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getCalender() { return calender; }
    public void setCalender(String calender) { this.calender = calender; }
    public String getIngredient() { return ingredient; }
    public void setIngredient(String ingredient) { this.ingredient = ingredient; }
    public String getProcess() { return process; }
    public void setProcess(String process) { this.process = process; }
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
        return String.format("{_id: %s, title: %s, author: %s, calendar: %s,ingredient: %s,process: %s",_id,title,author,calender.toString(),ingredient.toString(),process.toString());
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

    public String getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(String author_id) {
        this.author_id = author_id;
    }
}

//{
//        "bsonType": "object",
//        "properties": {
//        "_id": {
//        "bsonType": "string"
//        },
//        "_partition": {
//        "bsonType": "string"
//        },
//        "title": {
//        "bsonType": "string"
//        },
//        "author": {
//        "bsonType": "string"
//        },
//        "calender": {
//        "bsonType": "int"
//        },
//        "introduction": {
//        "bsonType": "string"
//        },
//        "ingredient": {
//        "bsonType": "array",
//        "items": {
//        "bsonType": "string"
//        }
//        },
//        "process": {
//        "bsonType": "array",
//        "items": {
//        "bsonType": "string"
//        }
//        }
//        },
//        "required": [
//        "_id",
//        "_partition",
//        "account",
//        "password",
//        "ingredient",
//        "process"
//        ],
//        "title": "Menu"
//        }