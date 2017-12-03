package life.sucks.org.structurednotepad;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import life.sucks.org.structurednotepad.database.NoteBaseHelper;
import life.sucks.org.structurednotepad.database.NoteCursorWrapper;
import life.sucks.org.structurednotepad.database.NoteDbSchema.NoteTable;

public class NoteLab {

    private static NoteLab sNoteLab;

    //private List<Note> mNotes;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static NoteLab get(Context context){
        if (sNoteLab == null){
            sNoteLab = new NoteLab(context);
        }
        return sNoteLab;
    }

    private NoteLab(Context context){
        mContext = context.getApplicationContext();
        mDatabase = new NoteBaseHelper(mContext).getWritableDatabase();
        //mNotes = new ArrayList<>();
        /*for (int i=0; i < 100; i++){
            Note note = new Note();
            note.setTitle("Note #"+i);
            note.setContent("Note #" + i + "'s Content");
            mNotes.add(note);
        }*/
    }

    public void addNote(Note n){
        //mNotes.add(n);
        ContentValues values = getContentValues(n);

        mDatabase.insert(NoteTable.NAME, null, values);
    }

    public void deleteNote(Note note){

        mDatabase.delete(NoteTable.NAME,
                NoteTable.Cols.UUID + " = ?",
                new String[] { note.getId().toString() });
        File temp_file = getPhotoFile(note);
        if (temp_file.exists() || temp_file != null){
            temp_file.delete();
        }
    }

    public List<Note> getNotes(){
        //return mNotes;
        //return new ArrayList<>();
        List<Note> notes = new ArrayList<>();

        NoteCursorWrapper cursor = queryNotes(null, null);

        try {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                notes.add(cursor.getNote());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return notes;
    }

    public Note getNote(UUID id){
        /*for (Note note : mNotes){
            if (note.getId().equals(id)){
                return note;
            }
        }*/
        //return null;
        NoteCursorWrapper cursor = queryNotes(
                NoteTable.Cols.UUID + " = ?",
                new String[] { id.toString() }
        );

        try {
            if (cursor.getCount() == 0){
                return null;
            }

            cursor.moveToFirst();
            return cursor.getNote();
        } finally {
            cursor.close();
        }
    }

    public File getPhotoFile(Note note){
        File externalFilesDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (externalFilesDir == null){
            return null;
        }

        return new File(externalFilesDir, note.getPhotoFileName());
    }

    public void updateNote(Note note){
        String uuidString = note.getId().toString();
        ContentValues values = getContentValues(note);

        mDatabase.update(NoteTable.NAME, values,
                NoteTable.Cols.UUID + " = ?",
                new String[]{ uuidString });
    }

    private static ContentValues getContentValues(Note note){
        ContentValues values = new ContentValues();
        values.put(NoteTable.Cols.UUID, note.getId().toString());
        values.put(NoteTable.Cols.TITLE, note.getTitle());
        values.put(NoteTable.Cols.DATE, note.getDate().getTime());
        values.put(NoteTable.Cols.CONTENT, note.getContent());
        values.put(NoteTable.Cols.LOCKED, note.isLocked() ? 1 : 0);

        return values;
    }

    private /*Cursor*/NoteCursorWrapper queryNotes(String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(
                NoteTable.NAME,
                null, //Columns - null select all columns
                whereClause,
                whereArgs,
                null, // Group by
                null, // Having
                null // Order by
        );
        //return cursor;
        return new NoteCursorWrapper(cursor);
    }

}
