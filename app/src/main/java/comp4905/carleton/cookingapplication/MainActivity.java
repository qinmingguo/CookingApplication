package comp4905.carleton.cookingapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import org.bson.types.ObjectId;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

import comp4905.carleton.cookingapplication.Model.Account;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.internal.SyncObjectServerFacade;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.User;
import io.realm.mongodb.sync.ClientResetRequiredError;
import io.realm.mongodb.sync.DiscardUnsyncedChangesStrategy;
import io.realm.mongodb.sync.SyncConfiguration;
import io.realm.mongodb.sync.SyncSession;

public class MainActivity extends AppCompatActivity {

    private  final String TAG = this.getClass().getSimpleName() + " @" + System.identityHashCode(this);

    //Request signal
    private static final int REQUEST_PERSONAL = 1;
    private static final int REQUEST_SETTING = 2;
    private static final int REQUEST_CREATE = 3;

    //View
    private TableLayout main_table;

    private TableRow current_table_row;

    private MaterialToolbar topAppBar;

    private DrawerLayout drawerLayout;

    private NavigationView navigationView;

    private ArrayList<View> table_view_group;

    private TextView email_field;

    //Number of table menu store
    private int current_menu_value;

    //Realm
    private Realm backgroundThreadRealm;

    private App taskapp;

    private User user;

    private Account account;

    private RealmConfiguration config;
    //data
    private RealmList<comp4905.carleton.cookingapplication.Model.Menu> main_menus_list;
    private RealmList<comp4905.carleton.cookingapplication.Model.Menu> favor_menus_list;
    private RealmList<comp4905.carleton.cookingapplication.Model.Menu> history_menus_list;
    private RealmList<comp4905.carleton.cookingapplication.Model.Menu> user_menus_list;

    @Override
    protected  void onStart(){
        super.onStart();

        Realm.init(this);
        taskapp = new App(new AppConfiguration.Builder(BuildConfig.MONGODB_REALM_APP_ID).defaultSyncClientResetStrategy(new DiscardUnsyncedChangesStrategy() {
            @Override
            public void onBeforeReset(Realm realm) {
                Log.w("EXAMPLE", "Beginning client reset for " + realm.getPath());

            }

            @Override
            public void onAfterReset(Realm before, Realm after) {
                Log.w("EXAMPLE", "Finished client reset for " + before.getPath());

            }

            @Override
            public void onError(SyncSession session, ClientResetRequiredError error) {
                Log.e("EXAMPLE", "Couldn't handle the client reset automatically." +
                        " Falling back to manual recovery: " + error.getErrorMessage());
            }
        }).build());

        user = taskapp.currentUser();

        if(user==null){
            startActivity(new Intent(this, LoginActivity.class));
        }else{
            config = new SyncConfiguration.Builder(user,"user="+user.getId()).allowWritesOnUiThread(true).build();

            Realm.getInstanceAsync(config, new Realm.Callback() {
                @Override
                public void onSuccess(Realm realm) {
                    backgroundThreadRealm = realm;
                }
            });

//            Log.i(TAG, user.getId());
//            Log.i(TAG, backgroundThreadRealm.toString());



//            main_menus_list = getAllMenu();
//            favor_menus_list = getUsersFavorMenu();
//            history_menus_list = getUsersHistoryMenu();
//            user_menus_list = getUsersOwnMenu();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //for test;
//        client.testEx();

        table_view_group = new ArrayList<>();

        current_menu_value = 0;

        //link main frame
        main_table = (TableLayout) findViewById(R.id.main_table);

        //link top bar
        topAppBar = (MaterialToolbar) findViewById(R.id.topAppBar);
        setSupportActionBar(topAppBar);

        //handle Navigation trigger
        topAppBar.setNavigationOnClickListener(view -> drawerLayout.openDrawer(navigationView));

        //link navigation view.
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationView = (NavigationView) findViewById(R.id.sidebar);
        email_field = (TextView)navigationView.getHeaderView(R.id.email_field_header);


        //handle the side bar selection.
//        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.Home_Button:
//                        // TODO:Change table to Home
//                        Log.i(TAG,"Home Button is pressed");
//                        //change Main Page title
//                        topAppBar.setTitle(R.string.home);
//                        //Clean main table menu items.
//                        main_table.removeAllViews();
//                        current_menu_value = 0;
//                        table_view_group.clear();
//                        for(int i = current_menu_value;i<current_menu_value+6&&i<main_menus_list.size();i++){
//                            if(current_table_row.getChildCount()==2){
//                                current_table_row = new TableRow(MainActivity.this);
//                                main_table.addView(current_table_row);
//                            }
//                            View new_menu = getLayoutInflater().inflate(R.layout.table_item,null);
//                            TextView title_text_view = new_menu.findViewById(R.id.menu_title_field);
//                            title_text_view.setText(main_menus_list.get(i).getTitle());
//                            TextView author_text_view = new_menu.findViewById(R.id.menu_author_field);
//                            author_text_view.setText(main_menus_list.get(i).getAuthor());
//                            TextView intro_text_view = new_menu.findViewById(R.id.menu_intro_field);
//                            intro_text_view.setText(main_menus_list.get(i).getIntroduction());
//                            Button go_button = new_menu.findViewById(R.id.menu_go_button);
//                            int index = i;
//                            go_button.setOnClickListener(view -> {
//                                //TODO: Go to Menu Read Activity.
//                                startReadMenu(main_menus_list.get(index));
//                            });
//                            Button favor_button = new_menu.findViewById(R.id.menu_add_favor_button);
//                            //TODO: handle favor button part
//                            if(favor_menus_list.contains(main_menus_list.get(i))){
//                                favor_button.setBackgroundColor(getResources().getColor(R.color.purple_200));
//                            }
//
//                            favor_button.setOnClickListener(view -> {
//                                if(account!=null){
//                                    //TODO: Add to favor
//                                    if(favor_menus_list.contains(main_menus_list.get(index))){
//                                        favor_button.setBackgroundColor(getResources().getColor(R.color.purple_700));
//                                        removeMenusFromFavor(main_menus_list.get(index));
//                                    }else{
//                                        favor_button.setBackgroundColor(getResources().getColor(R.color.purple_200));
//                                        addMenusToFavor(main_menus_list.get(index));
//                                    }
//                                }else{
//                                    Toast.makeText(MainActivity.this,"Please login first",Toast.LENGTH_SHORT).show();
//                                }
//                            });
//
//                            current_table_row.addView(new_menu);
//                            System.out.println("New View added: "+current_menu_value);
//                            table_view_group.add(new_menu);
//                            current_menu_value++;
//                        }
//                        // Action goes here
//                        break;
//                    case R.id.Favourite_Menu_Button:
//                        // TODO:Change table to Favorite
//                        Log.i(TAG,"Favourite Button is pressed");
//                        //check user login state
//                        if(user==null){
//                            Toast.makeText(MainActivity.this,"Please login first",Toast.LENGTH_SHORT).show();
//                            break;
//                        }
//                        //change Main Page title
//                        topAppBar.setTitle(R.string.favourite);
//                        //Clean main table menu items.
//                        main_table.removeAllViews();
//                        current_menu_value = 0;
//                        table_view_group.clear();
//
//                        for(int i = current_menu_value;i<current_menu_value+6&&i<favor_menus_list.size();i++){
//                            if(current_table_row.getChildCount()==2){
//                                current_table_row = new TableRow(MainActivity.this);
//                                main_table.addView(current_table_row);
//                            }
//                            View new_menu = getLayoutInflater().inflate(R.layout.table_item,null);
//                            TextView title_text_view = new_menu.findViewById(R.id.menu_title_field);
//                            title_text_view.setText(favor_menus_list.get(i).getTitle());
//                            TextView author_text_view = new_menu.findViewById(R.id.menu_author_field);
//                            author_text_view.setText(favor_menus_list.get(i).getAuthor());
//                            TextView intro_text_view = new_menu.findViewById(R.id.menu_intro_field);
//                            intro_text_view.setText(favor_menus_list.get(i).getIntroduction());
//                            Button go_button = new_menu.findViewById(R.id.menu_go_button);
//                            int index = i;
//                            go_button.setOnClickListener(view -> {
//                                //TODO: Go to Menu Read Activity.
//                                startReadMenu(favor_menus_list.get(index));
//                            });
//                            Button favor_button = new_menu.findViewById(R.id.menu_add_favor_button);
//                            //TODO: handle favor button part
//                            favor_button.setOnClickListener(view -> {
//                                if(account!=null){
//                                    //TODO: Add to favor
//                                    favor_button.setBackgroundColor(getResources().getColor(R.color.purple_700));
//                                    removeMenusFromFavor(favor_menus_list.get(index));
//                                }else{
//                                    Toast.makeText(MainActivity.this,"Please login first",Toast.LENGTH_SHORT).show();
//                                }
//                            });
//
//                            current_table_row.addView(new_menu);
//                            System.out.println("New View added: "+current_menu_value);
//                            table_view_group.add(new_menu);
//                            current_menu_value++;
//                        }
//                        // Action goes here
//                        break;
//                    case R.id.History_Menu_Button:
//                        // TODO:Change table to History
//                        Log.i(TAG,"History Button is pressed");
//                        //check user login state
//                        if(user==null){
//                            Toast.makeText(MainActivity.this,"Please login first",Toast.LENGTH_SHORT).show();
//                            break;
//                        }
//                        //change Main Page title
//                        topAppBar.setTitle(R.string.history);
//                        //Clean main table menu items.
//                        main_table.removeAllViews();
//                        current_menu_value = 0;
//                        table_view_group.clear();
//
//                        for(int i = current_menu_value;i<current_menu_value+6&&i<history_menus_list.size();i++){
//                            if(current_table_row.getChildCount()==2){
//                                current_table_row = new TableRow(MainActivity.this);
//                                main_table.addView(current_table_row);
//                            }
//                            View new_menu = getLayoutInflater().inflate(R.layout.table_item,null);
//                            TextView title_text_view = new_menu.findViewById(R.id.menu_title_field);
//                            title_text_view.setText(history_menus_list.get(i).getTitle());
//                            TextView author_text_view = new_menu.findViewById(R.id.menu_author_field);
//                            author_text_view.setText(history_menus_list.get(i).getAuthor());
//                            TextView intro_text_view = new_menu.findViewById(R.id.menu_intro_field);
//                            intro_text_view.setText(history_menus_list.get(i).getIntroduction());
//                            Button go_button = new_menu.findViewById(R.id.menu_go_button);
//                            int index = i;
//                            go_button.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    //TODO: Go to Menu Read Activity.
//                                    startReadMenu(history_menus_list.get(index));
//                                }
//                            });
//                            Button favor_button = new_menu.findViewById(R.id.menu_add_favor_button);
////                            if (client.getUser()==null){
////                                favor_button.setEnabled(false);
////                            }else{
//                            //TODO: handle favor button part
//                            favor_button.setOnClickListener(view -> {
//                                if(account!=null){
//                                    //TODO: Add to favor
//                                    if(favor_menus_list.contains(history_menus_list.get(index))){
//                                        favor_button.setBackgroundColor(getResources().getColor(R.color.purple_700));
//                                        removeMenusFromFavor(history_menus_list.get(index));
//                                    }else{
//                                        favor_button.setBackgroundColor(getResources().getColor(R.color.purple_200));
//                                        addMenusToFavor(history_menus_list.get(index));
//                                    }
//                                }else{
//                                    Toast.makeText(MainActivity.this,"Please login first",Toast.LENGTH_SHORT).show();
//                                }
//                            });
////                            }
//                            current_table_row.addView(new_menu);
//                            System.out.println("New View added: "+current_menu_value);
//                            table_view_group.add(new_menu);
//                            current_menu_value++;
//                        }
//                        // Action goes here
//                        break;
//                    case R.id.Own_Menu_Button:
//                        // TODO:Change table to History
//                        Log.i(TAG,"Own Button is pressed");
//                        //check user login state
//                        if(user==null){
//                            Toast.makeText(MainActivity.this,"Please login first",Toast.LENGTH_SHORT).show();
//                            break;
//                        }
//                        //change Main Page title
//                        topAppBar.setTitle(R.string.history);
//                        //Clean main table menu items.
//                        main_table.removeAllViews();
//                        current_menu_value = 0;
//                        table_view_group.clear();
//
//                        for(int i = current_menu_value;i<current_menu_value+6&&i<user_menus_list.size();i++){
//                            if(i%2==0){
//                                current_table_row = new TableRow(MainActivity.this);
//                                main_table.addView(current_table_row);
//                            }
//                            View new_menu = getLayoutInflater().inflate(R.layout.table_item,null);
//                            TextView title_text_view = new_menu.findViewById(R.id.menu_title_field);
//                            title_text_view.setText(user_menus_list.get(i).getTitle());
//                            TextView author_text_view = new_menu.findViewById(R.id.menu_author_field);
//                            author_text_view.setText(user_menus_list.get(i).getAuthor());
//                            TextView intro_text_view = new_menu.findViewById(R.id.menu_intro_field);
//                            intro_text_view.setText(user_menus_list.get(i).getIntroduction());
//                            Button go_button = new_menu.findViewById(R.id.menu_go_button);
//                            int index = i;
//                            go_button.setOnClickListener(view -> {
//                                //TODO: Go to Menu Read Activity.
//                                startReadMenu(user_menus_list.get(index));
//
//                            });
//                            Button favor_button = new_menu.findViewById(R.id.menu_add_favor_button);
////                            if (client.getUser()==null){
////                                favor_button.setEnabled(false);
////                            }else{
//                            //TODO: handle favor button part
//                            favor_button.setOnClickListener(view -> {
//                                if(account!=null){
//                                    //TODO: Add to favor
//                                    if(favor_menus_list.contains(user_menus_list.get(index))){
//                                        favor_button.setBackgroundColor(getResources().getColor(R.color.purple_700));
//                                        removeMenusFromFavor(user_menus_list.get(index));
//                                    }else{
//                                        favor_button.setBackgroundColor(getResources().getColor(R.color.purple_200));
//                                        addMenusToFavor(user_menus_list.get(index));
//                                    }
//                                }else{
//                                    Toast.makeText(MainActivity.this,"Please login first",Toast.LENGTH_SHORT).show();
//                                }
//                            });
////                            }
//                            current_table_row.addView(new_menu);
//                            System.out.println("New View added: "+current_menu_value);
//                            table_view_group.add(new_menu);
//                            current_menu_value++;
//                        }
//                        // Action goes here
//                        break;
//                    case R.id.Personal_Information_Button:
//                        // TODO:Start Personal Activity
//                        Log.i(TAG,"Information Button is pressed");
//                        // Action goes here
//                        if(user!=null){
//                        startPersonalActivity(account);
//                        }else{
//                        Toast.makeText(MainActivity.this,"Please login first",Toast.LENGTH_SHORT).show();
//                        }
//                        break;
//                    case R.id.Setting_Button:
//                        // TODO:Start Personal Setting Activity
//                        System.out.println("Setting Button clicked");
//                        startActivityForResult(new Intent(MainActivity.this,SettingActivity.class),REQUEST_SETTING);
//
//                        // Action goes here
//
//                        break;
//                }
//                return false;
//            }
//        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Associate searchable configuration with the SearchView
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.setting_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =(SearchManager) getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView =(SearchView) menu.findItem(R.id.search_button).getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_button) {// TODO:Start Create Menu Activity
            Toast.makeText(MainActivity.this, "add is pressed", Toast.LENGTH_SHORT).show();

            startCreateMenu();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //TODO: handle other activity result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERSONAL) {
            if(resultCode == RESULT_OK) {
                if(data.getBooleanExtra(String.valueOf(R.string.change),false)){
                    changeUserInfo(data.getStringExtra(String.valueOf(R.string.name)),
                            data.getIntExtra(String.valueOf(R.string.age),account.getAge()), data.getStringExtra(String.valueOf(R.string.phone_number)),
                            data.getStringExtra(String.valueOf(R.string.email)));
                }
            }
        }else if (requestCode == REQUEST_SETTING) {
            if(resultCode == RESULT_OK) {
                //TODO: handle setting result
                startActivity(new Intent(this,LoginActivity.class));
            }
        }else if (requestCode == REQUEST_CREATE) {
            if(resultCode == RESULT_OK) {
                //TODO: handle create result
                comp4905.carleton.cookingapplication.Model.Menu new_menu = new comp4905.carleton.cookingapplication.Model.Menu();
                new_menu.set_partition("user="+user.getId());
                new_menu.setTitle(data.getStringExtra(String.valueOf(R.string.title)));
                new_menu.setAuthor(account.getName());
                String pattern = "yyyy-MM-dd";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                new_menu.setCalender(simpleDateFormat.format(LocalDate.now()));
                new_menu.setIntroduction(data.getStringExtra(String.valueOf(R.string.intro)));
                RealmList<String> ingred = new RealmList<>();
                ingred.addAll(Arrays.asList(data.getStringArrayExtra(String.valueOf(R.string.ingredient))));
                new_menu.setIngredient(ingred);
                RealmList<String> p = new RealmList<>();
                p.addAll(Arrays.asList(data.getStringArrayExtra(String.valueOf(R.string.process))));
                new_menu.setProcess(p);
                CreateNewMenu(new_menu);
            }
        }
    }

    //TODO: Start PersonalActivity and pass User's Information and set request signal
    public void startPersonalActivity(Account u){
        Intent i = new Intent(this, PersonalActivity.class);
        i.putExtra(String.valueOf(R.string.username),u.getEmail());
        i.putExtra(String.valueOf(R.string.id),u.get_id());
        i.putExtra(String.valueOf(R.string.name),u.getName());
        i.putExtra(String.valueOf(R.string.age),u.getAge());
        i.putExtra(String.valueOf(R.string.phone_number),u.getPhone_number());
        i.putExtra(String.valueOf(R.string.email),u.getEmail());
        i.putExtra(String.valueOf(R.string.change),false);
        startActivityForResult(i, REQUEST_PERSONAL);
    }

    public boolean startSetting(){
        return false;
    }

    //TODO:Start CreateMenuActivity and set request signal
    public void startCreateMenu(){
        Intent i = new Intent(this, AddMenuActivity.class);
        startActivityForResult(i, REQUEST_CREATE);
    }

    //TODO:Start ReadMenuActivity and set request signal
    public void startReadMenu(comp4905.carleton.cookingapplication.Model.Menu m){
        Intent intent = new Intent(this, ReadMenuActivity.class);
        intent.putExtra(String.valueOf(R.string.title),m.getTitle());
        intent.putExtra(String.valueOf(R.string.author),m.getAuthor());
        intent.putExtra(String.valueOf(R.string.intro),m.getIntroduction());
        String[] ingredient = new String[m.getIngredient().size()];
        for(int i =0;i<m.getIngredient().size();i++){
            ingredient[i] = m.getIngredient().get(i);
        }
        String[] process =  new String[m.getProcess().size()];
        for(int i =0;i<m.getProcess().size();i++){
            process[i] = m.getProcess().get(i);
        }
        intent.putExtra(String.valueOf(R.string.ingredient),ingredient);
        intent.putExtra(String.valueOf(R.string.process),process);
        startActivityForResult(intent, REQUEST_CREATE);
    }

    //Close Realm
    @Override
    protected void onStop () {
        super.onStop();

        if(backgroundThreadRealm!=null){
            backgroundThreadRealm.close();
        }
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();

        backgroundThreadRealm.close();

    }

    public void addMenusToFavor(comp4905.carleton.cookingapplication.Model.Menu menu){
        favor_menus_list.add(menu);
        account.addFavor(menu);
        updateAccount(account);
    }

    public boolean removeMenusFromFavor(comp4905.carleton.cookingapplication.Model.Menu menu){
        if(favor_menus_list.remove(menu)&&account.removeFavor(menu)){
            updateAccount(account);
            return true;
        }else{
            return false;
        }
    }

    public void addMenusToHistory(comp4905.carleton.cookingapplication.Model.Menu menu){
        history_menus_list.add(menu);
        account.addHistory(menu);
        updateAccount(account);
    }



    public void changeUserInfo(String name,int age,String phone_number,String email){
        account.setName(name);
        account.setAge(age);
        account.setPhone_number(phone_number);
        account.setEmail(email);
        updateAccount(account);
    }

    public void CreateNewMenu(comp4905.carleton.cookingapplication.Model.Menu m){
        account.addOwn(m);
        //TODO: update server
        System.out.println("New menu Create:"+m.toString());
        insertMenu(m);
    }


    public RealmList<comp4905.carleton.cookingapplication.Model.Menu> getAllMenu(){
        RealmList<comp4905.carleton.cookingapplication.Model.Menu> all_menu= new RealmList<>();
        RealmResults<comp4905.carleton.cookingapplication.Model.Menu> results = backgroundThreadRealm.where(comp4905.carleton.cookingapplication.Model.Menu.class).findAll();
        all_menu.addAll(results);
        return all_menu;
    }

    public RealmList<comp4905.carleton.cookingapplication.Model.Menu> getUsersOwnMenu(){
        RealmList<comp4905.carleton.cookingapplication.Model.Menu> all_menu = getAllMenu();
        RealmList<comp4905.carleton.cookingapplication.Model.Menu> own_menu = new RealmList<>();
        RealmList<String> index = account.getOwn_menu_id_list();
        for(int i =0;i<all_menu.size();i++){
            assert all_menu.get(i) != null;
            if(index.contains(all_menu.get(i).get_id())){
                own_menu.add(all_menu.get(i));
            }
        }
        return own_menu;
    }

    public RealmList<comp4905.carleton.cookingapplication.Model.Menu> getUsersFavorMenu(){
        RealmList<comp4905.carleton.cookingapplication.Model.Menu> all_menu = getAllMenu();
        RealmList<comp4905.carleton.cookingapplication.Model.Menu> fav_menu = new RealmList<>();
        RealmList<String> index = account.getFavor_menu_list();
        for(int i =0;i<all_menu.size();i++){
            if(index.contains(all_menu.get(i).get_id())){
                fav_menu.add(all_menu.get(i));
            }
        }
        return fav_menu;
    }

    public RealmList<comp4905.carleton.cookingapplication.Model.Menu> getUsersHistoryMenu(){
        RealmList<comp4905.carleton.cookingapplication.Model.Menu> all_menu = getAllMenu();
        RealmList<comp4905.carleton.cookingapplication.Model.Menu> his_menu = new RealmList<>();
        RealmList<String> index = account.getHistory_menu_id_list();
        for(int i =0;i<all_menu.size();i++){
            assert all_menu.get(i) != null;
            assert all_menu.get(i) != null;
            if(index.contains(all_menu.get(i).get_id())){
                his_menu.add(all_menu.get(i));
            }
        }
        return his_menu;
    }

    public void insertMenu(comp4905.carleton.cookingapplication.Model.Menu menu){
        backgroundThreadRealm.executeTransaction(realm -> realm.insert(menu));
    }

    public void insertAccount(Account account){
        backgroundThreadRealm.executeTransaction(realm -> realm.insert(account));
    }

    public void deleteMenu(comp4905.carleton.cookingapplication.Model.Menu menu){
        backgroundThreadRealm.executeTransaction(realm -> {
            comp4905.carleton.cookingapplication.Model.Menu innermenu = realm.where(comp4905.carleton.cookingapplication.Model.Menu.class).equalTo("_id",menu.get_id()).findFirst();
            assert innermenu != null;
            innermenu.deleteFromRealm();
        });
    }

    public void deleteAccount(Account account){
        backgroundThreadRealm.executeTransaction(realm -> {
            Account inneraccount = realm.where(Account.class).equalTo("_id",account.get_id()).findFirst();
            assert inneraccount != null;
            inneraccount.deleteFromRealm();
        });
    }

    public void updateMenu(comp4905.carleton.cookingapplication.Model.Menu menu){
        backgroundThreadRealm.executeTransaction(realm -> {
            comp4905.carleton.cookingapplication.Model.Menu innermenu = realm.where(comp4905.carleton.cookingapplication.Model.Menu.class).equalTo("_id",menu.get_id()).findFirst();
            assert innermenu != null;
            innermenu.updateMenu(menu);
        });
    }

    public void updateAccount(Account account){
        backgroundThreadRealm.executeTransaction(realm -> {
            Account inneraccount = realm.where(Account.class).equalTo("_id",account.get_id()).findFirst();
            assert inneraccount != null;
            inneraccount.updateAccount(account);
        });
    }

}