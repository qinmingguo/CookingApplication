package comp4905.carleton.cookingapplication;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Locale;


public class CookingModeActivity extends AppCompatActivity {
    private  final String TAG = this.getClass().getSimpleName() + " @" + System.identityHashCode(this);

    private static final int APP_PERMISSIONS_RECORD_AUDIO = 1;
    private SpeechRecognizer speechRecognizer;
    private TextView process_field;
    private FloatingActionButton back_button,mic_button;
    private Button previous_button,next_button;
    private int current_step;
    private ProgressBar progressBar;
    private String[] process,ingredient;
    private ArrayList<String> page;
    private boolean OnRecord ;

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},APP_PERMISSIONS_RECORD_AUDIO);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            checkPermission();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cooking_mode);

        OnRecord = false;
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        mic_button = findViewById(R.id.mic_button);
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

        //SpeechRecognizer


        final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, true);
        speechRecognizerIntent.putExtra(
                RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS,
                5000
        );
        speechRecognizerIntent.putExtra(
                RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,
                5000
        );
        speechRecognizerIntent.putExtra(
                RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS,
                5000
        );
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {
                Toast.makeText(CookingModeActivity.this,"ready to speech",Toast.LENGTH_SHORT);
                Log.i(TAG,"ready to recognizer");
            }

            @Override
            public void onBeginningOfSpeech() {
                Toast.makeText(CookingModeActivity.this,"begin to speech",Toast.LENGTH_SHORT);
                Log.i(TAG,"begin to recognizer");
            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {
                Log.i(TAG,"received buffer");

            }

            @Override
            public void onEndOfSpeech() {
                Log.i(TAG,"end to recognizer");
                if(OnRecord){
                    speechRecognizer.startListening(speechRecognizerIntent);
                }else{

                }
            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                Log.i(TAG,"get  result:"+data);
                if(data.contains("next")){
                    next_button.performClick();
                    Toast.makeText(CookingModeActivity.this,"Next Page",Toast.LENGTH_SHORT);
                    if(current_step == page.size()-1){
                        Log.i(TAG,"last page");
                        speechRecognizer.stopListening();
                    }else{
                        speechRecognizer.startListening(speechRecognizerIntent);
                    }
                }else if(data.contains("previous")){
                    previous_button.performClick();
                    Toast.makeText(CookingModeActivity.this,"Previous Page",Toast.LENGTH_SHORT);
                    speechRecognizer.startListening(speechRecognizerIntent);
                }else if(data.contains("end")||data.contains("and")){
                    Log.i(TAG,"end read");
                    speechRecognizer.stopListening();
                    finish();
                }else if(data.contains("stop")){
                    Log.i(TAG,"stop record");
                    mic_button.performClick();
                }else{
                    speechRecognizer.startListening(speechRecognizerIntent);
                }
            }

            @Override
            public void onPartialResults(Bundle bundle) {
            }

            @Override
            public void onEvent(int i, Bundle bundle) {
                Log.i(TAG,"on event");
            }
        });
        Log.i(TAG,"Start to record"+SpeechRecognizer.isRecognitionAvailable(this));
        AlertDialog.Builder builder = new AlertDialog.Builder(CookingModeActivity.this);
        builder.setMessage("Are you Ready?");
        builder.setPositiveButton("Ready", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                OnRecord = true;
                speechRecognizer.startListening(speechRecognizerIntent);
                mic_button.setImageResource(R.drawable.ic_baseline_mic_24);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                OnRecord = false;
                speechRecognizer.stopListening();
                mic_button.setImageResource(R.drawable.ic_baseline_mic_off_24);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        mic_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(OnRecord){
                    OnRecord = false;
                    speechRecognizer.stopListening();
                    mic_button.setImageResource(R.drawable.ic_baseline_mic_off_24);
                }else{
                    OnRecord = true;
                    speechRecognizer.startListening(speechRecognizerIntent);
                    mic_button.setImageResource(R.drawable.ic_baseline_mic_24);
                }
            }
        });
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        speechRecognizer.destroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == APP_PERMISSIONS_RECORD_AUDIO && grantResults.length > 0 ){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show();
        }
    }






}