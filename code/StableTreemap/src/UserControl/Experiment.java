package UserControl;

import TreeMapGenerator.TreeMapGenerator;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

import treemap.DataFaciliation.DataFacilitator;
import treemap.dataStructure.DataMap;

/**
 *
 * @author Max Sondag
 */
public class Experiment {

    private TreeMapGenerator generator;
    private DataFacilitator df;
    private int maxTime;
    private SimulatorMaster simulator;
    private String datasetFolderOutput;
    private DataMap rootNode;

    /**
     * Simulates a treemap algorithm for timeSteps steps using df as the data
     * facialotor, generator as the algorithm
     *
     * @param df
     * @param generator
     * @param timeSteps
     */
    public Experiment(DataFacilitator df, TreeMapGenerator generator, int timeSteps, SimulatorMaster simulator, String datasetFolderOutput, DataMap rootNode) {
        this.df = df;
        this.generator = generator;
        this.maxTime = timeSteps;
        this.simulator = simulator;
        this.datasetFolderOutput = datasetFolderOutput;
        this.rootNode = rootNode;
        simulator.setDataFacilitator(df);
        simulator.setTreeMapGenerator(generator);

        maxTime = timeSteps;
        if (df.hasMaxTime()) {
            maxTime = df.getMaxTime();
        }
    }

    public void runExperiment() {
        boolean skipped = false;
        for (int time = 0; time <= maxTime; time++) {
        	int newTime = time;
            File f = new File(datasetFolderOutput + "/t" + newTime + ".rect");
            if (f.exists()) {
                skipped = true;
                continue;
            }

            String dataGeneratorParamaters = df.getParamaterDescription();
            String algorithmName = generator.getClass().getSimpleName();
            String algorithmParamaters = generator.getParamaterDescription();

            String commandIdentifier = "experiment;" + df.getExperimentName() + ";" + dataGeneratorParamaters + ";" + algorithmName + ";" + algorithmParamaters + ";time=" + time;
            generateTreeMap(time, commandIdentifier, rootNode);
            
        	File fileSortDate = new File(datasetFolderOutput);
            File[] filesSortDate = fileSortDate.listFiles();
            Arrays.sort(filesSortDate, new Comparator<File>() {
                public int compare(File f1, File f2) {
                    long diff = f1.lastModified() - f2.lastModified();
                    if (diff > 0)
                        return 1;
                    else if (diff == 0)
                        return 0;
                    else
                        return -1;
                }

                public boolean equals(Object obj) {
                    return true;
                }

            });
        	
        	File f2 = filesSortDate[filesSortDate.length-1];
            f2.renameTo(new File(datasetFolderOutput + "/t" + time + ".rect"));
        }

    }
    private static void orderByDate(String filePath) {
//        File file = new File(filePath);
//        File[] files = file.listFiles();
//        Arrays.sort(files, new Comparator<File>() {
//            public int compare(File f1, File f2) {
//                long diff = f1.lastModified() - f2.lastModified();
//                if (diff > 0)
//                    return 1;
//                else if (diff == 0)
//                    return 0;
//                else
//                    return -1;
//            }
//
//            public boolean equals(Object obj) {
//                return true;
//            }
//
//        });
//        for (int i = 0; i < files.length; i++) {
//            System.out.println(files[i].getName());
//            System.out.println(new Date(files[i].lastModified()));
//        }
    }

    private void generateTreeMap(int time, String commandIdentifier, DataMap rootNode) {
        simulator.getTreeMap(time, false, "experimentnoStability", rootNode);
    }
}
