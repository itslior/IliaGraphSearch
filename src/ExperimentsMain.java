import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import javafx.util.Pair;
import org.jgrapht.Graph;
import org.jgrapht.generate.BarabasiAlbertGraphGenerator;
import org.jgrapht.generate.GnmRandomGraphGenerator;
import org.jgrapht.generate.GraphGenerator;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.jgrapht.graph.specifics.UndirectedEdgeContainer;

import java.io.*;
import java.security.KeyPair;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;
import java.util.function.Supplier;

public class ExperimentsMain {
    final static String PATH = System.getProperty("user.dir") + "\\ExperimentsResources";
    final static String RESULTS = System.getProperty("user.dir") + "\\results\\";
    final static String MUTATION_PATH = System.getProperty("user.dir") + "\\MutationGraphs\\";
    final static String SELF_MADE = System.getProperty("user.dir") + "\\SelfMadeGraphs\\";
    final static String SELF_MADE_MUTATION = System.getProperty("user.dir") + "\\SelfMadeMutations\\";


    static File resAccBetaStar = new File(RESULTS + "Acc+BetaStar.csv");
    static File resWAccBetaHash = new File(RESULTS + "WAcc+BetaHash.csv");

    static Random random = new Random();

    static int nodesAdded, edgesForNodes, edgesAdded , edgesRemoved;

    public static void main(String[] args) {
//        RunWebsiteGraphs();

//        for(int i=6;i<=20;i+=1){
//            GenerateERGraphs(12,i);
//            GenerateBAGraphs(3,2,16);
//        }
        RunSelfMade();

    }

    /***
     * Self made graph generator - creates ER graphs
     * @param vertices
     * @param edges
     */
    public static void GenerateERGraphs(int vertices, int edges){
        GraphGenerator generatorFrom = new GnmRandomGraphGenerator(vertices,edges);

        DefaultUndirectedGraph from = new DefaultUndirectedGraph(DefaultEdge.class);
        from.setVertexSupplier(new VertexSupplier());
        generatorFrom.generateGraph(from);

        record(from, SELF_MADE + "ER-"+vertices+"-"+edges+"-"+System.currentTimeMillis());
    }

    /***
     * Self made graph generator - BA graphs
     * @param clicka
     * @param edges
     * @param vertices
     */
    public static void GenerateBAGraphs(int clicka, int edges, int vertices){
        GraphGenerator generatorFrom = new BarabasiAlbertGraphGenerator(clicka, edges, vertices);

        DefaultUndirectedGraph from = new DefaultUndirectedGraph(DefaultEdge.class);
        from.setVertexSupplier(new VertexSupplier());
        generatorFrom.generateGraph(from);

        record(from, SELF_MADE + "BA-"+vertices+"-"+edges+"-"+clicka+"-"+System.currentTimeMillis());
    }

    /***
     * Runs self made graphs
     */
    public static void RunSelfMade(){
        File folder = new File(SELF_MADE);
        File[] files = folder.listFiles();
        File folder2 = new File(SELF_MADE_MUTATION);
        File[] files2 = folder2.listFiles();
        int idx = 0;

        DefaultUndirectedGraph from;
        DefaultUndirectedGraph to;

        try{
            for(int i = 0; i < files.length; i++){
                System.out.println("********** Working on Iteration " + (i+1));
                String[] paths = files[i].toString().split("\\\\");
                String name = paths[paths.length-1];
                name = name.split("\\.")[0];

                for(int j=3; j<= 5;j++) {
                    System.out.println("********** Working on Instance " + (j-2));

                    edgesAdded = j;   //(3,1), (4,2), (5,3)
                    edgesRemoved = j-2;
                    nodesAdded = 2;
                    edgesForNodes = 2;

                    from = createGraph(files[i]);
                    to = createGraph(files2[idx]);
                    idx++;


//                    addEdges(to, edgesAdded);
//                    removeEdges(to, edgesRemoved);
//                    addVertices(to, nodesAdded, edgesForNodes);

//                    record(to,SELF_MADE_MUTATION + name +"-" + System.currentTimeMillis() + "-" + "Mutation"); //MUTATION

                    RunPair(from, to, 1.0, "Accuracy", "FBetaStar", name, resAccBetaStar);
//                    RunPair(from, to, 1.0, "WeightedAccuracy", "FBetaHash", name, resWAccBetaHash);
                }

            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    /***
     * Runs the graphs from the website given to me by Ilia
     */
    public static void RunWebsiteGraphs(){
        File folder = new File(PATH);
        File[] files = folder.listFiles();

        DefaultUndirectedGraph from;
        DefaultUndirectedGraph to;

        nodesAdded = 3;
        edgesForNodes = 3;
        edgesAdded = 5;
        edgesRemoved = 7;

        try{
            for(int i = 0; i < files.length; i++){
                String[] paths = files[i].toString().split("\\\\");
                String name = paths[paths.length-1];
                name = name.split("\\.")[0];

                name += "COMPLEMENT";

                from = createGraph(files[i]);
                from = getComplement(from);

                File mutationFile = new File(MUTATION_PATH + name + "Mutation");
                to = createGraph(mutationFile);

//                to = createGraph(files[i]);
//                to = getComplement(to);
//                addVertices(to,nodesAdded, edgesForNodes);
//                addEdges(to, edgesAdded);
//                removeEdges(to, edgesRemoved);


//                from = getComplement(from);
//                File mutation = new File(MUTATION_PATH + name + "Mutation.edges");
//                to = createGraph(mutation);
//                to = getComplement(to);


//                System.out.println(from.toString());
//                System.out.println(to.toString());
                System.out.println("Working on instance " + (i+1));

//                record(to,MUTATION_PATH + name + "Mutation"); //MUTATION

                RunPair(from, to, 1.0, "Accuracy", "FBetaStar", name, resAccBetaStar);
                RunPair(from, to, 1.0, "WeightedAccuracy", "FBetaHash", name, resWAccBetaHash);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    /***
     * Runs two measures and records them
     * @param from
     * @param to
     * @param weight
     * @param measure1
     * @param measure2
     * @param name
     * @param res
     */
    public static void RunPair(DefaultUndirectedGraph from, DefaultUndirectedGraph to, double weight, String measure1, String measure2, String name, File res){
        try{
            double EAlphaHat = getEAlphaHat(to, from.vertexSet().size());
            Solution first = singleRun(from,to, weight, measure1, EAlphaHat);
            Solution second = singleRun(from,to, weight, measure2, EAlphaHat);

            String data = name + "," + getGraphDetails(from,to, EAlphaHat) + "," + nodesAdded + "," + edgesForNodes + ","
                    + edgesAdded + "," + edgesRemoved;
            data += "," + first.getString(measure1) + "," + second.getString(measure2);
            data += "," + checkSameAlignment(first.getAlignment(), second.getAlignment());
            long dGenerated = first.generated - second.generated;
            long dTime = first.solutionTime - second.solutionTime;
            long precGenerated = 100*dGenerated/first.generated;
            long precTime = 100*dTime/first.solutionTime;

            data += "," + dGenerated + "," + dTime + "," + precGenerated + "," + precTime;

            recordData(data, res);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }


    /***
     * Create the graph from the file given
     * @param edgeFile
     * @return
     */
    static public DefaultUndirectedGraph createGraph(File edgeFile){
        DefaultUndirectedGraph graph = new DefaultUndirectedGraph(DefaultEdge.class);
        try {
            int graphMax = 0;
            Scanner myReader = new Scanner(edgeFile);
            while (myReader.hasNextLine()) {
                String[] data = myReader.nextLine().split(" ");

                int v1 = Integer.parseInt(data[0]);
                int v2 = Integer.parseInt(data[1]);

                graphMax = Math.max(Math.max(v1, v2), graphMax);

                if(v1 == v2)
                    continue;

                graph.addVertex(v1);
                graph.addVertex(v2);
                graph.addEdge(v1,v2);
            }

            for(int i=1; i<graphMax;i++){
                graph.addVertex(i);
            }

            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
//        System.out.println(graph.toString());
//        System.out.println(graph.edgeSet().size());
//        System.out.println(graph.vertexSet().size());
        return graph;
    }

    /***
     * Changes the graph by adding vertices
     * @param graph
     * @param verticesToAdd
     * @param edgesForEachVertex
     * @throws Exception
     */
    static public void addVertices(DefaultUndirectedGraph graph, int verticesToAdd, int edgesForEachVertex) throws Exception{
        if(edgesForEachVertex > graph.vertexSet().size())
            throw new Exception("Cannot add more edges than the total amount of vertices existing currently in the graph");

        int start = graph.vertexSet().size() + 1;

        for(int i=0; i<verticesToAdd; i++){
            int vertexIdx = start + i;
            graph.addVertex(vertexIdx);
            ArrayList list = new ArrayList();

            for(int j=1;j<vertexIdx;j++)
                list.add(j);

            for(int j=0;j<edgesForEachVertex;j++){
                int v = random.nextInt(list.size());
                graph.addEdge(list.remove(v), vertexIdx);
            }
        }
    }

    /***
     * Changes the graph by adding edges
     * @param graph
     * @param edgesToAdd
     * @throws Exception
     */
    static private void addEdges(DefaultUndirectedGraph graph, int edgesToAdd) throws Exception{
        int V = graph.vertexSet().size();
        int nonEdges = V*(V-1)/2 - graph.edgeSet().size();
        if(edgesToAdd > nonEdges)
            throw new Exception("Edges to add cannot be more than the non-edges that exist in the graph");

        ArrayList<Pair> list = new ArrayList<>();

        for(int i=1;i<=graph.vertexSet().size()-1;i++){
            for(int j=i+1;j<=graph.vertexSet().size();j++){
                if(!graph.containsEdge(i,j)){
                    Pair pair = new Pair(i,j);
                    list.add(pair);
                }
            }
        }

        for(int i=0;i<edgesToAdd;i++){
            int idx = random.nextInt(list.size());
            Pair pair = list.remove(idx);
            graph.addEdge(pair.getKey(),pair.getValue());
        }
    }

    /***
     * Changes the graph by removing edges
     * @param graph
     * @param edgesToRemove
     * @throws Exception
     */
    static private void removeEdges(DefaultUndirectedGraph graph, int edgesToRemove) throws Exception{
        if(edgesToRemove > graph.edgeSet().size())
            throw new Exception("Edges to remove cannot be more than the current edges that are in the graph");

        ArrayList<Pair> list = new ArrayList<>();

        for(int i=1;i<=graph.vertexSet().size()-1;i++){
            for(int j=i+1;j<=graph.vertexSet().size();j++){
                if(graph.containsEdge(i,j)){
                    Pair pair = new Pair(i,j);
                    list.add(pair);
                }
            }
        }

        for(int i=0;i<edgesToRemove;i++){
            int idx = random.nextInt(list.size());
            Pair pair = list.remove(idx);
            graph.removeEdge(pair.getKey(),pair.getValue());
        }
    }

    /**
     * POSSIBLE BUG IF VERTICES ARE MIXED - returns EAlphaHat of the graphs
     * @param graph
     * @param originalVertices
     * @return
     * @throws Exception
     */
    static private int getEAlphaHat(DefaultUndirectedGraph graph, int originalVertices) throws Exception{
        if(graph.vertexSet().size()<originalVertices)
            throw new Exception("Original vertices cannot be greater than the current vertices that are in the graph");

        int result = 0;
        for(int i=1;i<=originalVertices-1;i++){
            for(int j=i+1;j<=originalVertices;j++){
                if(graph.containsEdge(i,j))
                    result++;
            }
        }
        return result;
    }

    /***
     * Get the solution of a single run
     * @param from
     * @param to
     * @param weight
     * @param measure
     * @param EAlphaHat
     * @return
     */
    static private Solution singleRun(DefaultUndirectedGraph from, DefaultUndirectedGraph to, double weight, String measure, double EAlphaHat) {
        IliaGM_WBnB wbnb = new IliaGM_WBnB(weight);
        GraphMatching domain = new GraphMatching(from, to);
        return wbnb.search(domain, measure, from.edgeSet().size(), EAlphaHat);
    }

    /***
     * Initiate a single run iteration with a known solution
     * @param from
     * @param to
     * @param weight
     * @param measure
     * @param solution
     * @return Solution to the run
     */
    static private Solution singleRunWithInitialSolution(DefaultUndirectedGraph from, DefaultUndirectedGraph to, double weight, String measure, Solution solution){
        IliaGM_WBnB wbnb = new IliaGM_WBnB(weight);
        return wbnb.search(new GraphMatching(from, to), measure, solution);
    }

    /***
     * Records the results in the csv file
     * @param data
     * @param file
     */
    static private void recordData(String data, File file){
        try{
            Writer output = new BufferedWriter(new FileWriter(file, true));
            output.append(data + "\n");
            output.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    /***
     * Returns a string consisting of all information about the from and target graphs
     * @param from
     * @param to
     * @param EAlphaHat
     * @return data string
     */
    static private String getGraphDetails(DefaultUndirectedGraph from, DefaultUndirectedGraph to, double EAlphaHat){
        String init;
        double D1 = (2.0*from.edgeSet().size())/(from.vertexSet().size()*(from.vertexSet().size()-1));
        double D2 = (2.0*to.edgeSet().size())/(to.vertexSet().size()*(to.vertexSet().size()-1));
        init = from.vertexSet().size() + "," + to.vertexSet().size() + ","
                + from.edgeSet().size() + "," + to.edgeSet().size() + "," + EAlphaHat + "," + D1 + "," + D2;
        return init;
    }

    /***
     * Checks if the alignment is the same in both results
     * @param a1
     * @param a2
     * @return True if same alignment
     */
    static private String checkSameAlignment(int[] a1, int[] a2){
        if(a1.length != a2.length)
            return "False";
        for(int i=0;i<a1.length;i++){
            if(a1[i] != a2[i])
                return "False";
        }

        return "True";
    }

    /***
     * Records the GRAPH as a new txt file for future use
     * @param graph
     * @param name
     */
    static private void record(DefaultUndirectedGraph graph, String name){
        File file = new File(name);
        try{
            Writer output = new BufferedWriter(new FileWriter(file, true));
            String edges = graph.edgeSet().toString();
            edges = edges.replaceAll("[^ :,a-zA-Z0-9]", "");
            edges = edges.replaceAll(" : ", " ");
            edges = edges.replaceAll(", ", "\n");

            output.append(edges);
            output.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    /***
     * Returns the complement of the current graph
     * @param graph
     * @return DefaultUndirectedGraph
     */
    static private DefaultUndirectedGraph getComplement(DefaultUndirectedGraph graph){
        DefaultUndirectedGraph compl = new DefaultUndirectedGraph(DefaultEdge.class);
        int v = graph.vertexSet().size();
        for(int i=1; i<=v;i++)
            compl.addVertex(i);

        for(int i=1; i<v;i++){
            for(int j=i+1; j<=v;j++){
                if(!graph.containsEdge(i,j))
                    compl.addEdge(i,j);
            }
        }

        return compl;
    }
}
