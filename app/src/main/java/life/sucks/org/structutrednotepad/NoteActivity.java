package life.sucks.org.structutrednotepad;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.UUID;

public class NoteActivity extends SingleFragmentActivity {

    private static final String EXTRA_NOTE_ID = "life.sucks.org.structurednotepad.note_id";

    @Override
    protected Fragment createFragment(){
        //return new NoteFragment();
        UUID noteId = (UUID) getIntent().getSerializableExtra(EXTRA_NOTE_ID);
        return NoteFragment.newInstance(noteId);
    }

    public static Intent newIntent(Context packageContext, UUID noteId){
        Intent intent = new Intent(packageContext, NoteActivity.class);
        intent.putExtra(EXTRA_NOTE_ID, noteId);
        return intent;
    }

}
