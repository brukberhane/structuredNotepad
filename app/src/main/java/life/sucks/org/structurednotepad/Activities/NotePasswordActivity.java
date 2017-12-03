package life.sucks.org.structurednotepad.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.UUID;

import life.sucks.org.structurednotepad.Fragments.FingerprintFragment;
import life.sucks.org.structurednotepad.R;

public class NotePasswordActivity extends AppCompatActivity {

    //Note: This is redundant code
    private static final String EXTRA_NOTE = "life.sucks.org.structutrednotepad.locked_value";
    @SuppressWarnings("FieldCanBeLocal")
    private static String EXTRA_NOTE_ID = "NOTE_ID";


    public static Intent newInstance(Context packageContext, UUID noteID){
        Intent intent = new Intent(packageContext, NotePasswordActivity.class);
        intent.putExtra(EXTRA_NOTE_ID, noteID);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            FingerprintFragment fing = FingerprintFragment.newInstance("Request Fingerprint");
            fing.show(getSupportFragmentManager(), "Request Fingerprint");
        }
        //TODO: Get a working password activity up.



    }
}
