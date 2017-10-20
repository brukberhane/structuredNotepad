package life.sucks.org.structutrednotepad;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import java.util.List;

public class NoteListFragment extends Fragment{

    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";

    private RecyclerView mNoteRecyclerView;
    private Toolbar tb;
    private NoteAdapter mAdapter;
    private boolean mSubtitleVisible;
    private Note mNote;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_note_list, container, false);

        mNoteRecyclerView = (RecyclerView) view.findViewById(R.id.note_recycler_view);
        //mNoteRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //mNoteRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        mNoteRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, 1));

        tb = (Toolbar) view.findViewById(R.id.note_list_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(tb);

        if (savedInstanceState != null){
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        updateUI();

        return view;
    }

    private void updateUI(){
        NoteLab noteLab = NoteLab.get(getActivity());
        List<Note> notes = noteLab.getNotes();

        if (mAdapter == null) {
            mAdapter = new NoteAdapter(notes);
            mNoteRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setNotes(notes);
            mAdapter.notifyDataSetChanged();
        }

        updateSubtitle();
    }

    private class NoteHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTitleTextView;
        private TextView mDateTextView;
        private TextView mContentTextView;
        private Note mNote;

        public NoteHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    registerForContextMenu(view);
                    setTempNote(mNote);
                    return false;
                }
            });

            mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_note_title_text_view);
            mDateTextView = (TextView) itemView.findViewById(R.id.list_item_note_date_text_view);
            mContentTextView = (TextView) itemView.findViewById(R.id.list_item_note_content_text_view);

        }

        public void bindNote(Note note){
            mNote = note;
            mTitleTextView.setText(mNote.getTitle());
            mDateTextView.setText(DateFormat.format("EEE, MMM dd", mNote.getDate()));
            if (!note.isLocked()){
                if(1 == 1) {
                    mContentTextView.setText(mNote.getContent());
                }
            }
        }

        @Override
        public void onClick(View v) {
            //Toast.makeText(getActivity(), mNote.getTitle()+" clicked!", Toast.LENGTH_SHORT).show();
            //Intent intent = new Intent(getActivity(), NoteActivity.class);
            //Intent intent = NoteActivity.newIntent(getActivity(), mNote.getId());
            Intent inten = NotePagerActivity.newIntent(getActivity(), mNote.getId());
            startActivity(inten);
        }

    }

    private class NoteAdapter extends RecyclerView.Adapter<NoteHolder>{

        private List<Note> mNotes;

        public NoteAdapter(List<Note> notes) {
            mNotes = notes;
        }

        @Override
        public NoteHolder onCreateViewHolder(ViewGroup parent, int viewType){
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_note, parent, false);

            return new NoteHolder(view);
        }

        @Override
        public void onBindViewHolder(NoteHolder holder, int position){
            Note note = mNotes.get(position);
            //holder.mTitleTextView.setText(note.getTitle());
            holder.bindNote(note);
        }

        @Override
        public int getItemCount(){
            return mNotes.size();
        }

        public void setNotes(List<Note> notes){
            mNotes = notes;
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        updateUI();
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_note_list, menu);

        MenuItem subtitleItem = menu.findItem(R.id.menu_item_show_subtitle);
        if (mSubtitleVisible){
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_item_new_note:
                Note note = new Note();
                NoteLab.get(getActivity()).addNote(note);
                Intent intent = NotePagerActivity.newIntent(getActivity(), note.getId());
                startActivity(intent);
                return true;
            case R.id.menu_item_show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSubtitle(){
        NoteLab noteLab = NoteLab.get(getActivity());
        int noteSize = noteLab.getNotes().size();
        //String subtitle = getString(R.string.subtitle_format, noteSize);
        String subtitle = getResources().getQuantityString(R.plurals.subtitle_plural, noteSize, noteSize);

        //if (!mSubtitleVisible){
          //  subtitle = null;
        //}

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        //noinspection ConstantConditions
        //activity.getSupportActionBar().setSubtitle(subtitle);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, v, menuInfo);

        getActivity().getMenuInflater().inflate(R.menu.fragment_list_context_menu, menu);

        MenuItem lock = menu.findItem(R.id.context_menu_item_lock).setChecked(mNote.isLocked());

    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.context_menu_item_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Are you sure?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NoteLab.get(getActivity()).deleteNote(mNote);
                                updateUI();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).create().setTitle("Are you sure?");
                builder.show();
                return true;
            case R.id.context_menu_item_share:
                String share = getString(R.string.note_share_contents, mNote.getTitle(),
                        DateFormat.format("EEEE, MMM dd, yyyy", mNote.getDate()),
                        mNote.getContent());
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, share);
                i.putExtra(Intent.EXTRA_SUBJECT, "Send " + mNote.getTitle());
                i = Intent.createChooser(i, "Send "+ mNote.getTitle());
                startActivity(i);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void setTempNote(Note note){
        mNote = note;
    }

}
