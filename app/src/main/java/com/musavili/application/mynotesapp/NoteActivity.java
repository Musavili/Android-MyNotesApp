package com.musavili.application.mynotesapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

public class NoteActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();//the log messages use the class name as their tag.
    public static final String NOTE_POSITION = "com.musavili.application.mynotesapp.NOTE_POSITION";
    public static final String ORIGINAL_NOTE_COURSE_ID = "com.musavili.application.mynotesapp.ORIGINAL_NOTE_COURSE_ID";
    public static final String ORIGINAL_NOTE_TITLE = "com.musavili.application.mynotesapp.ORIGINAL_NOTE_TITLE";
    public static final String ORIGINAL_NOTE_TEXT = "com.musavili.application.mynotesapp.ORIGINAL_NOTE_TEXT";

    private NoteInfo mNote;
    private boolean mIsNewNote;
    private EditText mTextNoteTitle;
    private EditText mTextNoteText;
    private Spinner mSpinnerCourses;
    private int mNewNotePosition;
    private boolean mIsCancelling;
    private int POSITION_NOT_SET=-1;
    private String mOriginalNoteCourseId;
    private String mOriginalNoteTitle;
    private String mOriginalNoteText;
    private int mNotePosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //findViewById returns a view which needs to be cast. ctrl + shift + space puts the right cast type in the parenthesis
        mSpinnerCourses = (Spinner)findViewById(R.id.spinner_courses);

        List<CourseInfo> courses = DataManager.getInstance().getCourses();//create a list of courses from data manager(singleton)

        //we'll use the array(associated with lists&arrays) adapter to populate spinner
        //in this case the array adapter accepts a context, a resource and a list as the parameters
        ArrayAdapter<CourseInfo> adapterCourses = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,courses);
        //a spinner is associated with two resource files(the spinner items and the dropdown items)
        adapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);//R class provides important layout constants

        mSpinnerCourses.setAdapter(adapterCourses);//associate the spinner with the adapter

        readIntentExtras();//check for and read extras
        if(savedInstanceState == null) {
            saveOriginalNoteValues();
        }else{
            restoreOriginalNoteValues(savedInstanceState);
        }

        mTextNoteTitle = (EditText) findViewById(R.id.text_course);
        mTextNoteText = (EditText) findViewById(R.id.text_title);

        if(mIsNewNote) {//only displays the note if its note a new note
            createNewNote();
        }else
        displayNote(mSpinnerCourses, mTextNoteTitle, mTextNoteText);//update the referenced views with the right data
    }

    private void restoreOriginalNoteValues(Bundle savedInstanceState) {
        mOriginalNoteCourseId = savedInstanceState.getString(ORIGINAL_NOTE_COURSE_ID);
        mOriginalNoteTitle = savedInstanceState.getString(ORIGINAL_NOTE_TITLE);
        mOriginalNoteText = savedInstanceState.getString(ORIGINAL_NOTE_TEXT);
    }

    private void createNewNote() {
        DataManager dm = DataManager.getInstance();
        mNewNotePosition = dm.createNewNote();
        mNote = dm.getNotes().get(mNewNotePosition);
    }

    private void displayNote(Spinner spinnerCourses, EditText editNoteTitle, EditText editNoteText) {
        List<CourseInfo> courses = DataManager.getInstance().getCourses();//retrieve a list of courses from data manager
        int courseIndex = courses.indexOf(mNote.getCourse());//get the index of the course from the list used to populate spinner
        spinnerCourses.setSelection(courseIndex);//pass in the index of the course
        editNoteTitle.setText(mNote.getTitle());
        editNoteText.setText(mNote.getText());
    }

    private void readIntentExtras() {
        Intent intent = getIntent();//get the intent that started this activity
        //ctrl alt f promotes a variable to a field

        //read that particular noteInfo object selected from note list
        mNotePosition = intent.getIntExtra(NOTE_POSITION, POSITION_NOT_SET);
        //if the intent contains an mNote object(created in the listItem click event
        mIsNewNote = mNotePosition == POSITION_NOT_SET;//the add note fab button(new note) doesn't pass a note object as an intent extra
        if(!mIsNewNote)
            mNote = DataManager.getInstance().getNotes().get(mNotePosition);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send_mail) {
            sendMail();
        }else if(id == R.id.action_cancel){
            mIsCancelling = true;
            finish();//exit the activity (hence calling on pause)
        }else if(id == R.id.action_next){
            moveNext();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        MenuItem menuItem = menu.findItem(R.id.action_next);//find the menu item in our menu
        int lastNoteIndex = DataManager.getInstance().getNotes().size()-1;
        menuItem.setVisible(mNotePosition < lastNoteIndex);
        return super.onPrepareOptionsMenu(menu);
    }

    private void moveNext() {
        saveNote();
        ++mNotePosition;
        mNote = DataManager.getInstance().getNotes().get(mNotePosition);
        saveOriginalNoteValues();
        displayNote(mSpinnerCourses, mTextNoteTitle,mTextNoteText);

        invalidateOptionsMenu();//the system will call onPrepareOptionsMenu
    }

    private void saveOriginalNoteValues() {
        if(mIsNewNote)
            return;
        mOriginalNoteCourseId = mNote.getCourse().getCourseId();
        mOriginalNoteTitle = mNote.getTitle();
        mOriginalNoteText = mNote.getText();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mIsCancelling) {
            if(mIsNewNote) {
                DataManager.getInstance().removeNote(mNewNotePosition);
                Log.d("cancelling", "onPause: note is removed");
            } else {
                storePreviousNoteValues();
            }
        }else {
            saveNote();
            String isNew = "New?" + mIsNewNote + "isCancelling?" + mIsCancelling;
            Log.d(isNew, "onPause: note is saved");
        }
        Log.d(TAG, "onPause: NotePosition is " + mNotePosition);
    }

    private void storePreviousNoteValues() {
        CourseInfo course = DataManager.getInstance().getCourse(mOriginalNoteCourseId);
        mNote.setCourse(course);
        mNote.setTitle(mOriginalNoteTitle);
        mNote.setText(mOriginalNoteText);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ORIGINAL_NOTE_COURSE_ID,mOriginalNoteCourseId);//bundle is a map so key value pairs
        outState.putString(ORIGINAL_NOTE_TITLE,mOriginalNoteTitle);
        outState.putString(ORIGINAL_NOTE_TEXT,mOriginalNoteText);
    }

    private void saveNote() {
        mNote.setCourse((CourseInfo)mSpinnerCourses.getSelectedItem());
        mNote.setTitle(mTextNoteTitle.getText().toString());
        mNote.setText(mTextNoteText.getText().toString());
        
    }

    private void sendMail() {
        CourseInfo course = (CourseInfo) mSpinnerCourses.getSelectedItem();
        String subject = mTextNoteTitle.getText().toString();
        String text = " check out what I learned over at plural sight \"" + course.getTitle()
                + "\" \n" + mTextNoteText.getText().toString();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc2822");
        intent.putExtra(intent.EXTRA_SUBJECT, subject);//extra subject is an inbuilt extra type
        intent.putExtra(intent.EXTRA_TEXT, text);//extra text is an inbuilt extra type
        startActivity(intent);
    }
}