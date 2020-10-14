package com.musavili.application.mynotesapp;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

@RunWith(AndroidJUnit4.class)
public class NoteCreationTest {
    static DataManager sDataManager;

    @BeforeClass
    public static void classSetUp() throws Exception{
        sDataManager =  DataManager.getInstance();
    }

    @Rule
    public ActivityScenarioRule<NoteListActivity> mNoteListActivityActivityTestRule =//creates our test environment(the activity)
            new ActivityScenarioRule<>(NoteListActivity.class);

    @Test
    public void createNewNote(){
        CourseInfo course =  sDataManager.getCourse("java_lang");
        String title = "Test Note Title";
        String text = "This is the body of out test note";
        //ViewInteraction fabNewNote = onView(withId(R.id.fab));
        //fabNewNote.perform(click());

        onView(withId(R.id.fab)).perform(click());//clicks the fab button

        //on data looks for adapters. allOf, withId, are matchers
        onView(withId(R.id.spinner_courses)).perform(click());
        onData(allOf(instanceOf(CourseInfo.class),equalTo(course))).perform(click());

        onView(withId(R.id.text_course)).perform(typeText(title)).check(matches(withText(containsString(title))));
        onView(withId(R.id.text_title)).perform(typeText(text)).check(matches(withText(containsString(text))));
        pressBack();
        pressBack();

        int newIndex = sDataManager.getNotes().size()-1;
        NoteInfo newNote = sDataManager.getNotes().get(newIndex);
        assertEquals(course,newNote.getCourse());
        assertEquals(title,newNote.getTitle());
        assertEquals(text,newNote.getText());

       testFirsNote();
    }

    public static void testFirsNote(){
        //withId is a matcher
        //onView(withId(R.id.list_notes));

        //select a note instead of using fab
        onData(allOf(instanceOf(NoteInfo.class),equalTo(sDataManager.getNotes().get(0)))).perform(click());

        onView(withId(R.id.spinner_courses)).perform(click());
        onData(allOf(instanceOf(CourseInfo.class),equalTo(sDataManager.getCourse("java_lang")))).perform(click());
        onView(withId(R.id.text_course)).perform(typeText("\"Sum shtuff I edit hehe\""));
        onView(withId(R.id.text_title)).perform(typeText("\"KAYBOARD GO BRR\""));


        pressBack();
        pressBack();
    }
}