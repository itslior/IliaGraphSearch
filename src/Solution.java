import java.util.Arrays;

public class Solution {
    private double TP, FP, TN, FN, score;
    private int[] alignment;

    public double E1, V1, EAlphaHat;
    public long generated;
    public long solutionTime;

    /**
     * Initializes an empty solution
     */
    public Solution(){
        alignment = new int[0];
        score = Double.MAX_VALUE;
    }

//    public Solution(int[] alignment, double TP, double FP, double TN, double FN){
//        this.alignment = alignment;
//        this.TP = TP;
//        this.FP = FP;
//        this.TN = TN;
//        this.FN = FN;
//
//        score = 1 - getAccuracy();
//    }

    /**
     * Initializes the solution
     * @param alignment
     * @param TP
     * @param FP
     * @param TN
     * @param FN
     * @param measure
     * @param E1
     * @param V1
     * @param EAlphaHat
     */
    public Solution(int[] alignment, double TP, double FP, double TN, double FN, String measure, double E1, int V1, double EAlphaHat){
        this.alignment = alignment;
        this.TP = TP;
        this.FP = FP;
        this.TN = TN;
        this.FN = FN;
        this.E1 = E1;
        this.V1 = V1;
        this.EAlphaHat = EAlphaHat;

        switch(measure){
            case "Accuracy":
                score = 1 - getAccuracy();
                break;
            case "FBetaStar":
                score = 1 - getFBetaStar();
                break;
            case "WeightedAccuracy":
                score = 1 - getWeightedAccuracy();
                break;
            case "FBetaHash":
                score = 1 - getFBetaHash();
                break;
            default:
                score = (FP + FN);
        }
    }

    /**
     * Initializes a solution with another solution known prior
     * @param other
     */
    public Solution(Solution other){
        this.TP = other.TP;
        this.FP = other.FP;
        this.TN = other.TN;
        this.FN = other.FN;
        this.alignment = other.getAlignment();
        this.score = other.score;
        this.E1 = other.E1;
        this.EAlphaHat = other.EAlphaHat;
        this.generated = other.generated;
        this.solutionTime = other.solutionTime;
    }

    /**
     * Returns the alignment
     * @return
     */
    public int[] getAlignment(){
        int[] res = new int[alignment.length];
        for(int i=0;i<res.length;i++)
            res[i] = alignment[i];
        return res;
    }

    /**
     * Returns the accuracy measure
     * @return
     */
    public double getAccuracy(){
        return (TP + TN)/(TP + TN + FP + FN);
    }

//    private double getF1(){
//        return (2*TP)/(2*TP + FP + FN);
//    }

    /**
     * Returns the FBetaStar measure
     * @return
     */
    public double getFBetaStar(){
        if(EAlphaHat == 0)
            return Double.NaN;

        double BStar = E1/EAlphaHat; //CANCELED THE SQUARE ROOT
        return ((1+Math.pow(BStar, 2))*TP)/((1+Math.pow(BStar, 2))*TP + Math.pow(BStar, 2)*FN + FP);
    }

    /**
     * Returns FBetaHash measure
     * @return
     */
    public double getFBetaHash(){
        double BHash;
        double D = 2*E1/(V1*(V1-1));
        if(D < (3 - Math.sqrt(2))/4)
            BHash = 1/(4*(1-D));
        else
            BHash = (1 + Math.sqrt(-7 + 24*D - 16*Math.pow(D, 2)))/(4*(1-D));
        return ((1+Math.pow(BHash, 2))*TP)/((1+Math.pow(BHash, 2))*TP + Math.pow(BHash, 2)*FN + FP);
    }

    /**
     * Returns the score according to the measure used
     * @return
     */
    public double getScore() {
        return score;
    }

    /**
     * Returns mistakes in the alignment
     * @return
     */
    public double getMistakes(){
        return FP + FN;
    }

    /**
     * Returns correct amount of nodes in the alignment
     * @return
     */
    public double getCorrect(){
        return TP + TN;
    }

    /**
     * Returns a string containing all solution data depending on the measure used
     * @param method
     * @return
     */
    public String getString(String method){
        String align = "";
        String res = "";

        for(int i=0;i<this.alignment.length;i++){
            align += alignment[i] + " ";
        }

        switch(method){
            case "Accuracy":
                res += "" + getAccuracy();
                break;
            case "FBetaStar":
                res += "" + getFBetaStar();
                break;
            case "WeightedAccuracy":
                res += "" + getWeightedAccuracy();
                break;
            case "FBetaHash":
                res += "" + getFBetaHash();
                break;
        }

        res += "," + nodeCorrectness() + "," + solutionTime + "," +generated + "," + align + "," + getCorrect() + ","
                + getMistakes() + "," + FP + "," + FN;

        return res;
    }

    /**
     * Returns weightedACcuracy measure
     * @return
     */
    public double getWeightedAccuracy() {
        double positives = TP/(TP + FP);
        double negatives = TN/(TN + FN);
        return (positives+negatives)/2;
    }

    /**
     * Returns nodeCorrectness measure
     * @return
     */
    public double nodeCorrectness(){
        int counter = 0;
        for(int i=0; i<alignment.length; i++){
            if(alignment[i]==(i+1))
                counter++;
        }
        double length = alignment.length;
        return counter/length;
    }
}
