package main;

import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;
import ec.gp.GPProblem;
import ec.gp.koza.KozaFitness;
import ec.simple.SimpleProblemForm;
import ec.util.Parameter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * This class defines the GP problem to classify the grains of rice as either Cammeo or Osmacik utilizing Genetic Programming
 * using ECJ
 */
public class RiceClassifier extends GPProblem implements SimpleProblemForm {

    int TN=0,FP=0,FN=0,TP=0; //variable for confusion matrix
    public static final String P_DATA = "data";
    private final int TOTAL_NUM_OF_DATA_ROWS = 3810; //total rows of data
    private final int NUM_OF_DATA_FIELDS = 8; //total columns of data

    double[][] riceData = new double[TOTAL_NUM_OF_DATA_ROWS][NUM_OF_DATA_FIELDS]; //2d array to store rice data
    double[][] trainingData, testingData; //data will be split into these two matrices

    //variables to store the perimeter of rice
    public double area, perimeter, majorAxisLength, minorAxisLength, eccentricity, convexArea, extent;

    /**
     * This method reads the data file and fills up the rice data array
     */
    public void fileReader() {

        Scanner scanner;
        StringTokenizer tokenizer;

        //file to be read
        File file = new File("src/data/Rice_Cammeo_Osmancik.arff");
        try {
            scanner = new Scanner(file);

            int row_number = 0;
            while (scanner.hasNext()) {
                tokenizer = new StringTokenizer(scanner.next(), ",");

                for (int column = 0; column < this.NUM_OF_DATA_FIELDS - 1; column++) {
                    double data = Double.parseDouble(tokenizer.nextToken());
                    this.riceData[row_number][column] = data;
                }
                //if rice is cammeo we represent it with 1 if it is Osmacik we represent it using -1
                float riceType = tokenizer.nextToken().equalsIgnoreCase("Cammeo") ? 1 : -1;
                this.riceData[row_number][NUM_OF_DATA_FIELDS - 1] = riceType;

                row_number++;
            }

            //randomly shuffle the array
            shuffle2DArray(this.riceData);

            //split the dat into 2 parts here it is 60% training data and 40% testing data
            splitData(0.6);


        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }


    /**
     * This method splits the data into 2 arrays. the testing and training array
     * @param percentage percentage of data to put in training and remaining in testing
     */
    public void splitData(double percentage){

        final int TRAINING_DATA_ROWS = (int) Math.round(TOTAL_NUM_OF_DATA_ROWS * percentage);
        final int TESTING_DATA_ROWS = (int) Math.round(TOTAL_NUM_OF_DATA_ROWS - TOTAL_NUM_OF_DATA_ROWS * percentage);

        this.trainingData = new double[TRAINING_DATA_ROWS][this.NUM_OF_DATA_FIELDS];
        this.testingData = new double[TESTING_DATA_ROWS][this.NUM_OF_DATA_FIELDS];

        System.arraycopy(this.riceData, 0, trainingData, 0, TRAINING_DATA_ROWS);
        System.arraycopy(this.riceData,TRAINING_DATA_ROWS,testingData,0,TESTING_DATA_ROWS);

    }

    /**
     * This method suffles the given 2d array randomly
     * @param array array to be suffled
     */
    private static void shuffle2DArray(double[][] array) {
        // Convert 2D array to a list of arrays
        List<double[]> list = Arrays.asList(array);

        // Shuffle the list
        Collections.shuffle(list);

        // Convert the shuffled list back to a 2D array
        list.toArray(array);
    }

    /**
     * This method setups the GA problem by reading the data file and initializing the problem
     * @param state current state
     * @param base current base
     */
    @Override
    public void setup(final EvolutionState state, final Parameter base) {

        super.setup(state, base);

        if (!(input instanceof DoubleData)) {
            state.output.fatal("GPData class must subclass from " + DoubleData.class,
                    base.push(P_DATA), null);
        }

        fileReader();

    }

    /**
     * This is the fitness function of the rice problem
     * @param evolutionState evolution state
     * @param individual individual
     * @param subPopulation sub population
     * @param threadNum number of threads
     */
    @Override
    public void evaluate(EvolutionState evolutionState, Individual individual, int subPopulation, int threadNum) {
        //if the individual is already evaluated skip it
        if (!individual.evaluated) {

            DoubleData input = (DoubleData) this.input;

            int hits = 0;
            double expectedResult;

            for (double[] riceDatum : this.trainingData) {

                //initialize the current parameters
                area = riceDatum[0];
                perimeter = riceDatum[1];
                majorAxisLength = riceDatum[2];
                minorAxisLength = riceDatum[3];
                eccentricity = riceDatum[4];
                convexArea = riceDatum[5];
                extent = riceDatum[6];

                expectedResult = riceDatum[7];

                //calculate the total number of correct predictions
                hits = getHits(evolutionState, (GPIndividual) individual, threadNum, input, hits, expectedResult);

                //set the koza fitness parameters
                KozaFitness kozaFitness = ((KozaFitness) individual.fitness);
                kozaFitness.setStandardizedFitness(evolutionState, (double) (this.trainingData.length - hits) /this.trainingData.length);
                kozaFitness.hits = hits;
                individual.evaluated = true;
            }

        }
    }

    /**
     * This is a utility funtion to calculated the total number correct predicitons
     * @param state state
     * @param bestIndividual best individual of the current population
     * @param threadnum number of thread
     * @param input input
     * @param hits number of hits
     * @param expectedResult expected result
     * @return number of hits
     */
    private int getHits(EvolutionState state, GPIndividual bestIndividual, int threadnum, DoubleData input, int hits, double expectedResult) {
        bestIndividual.trees[0].child.eval(
                state, threadnum, input, stack, bestIndividual, this);

        //if the output of the rule is >=0 and that's what we were expecting increase hits
        if (input.x >= 0.0 && expectedResult == 1.0) {
            hits++;
        }
        //if the output of the rule is <0 and that's what we were expecting increase hits
        else if (input.x < 0.0 && expectedResult == -1.0) {
            hits++;
        }
        return hits;
    }

    /**
     * This method runs after the execution and prints the stats of the best individual.
     * Here we test our testing data to see the correctness of trained model.
     * @param state state
     * @param bestIndividual best individual of run
     * @param subpopulation sub population
     * @param threadnum number of threads
     * @param log log
     */
    @Override
    public void describe(EvolutionState state, Individual bestIndividual, int subpopulation, int threadnum, int log) {
        super.describe(state, bestIndividual, subpopulation, threadnum, log);

        if(!(bestIndividual instanceof GPIndividual))
            state.output.fatal("The best individual is not an instance of GPIndividual!!");


        DoubleData input = (DoubleData) this.input;
        int hits = 0;
        int[] confusionMatrix=null;
        double expectedResult;

        //check each testing datum in testing data by feeding it to the individual and getting the output
        for (double[] riceDatum : this.testingData) {

            area = riceDatum[0];
            perimeter = riceDatum[1];
            majorAxisLength = riceDatum[2];
            minorAxisLength = riceDatum[3];
            eccentricity = riceDatum[4];
            convexArea = riceDatum[5];
            extent = riceDatum[6];

            expectedResult = riceDatum[7];

            assert bestIndividual instanceof GPIndividual;

            hits = getHits(state, (GPIndividual) bestIndividual, threadnum, input, hits, expectedResult);
            confusionMatrix = getConfusionMatrix(state, (GPIndividual) bestIndividual, threadnum, input, expectedResult);
        }

        state.output.println("Best Individual's total correct hits: "+hits+" out of "+this.testingData.length,log);
        state.output.println("Best Individual's testing correctness: "+((double)hits / (double)this.testingData.length)*100+"%",log);

        if(hits == this.testingData.length)
            state.output.println("Best individual is OPTIMAL", log);
        else
            state.output.println("Best individual is not optimal.",log);


        //confusion matrix print
        state.output.println("\nConfusion Matrix:",log);
        assert confusionMatrix != null;
        state.output.println("\t\tTRUE NEGATIVE:\t"+confusionMatrix[0] +"\t\tFALSE POSITIVE:\t"+ confusionMatrix[1],log);
        state.output.println("\t\tFALSE NEGATIVE:\t"+confusionMatrix[2] +"\t\tTRUE POSITIVE:\t"+ confusionMatrix[3],log);

    }

    private int[] getConfusionMatrix(EvolutionState state, GPIndividual bestIndividual, int threadnum, DoubleData input, double expectedResult) {



        bestIndividual.trees[0].child.eval(
                state, threadnum, input, stack, bestIndividual, this);

        //true positive
        if ((input.x >= 0.0 && expectedResult > 0) || (input.x < 0.0 && expectedResult < 0))
            TP++;

        //true negative
        else if ((input.x < 0.0 && expectedResult < 0) || (input.x >= 0.0 && expectedResult > 0))
            TN++;

        //false positive
        else if (input.x >= 0 && expectedResult < 0 || (input.x < 0 && expectedResult >= 0))
            FP++;

        //false negative
        else //if (input.x < 0 && expectedResult >= 0 || (input.x >= 0 && expectedResult < 0))
            FN++;

        return new int[]{TN,FP,FN,TP};
    }

}