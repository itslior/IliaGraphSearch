import org.jgrapht.generate.BarabasiAlbertGraphGenerator;
import org.jgrapht.generate.GnmRandomGraphGenerator;
import org.jgrapht.generate.GraphGenerator;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.jgrapht.util.SupplierUtil;
import org.junit.Test;

import javax.xml.bind.SchemaOutputResolver;
import java.util.function.Supplier;

import static org.junit.Assert.*;

public class IliaGM_WBnBTest {

    public GraphMatching domain;

    /**
     * Tests the algorithm with a size 5 and size 7 graphs
     */
    @Test
    public void TestAlgorithm5And7(){
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

        domain = new GraphMatching(graphFrom, graphTo);
        IliaGM_WBnB wbnb = new IliaGM_WBnB();

        int[] solution = wbnb.search(domain, null, 0, 0).getAlignment();
        for(int i=0;i<solution.length;i++){
            System.out.print(solution[i] +" , ");
        }
    }

    /**
     * tests Barabasi Albert graphs
     */
    @Test
    public void TestBarabasiAlbert(){
        GraphGenerator generatorFrom = new BarabasiAlbertGraphGenerator(3,3,12);
        GraphGenerator generatorTo = new BarabasiAlbertGraphGenerator(3,3,20);

        DefaultUndirectedGraph from = new DefaultUndirectedGraph(DefaultEdge.class);
        from.setVertexSupplier(new VertexSupplier());
        generatorFrom.generateGraph(from);

        DefaultUndirectedGraph to = new DefaultUndirectedGraph(DefaultEdge.class);
        to.setVertexSupplier(new VertexSupplier());
        generatorTo.generateGraph(to);

        int weight = 10;

        for(int j=0; j < 10; j++){
            System.out.println("Run number : " + (j + 1));

            singleRun(from, to, weight/10.0, "", 0);

            weight++;
        }
    }

    /**
     * Tests the run of ErdosRenyi
     */
    @Test
    public void TestErdosRenyi(){
        GraphGenerator generatorFrom = new GnmRandomGraphGenerator(12,25);
        GraphGenerator generatorTo = new GnmRandomGraphGenerator(15,35);

        DefaultUndirectedGraph from = new DefaultUndirectedGraph(DefaultEdge.class);
        from.setVertexSupplier(new VertexSupplier());
        generatorFrom.generateGraph(from);

        DefaultUndirectedGraph to = new DefaultUndirectedGraph(DefaultEdge.class);
        to.setVertexSupplier(new VertexSupplier());
        generatorTo.generateGraph(to);

        singleRun(from, to, 1.0, "Accuracy", 0);
    }

    /**
     * Tests a specific case known
     */
    @Test
    public void specificCase(){
        DefaultUndirectedGraph from = new DefaultUndirectedGraph(DefaultEdge.class);
        int[][] pairsF = {{1,2}, {1,3}, {2,3}, {4,1}, {4,2}, {4,3}, {5,3}, {5,4}, {5,1}, {6,3}, {6,2}, {6,4}, {7,6}, {7,1}, {7,2}, {8,5}, {8,1}, {8,4}};
        int[] verticesFrom = {1, 2, 3, 4, 5, 6, 7, 8};
        generateSpecific(from, verticesFrom , pairsF);

        DefaultUndirectedGraph to = new DefaultUndirectedGraph(DefaultEdge.class);
        int[][] pairsT = {{1,2}, {1,3}, {2,3}, {4,1}, {4,3}, {4,2}, {5,3}, {5,1}, {5,4}, {6,5}, {6,4}, {6,1}, {7,6}, {7,1}, {7,3}, {8,3}, {8,5}, {8,4}, {9,3}, {9,5}, {9,4}, {10,3}, {10,5}, {10,4}, {11,1}, {11,5}, {11,4}};
        int[] verticesTo = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
        generateSpecific(to, verticesTo , pairsT);

        String measure = "";

        singleRun(from, to, 1, measure, 0);
        singleRun(from, to, 1.5, measure, 0);
        singleRun(from, to, 1.6, measure, 0);
        singleRun(from, to, 1.7, measure, 0);
    }

    /**
     * Does a single run of a problem given
     * @param from
     * @param to
     * @param weight
     * @param measure
     * @param EAlphaHat
     */
    private void singleRun(DefaultUndirectedGraph from, DefaultUndirectedGraph to, double weight, String measure, double EAlphaHat){
        IliaGM_WBnB wbnb = new IliaGM_WBnB(weight);
        Solution solution = wbnb.search(new GraphMatching(from, to), measure, from.edgeSet().size(), EAlphaHat);
        int[] alignment = solution.getAlignment();
        System.out.println("Solution time is : "+ solution.solutionTime);

        for(int i=0;i<alignment.length;i++){
            System.out.print(alignment[i] +" , ");
        }
        System.out.println();
        System.out.println("Nodes generated : " + solution.generated);
        System.out.println("Weight is : " + weight);
        System.out.println("Mistakes : " + solution.getMistakes() + ", Correct : " + solution.getCorrect());
        System.out.println("Score is : " + solution.getScore());
        System.out.println("Accuracy is : " + solution.getAccuracy());
        System.out.println("FBetaStar is : " + solution.getFBetaStar());
        System.out.println("**************************************************************");
    }

    /**
     * Generates a specific graph - helps us test the graphs in prior functions
     * @param graph
     * @param vertices
     * @param edges
     */
    private void generateSpecific(DefaultUndirectedGraph graph, int[] vertices, int[][] edges){
        for(int i=0;i<vertices.length;i++){
            graph.addVertex(vertices[i]);
        }
        for(int i=0;i<edges.length;i++){
            graph.addEdge(edges[i][0], edges[i][1]);
        }
    }
}