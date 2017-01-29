package com.turlir.example;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class PaletteIteratorTest {

    private static final float LENGTH = 13;

    private Iterator<Integer> mIterator;

    @Before
    public void setUp() { // mocking palette.xml
        TypedArray mockArray = Mockito.mock(TypedArray.class);
        when(mockArray.length()).thenReturn((int) LENGTH);
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
    public void hasNext() throws Exception {
        for (int i = 0; i < 2 * LENGTH; i++) {
            mIterator.next();
            assertTrue(mIterator.hasNext());
        }
    }

    @Test
    public void next() throws Exception {
        for (int i = 0; i < 2 * LENGTH; i++) {
            Integer next = mIterator.next();
            assertNotNull(next);
            assertEquals((int) (i % LENGTH), next.intValue());
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void remove() {
        mIterator.remove();
    }

}