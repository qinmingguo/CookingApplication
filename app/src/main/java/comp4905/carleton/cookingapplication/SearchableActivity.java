package comp4905.carleton.cookingapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;

import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.util.ArrayList;

import comp4905.carleton.cookingapplication.Model.Account;
import comp4905.carleton.cookingapplication.Model.Menu;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.log.LogLevel;
import io.realm.log.RealmLog;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.AppException;
import io.realm.mongodb.User;
import io.realm.mongodb.sync.SyncConfiguration;
import io.realm.mongodb.sync.SyncSession;

public class SearchableActivity extends AppCompatActivity {
    private  final String TAG = this.getClass().getSimpleName() + " @" + System.identityHashCode(this);

    private Realm backgroundThreadRealm;

    private App app;

    private User user;

    private RealmConfiguration config;

    private MaterialToolbar topAppBar;

    private TableLayout main_table;

    private TableRow current_table_row;

    private Account account;

    //Number of table menu store
    private int current_menu_value;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);
        setResult(RESULT_CANCELED);

        String query = "";
        Log.i(TAG,"SearchableActivity start");
        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            Log.e(TAG,"Query: "+query);
        }else{
            Log.e(TAG, "Not search action");
            finish();
        }

        Realm.init(this);
        app = new App(new AppConfiguration.Builder(BuildConfig.MONGODB_REALM_APP_ID).defaultSyncErrorHandler(new SyncSession.ErrorHandler() {
            @Override
            public void onError(SyncSession session, AppException error) {
                Log.e(TAG, "Sync error: ${error.errorMessage}");
            }
        }).build());
        if(BuildConfig.DEBUG){
            RealmLog.setLevel(LogLevel.ALL);
        }

        main_table = findViewById(R.id.main_table_search);
        user = app.currentUser();

        config = new SyncConfiguration.Builder(user,"menu").build();

        backgroundThreadRealm = Realm.getInstance(config);

        if(backgroundThreadRealm==null){
            Log.i(TAG,"Can't connect");
            finish();
        }

        RealmResults<Menu> menus = backgroundThreadRealm.where(Menu.class).contains("title",query).findAll();

        if(menus.size()==0){
            setResult(RESULT_OK);
            finish();
        }

        topAppBar =  findViewById(R.id.topAppBar_search);
        setSupportActionBar(topAppBar);
        topAppBar.setNavigationOnClickListener(view -> {

            backgroundThreadRealm.close();
            finish();
        });

        current_table_row= null;
        current_menu_value = 0;

        // Action goes here
        for(int i = current_menu_value;i<current_menu_value+6&&i<menus.size();i++){
            if(current_table_row==null||current_table_row.getChildCount()==2){
                current_table_row = new TableRow(SearchableActivity.this);
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
                //Go to Menu Read Activity.
                startReadMenu(menus.get(index));
//                addMenusToHistory(menus.get(index));
            });
//            Button favor_button = new_menu.findViewById(R.id.menu_add_favor_button);
//            // handle favor button part
//            favor_button.setOnClickListener(view -> {
//                if(account!=null){
//                    //Add to favor
//                    favor_button.setBackgroundColor(getResources().getColor(R.color.purple_700));
//                    removeMenusFromFavor(menus.get(index));
//                    current_table_row = (TableRow) new_menu.getParent();
//                    current_table_row.removeView(new_menu);
//                }else{
//                    Toast.makeText(SearchableActivity.this,"Please login first",Toast.LENGTH_SHORT).show();
//                }
//            });
            current_table_row.addView(new_menu);
            System.out.println("New View added: "+current_menu_value);
            current_menu_value++;
        }
    }

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

    @Override
    protected void onDestroy () {
        super.onDestroy();

        backgroundThreadRealm.close();
    }

    @Override
    protected void onStop () {
        super.onStop();

        backgroundThreadRealm.close();
    }
}