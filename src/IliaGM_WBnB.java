
public class IliaGM_WBnB {
    private double weight;
    private long generated;
    private Solution solution;

    private String measure = "";
    private double E1;
    private double EAlphaHat;
    private GraphMatching domain;

    /**
     * Constructor with the weight of which we wanna cut the branches (Bounded - Sub optimality feature)
     * @param weight
     */
    public IliaGM_WBnB(double weight){
        this.weight = weight;
    }

    /**
     * Ignores Sub - optimality and finds the optimal solution
     */
    public IliaGM_WBnB(){
        this(1.0);
    }

    public String getName(){
        return "Ilia's Graph Matching - WBnB";
    }

    /**
     * Main function to initialize the run
     * @param domain
     * @param measure
     * @param E1
     * @param EAlphaHat
     * @return
     */
    public Solution search(GraphMatching domain, String measure, double E1, double EAlphaHat){
        this.solution = new Solution();
        if(measure == null)
            this.measure = "";
        else
            this.measure = measure;

        this.E1 = E1;
        this.EAlphaHat = EAlphaHat;

        return WBnB(domain);
    }

    /**
     * Main function to initialize the search run with a known solution
     * @param domain
     * @param measure
     * @param known
     * @return
     */
    public Solution search(GraphMatching domain, String measure, Solution known){
        this.solution = new Solution(known);

        if(measure == null)
            this.measure = "";
        else
            this.measure = measure;

        this.E1 = solution.E1;
        this.EAlphaHat = solution.EAlphaHat;

        return WBnB(domain);
    }

    /**
     * The algorithm that looks for the solution.
     * The algorithm works in a WBnB fashion. it gets it's first solution by placing the vertices in the order
     * they appear in the domain's vertex array.
     * it saves the initial solution and then starts cutting branches if it already sees the best possible result is
     * equal or less than the best one found so far.
     * @param domain
     * @return the solution found after the run
     */
    private Solution WBnB(GraphMatching domain){
        long cpuStart = System.currentTimeMillis();
        this.domain = domain;

        generated = 0;
        do{
            generated++;

            if(domain.getIndex()==0)
                System.out.println("->" + domain.getAlignment()[0] + " | time: " + (System.currentTimeMillis() - cpuStart) + " | generated : " + generated);

            double currentMeasure = getBestMeasure(domain.getMeasures(), domain.getV1());

            if(domain.isFullAlignment() && currentMeasure < solution.getScore()){
                int[] measures = domain.getMeasures();

                solution = new Solution(domain.getAlignment(), measures[GraphMatching.TP], measures[GraphMatching.FP],
                            measures[GraphMatching.TN], measures[GraphMatching.FN], measure, E1, domain.getV1(), EAlphaHat);
            }
            else if(currentMeasure * weight < solution.getScore()){
                if(domain.goToSon()){
                    continue;
                }
                else if(skipBranch(domain))
                    continue;
                else
                    break;
            }
            else if(!skipBranch(domain))
                break;
        }while(true);

        solution.solutionTime = System.currentTimeMillis() - cpuStart;
        solution.generated = generated;
        return solution;
    }

    /**
     * Returns the best measure found so far
     * @param measures
     * @param V1
     * @return
     */
    private double getBestMeasure(int[] measures, double V1) {
        double FP = measures[GraphMatching.FP];
        double FN = measures[GraphMatching.FN];
        double TPBest = E1 - FP;
        double TNBest = (V1*(V1-1))/2 - E1 - FN;
        double res;

        switch(measure){
            case "Accuracy":
                res = 1 - (TPBest + TNBest)/(TPBest + TNBest + FP + FN);
                break;
            case "FBetaStar":
                double BStar = E1/EAlphaHat; //CANCELED THE SQUARE ROOT
                res = 1 - ((1+Math.pow(BStar, 2))*TPBest)/((1+Math.pow(BStar, 2))*TPBest + Math.pow(BStar, 2)*FN + FP);
                break;
            case "WeightedAccuracy":
                double positives = TPBest/(TPBest + FP);
                double negatives = TNBest/(TNBest + FN);
                res = 1 - (positives+negatives)/2;
                break;
            case "FBetaHash":
                double BHash;
                double D = domain.DensityFrom();
                if(D < (3 - Math.sqrt(2))/4)
                    BHash = 1/(4*(1-D));
                else
                    BHash = (1 + Math.sqrt(-7 + 24*D - 16*Math.pow(D, 2)))/(4*(1-D));
                res = 1 - ((1+Math.pow(BHash, 2))*TPBest)/((1+Math.pow(BHash, 2))*TPBest + Math.pow(BHash, 2)*FN + FP);
                break;
            default:
                res = FP + FN;
        }

        return res;
    }

    /**
     * Skips the current branch as we can tell no better solution can be found there using the domain functions
     * @param domain
     * @return true if there's a branch we have yet to check
     */
    private boolean skipBranch(GraphMatching domain){
        if(domain.goToBrother())
            return true;
        else if(domain.goToNextUncle())
            return true;
        return false;
    }
}
