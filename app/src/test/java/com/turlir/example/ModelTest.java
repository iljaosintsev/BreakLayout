package com.turlir.example;

import com.turlir.breaklayout.BreakLayout;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ModelTest {

    private static final int ID = BreakLayout.MODE_RIGHT;

    private Model model;

    @Before
    public void setUp() {
        model = new Model(ID);
    }

    @Test
    public void isInc() {
        for (int i = Model.DEFAULT_COUNT; i < Model.MAX; i++) {
            assertTrue(model.isInc());
            model.inc();
        }
        assertFalse(model.isInc());
    }

    @Test
    public void inc() {
        for (int i = Model.DEFAULT_COUNT; i < Model.MAX; i++) {
            int inc = model.inc();
            assertEquals(i + 1, inc);
        }
        int inc = model.inc();
        assertEquals(Model.MAX, inc);
    }

    @Test
    public void isDec() {
        model = new Model(ID, Model.MAX);
        for (int i = Model.MAX; i > Model.DEFAULT_COUNT; i--) {
            assertTrue(model.isDec());
            model.dec();
        }
        assertFalse(model.isDec());
    }

    @Test
    public void dec() {
        model = new Model(ID, Model.MAX);
        for (int i = Model.MAX; i > Model.DEFAULT_COUNT; i--) {
            int dec = model.dec();
            assertEquals(i - 1, dec);
        }
        int dec = model.dec();
        assertEquals(Model.DEFAULT_COUNT, dec);
    }

    @Test
    public void getCount() {
        int c = model.getCount();
        assertEquals(Model.DEFAULT_COUNT, c);

        int o = model.inc();
        c = model.getCount();
        assertEquals(o, c);
        assertEquals(Model.DEFAULT_COUNT + 1, c);

        o = model.dec();
        c = model.getCount();
        assertEquals(o, c);
        assertEquals(Model.DEFAULT_COUNT, c);
    }

    @Test
    public void getId() {
        int i = model.getId();
        assertEquals(ID, i);

        model = new Model(BreakLayout.MODE_LEFT, Model.DEFAULT_COUNT);
        i = model.getId();
        assertEquals(BreakLayout.MODE_LEFT, i);
    }

}