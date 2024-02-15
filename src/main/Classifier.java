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

public class Classifier extends GPProblem implements SimpleProblemForm {

    private final int TOTAL_NUM_OF_DATA_ROWS = 3810;
    private final int NUM_OF_DATA_FIELDS = 8;

    public static final String P_DATA = "data";
    public double area, perimeter, majorAxisLength, minorAxisLength, eccentricity, convexArea, extent;

    double[][] riceData = new double[TOTAL_NUM_OF_DATA_ROWS][NUM_OF_DATA_FIELDS];
    double[][] trainingData, testingData;

    public void fileReader() {

        Scanner scanner;
        StringTokenizer tokenizer;

        File file = new File("src/data/Rice_Cammeo_Osmancik.arff");
        try {
            scanner = new Scanner(file);

            int row_number = 0;
            while (scanner.hasNext()) {
                tokenizer = new StringTokenizer(scanner.next(), ",");
                for (int column = 0; column < this.NUM_OF_DATA_FIELDS - 1; column++) {
                    //BigDecimal data = new BigDecimal(tokenizer.nextToken());
                    double data = Double.parseDouble(tokenizer.nextToken());
                    this.riceData[row_number][column] = data;
                }
                float riceType = tokenizer.nextToken().equalsIgnoreCase("Cammeo") ? 1 : -1;
                this.riceData[row_number][NUM_OF_DATA_FIELDS - 1] = riceType;

                row_number++;
            }
            shuffle2DArray(this.riceData);

            splitData(0.6);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void splitData(double percentage){

        final int TRAINING_DATA_ROWS = (int) Math.round(TOTAL_NUM_OF_DATA_ROWS * percentage);
        final int TESTING_DATA_ROWS = (int) Math.round(TOTAL_NUM_OF_DATA_ROWS - TOTAL_NUM_OF_DATA_ROWS * percentage);

        this.trainingData = new double[TRAINING_DATA_ROWS][this.NUM_OF_DATA_FIELDS];
        this.testingData = new double[TESTING_DATA_ROWS][this.NUM_OF_DATA_FIELDS];
        System.arraycopy(this.riceData, 0, trainingData, 0, TRAINING_DATA_ROWS);
        System.arraycopy(this.riceData,TRAINING_DATA_ROWS,testingData,0,TESTING_DATA_ROWS);

    }

    public void printArray(double[][] arr) {
        for (double[] doubles : arr) {
            for (int j = 0; j < arr[0].length; j++) {
                System.out.print(doubles[j] + " ");
            }
            System.out.println();
        }
    }

    public static void shuffle2DArray(double[][] array) {
        // Convert 2D array to a list of arrays
        List<double[]> list = Arrays.asList(array);

        // Shuffle the list
        Collections.shuffle(list);

        // Convert the shuffled list back to a 2D array
        list.toArray(array);
    }

    @Override
    public void setup(final EvolutionState state,
                      final Parameter base) {
        // very important, remember this
        super.setup(state, base);

        // verify our input is the right class (or subclasses from it)
        if (!(input instanceof DoubleData)) {
            state.output.fatal("GPData class must subclass from " + DoubleData.class,
                    base.push(P_DATA), null);
        }

        fileReader();

    }

    @Override
    public void evaluate(EvolutionState evolutionState, Individual individual, int subPopulation, int threadNum) {
        if (!individual.evaluated) {

            DoubleData input = (DoubleData) this.input;

            int hits = 0;
            double sum=0, expectedResult=0,result=0;

            for (double[] riceDatum : this.riceData) {


                area = riceDatum[0];
                perimeter = riceDatum[1];
                majorAxisLength = riceDatum[2];
                minorAxisLength = riceDatum[3];
                eccentricity = riceDatum[4];
                convexArea = riceDatum[5];
                extent = riceDatum[6];

                expectedResult = riceDatum[7];

                ((GPIndividual) individual).trees[0].child.eval(
                        evolutionState, threadNum, input, stack, ((GPIndividual) individual), this);

                if (input.x >= 0.0 && expectedResult > 0.0) {
                    hits++;
                    //result = Math.abs(expectedResult - input.x);
                    //sum+=result;
                }
                else if (input.x < 0.0 && expectedResult < 0.0) {
                    hits++;
                    //result = Math.abs(expectedResult - input.x);
                    //sum+=result;
                }

                //System.out.println("------------------------------------------------------------------------------>>>" + sum);

                KozaFitness kozaFitness = ((KozaFitness) individual.fitness);
                kozaFitness.setStandardizedFitness(evolutionState, this.riceData.length-hits);
                kozaFitness.hits = hits;
                individual.evaluated = true;
            }

        }
    }

}