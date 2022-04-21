package comp4905.carleton.cookingapplication.Model;


import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Account extends RealmObject {
    @PrimaryKey
    @Required
    @Nonnull
    private String _id;

    @Required
    private String _partition;
    @Required
    private String account;
    @Required
    private String password;
    private String name;
    private Integer age;
    private String email;
    private String phone_number;
    @Required
    private RealmList<String> own_menu_id_list;
    @Required
    private RealmList<String> favor_menu_list;
    @Required
    private RealmList<String> history_menu_id_list;

    public Account() {
        _id = ObjectId.get().toString();
    }

    // Standard getters & setters
    public String get_id() { return _id; }
    public String getAccount() { return account; }
    public void setAccount(String account) { this.account = account; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public RealmList<String> getFavor_menu_list() { return favor_menu_list; }
    public RealmList<String> getHistory_menu_id_list() { return history_menu_id_list; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public RealmList<String> getOwn_menu_id_list() { return own_menu_id_list; }
    public String getPassword() { return password; }
    public void setPassword(String pass) { password=pass; }
    public String getPhone_number() { return phone_number; }
    public void setPhone_number(String phone_number) { this.phone_number = phone_number; }

    public void addFavor(Menu f) {
        favor_menu_list.add(f.get_id());
    }
    public boolean removeFavor(Menu f) {
        return favor_menu_list.remove(f.get_id());
    }

    public void addOwn(Menu m){
        // TODO: Add menuid to list;
        own_menu_id_list.add(m.get_id());
    }

    public void removeOwn(Menu m){
        // TODO: remove menuid from list;
        own_menu_id_list.remove(m.get_id());
    }


    public void addHistory(Menu m){
        // TODO: Add menuid to list;
        history_menu_id_list.add(m.get_id());
    }

    public void removeHistory(Menu m){
        // TODO: remove menuid from list;
        history_menu_id_list.remove(m.get_id());
    }

    public boolean update(String Name,int Age,String Account,String Password,String Email,String PhoneNumber){
        name = Name;
        age = Age;
        account = Account;
        password = Password;
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

    public String get_partition() {
        return _partition;
    }
    public void set_partition(String np) {
        _partition=np;
    }

}
