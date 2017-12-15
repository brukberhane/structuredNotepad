package life.sucks.org.structurednotepad.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.widget.Toast;

import java.util.UUID;

import life.sucks.org.structurednotepad.Fragments.FingerprintFragment;
import life.sucks.org.structurednotepad.Fragments.NoteFragment;
import life.sucks.org.structurednotepad.Fragments.PasswordFragment;
import life.sucks.org.structurednotepad.R;
import life.sucks.org.structurednotepad.SingleFragmentActivity;

public class NotePasswordActivity extends SingleFragmentActivity {

    private static final String PREFERENCES = "life.sucks.org.structurednotepad.preferences_name";
    private static final String PASSWORD_VALUE = "life.sucks.org.structurednotepad.pass_value";
    private static final String PASSWORD_TYPE = "life.sucks.org.structurednotepad.pass_type";
    @SuppressWarnings("FieldCanBeLocal")
    private static String EXTRA_NOTE_ID = "NOTE_ID";


    public static Intent newInstance(Context packageContext, UUID noteID){
        Intent intent = new Intent(packageContext, NotePasswordActivity.class);
        intent.putExtra(EXTRA_NOTE_ID, noteID);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        return new PasswordFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        //TODO: Get the password Registration fragment working.
        SharedPreferences preferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PASSWORD_TYPE, "num");
        editor.apply();

        //noinspection ConstantConditions
        if (preferences.getString(PASSWORD_VALUE, "") == null){

            Toast.makeText(this, "Init setup here", Toast.LENGTH_SHORT).show();

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            FingerprintFragment fing = FingerprintFragment.newInstance("Request Fingerprint");
            fing.show(getSupportFragmentManager(), "Request Fingerprint");
        }

    }
}
