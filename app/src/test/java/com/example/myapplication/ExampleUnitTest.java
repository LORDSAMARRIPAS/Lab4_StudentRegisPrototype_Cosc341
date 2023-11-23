package com.example.myapplication;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void studentNumberIsValid() {
        MainActivity2 activity = new MainActivity2();
        assertTrue(activity.isValidStudent("12345678", "John", "Doe"));
    }

    @Test
    public void studentNumberIsInvalid() {
        MainActivity2 activity = new MainActivity2();
        assertFalse(activity.isValidStudent("1234", "John", "Doe"));
    }

}