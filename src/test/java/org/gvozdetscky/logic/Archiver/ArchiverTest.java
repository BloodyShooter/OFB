package org.gvozdetscky.logic.Archiver;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ArchiverTest {
    @Test
    public void compressed() throws Exception {
        byte[] compressed = Archiver.compressed("11111111".getBytes());
        assertEquals(compressed.length, 2);
        compressed = Archiver.compressed("1234567890".getBytes());
        assertEquals(compressed.length, 11);
    }

    @Test
    public void deCompressed() throws Exception {
        byte[] deCompressed = Archiver.deCompressed(new byte[] {8, 41});
        assertEquals(deCompressed.length, 10);
        deCompressed = Archiver.deCompressed(new byte[] {-10, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50});
        assertEquals(deCompressed.length, 10);
    }
}
