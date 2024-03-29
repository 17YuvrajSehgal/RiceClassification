package main;

import ec.Evolve;

/**
 * This is the main class
 */
public class Main {
    public static void main(String[] args) {
        String pathToFiles = "src/results/";
        int numberOfJobs = 10;
        String statisticType = "ec.gp.koza.KozaShortStatistics";
        String[] runConfig = new String[] {
                Evolve.A_FILE, "src/parameterFiles/rice.params",
                //"-p", ("stat="+statisticType),
                "-p", ("stat.file=$"+pathToFiles+"out.stat"),
                "-p", ("jobs="+numberOfJobs)
        };
        Evolve.main(runConfig);
    }
}
