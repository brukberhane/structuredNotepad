package life.sucks.org.structurednotepad.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.UUID;

import life.sucks.org.structurednotepad.R;

public class PasswordFragment extends Fragment {
    private static final String PREFERENCES = "life.sucks.org.structurednotepad.preferences_name";
    private static final String PASSWORD_VALUE = "life.sucks.org.structurednotepad.pass_value";
    private static final String PASSWORD_TYPE = "life.sucks.org.structurednotepad.pass_type";
    private static String EXTRA_NOTE_ID = "NOTE_ID";

    private Toolbar mToolbar;
    private String mPassword;
    private String mPassType;
    private EditText mPass;
    private UUID noteId;

    public PasswordFragment(){
        //Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        assert mToolbar == null;

        SharedPreferences preferences = getActivity().getSharedPreferences(PREFERENCES,
                Context.MODE_PRIVATE);
        mPassword = preferences.getString(PASSWORD_VALUE, "");
        mPassType = preferences.getString(PASSWORD_TYPE, "");
        noteId = (UUID)getActivity().getIntent().getSerializableExtra(EXTRA_NOTE_ID);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)


    {
        View v  = inflater.inflate(R.layout.fragment_password, container,false);

        mToolbar = v.findViewById(R.id.toolbar_fragment_password);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        mToolbar.setTitle("Password");

        mPass = v.findViewById(R.id.edit_text_fragment_password);

        assert mPassword != null;
        assert mPassType != null;

        if (mPassType.equals("num")){
            mPass.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        }

        mPass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                if (mPass.getText().toString().equals(mPassword)){
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(EXTRA_NOTE_ID, noteId);
                    getActivity().setResult(Activity.RESULT_OK, returnIntent);
                    getActivity().finish();
                }

                return true;
            }
        });

        mPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Nothing here
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mPass.getText().toString().equals(mPassword)){
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(EXTRA_NOTE_ID, noteId);
                    getActivity().setResult(Activity.RESULT_OK, returnIntent);
                    getActivity().finish();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //Also nothing
            }
        });

        return v;
    }

}
