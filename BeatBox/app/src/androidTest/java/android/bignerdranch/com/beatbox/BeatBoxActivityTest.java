package android.bignerdranch.com.beatbox;

import android.bignerdranch.com.beatbox.BeatBoxActivity;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.IsAnything.anything;

@RunWith(AndroidJUnit4.class)
public class BeatBoxActivityTest {
    @Rule
    public ActivityScenarioRule<BeatBoxActivity> mActivityRule = new ActivityScenarioRule<>(BeatBoxActivity.class);

    @Test
    public void soundButtonWorks() {
        onView(withText("66_indios"));
    }


}