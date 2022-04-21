package comp4905.carleton.cookingapplication;


import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


public class CookingModeActivity extends AppCompatActivity {

    private static final int APP_PERMISSIONS_RECORD_AUDIO = 1;
    private MediaRecorder recorder;

    private TextView process_field;
    private FloatingActionButton back_button;
    private Button previous_button,next_button;
    private int current_step;
    private ProgressBar progressBar;
    private String[] process,ingredient;
    private ArrayList<String> page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cooking_mode);

        process_field = findViewById(R.id.current_process_field);
        back_button = findViewById(R.id.back_button_cooking);
        previous_button = findViewById(R.id.previous_button);
        next_button = findViewById(R.id.next_button);
        process = this.getIntent().getStringArrayExtra(String.valueOf(R.string.process));
        ingredient = this.getIntent().getStringArrayExtra(String.valueOf(R.string.ingredient));
        progressBar = findViewById(R.id.progress_bar);

        //initial page index
        current_step = 0;

        //initial page content
        page = new ArrayList<String>();
        String ingredient_page = "Please prepare following ingredient...\n";
        for(int i =0;i<ingredient.length;i++){
            ingredient_page+=ingredient[i];
            ingredient_page+="\n";
        }
        page.add(ingredient_page);
        for(int i =0;i<process.length;i++){
            page.add((i+1)+". "+process[i]);
        }
        page.add("Finish Cooking!");
        System.out.println(page.size());
        process_field.setText(page.get(current_step));
        progressBar.setProgress(100/page.size(),true);

        //set handler for back button
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //set handler for previous button
        previous_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(current_step==1){
                    current_step--;
                    process_field.setText(page.get(current_step));
                    previous_button.setEnabled(false);
                    previous_button.setBackgroundColor(getResources().getColor(R.color.button_color_unable));
                    progressBar.incrementProgressBy(-100/page.size());
                }else if(current_step ==page.size()-1){
                    current_step--;
                    process_field.setText(page.get(current_step));
                    next_button.setEnabled(true);
                    next_button.setBackgroundColor(getResources().getColor(R.color.button_color_enable));
                    progressBar.incrementProgressBy(-100/page.size());
                }else{
                    current_step--;
                    process_field.setText(page.get(current_step));
                    progressBar.incrementProgressBy(-100/page.size());
                }
            }
        });

        //set handler for next button
        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(current_step==0){
                    current_step++;
                    process_field.setText(page.get(current_step));
                    previous_button.setEnabled(true);
                    previous_button.setBackgroundColor(getResources().getColor(R.color.button_color_enable));
                    progressBar.incrementProgressBy(100/page.size());
                }else if(current_step ==page.size()-2){
                    current_step++;
                    process_field.setText(page.get(current_step));
                    next_button.setEnabled(false);
                    next_button.setBackgroundColor(getResources().getColor(R.color.button_color_unable));
                    progressBar.incrementProgressBy(100/page.size());
                }else{
                    current_step++;
                    process_field.setText(page.get(current_step));
                    progressBar.incrementProgressBy(100/page.size());
                }
            }
        });


    }




}