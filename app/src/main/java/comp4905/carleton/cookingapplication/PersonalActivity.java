package comp4905.carleton.cookingapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;

public class PersonalActivity extends AppCompatActivity {

    private EditText name_field,age_field,phone_field,email_field;
    private TextView username_field,id_field;
    private Button change_button;
    private MaterialToolbar topAppBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);


        topAppBar = (MaterialToolbar) findViewById(R.id.topAppBar_Personal_Info);
        setSupportActionBar(topAppBar);

        topAppBar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //fill information to text field
        username_field = (TextView) findViewById(R.id.username_field);
        username_field.setText(this.getIntent().getStringExtra(String.valueOf(R.string.username)));
        name_field = (EditText) findViewById(R.id.Name_text_field);
        name_field.setText(this.getIntent().getStringExtra(String.valueOf(R.string.name)));
        age_field = (EditText) findViewById(R.id.Age_text_field);
        age_field.setText(this.getIntent().getStringExtra(String.valueOf(R.string.age)));
        phone_field = (EditText) findViewById(R.id.PhoneNumebr_text_field);
        phone_field.setText(this.getIntent().getStringExtra(String.valueOf(R.string.phone_number)));
        email_field = (EditText) findViewById(R.id.Email_text_field);
        email_field.setText(this.getIntent().getStringExtra(String.valueOf(R.string.email)));
        id_field = (TextView) findViewById(R.id.id_field);
        id_field.setText(this.getIntent().getStringExtra(String.valueOf(R.string.id)));

        change_button = (Button) findViewById(R.id.Change_Button);

        //handle change button
        change_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra(String.valueOf(R.string.name),name_field.getText().toString());
                intent.putExtra(String.valueOf(R.string.age),Integer.parseInt(age_field.getText().toString()));
                intent.putExtra(String.valueOf(R.string.phone_number),phone_field.getText().toString());
                intent.putExtra(String.valueOf(R.string.email),email_field.getText().toString());
                intent.putExtra(String.valueOf(R.string.change),true);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }


}