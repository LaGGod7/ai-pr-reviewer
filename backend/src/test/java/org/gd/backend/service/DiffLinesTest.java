package org.gd.backend.service;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DiffLinesTest {

    @Test
    void commentableLines_includesAddedAndContext_skipsRemoved(){
        String patch= """   
                @@ -10,3 +10,4 @@
                 context line
                -removed line
                +added one
                +added two
                 more context
                """;
        assertEquals(Set.of(10,11,12,13),DiffLines.commentableLines(patch));
    }

    @Test
    void commentableLines_nullPatch_returnsEmpty(){
        assertTrue(DiffLines.commentableLines(null).isEmpty());
    }

}