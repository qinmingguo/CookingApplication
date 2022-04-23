package comp4905.carleton.cookingapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;

import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

import comp4905.carleton.cookingapplication.Model.Account;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.User;
import io.realm.mongodb.sync.SyncConfiguration;
public class MainActivity extends AppCompatActivity {

    private  final String TAG = this.getClass().getSimpleName() + " @" + System.identityHashCode(this);

    //Request signal
    private static final int REQUEST_PERSONAL = 1;
    private static final int REQUEST_SETTING = 2;
    private static final int REQUEST_CREATE = 3;
    private static final int REQUEST_LOGIN = 4;

    //View
    private TableLayout main_table;

    private TableRow current_table_row;

    private MaterialToolbar topAppBar;

    private DrawerLayout drawerLayout;

    private NavigationView navigationView;

    private NestedScrollView scrollView;

    private TextView email_field,name_field;

    private View headerLayout;

    private Button more_button;

    //Number of table menu store
    private int current_menu_value;

    //local Client
    private Client client;


    @Override
    protected  void onStart(){
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initial Realm stuff
        Realm.init(this);
        client = new Client();

        if(client.getUser()==null){
            //if don't have user then start to login
            startActivityForResult(new Intent(this, LoginActivity.class),REQUEST_LOGIN);
        }else {

        }
        current_menu_value = 0;

        if(client.getUser()!=null&&client.getAccount()!=null){
            if(client.getAccount().getEmail()!=null&&email_field!=null){
                email_field.setText(client.getAccount().getEmail());
            }
            if(client.getAccount().getName()!=null&&name_field!=null){
                name_field.setText(client.getAccount().getName());
            }
        }
        //link main frame
        main_table =  findViewById(R.id.main_table);

        //link top bar
        topAppBar =  findViewById(R.id.topAppBar);
        setSupportActionBar(topAppBar);

        //handle Navigation trigger
        topAppBar.setNavigationOnClickListener(view -> drawerLayout.openDrawer(navigationView));

        //link navigation view.
        drawerLayout =  findViewById(R.id.drawerLayout);
        navigationView =  findViewById(R.id.sidebar);
        headerLayout = navigationView.inflateHeaderView(R.layout.header_navigation_drawer);
        email_field = (TextView)headerLayout.findViewById(R.id.email_field_header);
        name_field = (TextView)headerLayout.findViewById(R.id.name_field_header);
        scrollView = findViewById(R.id.scroll_view);

        email_field.setText(client.getAccount().getEmail());
        name_field.setText(client.getAccount().getName());

        more_button = findViewById(R.id.more_button);
        more_button.setVisibility(View.INVISIBLE);
        more_button.setEnabled(false);


        //handle the side bar selection.
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.Home_Button:
                    // Change table to Home
                    Log.i(TAG,"Home Button is pressed");
                    //initial menus list
                    client.getAllMenu();
                    client.getUsersFavorMenu();
                    client.getUsersHistoryMenu();
                    if(client.get_main_menus_list()==null){
                        break;
                    }

                    //change Main Page title
                    topAppBar.setTitle(R.string.home);

                    //Clean main table menu items.
                    main_table.removeAllViews();
                    current_table_row= null;
                    current_menu_value = 0;
                    more_button.setVisibility(View.INVISIBLE);

                    // Action goes here
                    int local_main_value_out = current_menu_value;
                    for(int i = current_menu_value;(i<local_main_value_out+6)&&(i<client.get_main_menus_list().size());i++){
                        if(current_table_row==null||current_table_row.getChildCount()==2){
                            current_table_row = new TableRow(MainActivity.this);
                            main_table.addView(current_table_row);
                        }
                        View new_menu = getLayoutInflater().inflate(R.layout.table_item,null);
                        TextView title_text_view = new_menu.findViewById(R.id.menu_title_field);
                        title_text_view.setText(client.get_main_menus_list().get(i).getTitle());
                        TextView author_text_view = new_menu.findViewById(R.id.menu_author_field);
                        author_text_view.setText(client.get_main_menus_list().get(i).getAuthor());
                        TextView intro_text_view = new_menu.findViewById(R.id.menu_intro_field);
                        intro_text_view.setText(client.get_main_menus_list().get(i).getIntroduction());
                        Button go_button = new_menu.findViewById(R.id.menu_go_button);
                        int index = i;
                        go_button.setOnClickListener(view -> {
                            // Go to Menu Read Activity.
                            startReadMenu(client.get_main_menus_list().get(index));
                            client.addMenusToHistory(client.get_main_menus_list().get(index));
                        });
                        MaterialButton favor_button = new_menu.findViewById(R.id.menu_add_favor_button);

                        // handle favor button part
                        if(client.get_favor_menus_list().contains(client.get_main_menus_list().get(i))){
                            favor_button.setIconTint(ColorStateList.valueOf(getResources().getColor(R.color.purple_200)));
                        }
                        favor_button.setOnClickListener(view -> {
                            if(client.getAccount()!=null){
                                // Add to favor
                                if(client.get_favor_menus_list().contains(client.get_main_menus_list().get(index))){
                                    favor_button.setIconTint(ColorStateList.valueOf(getResources().getColor(R.color.purple_500)));
                                    client.removeMenusFromFavor(client.get_main_menus_list().get(index));
                                }else{
                                    favor_button.setIconTint(ColorStateList.valueOf(getResources().getColor(R.color.purple_200)));
                                    client.addMenusToFavor(client.get_main_menus_list().get(index));
                                }
                            }else{
                                Toast.makeText(MainActivity.this,"Please login first",Toast.LENGTH_SHORT).show();
                            }
                        });
                        current_table_row.addView(new_menu);
//                        System.out.println("New View added: "+current_menu_value);
                        current_menu_value++;
                    }
                    more_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int local_main_value_in = current_menu_value;
                            for(int i = current_menu_value;(i<local_main_value_in+6)&&i<client.get_main_menus_list().size();i++){
                                if(current_table_row==null||current_table_row.getChildCount()==2){
                                    current_table_row = new TableRow(MainActivity.this);
                                    main_table.addView(current_table_row);
                                }
                                View new_menu = getLayoutInflater().inflate(R.layout.table_item,null);
                                TextView title_text_view = new_menu.findViewById(R.id.menu_title_field);
                                title_text_view.setText(client.get_main_menus_list().get(i).getTitle());
                                TextView author_text_view = new_menu.findViewById(R.id.menu_author_field);
                                author_text_view.setText(client.get_main_menus_list().get(i).getAuthor());
                                TextView intro_text_view = new_menu.findViewById(R.id.menu_intro_field);
                                intro_text_view.setText(client.get_main_menus_list().get(i).getIntroduction());
                                Button go_button = new_menu.findViewById(R.id.menu_go_button);
                                int index = i;
                                go_button.setOnClickListener(view -> {
                                    // Go to Menu Read Activity.
                                    startReadMenu(client.get_main_menus_list().get(index));
                                    client.addMenusToHistory(client.get_main_menus_list().get(index));
                                });
                                MaterialButton favor_button = new_menu.findViewById(R.id.menu_add_favor_button);

                                // handle favor button part
                                if(client.get_favor_menus_list().contains(client.get_main_menus_list().get(i))){
                                    favor_button.setIconTint(ColorStateList.valueOf(getResources().getColor(R.color.purple_200)));
                                }
                                favor_button.setOnClickListener(view -> {
                                    if(client.getAccount()!=null){
                                        // Add to favor
                                        if(client.get_favor_menus_list().contains(client.get_main_menus_list().get(index))){
                                            favor_button.setIconTint(ColorStateList.valueOf(getResources().getColor(R.color.purple_500)));
                                            client.removeMenusFromFavor(client.get_main_menus_list().get(index));
                                        }else{
                                            favor_button.setIconTint(ColorStateList.valueOf(getResources().getColor(R.color.purple_200)));
                                            client.addMenusToFavor(client.get_main_menus_list().get(index));
                                        }
                                    }else{
                                        Toast.makeText(MainActivity.this,"Please login first",Toast.LENGTH_SHORT).show();
                                    }
                                });
                                current_table_row.addView(new_menu);
//                                System.out.println("New View added: "+current_menu_value);
                                current_menu_value++;
                            }
                            more_button.setVisibility(View.INVISIBLE);
                        }
                    });
                    scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                        @Override
                        public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                            if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                                Log.i(TAG, "BOTTOM SCROLL");
                                if(current_menu_value<client.get_main_menus_list().size()){
                                    more_button.setVisibility(View.VISIBLE);
                                    more_button.setEnabled(true);
                                }
                            }
                            if (scrollY < (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                                more_button.setVisibility(View.INVISIBLE);
                                more_button.setEnabled(false);
                            }
                        }
                    });
                    break;
                case R.id.Favourite_Menu_Button:
                    // Change table to Favorite
                    Log.i(TAG,"Favourite Button is pressed");
                    //initial menus list
                    client.getUsersFavorMenu();
                    client.getUsersHistoryMenu();

                    //check user login state
                    if(client.getUser()==null){
                        Toast.makeText(MainActivity.this,"Please login first",Toast.LENGTH_SHORT).show();
                        break;
                    }
                    if(client.get_favor_menus_list()==null){
                        break;
                    }

                    //change Main Page title
                    topAppBar.setTitle(R.string.favourite);

                    //Clean main table menu items.
                    main_table.removeAllViews();
                    current_table_row= null;
                    current_menu_value = 0;
                    more_button.setVisibility(View.INVISIBLE);

                    // Action goes here
                    int local_value_favor_out = current_menu_value;
                    for(int i = current_menu_value;i<local_value_favor_out+6&&i<client.get_favor_menus_list().size();i++){
                        if(current_table_row==null||current_table_row.getChildCount()==2){
                            current_table_row = new TableRow(MainActivity.this);
                            main_table.addView(current_table_row);
                        }
                        View new_menu = getLayoutInflater().inflate(R.layout.table_item,null);
                        TextView title_text_view = new_menu.findViewById(R.id.menu_title_field);
                        title_text_view.setText(client.get_favor_menus_list().get(i).getTitle());
                        TextView author_text_view = new_menu.findViewById(R.id.menu_author_field);
                        author_text_view.setText(client.get_favor_menus_list().get(i).getAuthor());
                        TextView intro_text_view = new_menu.findViewById(R.id.menu_intro_field);
                        intro_text_view.setText(client.get_favor_menus_list().get(i).getIntroduction());
                        Button go_button = new_menu.findViewById(R.id.menu_go_button);
                        int index = i;
                        go_button.setOnClickListener(view -> {
                            //Go to Menu Read Activity.
                            startReadMenu(client.get_favor_menus_list().get(index));
                            client.addMenusToHistory(client.get_favor_menus_list().get(index));
                        });
                        MaterialButton favor_button = new_menu.findViewById(R.id.menu_add_favor_button);
                        // handle favor button part
                        favor_button.setOnClickListener(view -> {
                            if(client.getAccount()!=null){
                                //Add to favor
                                favor_button.setIconTint(ColorStateList.valueOf(getResources().getColor(R.color.purple_500)));
                                client.removeMenusFromFavor(client.get_favor_menus_list().get(index));
                                current_table_row = (TableRow) new_menu.getParent();
                                current_table_row.removeView(new_menu);
                            }else{
                                Toast.makeText(MainActivity.this,"Please login first",Toast.LENGTH_SHORT).show();
                            }
                        });
                        current_table_row.addView(new_menu);
//                        System.out.println("New View added: "+current_menu_value);
                        current_menu_value++;
                    }
                    more_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int local_value_favor_in = current_menu_value;
                            for(int i = current_menu_value;i<local_value_favor_in+6&&i<client.get_favor_menus_list().size();i++){
                                if(current_table_row==null||current_table_row.getChildCount()==2){
                                    current_table_row = new TableRow(MainActivity.this);
                                    main_table.addView(current_table_row);
                                }
                                View new_menu = getLayoutInflater().inflate(R.layout.table_item,null);
                                TextView title_text_view = new_menu.findViewById(R.id.menu_title_field);
                                title_text_view.setText(client.get_favor_menus_list().get(i).getTitle());
                                TextView author_text_view = new_menu.findViewById(R.id.menu_author_field);
                                author_text_view.setText(client.get_favor_menus_list().get(i).getAuthor());
                                TextView intro_text_view = new_menu.findViewById(R.id.menu_intro_field);
                                intro_text_view.setText(client.get_favor_menus_list().get(i).getIntroduction());
                                Button go_button = new_menu.findViewById(R.id.menu_go_button);
                                int index = i;
                                go_button.setOnClickListener(view -> {
                                    //Go to Menu Read Activity.
                                    startReadMenu(client.get_favor_menus_list().get(index));
                                    client.addMenusToHistory(client.get_favor_menus_list().get(index));
                                });
                                MaterialButton favor_button = new_menu.findViewById(R.id.menu_add_favor_button);
                                // handle favor button part
                                favor_button.setOnClickListener(view -> {
                                    if(client.getAccount()!=null){
                                        //Add to favor
                                        favor_button.setIconTint(ColorStateList.valueOf(getResources().getColor(R.color.purple_500)));
                                        client.removeMenusFromFavor(client.get_favor_menus_list().get(index));
                                        current_table_row = (TableRow) new_menu.getParent();
                                        current_table_row.removeView(new_menu);
                                    }else{
                                        Toast.makeText(MainActivity.this,"Please login first",Toast.LENGTH_SHORT).show();
                                    }
                                });
                                current_table_row.addView(new_menu);
//                                System.out.println("New View added: "+current_menu_value);
                                current_menu_value++;
                            }
                            more_button.setVisibility(View.INVISIBLE);
                        }
                    });
                    scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                        @Override
                        public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                            if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                                Log.i(TAG, "BOTTOM SCROLL");
                                if(current_menu_value<client.get_favor_menus_list().size()){
                                    more_button.setVisibility(View.VISIBLE);
                                    more_button.setEnabled(true);
                                }
                            }
                            if (scrollY < (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                                more_button.setVisibility(View.INVISIBLE);
                                more_button.setEnabled(false);
                            }
                        }
                    });
                    break;
                case R.id.History_Menu_Button:
                    //Change table to History
                    Log.i(TAG,"History Button is pressed");
                    //initial menus list
                    client.getUsersHistoryMenu();
                    client.getUsersFavorMenu();
                    //check user login state
                    if(client.getUser()==null){
                        Toast.makeText(MainActivity.this,"Please login first",Toast.LENGTH_SHORT).show();
                        break;
                    }
                    if(client.get_history_menus_list()==null){
                        break;
                    }

                    //change Main Page title
                    topAppBar.setTitle(R.string.history);

                    //Clean main table menu items.
                    main_table.removeAllViews();
                    current_table_row= null;
                    current_menu_value = 0;
                    more_button.setVisibility(View.INVISIBLE);

                    // Action goes here
                    int local_value_history_out = current_menu_value;
                    for(int i = current_menu_value;i<local_value_history_out+6&&i<client.get_history_menus_list().size();i++){
                        if(current_table_row==null||current_table_row.getChildCount()==2){
                            current_table_row = new TableRow(MainActivity.this);
                            main_table.addView(current_table_row);
                        }
                        View new_menu = getLayoutInflater().inflate(R.layout.table_item,null);
                        TextView title_text_view = new_menu.findViewById(R.id.menu_title_field);
                        title_text_view.setText(client.get_history_menus_list().get(i).getTitle());
                        TextView author_text_view = new_menu.findViewById(R.id.menu_author_field);
                        author_text_view.setText(client.get_history_menus_list().get(i).getAuthor());
                        TextView intro_text_view = new_menu.findViewById(R.id.menu_intro_field);
                        intro_text_view.setText(client.get_history_menus_list().get(i).getIntroduction());
                        Button go_button = new_menu.findViewById(R.id.menu_go_button);
                        int index = i;
                        go_button.setOnClickListener(view -> {
                            //Go to Menu Read Activity.
                            startReadMenu(client.get_history_menus_list().get(index));
                        });
                        MaterialButton favor_button = new_menu.findViewById(R.id.menu_add_favor_button);
                        //handle favor button part
                        favor_button.setOnClickListener(view -> {
                            if(client.getAccount()!=null){
                                //Add to favor
                                if(client.get_favor_menus_list().contains(client.get_history_menus_list().get(index))){
                                    favor_button.setIconTint(ColorStateList.valueOf(getResources().getColor(R.color.purple_500)));
                                    client.removeMenusFromFavor(client.get_history_menus_list().get(index));
                                }else{
                                    favor_button.setIconTint(ColorStateList.valueOf(getResources().getColor(R.color.purple_200)));
                                    client.addMenusToFavor(client.get_history_menus_list().get(index));
                                }
                            }else{
                                Toast.makeText(MainActivity.this,"Please login first",Toast.LENGTH_SHORT).show();
                            }
                        });
//                            }
                        current_table_row.addView(new_menu);
//                        System.out.println("New View added: "+current_menu_value);
                        current_menu_value++;
                    }
                    more_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int local_value_history_in = current_menu_value;
                            for(int i = current_menu_value;i<local_value_history_in+6&&i<client.get_history_menus_list().size();i++){
                                if(current_table_row==null||current_table_row.getChildCount()==2){
                                    current_table_row = new TableRow(MainActivity.this);
                                    main_table.addView(current_table_row);
                                }
                                View new_menu = getLayoutInflater().inflate(R.layout.table_item,null);
                                TextView title_text_view = new_menu.findViewById(R.id.menu_title_field);
                                title_text_view.setText(client.get_history_menus_list().get(i).getTitle());
                                TextView author_text_view = new_menu.findViewById(R.id.menu_author_field);
                                author_text_view.setText(client.get_history_menus_list().get(i).getAuthor());
                                TextView intro_text_view = new_menu.findViewById(R.id.menu_intro_field);
                                intro_text_view.setText(client.get_history_menus_list().get(i).getIntroduction());
                                Button go_button = new_menu.findViewById(R.id.menu_go_button);
                                int index = i;
                                go_button.setOnClickListener(view -> {
                                    //Go to Menu Read Activity.
                                    startReadMenu(client.get_history_menus_list().get(index));
                                });
                                MaterialButton favor_button = new_menu.findViewById(R.id.menu_add_favor_button);
                                //handle favor button part
                                favor_button.setOnClickListener(view -> {
                                    if(client.getAccount()!=null){
                                        //Add to favor
                                        if(client.get_favor_menus_list().contains(client.get_history_menus_list().get(index))){
                                            favor_button.setIconTint(ColorStateList.valueOf(getResources().getColor(R.color.purple_500)));
                                            client.removeMenusFromFavor(client.get_history_menus_list().get(index));
                                        }else{
                                            favor_button.setIconTint(ColorStateList.valueOf(getResources().getColor(R.color.purple_200)));
                                            client.addMenusToFavor(client.get_history_menus_list().get(index));
                                        }
                                    }else{
                                        Toast.makeText(MainActivity.this,"Please login first",Toast.LENGTH_SHORT).show();
                                    }
                                });
//                            }
                                current_table_row.addView(new_menu);
//                                System.out.println("New View added: "+current_menu_value);
                                current_menu_value++;
                            }
                            more_button.setVisibility(View.INVISIBLE);
                        }
                    });
                    scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                        @Override
                        public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                            if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                                Log.i(TAG, "BOTTOM SCROLL");
                                if(current_menu_value<client.get_history_menus_list().size()){
                                    more_button.setVisibility(View.VISIBLE);
                                    more_button.setEnabled(true);
                                }
                            }
                            if (scrollY < (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                                more_button.setVisibility(View.INVISIBLE);
                                more_button.setEnabled(false);
                            }
                        }
                    });
                    break;
                case R.id.Own_Menu_Button:
                    // Change table to History
                    Log.i(TAG,"Own Button is pressed");
                    //initial menus list
                    client.getUsersOwnMenu();
                    client.getUsersFavorMenu();
                    client.getUsersHistoryMenu();
                    //check user login state
                    if(client.getUser()==null){
                        Toast.makeText(MainActivity.this,"Please login first",Toast.LENGTH_SHORT).show();
                        break;
                    }
                    if(client.get_user_menus_list()==null){
                        break;
                    }
                    //change Main Page title
                    topAppBar.setTitle(R.string.own);
                    //Clean main table menu items.
                    main_table.removeAllViews();
                    current_table_row= null;
                    current_menu_value = 0;
                    more_button.setVisibility(View.INVISIBLE);

                    // Action goes here
                    int local_value_owns_out = current_menu_value;
                    for(int i = current_menu_value;i<local_value_owns_out+6&&i<client.get_user_menus_list().size();i++){
                        if(current_table_row==null||current_table_row.getChildCount()==2){
                            current_table_row = new TableRow(MainActivity.this);
                            main_table.addView(current_table_row);
                        }
                        View new_menu = getLayoutInflater().inflate(R.layout.table_item,null);
                        TextView title_text_view = new_menu.findViewById(R.id.menu_title_field);
                        title_text_view.setText(client.get_user_menus_list().get(i).getTitle());
                        TextView author_text_view = new_menu.findViewById(R.id.menu_author_field);
                        author_text_view.setText(client.get_user_menus_list().get(i).getAuthor());
                        TextView intro_text_view = new_menu.findViewById(R.id.menu_intro_field);
                        intro_text_view.setText(client.get_user_menus_list().get(i).getIntroduction());
                        Button go_button = new_menu.findViewById(R.id.menu_go_button);
                        int index = i;
                        go_button.setOnClickListener(view -> {
                            // Go to Menu Read Activity.
                            startReadMenu(client.get_user_menus_list().get(index));
                            client.addMenusToHistory(client.get_user_menus_list().get(index));

                        });
                        MaterialButton favor_button = new_menu.findViewById(R.id.menu_add_favor_button);
                        // handle favor button part
                        favor_button.setOnClickListener(view -> {
                            if(client.getAccount()!=null){
                                // Add to favor
                                if(client.get_favor_menus_list().contains(client.get_user_menus_list().get(index))){
                                    favor_button.setIconTint(ColorStateList.valueOf(getResources().getColor(R.color.purple_500)));
                                    client.removeMenusFromFavor(client.get_user_menus_list().get(index));
                                }else{
                                    favor_button.setIconTint(ColorStateList.valueOf(getResources().getColor(R.color.purple_200)));
                                    client.addMenusToFavor(client.get_user_menus_list().get(index));
                                }
                            }else{
                                Toast.makeText(MainActivity.this,"Please login first",Toast.LENGTH_SHORT).show();
                            }
                        });
//                            }
                        current_table_row.addView(new_menu);
//                        System.out.println("New View added: "+current_menu_value);
                        current_menu_value++;
                    }
                    more_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int local_value_owns_in = current_menu_value;
                            for(int i = current_menu_value;i<local_value_owns_in+6&&i<client.get_user_menus_list().size();i++){
                                if(current_table_row==null||current_table_row.getChildCount()==2){
                                    current_table_row = new TableRow(MainActivity.this);
                                    main_table.addView(current_table_row);
                                }
                                View new_menu = getLayoutInflater().inflate(R.layout.table_item,null);
                                TextView title_text_view = new_menu.findViewById(R.id.menu_title_field);
                                title_text_view.setText(client.get_user_menus_list().get(i).getTitle());
                                TextView author_text_view = new_menu.findViewById(R.id.menu_author_field);
                                author_text_view.setText(client.get_user_menus_list().get(i).getAuthor());
                                TextView intro_text_view = new_menu.findViewById(R.id.menu_intro_field);
                                intro_text_view.setText(client.get_user_menus_list().get(i).getIntroduction());
                                Button go_button = new_menu.findViewById(R.id.menu_go_button);
                                int index = i;
                                go_button.setOnClickListener(view -> {
                                    // Go to Menu Read Activity.
                                    startReadMenu(client.get_user_menus_list().get(index));
                                    client.addMenusToHistory(client.get_user_menus_list().get(index));

                                });
                                MaterialButton favor_button = new_menu.findViewById(R.id.menu_add_favor_button);
                                // handle favor button part
                                favor_button.setOnClickListener(view -> {
                                    if(client.getAccount()!=null){
                                        // Add to favor
                                        if(client.get_favor_menus_list().contains(client.get_user_menus_list().get(index))){
                                            favor_button.setIconTint(ColorStateList.valueOf(getResources().getColor(R.color.purple_500)));
                                            client.removeMenusFromFavor(client.get_user_menus_list().get(index));
                                        }else{
                                            favor_button.setIconTint(ColorStateList.valueOf(getResources().getColor(R.color.purple_200)));
                                            client.addMenusToFavor(client.get_user_menus_list().get(index));
                                        }
                                    }else{
                                        Toast.makeText(MainActivity.this,"Please login first",Toast.LENGTH_SHORT).show();
                                    }
                                });
//                            }
                                current_table_row.addView(new_menu);
//                                System.out.println("New View added: "+current_menu_value);
                                current_menu_value++;
                            }
                            more_button.setVisibility(View.INVISIBLE);
                        }
                    });
                    scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                        @Override
                        public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                            if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                                Log.i(TAG, "BOTTOM SCROLL");
                                if(current_menu_value<client.get_user_menus_list().size()){
                                    more_button.setVisibility(View.VISIBLE);
                                    more_button.setEnabled(true);
                                }
                            }
                            if (scrollY < (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                                more_button.setVisibility(View.INVISIBLE);
                                more_button.setEnabled(false);
                            }
                        }
                    });
                    break;
                case R.id.Personal_Information_Button:
                    // Start Personal Activity
                    Log.i(TAG,"Information Button is pressed");
                    // Action goes here
                    if(client.getUser()!=null&&client.getAccount()!=null){
                        Log.i(TAG,client.getAccount().toString());
                        startPersonalActivity(client.getAccount());
                    }else if(client.getUser() != null){
                        startPersonalActivity(client.getAccount());
                    }else{
                        Toast.makeText(MainActivity.this,"Please login first",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.Setting_Button:
                    // Start Personal Setting Activity
                    Log.i(TAG,"Setting Button clicked");
                    // Action goes here
                    startActivityForResult(new Intent(MainActivity.this,SettingActivity.class),REQUEST_SETTING);
                    break;
            }
            return false;
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.setting_menu, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =(SearchManager) getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView =(SearchView) menu.findItem(R.id.search_button).getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                Intent intent = new Intent(MainActivity.this,SearchableActivity.class);
//                intent.setAction(Intent.ACTION_SEARCH);
//                intent.putExtra(SearchManager.QUERY,searchView.getQuery().toString());
//                startActivityForResult(intent,REQUEST_SEARCH);

                RealmResults<comp4905.carleton.cookingapplication.Model.Menu> menus = client.searchMenuResult(query);

                if(menus.size()==0){
                    Toast.makeText(MainActivity.this,"No Search Result",Toast.LENGTH_SHORT).show();
                }
                current_table_row= null;
                current_menu_value = 0;
                topAppBar.setTitle(R.string.search);
                //Clean main table menu items.
                main_table.removeAllViews();
                int local_research_value_out = current_menu_value;
                for(int i = current_menu_value;(i<local_research_value_out+6)&&(i<menus.size());i++){
                    if(current_table_row==null||current_table_row.getChildCount()==2){
                        current_table_row = new TableRow(MainActivity.this);
                        main_table.addView(current_table_row);
                    }
                    View new_menu = getLayoutInflater().inflate(R.layout.table_item,null);
                    TextView title_text_view = new_menu.findViewById(R.id.menu_title_field);
                    title_text_view.setText(menus.get(i).getTitle());
                    TextView author_text_view = new_menu.findViewById(R.id.menu_author_field);
                    author_text_view.setText(menus.get(i).getAuthor());
                    TextView intro_text_view = new_menu.findViewById(R.id.menu_intro_field);
                    intro_text_view.setText(menus.get(i).getIntroduction());
                    Button go_button = new_menu.findViewById(R.id.menu_go_button);
                    int index = i;
                    go_button.setOnClickListener(view -> {
                        // Go to Menu Read Activity.
                        startReadMenu(menus.get(index));
                        client.addMenusToHistory(menus.get(index));
                    });
                    MaterialButton favor_button = new_menu.findViewById(R.id.menu_add_favor_button);

                    // handle favor button part
                    if(client.get_favor_menus_list().contains(menus.get(i))){
                        favor_button.setIconTint(ColorStateList.valueOf(getResources().getColor(R.color.purple_200)));
                    }
                    favor_button.setOnClickListener(view -> {
                        if(client.getAccount()!=null){
                            // Add to favor
                            if(client.get_favor_menus_list().contains(client.get_main_menus_list().get(index))){
                                favor_button.setIconTint(ColorStateList.valueOf(getResources().getColor(R.color.purple_500)));
                                client.removeMenusFromFavor(client.get_main_menus_list().get(index));
                            }else{
                                favor_button.setIconTint(ColorStateList.valueOf(getResources().getColor(R.color.purple_200)));
                                client.addMenusToFavor(client.get_main_menus_list().get(index));
                            }
                        }else{
                            Toast.makeText(MainActivity.this,"Please login first",Toast.LENGTH_SHORT).show();
                        }
                    });
                    current_table_row.addView(new_menu);
//                    System.out.println("New View added: "+current_menu_value);
                    current_menu_value++;
                }
                more_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int local_research_value_in = current_menu_value;
                        for(int i = current_menu_value;(i<local_research_value_in+6)&&i<menus.size();i++){
                            if(current_table_row==null||current_table_row.getChildCount()==2){
                                current_table_row = new TableRow(MainActivity.this);
                                main_table.addView(current_table_row);
                            }
                            View new_menu = getLayoutInflater().inflate(R.layout.table_item,null);
                            TextView title_text_view = new_menu.findViewById(R.id.menu_title_field);
                            title_text_view.setText(menus.get(i).getTitle());
                            TextView author_text_view = new_menu.findViewById(R.id.menu_author_field);
                            author_text_view.setText(menus.get(i).getAuthor());
                            TextView intro_text_view = new_menu.findViewById(R.id.menu_intro_field);
                            intro_text_view.setText(menus.get(i).getIntroduction());
                            Button go_button = new_menu.findViewById(R.id.menu_go_button);
                            int index = i;
                            go_button.setOnClickListener(view -> {
                                // Go to Menu Read Activity.
                                startReadMenu(menus.get(index));
                                client.addMenusToHistory(menus.get(index));
                            });
                            MaterialButton favor_button = new_menu.findViewById(R.id.menu_add_favor_button);

                            // handle favor button part
                            if(client.get_favor_menus_list().contains(menus.get(i))){
                                favor_button.setIconTint(ColorStateList.valueOf(getResources().getColor(R.color.purple_200)));
                            }
                            favor_button.setOnClickListener(view -> {
                                if(client.getAccount()!=null){
                                    // Add to favor
                                    if(client.get_favor_menus_list().contains(menus.get(index))){
                                        favor_button.setIconTint(ColorStateList.valueOf(getResources().getColor(R.color.purple_500)));
                                        client.removeMenusFromFavor(menus.get(index));
                                    }else{
                                        favor_button.setIconTint(ColorStateList.valueOf(getResources().getColor(R.color.purple_200)));
                                        client.addMenusToFavor(menus.get(index));
                                    }
                                }else{
                                    Toast.makeText(MainActivity.this,"Please login first",Toast.LENGTH_SHORT).show();
                                }
                            });
                            current_table_row.addView(new_menu);
//                            System.out.println("New View added: "+current_menu_value);
                            current_menu_value++;
                        }
                        more_button.setVisibility(View.INVISIBLE);
                    }
                });
                scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                    @Override
                    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                        if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                            Log.i(TAG, "BOTTOM SCROLL");
                            if(current_menu_value<client.get_main_menus_list().size()){
                                more_button.setVisibility(View.VISIBLE);
                                more_button.setEnabled(true);
                            }
                        }
                        if (scrollY < (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                            more_button.setVisibility(View.INVISIBLE);
                            more_button.setEnabled(false);
                        }
                    }
                });
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_button) {// Start Create Menu Activity
            Toast.makeText(MainActivity.this, "add is pressed", Toast.LENGTH_SHORT).show();
            startCreateMenu();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // handle other activity result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERSONAL) {
            if(resultCode == RESULT_OK) {
                if(data.getBooleanExtra(String.valueOf(R.string.change),false)){
                    client.updateAccount(data.getStringExtra(String.valueOf(R.string.name)),
                            data.getIntExtra(String.valueOf(R.string.age),0), data.getStringExtra(String.valueOf(R.string.phone_number)),
                            data.getStringExtra(String.valueOf(R.string.email)));
                }
                client.re_CheckAccount();

                email_field.setText(client.getAccount().getEmail());
                name_field.setText(client.getAccount().getName());
            }
        }else if (requestCode == REQUEST_SETTING) {
            if(resultCode == RESULT_OK) {
                //handle setting result
                startActivityForResult(new Intent(this,LoginActivity.class),REQUEST_LOGIN);
            }
        }else if (requestCode == REQUEST_CREATE) {
            if(resultCode == RESULT_OK) {
                //handle create result
                LocalDate date = LocalDate.now();
                String[] ingredient_list = data.getStringArrayExtra(String.valueOf(R.string.ingredient));
                String ingredient = "";
                for (String s : ingredient_list) {
                    ingredient += s;
                    ingredient += ";";
                }
                String[] process_list = data.getStringArrayExtra(String.valueOf(R.string.process));
                String p = "";
                for (String s : process_list) {
                    p += s;
                    p += ";";
                }
                client.CreateNewMenu(client.getAccount().get_id(),data.getStringExtra(String.valueOf(R.string.title)),client.getAccount().getName(),date.toString(),String.valueOf(R.string.introduction),
                        ingredient,p);
            }
        }else if(requestCode == REQUEST_LOGIN){
            if(resultCode == RESULT_OK){
                client.re_CheckAccount();

                email_field.setText(client.getAccount().getEmail());
                name_field.setText(client.getAccount().getName());
            }else{

            }
        }
//        else if(requestCode == REQUEST_SEARCH){
//            if(resultCode == RESULT_OK){
//                Toast.makeText(this,"No Search Result",Toast.LENGTH_SHORT).show();
//            }else{
//                return;
//            }
//        }
    }

    //TODO: Start PersonalActivity and pass User's Information and set request signal
    public void startPersonalActivity(Account u){
        Intent i = new Intent(this, PersonalActivity.class);
        i.putExtra(String.valueOf(R.string.username),u.getEmail());
        i.putExtra(String.valueOf(R.string.id),u.get_id());
        i.putExtra(String.valueOf(R.string.name),u.getName());
        i.putExtra(String.valueOf(R.string.age),u.getAge().toString());
        i.putExtra(String.valueOf(R.string.phone_number),u.getPhone_number());
        i.putExtra(String.valueOf(R.string.email),u.getEmail());
        i.putExtra(String.valueOf(R.string.change),false);
        startActivityForResult(i, REQUEST_PERSONAL);
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
        String[] ingredient = m.getIngredient().split(";");
        String[] process = m.getProcess().split(";");
        intent.putExtra(String.valueOf(R.string.ingredient),ingredient);
        intent.putExtra(String.valueOf(R.string.process),process);
        startActivity(intent);
    }



//    public void deleteMenu(comp4905.carleton.cookingapplication.Model.Menu menu){
//        backgroundThreadRealm.executeTransaction(realm -> {
//            comp4905.carleton.cookingapplication.Model.Menu innermenu = realm.where(comp4905.carleton.cookingapplication.Model.Menu.class).equalTo("_id",menu.get_id()).findFirst();
//            assert innermenu != null;
//            innermenu.deleteFromRealm();
//        });
//    }
//
//    public void deleteAccount(Account account){
//        backgroundThreadRealm.executeTransaction(realm -> {
//            Account inneraccount = realm.where(Account.class).equalTo("_id",account.get_id()).findFirst();
//            assert inneraccount != null;
//            inneraccount.deleteFromRealm();
//        });
//    }

//    public void updateMenu(comp4905.carleton.cookingapplication.Model.Menu menu){
//        backgroundThreadRealm.executeTransaction(realm -> {
//            comp4905.carleton.cookingapplication.Model.Menu innermenu = realm.where(comp4905.carleton.cookingapplication.Model.Menu.class).equalTo("_id",menu.get_id()).findFirst();
//            assert innermenu != null;
//            innermenu.updateMenu(menu);
//        });
//    }



    @Override
    protected void onDestroy () {
        super.onDestroy();

        client.close();
    }

    @Override
    protected void onStop () {
        super.onStop();

    }

}