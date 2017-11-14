package life.sucks.org.structutrednotepad;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class NotePasswordActivity extends AppCompatActivity {

    //Note: This is redundant code
    private static final String EXTRA_NOTE = "life.sucks.org.structutrednotepad.locked_value";

    public static Intent newInstance(Context packageContext){
        return new Intent(packageContext, NotePasswordActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//            FingerprintFragment.newInstance();
//        }

        FingerprintFragment.newInstance("mlem");
    }
}
