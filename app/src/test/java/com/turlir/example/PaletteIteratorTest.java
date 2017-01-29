package com.turlir.example;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ListIterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class PaletteIteratorTest {

    private static final int LENGTH = 13;

    private ListIterator<Integer> mIterator;

    @Before
    public void setUp() { // mocking palette.xml
        TypedArray mockArray = Mockito.mock(TypedArray.class);
        when(mockArray.length()).thenReturn(LENGTH);
        for (int i = 0; i < LENGTH; i++) {
            when(mockArray.getColor(eq(i), anyInt())).thenReturn(i);
        }

        Resources mockRes = Mockito.mock(Resources.class);
        when(mockRes.obtainTypedArray(anyInt())).thenReturn(mockArray);

        Context mockContext = Mockito.mock(Context.class);
        when(mockContext.getResources()).thenReturn(mockRes);

        mIterator = new PaletteIterator(mockContext);
    }

    @Test
    public void hasNext() {
        for (int i = 0; i < 2 * LENGTH; i++) {
            mIterator.next();
            assertTrue(mIterator.hasNext());
        }
    }

    @Test
    public void next() {
        for (int i = 0; i < 2 * LENGTH; i++) {
            Integer next = mIterator.next();
            assertNotNull(next);
            assertEquals(i % LENGTH, next.intValue());
        }
    }

    @Test
    public void hasPrevious() {
        for (int i = 0; i < 2 * LENGTH; i++) {
            mIterator.previous();
            assertTrue(mIterator.hasPrevious());
        }
    }

    @Test
    public void previous() {
        for (int i = 2 * LENGTH; i > 0; i--) {
            Integer prev = mIterator.previous();
            assertNotNull(prev);
            assertEquals(i % LENGTH, prev.intValue());
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void remove() {
        mIterator.remove();
    }

}