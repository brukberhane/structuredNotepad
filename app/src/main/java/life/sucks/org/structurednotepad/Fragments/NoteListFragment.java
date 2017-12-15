package life.sucks.org.structurednotepad.Fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.format.DateFormat;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import java.util.List;
import java.util.UUID;

import life.sucks.org.structurednotepad.Note;
import life.sucks.org.structurednotepad.NoteLab;
import life.sucks.org.structurednotepad.Activities.NotePagerActivity;
import life.sucks.org.structurednotepad.Activities.NotePasswordActivity;
import life.sucks.org.structurednotepad.R;

public class NoteListFragment extends Fragment{

    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";
    private static final int REQUEST_PASSWORD_FOR_CHANGE_LOCK = 2;
    private static final int REQUEST_PASSWORD_FOR_ACCESS_LOCK = 3;
    @SuppressWarnings("FieldCanBeLocal")
    private static String EXTRA_NOTE_ID = "NOTE_ID";

    private RecyclerView mNoteRecyclerView;
    private TextView EmptyView;
    @SuppressWarnings("FieldCanBeLocal")
    private Toolbar tb;
    private NoteAdapter mAdapter;
    private boolean mSubtitleVisible;
    private Note mNote;
    private Callbacks mCallbacks;
    private CBacks mCBacks;
    private boolean isTab;


    /**
     * Required interface for hosting activities.
     */
    public interface Callbacks{
        void onNoteSelected(Note note);
    }

    /**
     * Interface for adjusting list view for tabs
     */
    public interface CBacks {
        void onCreated();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        mCallbacks = (Callbacks) activity;
        mCBacks = (CBacks) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        mCBacks.onCreated();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_note_list, container, false);

        Button addNoteButton = view.findViewById(R.id.add_note_button_fragment_note_list);
        mNoteRecyclerView = view.findViewById(R.id.note_recycler_view);
        EmptyView = view.findViewById(R.id.empty_view);

        //mNoteRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //mNoteRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        if (!isTab) {
            mNoteRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, 1));
        } else {
            mNoteRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }

        tb = view.findViewById(R.id.note_list_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(tb);

        if (savedInstanceState != null){
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        addNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Note note = new Note();
                NoteLab.get(getActivity()).addNote(note);
                updateUI();
                mCallbacks.onNoteSelected(note);
            }
        });

        updateUI();

        return view;
    }

    public void updateUI(){
        NoteLab noteLab = NoteLab.get(getActivity());
        List<Note> notes = noteLab.getNotes();

        if (mAdapter == null) {
            mAdapter = new NoteAdapter(notes);
            mNoteRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setNotes(notes);
            mAdapter.notifyDataSetChanged();
        }

        if (mAdapter.mNotes.isEmpty()){
            mNoteRecyclerView.setVisibility(View.GONE);
            EmptyView.setVisibility(View.VISIBLE);
        } else {
            mNoteRecyclerView.setVisibility(View.VISIBLE);
            EmptyView.setVisibility(View.GONE);
        }

        updateSubtitle();
    }

    private class NoteHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTitleTextView;
        private TextView mDateTextView;
        private TextView mContentTextView;
        private Note mNote;

        NoteHolder(View itemView){
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

        void bindNote(Note note){
            mNote = note;
            mTitleTextView.setText(mNote.getTitle());
            mDateTextView.setText(DateFormat.format("EEE, MMM dd", mNote.getDate()));

            if (!isTab) {
                if (!note.isLocked()) {
                    mContentTextView.setText(mNote.getContent());
                } else {
                    mContentTextView.setText("");
                }
            }

        }

        @Override
        public void onClick(View v) {
            //Toast.makeText(getActivity(), mNote.getTitle()+" clicked!", Toast.LENGTH_SHORT).show();
            //Intent intent = new Intent(getActivity(), NoteActivity.class);
            //Intent intent = NoteActivity.newIntent(getActivity(), mNote.getId());
            if (!mNote.isLocked()) {
                mCallbacks.onNoteSelected(mNote);
            } else {
                Intent fingerprint = NotePasswordActivity.newInstance(getActivity(), mNote.getId());
                startActivityForResult(fingerprint, REQUEST_PASSWORD_FOR_ACCESS_LOCK);
            }
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
    public void onDetach(){
        super.onDetach();
        mCallbacks = null;
        mCBacks = null;
    }

    //NOTE: This code is not going to be used.
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
//        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.fragment_note_list, menu);
//
//        MenuItem subtitleItem = menu.findItem(R.id.menu_item_show_subtitle);
//        if (mSubtitleVisible){
//            subtitleItem.setTitle(R.string.hide_subtitle);
//        } else {
//            subtitleItem.setTitle(R.string.show_subtitle);
//        }
//    }

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

        menu.findItem(R.id.context_menu_item_lock).setChecked(mNote.isLocked());

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
            case R.id.context_menu_item_lock:
                Intent fingerprint = NotePasswordActivity.newInstance(getActivity(), mNote.getId());
                //new Intent(getActivity(), NotePasswordActivity.class);
                //fingerprint.putExtra(EXTRA_NOTE_ID, mNote.getId());
                startActivityForResult(fingerprint, REQUEST_PASSWORD_FOR_CHANGE_LOCK);
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        if (resultCode == Activity.RESULT_OK){

            switch (requestCode){

                case REQUEST_PASSWORD_FOR_CHANGE_LOCK:

                    mNote = NoteLab.get(getActivity())
                            .getNote((UUID)intent.getSerializableExtra(EXTRA_NOTE_ID));

                    mNote.setLocked(!mNote.isLocked());
                    NoteLab.get(getActivity()).updateNote(mNote);

                    updateUI();

                    break;

                case REQUEST_PASSWORD_FOR_ACCESS_LOCK:

                    mNote = NoteLab.get(getActivity())
                            .getNote((UUID)intent.getSerializableExtra(EXTRA_NOTE_ID));

                    mCallbacks.onNoteSelected(mNote);

                    break;

                default:

                    break;
            }

        }
    }

    public void setTempNote(Note note){
        mNote = note;
    }

    public void setIsTab(boolean n){
        isTab = n;
    }

}
