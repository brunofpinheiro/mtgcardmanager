package com.br.mtgcardmanager;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.KeyEvent;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressKey;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isFocusable;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {
    private String card_to_search;

    @Before
    public void initVariables(){
        card_to_search = "Siege Rhino";
    }

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);

    @Test
    public void searchTest() throws InterruptedException {
        onView(withId(R.id.search)).perform(click());
        onView(withId(R.id.search_src_text)).perform(typeText(card_to_search), pressKey(KeyEvent.KEYCODE_ENTER));
        Thread.sleep(10000);
        onView(withId(R.id.ivCardImageID)).check(matches(isFocusable()));
    }
}