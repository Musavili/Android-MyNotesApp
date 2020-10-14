package com.musavili.application.mynotesapp;

import org.junit.Test;

import static org.junit.Assert.*;

public class DataManagerTest {

    @Test
    public void createNewNote() throws Exception{
        DataManager dm = DataManager.getInstance();
        final CourseInfo course = dm.getCourse("android_async");
        final String noteTitle = "Test note title ";
        final String noteText = " This is the body of the text note test";

        int noteIndex = dm.createNewNote();
        NoteInfo note = dm.getNotes().get(noteIndex);
        note.setCourse(course);
        note.setTitle(noteTitle);
        note.setText(noteText);

        NoteInfo compareNote = dm.getNotes().get(noteIndex);
        //first parameter is the expected value and the second is the value being tested
        assertEquals(course, compareNote.getCourse());
        assertEquals(noteTitle, compareNote.getTitle());
        assertEquals(noteText,compareNote.getText());
    }
}