package life.sucks.org.structurednotepad.Activities;

import android.content.Context;
import android.content.Intent;
import androidx.fragment.app.Fragment;

import java.util.UUID;

import life.sucks.org.structurednotepad.Fragments.NoteFragment;
import life.sucks.org.structurednotepad.SingleFragmentActivity;

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
