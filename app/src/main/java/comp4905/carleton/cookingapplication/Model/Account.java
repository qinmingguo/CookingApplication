package comp4905.carleton.cookingapplication.Model;


import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;
import io.realm.annotations.Required;

@RealmClass
public class Account extends RealmObject {
    @PrimaryKey
    @Required
    private String _id;
    @Required
    private String _partition;
    @Required
    private String account;
    private Integer age;
    private String email;
    @Required
    private String favor_menu_list;
    @Required
    private String history_menu_id_list;
    private String name;
    @Required
    private String own_menu_id_list;
    @Required
    private String password;
    private String phone_number;
    // Standard getters & setters
    public String get_id() { return _id; }
    public void set_id(String _id) { this._id = _id; }
    public String get_partition() { return _partition; }
    public void set_partition(String _partition) { this._partition = _partition; }
    public String getAccount() { return account; }
    public void setAccount(String account) { this.account = account; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFavor_menu_list() { return favor_menu_list; }
    public void setFavor_menu_list(String favor_menu_list) { this.favor_menu_list = favor_menu_list; }
    public String getHistory_menu_id_list() { return history_menu_id_list; }
    public void setHistory_menu_id_list(String history_menu_id_list) { this.history_menu_id_list = history_menu_id_list; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getOwn_menu_id_list() { return own_menu_id_list; }
    public void setOwn_menu_id_list(String own_menu_id_list) { this.own_menu_id_list = own_menu_id_list; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getPhone_number() { return phone_number; }
    public void setPhone_number(String phone_number) { this.phone_number = phone_number; }

    public void addFavor(Menu f) {
        favor_menu_list+=(f.get_id()+";");
    }
    public boolean removeFavor(Menu f) {
        if(favor_menu_list.contains(f.get_id())){
            String[] list = favor_menu_list.split(f.get_id()+";");
            String re = "";
            for(int i =0;i<list.length;i++){
                re+=list[i];
            }
            favor_menu_list=re;
            return true;
        }else{
            return false;
        }
    }

    public void addOwn(Menu m){
        own_menu_id_list+=(m.get_id()+";");
    }

    public void removeOwn(Menu m){
        String[] list = own_menu_id_list.split(m.get_id()+";");
        String re = "";
        for(int i =0;i<list.length;i++){
            re+=list[i];
        }
        own_menu_id_list=re;
    }


    public void addHistory(Menu m){
        if(history_menu_id_list.contains(m.get_id())){
            return;
        }
        history_menu_id_list+=(m.get_id()+";");
    }

    public void removeHistory(Menu m){
        String[] list = history_menu_id_list.split(m.get_id()+";");
        String re = "";
        for(int i =0;i<list.length;i++){
            re+=list[i];
        }
        history_menu_id_list=re;
    }

    public boolean update(String Name,int Age,String Email,String PhoneNumber){
        name = Name;
        age = Age;
        email = Email;
        phone_number = PhoneNumber;
        return true;
    }

    public boolean updateAccount(Account a){
        name = a.getName();
        age = a.getAge();
        account = a.getAccount();
        password = a.getPassword();
        email = a.getEmail();
        phone_number = a.getPhone_number();
        own_menu_id_list = a.getOwn_menu_id_list();
        history_menu_id_list = a.getHistory_menu_id_list();
        favor_menu_list = a.getFavor_menu_list();
        return true;
    }

    public Document toDoc(){
        // TODO: transfer to document;
        org.bson.Document document = new org.bson.Document();
        document.append("_id",get_id());
        document.append("_partition",get_partition());
        document.append("name",getName());
        document.append("age",getAge());
        document.append("account",getAccount());
        document.append("password",getPassword());
        document.append("phone_number",getPhone_number());
        document.append("email",getEmail());
        document.append("own_menu_id_list",getOwn_menu_id_list());
        document.append("favor_menu_list",getFavor_menu_list());
        document.append("history_menu_id_list",getHistory_menu_id_list());
        return document;
    }

}

//{
//  "bsonType": "object",
//  "properties": {
//    "_id": {
//      "bsonType": "string"
//    },
//    "_partition": {
//      "bsonType": "string"
//    },
//    "account": {
//      "bsonType": "string"
//    },
//    "password": {
//      "bsonType": "string"
//    },
//    "name": {
//      "bsonType": "string"
//    },
//    "age": {
//      "bsonType": "string"
//    },
//    "email": {
//      "bsonType": "int"
//    },
//    "phone_number": {
//      "bsonType": "string"
//    },
//    "own_menu_id_list": {
//        "bsonType": "string"
//    },
//    "favor_menu_list": {
//        "bsonType": "string"
//    },
//    "history_menu_id_list": {
//        "bsonType": "string"
//    }
//  },
//  "required": [
//    "_id",
//    "_partition",
//    "account",
//    "password",
//    "own_menu_id_list",
//    "favor_menu_list",
//    "history_menu_id_list"
//  ],
//  "title": "Account"
//}