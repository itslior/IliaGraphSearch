import org.junit.Test;


import static org.junit.Assert.*;

public class GraphMatchingTest {

    public GraphMatching domain;
    int[][] graphFrom = {{0, 1, 1, 0, 1},
            {1, 0, 1, 1, 1},
            {1, 1, 0, 0, 0},
            {0, 1, 0, 0, 1},
            {1, 1, 0, 1, 0}};
    int[][] graphTo = {{0, 1, 1, 0, 1, 0},
            {1, 0, 1, 1, 1, 0},
            {1, 1, 0, 0, 0, 1},
            {0, 1, 0, 0, 1, 0},
            {1, 1, 0, 1, 0, 1},
            {0, 0, 1, 0, 1, 0}};

    /**
     * Pre determined test with known results
     */
    @Test
    public void TestOperators() {

        domain = new GraphMatching(graphFrom, graphTo);

//        Iterator iterator = domain.openVertices.iterator();
//
//        while(iterator.hasNext()){
//            System.out.println(iterator.next());
//        }

        assertFalse("Can't go to uncle, only to brother from start location", domain.goToNextUncle());
        assertEquals("Index should be at 0 currently", 0, domain.getIndex());
        assertEquals("Array should be with only one placement", 1, domain.getAlignment().length);
        assertEquals("First element placed should be:", 2, domain.getAlignment()[0]);
        assertFalse("Not a full alignment", domain.isFullAlignment());

        int[] measures = domain.getMeasures();
        for(int i=0;i<measures.length;i++){
            assertEquals("No measures can be done with only one vertice placed", 0, measures[i]);
        }

        domain.goToSon();
        assertEquals("Second element placed should be:", 5, domain.getAlignment()[1]);

        measures = domain.getMeasures();
        assertEquals("True Positive:", 1, measures[GraphMatching.TP]);
        assertEquals("False Positive:", 0, measures[GraphMatching.FP]);
        assertEquals("True Negative:",0, measures[GraphMatching.TN]);
        assertEquals("False Negative:", 0, measures[GraphMatching.FN]);
        assertEquals("Correct:", 1, measures[GraphMatching.CORRECT]);
        assertEquals("Mistakes:", 0, measures[GraphMatching.MISTAKES]);

        domain.goToSon();
        assertEquals("Third element placed should be:", 1, domain.getAlignment()[2]);

        measures = domain.getMeasures();
        assertEquals("True Positive:", 3, measures[GraphMatching.TP]);
        assertEquals("False Positive:", 0, measures[GraphMatching.FP]);
        assertEquals("True Negative:",0, measures[GraphMatching.TN]);
        assertEquals("False Negative:", 0, measures[GraphMatching.FN]);
        assertEquals("Correct:", 3, measures[GraphMatching.CORRECT]);
        assertEquals("Mistakes:", 0, measures[GraphMatching.MISTAKES]);

        while(domain.goToSon());

        assertTrue("Alignment should be full now", domain.isFullAlignment());
        int[] alignment = domain.getAlignment();

        //Alignment is 5,2,3,1,6 - if we do one go to brother it will change to 5,2,3,1,4
        measures = domain.getMeasures();
        assertEquals("True Positive:", 5, measures[GraphMatching.TP]);
        assertEquals("False Positive:", 2, measures[GraphMatching.FP]);
        assertEquals("True Negative:",1, measures[GraphMatching.TN]);
        assertEquals("False Negative:", 2, measures[GraphMatching.FN]);
        assertEquals("Correct:", 6, measures[GraphMatching.CORRECT]);
        assertEquals("Mistakes:", 4, measures[GraphMatching.MISTAKES]);

        assertTrue("Go to brother is available causing the alignment to change from 6 to 4 at last slot", domain.goToBrother());

        assertTrue("Last vertex is 6", domain.getAlignment()[4] == 6);

        assertTrue("Go to uncle is possible", domain.goToNextUncle());
        assertEquals("Last vertex placed should be:", 4, domain.getAlignment()[3]);
    }
}