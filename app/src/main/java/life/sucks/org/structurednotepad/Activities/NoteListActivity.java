package life.sucks.org.structurednotepad.Activities;

import android.content.Intent;
import android.support.v4.app.Fragment;

import life.sucks.org.structurednotepad.Fragments.NoteFragment;
import life.sucks.org.structurednotepad.Fragments.NoteListFragment;
import life.sucks.org.structurednotepad.Note;
import life.sucks.org.structurednotepad.R;
import life.sucks.org.structurednotepad.SingleFragmentActivity;

public class NoteListActivity extends SingleFragmentActivity implements NoteListFragment.Callbacks,
                NoteFragment.Callbacks, NoteListFragment.CBacks{

    @Override
    protected Fragment createFragment(){
        return new NoteListFragment();
    }

    @Override
    protected int getLayoutResId(){
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onNoteSelected(Note note){
        if (findViewById(R.id.detail_fragment_container) == null){
            Intent intent = NotePagerActivity.newIntent(this, note.getId());
            startActivity(intent);
        } else {
            Fragment newDetail = NoteFragment.newInstance(note.getId());

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }
    }

    public void onCreated(){
        NoteListFragment listFragment = (NoteListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);

        if (findViewById(R.id.detail_fragment_container) == null){
            //This means it's not a tablet
            listFragment.setIsTab(false);
        } else {
            //This means that it is a tablet
            listFragment.setIsTab(true);
        }
    }

    public void onNoteUpdated(Note note){
        NoteListFragment listFragment = (NoteListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
    }

}
