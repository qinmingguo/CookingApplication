package comp4905.carleton.cookingapplication;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import org.bson.types.ObjectId;

import java.io.FileNotFoundException;

import comp4905.carleton.cookingapplication.Model.Account;
import io.realm.ImportFlag;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.log.LogLevel;
import io.realm.log.RealmLog;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.AppException;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.sync.SyncSession;

public class LoginActivity extends AppCompatActivity {
    private  final String TAG = this.getClass().getSimpleName() + " @" + System.identityHashCode(this);
    private TextInputEditText account_field,password_field;
    private Button login_button,sign_up_button;
    private ImageButton back_button;
    private App taskapp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Realm.init(this);
        taskapp = new App(new AppConfiguration.Builder(BuildConfig.MONGODB_REALM_APP_ID).defaultSyncErrorHandler(new SyncSession.ErrorHandler() {
            @Override
            public void onError(SyncSession session, AppException error) {
                Log.e(TAG, "Sync error: ${error.errorMessage}");
            }
        }).build());
        if(BuildConfig.DEBUG){
            RealmLog.setLevel(LogLevel.ALL);
        }

        account_field = findViewById(R.id.account_field);
        password_field = findViewById(R.id.password_field);
        login_button = findViewById(R.id.login_button);
        sign_up_button = findViewById(R.id.sign_up_button);
        back_button = findViewById(R.id.back_button_login);

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Credentials credentials = Credentials.anonymous();
                taskapp.loginAsync(credentials,call->{
                    login_button.setEnabled(true);
                    sign_up_button.setEnabled(true);
                    if(!call.isSuccess()){
                        Log.e(TAG, "Invalid username or password");
                        Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_LONG).show();
                    }else{
                        finish();
                    }
                });
                Toast.makeText(LoginActivity.this,"Login fail",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(LoginActivity.this,"Confirm is pressed",Toast.LENGTH_SHORT).show();
                login(false);
            }
        });

        sign_up_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login(true);
            }
        });

    }

    private Boolean validateCredentials(){
        if(account_field.getText().toString().isEmpty()||password_field.getText().toString().isEmpty()){
            return false;
        }else{
            return true;
        }
    }

    private void login(Boolean create){
        if(!validateCredentials()){
            Log.e(TAG, "Invalid username or password");
            Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_LONG).show();
        }

        login_button.setEnabled(false);
        sign_up_button.setEnabled(false);

        String username = this.account_field.getText().toString();
        String password = this.password_field.getText().toString();

        if(create){
            //TODO: Register a User by username and password
            taskapp.getEmailPassword().registerUserAsync(username,password,call->{
                login_button.setEnabled(true);
                sign_up_button.setEnabled(true);
                if(!call.isSuccess()) {
                    Log.e(TAG, "REGISTER:Error: "+call.getError()+"");
                    Toast.makeText(LoginActivity.this, "Error: "+call.getError()+"", Toast.LENGTH_LONG).show();
                }else{
                    Log.i(TAG, "REGISTER:Successfully registered user.");
                    login(false);
                }
            });
        }else{
            //TODO: Log in with the supplied username and password
            Credentials credentials = Credentials.emailPassword(username,password);
            taskapp.loginAsync(credentials,call->{
                login_button.setEnabled(true);
                sign_up_button.setEnabled(true);
                if(!call.isSuccess()){
                    Log.e(TAG, "LOGIN:Invalid username or password");
                    Toast.makeText(LoginActivity.this, "LOGIN:Invalid username or password", Toast.LENGTH_LONG).show();
                }else{
                    taskapp.switchUser(call.get());
//                    System.out.println(call.get().getId());
//                    System.out.println(taskapp.currentUser().getId());
                    setResult(RESULT_OK);
                    finish();
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // the ui thread realm uses asynchronous transactions, so we can only safely close the realm
        // when the activity ends and we can safely assume that those transactions have completed
    }

    @Override
    protected void onStop() {
        super.onStop();
        // the ui thread realm uses asynchronous transactions, so we can only safely close the realm
        // when the activity ends and we can safely assume that those transactions have completed
    }
}