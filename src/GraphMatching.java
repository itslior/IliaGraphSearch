import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;


import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;


public class GraphMatching {
    public final static int TP = 0, FP = 1, TN = 2, FN = 3, CORRECT = 4, MISTAKES = 5;

    private DefaultUndirectedGraph jGraphFrom;
    private DefaultUndirectedGraph jGraphTo;

    private int[][] measuresArr;
    private int prime;

    public SortedSet<Vertex> openVertices;
    Vertex[] alignment;
    int idx;

    /***
     * AdjacentMatrix constructor
     * @param Graph1Edges
     * @param Graph2Edges
     */
    public GraphMatching(int[][] Graph1Edges, int[][] Graph2Edges){
        int[][] graphFrom;
        int[][] graphTo;

        alignment = new Vertex[Math.min(Graph1Edges.length, Graph2Edges.length)];

        if(Graph1Edges.length >= Graph2Edges.length){
            graphFrom = Graph2Edges;
            graphTo = Graph1Edges;
        }
        else
        {
            graphFrom = Graph1Edges;
            graphTo = Graph2Edges;
        }


        jGraphFrom = new DefaultUndirectedGraph(DefaultEdge.class);
        for(int i=0;i<graphFrom.length;i++){
            jGraphFrom.addVertex(i+1);
        }

        jGraphTo = new DefaultUndirectedGraph(DefaultEdge.class);
        for(int i=0;i<graphTo.length;i++){
            jGraphTo.addVertex(i+1);
        }

        calcPrime();
        openVertices = new TreeSet<>();

        for(int i=0; i<graphTo.length; i++){
            int key = i+1;
            int rank = 0;

            for(int j=0; j<graphTo.length; j++){
                if(graphTo[i][j] == 1) {
                    rank++;
                    jGraphTo.addEdge(key, j+1);
                }
            }

            Vertex v = new Vertex(key, rank);
            openVertices.add(v);
        }


        for(int i=0; i<graphFrom.length; i++){
            int key = i+1;
            int rank = 0;

            for(int j=0; j<graphFrom.length; j++){
                if(graphFrom[i][j] == 1) {
                    rank++;
                    jGraphFrom.addEdge(key, j+1);
                }
            }
        }

        measuresArr = new int[jGraphFrom.vertexSet().size()][4];

        this.idx = -1;
        goToSon();
    }

    /***
     * DefaultUndirectedGraph constructor
     * @param Graph1
     * @param Graph2
     */
    public GraphMatching(DefaultUndirectedGraph Graph1, DefaultUndirectedGraph Graph2){
        alignment = new Vertex[Math.min(Graph1.vertexSet().size(), Graph2.vertexSet().size())];

        if(Graph1.vertexSet().size() < Graph2.vertexSet().size()){
            jGraphFrom = Graph1;
            jGraphTo = Graph2;
        }
        else{
            jGraphFrom = Graph2;
            jGraphTo = Graph1;
        }

        calcPrime();

        openVertices = new TreeSet<>();

        for(int i=1; i <= jGraphTo.vertexSet().size(); i++){
            Vertex v = new Vertex(i, jGraphTo.degreeOf(i));
            openVertices.add(v);
        }

        measuresArr = new int[jGraphFrom.vertexSet().size()][4];

        this.idx = -1;
        goToSon();
    }

    /***
     * replaces the current and the node at the index-1 location in the alignment array.
     * @return true if such node exists
     */
    public boolean goToNextUncle() {
        if(idx>0){
            Vertex son = alignment[idx];
            openVertices.add(son);
        }

        while(idx>0){
            idx--;

            Vertex dad = alignment[idx];
            openVertices.add(dad);

            if(getNext(dad))
                return true;
        }
        return false;
    }

    /***
     * replaces the current node in the alignment array with the next node
     * @return true if such node exists
     */
    public boolean goToBrother(){
        Vertex self = alignment[idx];
        openVertices.add(self);

        return getNext(self);
    }

    /***
     * Increases the index of the alignment by 1 and places the next vertex
     * @return true if such node exists
     */
    public boolean goToSon(){
        if(idx+1 >= alignment.length)
            return false;

        idx++;
        return getNext(null);
    }

    /***
     * Returns the next node you can get.
     * @param toSwitch - the node which we are currently in and want to advance to the next one
     * @return the next node
     */
    private boolean getNext(Vertex toSwitch){
        Iterator iterator = openVertices.iterator();
        while(toSwitch != null && iterator.hasNext()){
            Vertex curr = (Vertex) iterator.next();
            if(curr.equals(toSwitch))
                break;
        }

        if(iterator.hasNext()){
            Vertex next = (Vertex) iterator.next();
            alignment[idx] = next;
            openVertices.remove(next);
            updateMeasures();
            return true;
        }

        return false;
    }

    /***
     * Updates the measure array when switching adjacent indexes
     */
    private void updateMeasures() {
        if(idx >= 1){
            measuresArr[idx][TP] = measuresArr[idx-1][TP];
            measuresArr[idx][FP] = measuresArr[idx-1][FP];
            measuresArr[idx][TN] = measuresArr[idx-1][TN];
            measuresArr[idx][FN] = measuresArr[idx-1][FN];
            for(int i=0;i<idx; i++){
                updateAtIndex(idx, i+1, idx+1, alignment[i].key, alignment[idx].key);
            }
        }
    }

    /***
     * Updates the specific index of the measures array - saves run time
     * @param idx of alignment array
     * @param srcFrom vertex from the fromGraph
     * @param targetFrom vertex from the fromGraph
     * @param srcTo vertex from the toGraph
     * @param targetTo vertex from the toGraph
     */
    private void updateAtIndex(int idx, int srcFrom, int targetFrom, int srcTo, int targetTo){
        if(jGraphFrom.containsEdge(srcFrom,targetFrom)) {
            if(jGraphTo.containsEdge(srcTo,targetTo))
                measuresArr[idx][TP]++;
            else
                measuresArr[idx][FP]++;
        }
        else {
            if (jGraphTo.containsEdge(srcTo, targetTo))
                measuresArr[idx][FN]++;
            else
                measuresArr[idx][TN]++;
        }
    }

    /***
     * Returns the measusures - TP FP TN FN CORRECT MISTAKES
     * @return measures
     */
    public int[] getMeasures(){
        int[] measures = new int[6];

        measures[TP] = measuresArr[idx][TP];
        measures[FP] = measuresArr[idx][FP];
        measures[TN] = measuresArr[idx][TN];
        measures[FN] = measuresArr[idx][FN];
        measures[CORRECT] = measures[TP] + measures[TN];
        measures[MISTAKES] = measures[FP] + measures[FN];

        return measures;
    }

    /***
     * Returns the alignment
     * @return alignment[]
     */
    public int[] getAlignment(){
        int size;
        if(this.alignment[idx].key != 0)
            size = idx+1;
        else
            size = idx;
        int[] alignment = new int[size];
        for(int i=0;i<=idx;i++){
            alignment[i] = this.alignment[i].key;
        }

        return alignment;
    }

    /***
     * Checks if full alignment
     * @return true if full, false otherwise
     */
    public boolean isFullAlignment(){
        return idx==alignment.length-1;
    }

    /***
     * Returns the current index of the alignment
     * @return idx
     */
    public int getIndex(){
        return idx;
    }

    /***
     * Returns the number of edges of the smaller graph
     * @return Edges(From)
     */
    public int getE1(){
        return jGraphFrom.edgeSet().size();
    }

    /***
     * Returns the edges of the larger graph
     * @return Edges(To)
     */
    public int getE2(){
        return jGraphTo.edgeSet().size();
    }

    /***
     * Returns the number of vertices of the smaller graph
     * @return Vertices(From)
     */
    public int getV1(){
        return jGraphFrom.vertexSet().size();
    }


    /***
     * Returns the number of vertices of the larger graph
     * @return Vertices(To)
     */
    public int getV2(){
        return jGraphTo.vertexSet().size();
    }

    /***
     * Returns the density of the smaller graph
     * @return Density(From)
     */
    public double DensityFrom(){
        return (2.0*jGraphFrom.edgeSet().size())/(jGraphFrom.vertexSet().size()*(jGraphFrom.vertexSet().size()-1));
    }

    /***
     * Returns the density of the larger graph
     * @return Density(To)
     */
    public double DensityTo(){
        return (2.0*jGraphTo.edgeSet().size())/(jGraphTo.vertexSet().size()*(jGraphTo.vertexSet().size()-1));
    }

    /***
     * Calculates the prime number in order to mix the vertices in the search.
     * Only in use in the compareTo function in Vertices
     */
    private void calcPrime() {
        prime = jGraphTo.vertexSet().size()+1;
        while(true){
            boolean isPrime = true;
            int rootN = (int) Math.sqrt(prime);

            for(int i = 2; i<=rootN; i++){
                if(prime%i==0) {
                    isPrime = false;
                    break;
                }
            }

            if(isPrime)
                return;

            prime++;
        }
    }


    /***
     * Vertex class with the basic functions.
     */
    private class Vertex implements Comparable{
        public int key;
        public int rank;

        public Vertex(int key, int rank){
            this.key = key;
            this.rank = rank;
        }

        public boolean equals(Object v){
            if(v instanceof Vertex){
                if((((Vertex) v).key == this.key) && ((Vertex)v).rank == this.rank)
                    return true;
            }
            return false;
        }

        @Override
        public int compareTo(Object o) {
            Vertex other = (Vertex) o;
            if(this.key == other.key) //same key?
                return 0;
            else if(this.rank != other.rank)  //ranks are different?
                return other.rank - this.rank;
            else{ //ranks are the same, keys are different
//                int thisHash = (this.key * (prime-1)/2) % prime;
//                int otherHash = (other.key * (prime-1)/2) % prime;
//
//                if(thisHash != otherHash) //Hashes are different?
//                    return otherHash - thisHash;
//                else {  //Hashes are the same
                    return this.key - other.key;
//                }
            }
        }

        public String toString(){
            return "("+ key + "," + rank +")";
        }
    }
}
