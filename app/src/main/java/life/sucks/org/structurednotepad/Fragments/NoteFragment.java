package life.sucks.org.structurednotepad.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.text.format.DateFormat;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.util.Date;
import java.util.UUID;

import life.sucks.org.structurednotepad.Note;
import life.sucks.org.structurednotepad.NoteLab;
import life.sucks.org.structurednotepad.PictureUtils;
import life.sucks.org.structurednotepad.R;

public class NoteFragment extends Fragment{

    private static final String ARG_NOTE_ID = "note_id";
    private static final String DIALOG_DATE = "DialogDate";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_PHOTO = 2;

    private Note mNote;
    private File mPhotoFile;
    private EditText mTitleField;
    private Button mDateButton;
    private EditText mContentField;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private PackageManager packageManager;
    private Callbacks mCallBacks;

    /**
     * Required interface for hosting activities
     */
    public interface Callbacks{
        void onNoteUpdated(Note note);
    }

    public static NoteFragment newInstance(UUID noteId){
        Bundle args = new Bundle();
        args.putSerializable(ARG_NOTE_ID, noteId);

        NoteFragment fragment = new NoteFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        mCallBacks = (Callbacks)activity;
    }

    @Override
    public void onCreate(Bundle savedInstaneState){
        super.onCreate(savedInstaneState);
        setHasOptionsMenu(true);
        //mNote = new Note();
        //UUID noteId = (UUID) getActivity().getIntent().getSerializableExtra(NoteActivity.EXTRA_NOTE_ID);
        UUID noteId = (UUID) getArguments().getSerializable(ARG_NOTE_ID);
        mNote = NoteLab.get(getActivity()).getNote(noteId);
        mPhotoFile = NoteLab.get(getActivity()).getPhotoFile(mNote);
        packageManager = getActivity().getPackageManager();

        //NOTE: This is for The URI exposure crashes on Android 7 and up.
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
    }

    @Override
    public void onPause(){
        super.onPause();

        NoteLab.get(getActivity()).updateNote(mNote);
    }

    @Override
    public void onDetach(){
        super.onDetach();
        mCallBacks = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_note, container, false);

        mTitleField = (EditText) v.findViewById(R.id.note_title);
        mTitleField.setText(mNote.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //This is intentionally left blank
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mNote.setTitle(s.toString());
                updateNote();
            }

            @Override
            public void afterTextChanged(Editable s) {
                //This too
            }
        });

        mDateButton = (Button) v.findViewById(R.id.note_date);
        //mDateButton.setText(mNote.getDate().toString());
        mDateButton.requestFocus();
        updateDate();
        //mDateButton.setEnabled(false);
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                //DatePickerFragment dialog = new DatePickerFragment();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mNote.getDate());
                dialog.setTargetFragment(NoteFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mContentField = (EditText) v.findViewById(R.id.note_content);
        mContentField.setText(mNote.getContent());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mContentField.setNestedScrollingEnabled(true);
        }
        mContentField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mNote.setContent(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mPhotoButton = (ImageButton) v.findViewById(R.id.note_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        boolean canTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);

        if (canTakePhoto){
            Uri uri = Uri.fromFile(mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });


        mPhotoView = (ImageView) v.findViewById(R.id.note_photo);
        updatePhotoView();
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                ImageFragment dialog = ImageFragment.newInstance(mPhotoFile.getPath());
                dialog.show(fm, "Show Image");
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode != Activity.RESULT_OK){
            return;
        }

        if (requestCode == REQUEST_DATE){
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mNote.setDate(date);
            updateNote();
            updateDate();
        } else if (requestCode == REQUEST_PHOTO){
            updatePhotoView();
        }
    }

    private void updateNote(){
        NoteLab.get(getActivity()).updateNote(mNote);
        mCallBacks.onNoteUpdated(mNote);
    }

    private void updateDate() {
        mDateButton.setText(android.text.format.DateFormat
                .format("EEEE, MMM dd, yyyy", mNote.getDate()));
    }

    /*@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.note_menu, menu);
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_item_delete_note:
                NoteLab.get(getActivity()).deleteNote(mNote);
                getActivity().finish();
                return true;
            case R.id.menu_item_share_note:
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getShareNote());
                i.putExtra(Intent.EXTRA_SUBJECT, "Send "+mNote.getTitle());
                i = Intent.createChooser(i, "Send "+mNote.getTitle());
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private String getShareNote(){
        String share_note;
        share_note = getString(R.string.note_share_contents, mNote.getTitle(),
                DateFormat.format("EEEE, MMM dd, yyyy", mNote.getDate()), mNote.getContent());
        return share_note;
    }

    private void updatePhotoView(){
        if (mPhotoFile == null || !mPhotoFile.exists()){
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }

}
