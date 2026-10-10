package ch.epfl.rechor.journey;


import org.junit.jupiter.api.Test;

import static ch.epfl.rechor.journey.PackedCriteria.*;
import static org.junit.jupiter.api.Assertions.*;
import ch.epfl.rechor.journey.ParetoFront;
import ch.epfl.rechor.journey.ParetoFront.Builder;

import java.util.NoSuchElementException;

public class MyParetoFrontTest {
     @Test
     public void ParetoFrontConstructorTest() {
         Builder builder = new Builder();
         assertEquals(true , builder.isEmpty());
     }

     @Test
     public void ParetoFrontAddClearTest() {
         Builder builder = new Builder();
         var test = builder.add(0b0000000000111000000000000000000000000000000000000000000000000000L);
         assertEquals(false , test.isEmpty());
         builder.clear();
         assertEquals(true, builder.isEmpty());
         test = builder.add(0b0000000000111000000000000000000000000000000000000000000000000000L).add(0b0000000000011000000000111000000000000000000000000000000000000000L);
         test = builder.add(0b0000000000011000000000000000111000000000000000000000000000000000L);
     }

     @Test
     public void ParetoFrontAddPackWork(){
         Builder builder = new Builder();
         var test = builder.add(44 , 4 , 0);
         test = builder.add(30 , 6 , 0);
         assertEquals(false , test.isEmpty());
         builder.clear();
         assertEquals(true , test.isEmpty());
     }

     @Test
     void copyConstructorWorks() {
         Builder builder = new Builder();
         builder.add(0b010000000000100000000000111000000000000000000000000000L);
         Builder builder2 = new Builder(builder);
         builder2.clear();
     }

     @Test
     void clearAndisEmptyWork() {
         Builder builder = new Builder();
         assertTrue(builder.isEmpty());
         builder.add(0b010000000000100000000000111000000000000000000000000000L);
         assertFalse(builder.isEmpty());
         builder.clear();
         assertTrue(builder.isEmpty());
     }

     @Test
     void ParetoFrontAddAllWorks(){
         Builder builder = new Builder();
         var test = builder.add(44 , 4 , 0).add(15 , 15 , 0).add(30 , 6 , 0 );
         Builder builder2 = new Builder();
         var test1 = builder2.add(30 , 6 , 0 );
         test1.addAll(test);
         assertEquals("15 15 0\r\n30 6 0\r\n44 4 0\r\n", builder.toString());
     }

     @Test
     void ParetoFrontAddWorks1() {
         Builder builder = new Builder();
         builder.add(44, 4, 0)
                 .add(15, 15, 0)
                 .add(30, 6, 0)
                 .add(15, 15, 0)
                 .add(15, 5, 2)
                 .add(30, 6, 1);
         assertEquals("15 5 2\r\n44 4 0\r\n", builder.toString());
     }

    @Test
    void ParetoFrontAddWorks2() {
        Builder builder = new Builder();
        builder.add(44, 4, 0)
                .add(15, 15, 2)
                .add(30, 6, 0)
                .add(15, 15, 0)
                .add(30, 6, 1);
        assertEquals("15 15 2\r\n30 6 0\r\n44 4 0\r\n", builder.toString());
    }

    @Test
    void ParetoFrontFullyDominatesWorks() {
         Builder dominates = new Builder();
         dominates.add(0b0111100010100000000101100000010000000000000000000000000000000000L)
                 .add(0b0111100010100000000001111000111100000000000000000000000000000000L)
                 .add(0b0111100010100000000011110000011000000000000000000000000000000000L);
         Builder notDominated = new Builder();
         notDominated.add(-196, 4, 0)
                .add(-225, 15, 0)
                .add(-210, 6, 0);
         Builder dominated = new Builder();
         dominated.add(-225, 15, 0)
                .add(-224, 15, 0)
                .add(-223, 18, 0);
         assertTrue(dominates.fullyDominates(notDominated, -5));
         assertTrue(dominates.fullyDominates(dominates, -5));
         assertTrue(dominates.fullyDominates(dominated, -5));
    }

    @Test
    void forEachWorksWithtoString() {
        Builder dominates = new Builder();
        dominates.add(0b0111100010100000000101100000010000000000000000000000000000000000L)
                .add(0b0111100010100000000001111000111100000000000000000000000000000000L)
                .add(0b0111100010100000000011110000011000000000000000000000000000000000L);
        assertEquals("-5 -225 15 0\r\n-5 -210 6 0\r\n-5 -196 4 0\r\n", dominates.toString());
    }

    @Test
    void ParetoFrontBuildWorks(){
        Builder pareto = new Builder();
        pareto.add(0b0000000000001000000101100000010000000000000000000000000000000000L)
                .add(0b0000000000001000000001111000111100000000000000000000000000000000L)
                .add(0b0000000000001000000011110000011000000000000000000000000000000000L);
        ParetoFront test = pareto.build();
        assertEquals(3 , test.size());
        pareto.add(0b0000000000001000000101100000010000000000000000000000000000000000L);
        assertEquals(3, test.size());

        Builder pareto2 = new Builder();
        pareto2.add(0b0000000000001000000101100000010000000000000000000000000000000000L);
        ParetoFront test2 = pareto2.build();
        assertEquals(1, test2.size());

    }

    @Test
    void ParetoFrontBuildGet(){
        Builder pareto = new Builder();
        pareto.add(0b0000000000001000000101100000010000000000000000000000000000000000L)
                .add(0b0000000000001000000001111000111100000000000000000000000000000000L)
                .add(0b0000000000001000000011110000011000000000000000000000000000000000L);
        ParetoFront test = pareto.build();
        assertEquals(0b0000000000001000000101100000010000000000000000000000000000000000L , test.get(-196 , 4));
        assertEquals(0b0000000000001000000001111000111100000000000000000000000000000000L , test.get(-225 , 15));
        assertThrows(NoSuchElementException.class, () -> {
            test.get(-160 , 5);
        });
    }


}


