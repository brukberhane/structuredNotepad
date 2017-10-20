package life.sucks.org.structutrednotepad;

import java.util.Date;
import java.util.UUID;

public class Note {

    private UUID mId;
    private String mTitle;
    private Date mDate;
    private String mContent;
    private boolean mLocked;

    public Note(){
        //Generate unique identifier
        //mId = UUID.randomUUID();
        //mDate = new Date();
        this(UUID.randomUUID());
    }

    public Note(UUID id){
        mId = id;
        mDate = new Date();
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public String getPhotoFileName(){
        return "IMG_"+getId().toString()+".jpg";
    }

    public boolean isLocked() {
        return mLocked;
    }

    public void setLocked(boolean locked) {
        mLocked = locked;
    }
}
