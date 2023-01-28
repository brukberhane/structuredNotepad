package life.sucks.org.structurednotepad.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.List;
import java.util.UUID;

import life.sucks.org.structurednotepad.Fragments.NoteFragment;
import life.sucks.org.structurednotepad.Note;
import life.sucks.org.structurednotepad.NoteLab;
import life.sucks.org.structurednotepad.R;

public class NotePagerActivity extends AppCompatActivity implements NoteFragment.Callbacks{

    private static final String EXTRA_NOTE_ID = "com.bignerdranch.android.criminalintent.crime_id";

    private ViewPager mViewPager;
    private List<Note> mNotes;

    public static Intent newIntent(Context packageContext, UUID noteId){
        Intent intent = new Intent(packageContext, NotePagerActivity.class);
        intent.putExtra(EXTRA_NOTE_ID, noteId);
        return intent;
    }

    public void onNoteUpdated(Note note){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_pager);

        UUID noteId = (UUID) getIntent().getSerializableExtra(EXTRA_NOTE_ID);

        mViewPager = (ViewPager) findViewById(R.id.activity_note_pager_view_pager);

        mNotes = NoteLab.get(this).getNotes();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Note note = mNotes.get(position);
                return NoteFragment.newInstance(note.getId());
            }

            @Override
            public int getCount() {
                return mNotes.size();
            }
        });

        for (int i=0; i < mNotes.size(); i++){
            if (mNotes.get(i).getId().equals(noteId)){
                mViewPager.setCurrentItem(i);
                break;
            }
        }

    }

}
