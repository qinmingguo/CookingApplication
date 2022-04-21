package comp4905.carleton.cookingapplication;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.gridlayout.widget.GridLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;

public class AddMenuActivity extends AppCompatActivity {
    private ImageButton add_ingredient_button,add_process_button;
    private EditText title_field;
    private Button confirm_button;
    private MaterialToolbar topAppBar;
    private ArrayList<EditText> ingredient_text_field_list;
    private ArrayList<EditText> process_text_field_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_menu);

        ingredient_text_field_list = new ArrayList<EditText>();
        process_text_field_list = new ArrayList<EditText>();

        add_ingredient_button = (ImageButton) findViewById(R.id.Add_Ingredient_Button);

        add_process_button = (ImageButton) findViewById(R.id.Add_Process_Button);

        title_field = (EditText) findViewById(R.id.Title_text_field);

        ingredient_text_field_list.add((EditText) findViewById(R.id.Ingredient_text_field_1));

        process_text_field_list.add((EditText) findViewById(R.id.Process_text_field_1));


        confirm_button = (Button) findViewById(R.id.confirm_button_add);

        topAppBar = (MaterialToolbar) findViewById(R.id.topAppBar_add);
        setSupportActionBar(topAppBar);

        //stop activity and back to MainActivity
        topAppBar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //add more ingredient
        add_ingredient_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                LinearLayout ingredient_layout = (findViewById(R.id.Ingredient_Space));
                EditText new_ingredient_field = new EditText(AddMenuActivity.this);
                ingredient_text_field_list.add(new_ingredient_field);
                ingredient_layout.addView(new_ingredient_field);
            }
        });

        //add more process
        add_process_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                LinearLayout process_layout = (findViewById(R.id.Process_Space));
                EditText new_ingredient_field = new EditText(AddMenuActivity.this);
                process_text_field_list.add(new_ingredient_field);
                process_layout.addView(new_ingredient_field);
            }
        });

        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AddMenuActivity.this,"Confirm is pressed",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra(String.valueOf(R.string.title),title_field.getText().toString());
                String[] ingredient = new String[ingredient_text_field_list.size()];
                for(int i =0;i<ingredient_text_field_list.size();i++){
                    ingredient[i]=ingredient_text_field_list.get(i).getText().toString();
                }
                intent.putExtra(String.valueOf(R.string.ingredient),ingredient);
                String[] process = new String[process_text_field_list.size()];
                for(int i =0;i<process_text_field_list.size();i++){
                    process[i]=process_text_field_list.get(i).getText().toString();
                }
                intent.putExtra(String.valueOf(R.string.process),process);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }

}