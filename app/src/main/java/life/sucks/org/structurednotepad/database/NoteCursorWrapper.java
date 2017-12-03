package life.sucks.org.structurednotepad.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Date;
import java.util.UUID;

import life.sucks.org.structurednotepad.Note;
import life.sucks.org.structurednotepad.database.NoteDbSchema.NoteTable;

public class NoteCursorWrapper extends CursorWrapper{

    public NoteCursorWrapper(Cursor cursor){
        super(cursor);
    }

    public Note getNote(){
        String uuidString = getString(getColumnIndex(NoteTable.Cols.UUID));
        String title = getString(getColumnIndex(NoteTable.Cols.TITLE));
        long date = getLong(getColumnIndex(NoteTable.Cols.DATE));
        String content = getString(getColumnIndex(NoteTable.Cols.CONTENT));
        int isLocked = getInt(getColumnIndex(NoteTable.Cols.LOCKED));

        Note note = new Note(UUID.fromString(uuidString));
        note.setTitle(title);
        note.setDate(new Date(date));
        note.setContent(content);
        note.setLocked(isLocked != 0);

        return note;
    }

}
