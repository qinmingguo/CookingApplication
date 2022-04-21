package comp4905.carleton.cookingapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;

public class ReadMenuActivity extends AppCompatActivity {

    private TextView title_text_view,author_text_view;
    private LinearLayout ingredient_layout,process_layout;
    private String[] ingredient,process;
    private MaterialToolbar topAppBar;
    private Button cooking_mode_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_menu);

        topAppBar = (MaterialToolbar) findViewById(R.id.topAppBar_Menu);
        setSupportActionBar(topAppBar);

        topAppBar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        title_text_view = (TextView)findViewById(R.id.title_field_read);
        title_text_view.setText(this.getIntent().getStringExtra(String.valueOf(R.string.title)));
        author_text_view = (TextView)findViewById(R.id.author_field_read);
        author_text_view.setText(this.getIntent().getStringExtra(String.valueOf(R.string.author)));
        ingredient_layout = (LinearLayout)findViewById(R.id.ingredient_field_read);
        process_layout = (LinearLayout)findViewById(R.id.process_field_read);

        ingredient = this.getIntent().getStringArrayExtra(String.valueOf(R.string.ingredient));
        process = this.getIntent().getStringArrayExtra(String.valueOf(R.string.process));
        cooking_mode_button = findViewById(R.id.cooking_model_button);

        for(int i =0;i<ingredient.length;i++){
            if(ingredient[i]!="null"&&ingredient[i]!=null){
                TextView new_text_field = new TextView(ReadMenuActivity.this);
                new_text_field.setText(ingredient[i]);
                new_text_field.setTextSize(20);
                ingredient_layout.addView(new_text_field);
            }else{
                break;
            }
        }

        for(int i =0;i<process.length;i++){
            if(process[i]!="null"&&process[i]!=null){
                TextView new_text_field = new TextView(ReadMenuActivity.this);
                new_text_field.setText((i+1)+". "+process[i]);
                new_text_field.setTextSize(20);
                process_layout.addView(new_text_field);
            }else{
                break;
            }
        }

        cooking_mode_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCookingMode();
            }
        });
    }

    public void startCookingMode(){
        Intent i = new Intent(this, CookingModeActivity.class);
        i.putExtra(String.valueOf(R.string.process),process);
        i.putExtra(String.valueOf(R.string.ingredient),ingredient);
        startActivity(i);
    }

}