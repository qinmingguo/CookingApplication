package comp4905.carleton.cookingapplication;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Arrays;

import comp4905.carleton.cookingapplication.Model.Account;
import comp4905.carleton.cookingapplication.Model.Menu;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.User;
import io.realm.mongodb.sync.SyncConfiguration;

public class Client {


    //Realm
    private Realm backgroundThreadRealm,MenuThreadRealm;

    private App app;

    private User user;

    private Account account;

    private RealmConfiguration config;
    //data
    private RealmList<Menu> main_menus_list;
    private RealmList<Menu> favor_menus_list;
    private RealmList<Menu> history_menus_list;
    private RealmList<Menu> user_menus_list;

    public Client(){
        app = new App(new AppConfiguration.Builder(BuildConfig.MONGODB_REALM_APP_ID).build());
        user = app.currentUser();
        config = new SyncConfiguration.Builder(user,"account="+user.getId()).allowWritesOnUiThread(true).build();

        backgroundThreadRealm = Realm.getInstance(config);

        config = new SyncConfiguration.Builder(user,"menu").allowWritesOnUiThread(true).build();

        MenuThreadRealm = Realm.getInstance(config);

        account = backgroundThreadRealm.where(Account.class).equalTo("_partition","account="+user.getId()).findFirst();
        //check account is vaild
        if(account==null){
            //user don't have a account yet, create new one
            backgroundThreadRealm.executeTransaction(realm -> {
                Account a = new Account();
                a.set_partition("account="+user.getId());
                a.set_id(ObjectId.get().toString());
                a.setAccount("XXX");
                a.setAge(0);
                a.setEmail(user.getCustomData().getString("name"));
                a.setName("XXX");
                a.setPhone_number("000-000-0000");
                a.setFavor_menu_list("");
                a.setHistory_menu_id_list("");
                a.setOwn_menu_id_list("");
                a.setPassword("");
                realm.insertOrUpdate(a);
            });
            account = backgroundThreadRealm.where(Account.class).equalTo("_partition","account="+user.getId()).findFirst();
        }

        getAllMenu();
        getUsersFavorMenu();
        getUsersHistoryMenu();
        getUsersOwnMenu();
    }

    public Account getAccount(){
        return account;
    }

    public void re_CheckAccount(){
        user = app.currentUser();
        account = backgroundThreadRealm.where(Account.class).equalTo("_partition","account="+user.getId()).findFirst();
        if(account==null){
            backgroundThreadRealm.executeTransaction(realm -> {
                Account a = new Account();
                a.set_partition("account="+user.getId());
                a.set_id(ObjectId.get().toString());
                a.setAccount("XXX");
                a.setAge(0);
                a.setEmail(user.getCustomData().getString("name"));
                a.setName("XXX");
                a.setPhone_number("000-000-0000");
                a.setFavor_menu_list("");
                a.setHistory_menu_id_list("");
                a.setOwn_menu_id_list("");
                a.setPassword("");
                realm.insertOrUpdate(a);
            });
            account = backgroundThreadRealm.where(Account.class).equalTo("_partition","account="+user.getId()).findFirst();
        }
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public RealmList<Menu> get_main_menus_list(){
        return main_menus_list;
    }
    public RealmList<Menu> get_favor_menus_list(){
        return favor_menus_list;
    }
    public RealmList<Menu> get_history_menus_list(){
        return history_menus_list;
    }
    public RealmList<Menu> get_user_menus_list(){
        return user_menus_list;
    }

    public void updateAccount(String name,int age,String phone_number,String email){
        backgroundThreadRealm.executeTransaction(realm -> {
            Account inner_account = realm.where(Account.class).equalTo("_id",account.get_id()).findFirst();
            assert inner_account != null;
            inner_account.update(name,age,email,phone_number);
            realm.insertOrUpdate(inner_account);
        });
    }

    public void addMenusToFavor(comp4905.carleton.cookingapplication.Model.Menu menu){
        favor_menus_list.add(menu);
        backgroundThreadRealm.executeTransaction(realm -> {
            Account inner_account = realm.where(Account.class).equalTo("_id",account.get_id()).findFirst();
            assert inner_account != null;
            inner_account.addFavor(menu);
            realm.insertOrUpdate(inner_account);
        });
    }

    public void removeMenusFromFavor(comp4905.carleton.cookingapplication.Model.Menu menu){
        backgroundThreadRealm.executeTransaction(realm -> {
            Account inner_account = realm.where(Account.class).equalTo("_id",account.get_id()).findFirst();
            assert inner_account != null;
            inner_account.removeFavor(menu);
            realm.insertOrUpdate(inner_account);
        });
    }

    public void addMenusToHistory(comp4905.carleton.cookingapplication.Model.Menu menu){
        history_menus_list.add(menu);
        backgroundThreadRealm.executeTransaction(realm -> {
            Account inner_account = realm.where(Account.class).equalTo("_id",account.get_id()).findFirst();
            assert inner_account != null;
            inner_account.addHistory(menu);
            realm.insertOrUpdate(inner_account);
        });
    }

    public void CreateNewMenu(String author_id,String title,String author,String calender,String intro,String ingredient,String process){
        // update server
        insertMenu(author_id,title,author,calender,intro,ingredient,process);
        comp4905.carleton.cookingapplication.Model.Menu menu = MenuThreadRealm.where(comp4905.carleton.cookingapplication.Model.Menu.class).equalTo("author_id",account.get_id()).findFirst();
        backgroundThreadRealm.executeTransaction(realm -> {
            Account inner_account = realm.where(Account.class).equalTo("_id",account.get_id()).findFirst();
            assert inner_account != null;
            inner_account.addOwn(menu);
            realm.insertOrUpdate(inner_account);
        });
    }

    public void getAllMenu(){
        RealmList<comp4905.carleton.cookingapplication.Model.Menu> all_menu= new RealmList<>();
        RealmResults<Menu> results = MenuThreadRealm.where(comp4905.carleton.cookingapplication.Model.Menu.class).findAll();
        all_menu.addAll(results);
        main_menus_list = all_menu;
    }

    public void getUsersOwnMenu(){
        RealmList<comp4905.carleton.cookingapplication.Model.Menu> all_menu = main_menus_list;
        RealmList<comp4905.carleton.cookingapplication.Model.Menu> own_menu = new RealmList<>();
        ArrayList<String> index = new ArrayList<>(Arrays.asList(account.getOwn_menu_id_list().split(";")));
        for(int i =0;i<all_menu.size();i++){
            assert all_menu.get(i) != null;
            if(index.contains(all_menu.get(i).get_id())){
                own_menu.add(all_menu.get(i));
            }
        }
        user_menus_list = own_menu;
    }

    public void getUsersFavorMenu(){
        RealmList<comp4905.carleton.cookingapplication.Model.Menu> all_menu = main_menus_list;
        RealmList<comp4905.carleton.cookingapplication.Model.Menu> fav_menu = new RealmList<>();
        ArrayList<String> index = new ArrayList<>();
        index.addAll(Arrays.asList(account.getFavor_menu_list().split(";")));
        for(int i =0;i<all_menu.size();i++){
            if(index.contains(all_menu.get(i).get_id())){
                fav_menu.add(all_menu.get(i));
            }
        }
        favor_menus_list = fav_menu;
    }

    public void getUsersHistoryMenu(){
        RealmList<comp4905.carleton.cookingapplication.Model.Menu> all_menu = main_menus_list;
        RealmList<comp4905.carleton.cookingapplication.Model.Menu> his_menu = new RealmList<>();
        ArrayList<String> index = new ArrayList<>();
        index.addAll(Arrays.asList(account.getHistory_menu_id_list().split(";")));
        for(int i =0;i<all_menu.size();i++){
            assert all_menu.get(i) != null;
            if(index.contains(all_menu.get(i).get_id())){
                his_menu.add(all_menu.get(i));
            }
        }
        history_menus_list = his_menu;
    }

    public void insertMenu(String author_id,String title,String author,String calender,String intro,String ingredient,String process){
        MenuThreadRealm.executeTransaction(realm -> {
            comp4905.carleton.cookingapplication.Model.Menu menu = new comp4905.carleton.cookingapplication.Model.Menu();
            String return_id = ObjectId.get().toString();
            menu.setAuthor_id(author_id);
            menu.set_id(return_id);
            menu.set_partition("menu");
            menu.setTitle(title);
            menu.setAuthor(author);
            menu.setIntroduction(intro);
            menu.setCalender(calender);
            menu.setIngredient(ingredient);
            menu.setProcess(process);
            realm.insertOrUpdate(menu);
        });
    }

    public RealmResults<Menu> searchMenuResult(String query){
        return MenuThreadRealm.where(comp4905.carleton.cookingapplication.Model.Menu.class).contains("title",query).findAll();
    }

    public void close(){
        backgroundThreadRealm.close();
        MenuThreadRealm.close();
    }
}
