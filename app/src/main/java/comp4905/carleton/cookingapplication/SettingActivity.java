package comp4905.carleton.cookingapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.sync.SyncConfiguration;

public class SettingActivity extends AppCompatActivity {

    private Chip set_password_button,logout_button,clear_cache_button;

    private MaterialToolbar topAppBar;

    private App taskapp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        taskapp = new App(new AppConfiguration.Builder(BuildConfig.MONGODB_REALM_APP_ID).build());

        topAppBar = (MaterialToolbar) findViewById(R.id.topAppBar_Setting);
        setSupportActionBar(topAppBar);

        topAppBar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onStop();
            }
        });

        logout_button = (Chip)findViewById(R.id.Logout_Button);
//        clear_cache_button = (Chip)findViewById(R.id.Clear_cache_Button);



        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
        });


//        clear_cache_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//            }
//        });
    }

    @Override
    protected void onStop () {
        super.onStop();

    }

    @Override
    protected void onDestroy () {
        super.onDestroy();

    }


}