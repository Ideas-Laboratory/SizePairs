package UserControl;

import TreeMapGenerator.Packing;
import TreeMapGenerator.TreeMapGenerator;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import statistics.Baseline.BaseLineGenerator;
import treemap.DataFaciliation.DataFacilitator;
import treemap.DataFaciliation.DataFileManager;
import treemap.DataFaciliation.DataFileManagerFast;
import treemap.ModelController;
import treemap.dataStructure.DataMap;
import treemap.dataStructure.Rectangle;
import treemap.dataStructure.TreeMap;

/**
 *
 * @author Max Sondag
 */
public class Simulator extends SimulatorMaster {

    /**
     * where the input data is stored. In the case of baseline this should be a
     * single file
     */
    private File inputFolder = new File("E:/MyCode/Treemap/TreemapComparison/TreemapComparison/Data/testOftime");
    /**
     * where the output rectangles will be sent to
     */
    private File outputFolder = new File("E:/MyCode/Treemap/TreemapComparison/TreemapComparison/rubish");
    /**
     * the width of the input rectangle
     */
    private int width = 1000;
    /**
     * the height of the input rectangle
     */
    private int height = 1000;
    /**
     * the technique used
     */
    private String technique = "Packing";
    /**
     * whether we are going to generate baselines
     */
    private boolean generateBaseLines = false;

    /**
     * At which filenumber we start. Used to restart the program at a further
     * point in the file list.
     */
    private int startI = 0;

    public static void main(String args[]) {
        new Simulator(args);
    }

    public Simulator(String args[]) {
        super();
        parseArguments(args);
        treeMapRectangle = new Rectangle(0, 0, width, height);
        runExperiments();
    }

    private void parseArguments(String args[]) {
        List<String> argumentList = Arrays.asList(args);
        ListIterator<String> it = argumentList.listIterator();
        while (it.hasNext()) {
            String arg = it.next();
            System.out.println("arg = " + arg);
            switch (arg) {
                case "-technique":
                    technique = it.next();
                    System.out.println("technique = " + technique);
                    break;
                case "-baseline":
                    generateBaseLines = Boolean.parseBoolean(it.next());
                    System.out.println("generateBaseLines = " + generateBaseLines);
                    break;
                case "-inputfolder":
                    inputFolder = new File(it.next());
                    System.out.println("inputFolder = " + inputFolder);
                    break;
                case "-outputfolder":
                    outputFolder = new File(it.next());
                    System.out.println("outputFolder = " + outputFolder);
                    break;
                case "-width":
                    width = Integer.parseInt(it.next());
                    System.out.println("width = " + width);
                    break;
                case "-height":
                    height = Integer.parseInt(it.next());
                    System.out.println("height = " + height);
                    break;
                case "-startI":
                    startI = Integer.parseInt(it.next());
                    System.out.println("startI = " + startI);
                    break;
            }
        }
    }

    public Simulator() {
        super();
        treeMapRectangle = new Rectangle(0, 0, 1920, 1080);
        try {
        	runExperiments();
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            
        }
        
    }

    @Override
    public void setStability(Map<String, Double> stabilities) {
        //Do nothing, not needed
    }

    @Override
    public void setAspectRatioBeforeMoves(double maxAspectRatio) {
        //Do nothing, not needed
    }

    @Override
    public void setAspectRatioAfterMoves(double maxAspectRatio) {
        //Do nothing, not needed
    }

    @Override
    protected TreeMap updateCurrentTreeMap(int time) {
        return modelController.updateCurrentTreeMap(time);
    }

    @Override
    protected boolean getTreeMap(int time, boolean useStored, String commandIdentifier, DataMap rootNode) {
//        TreeMap nextTreeMap = modelController.getTreeMap(time, false, treeMapRectangle, "noStability");
        TreeMap nextTreeMap = modelController.getTreeMap(time, false, treeMapRectangle, commandIdentifier, rootNode);
        if (nextTreeMap == null) {
            return false;
        } else {
//            updateTreeMap(nextTreeMap);
            return true;
        }
    }

    public void setTimeOutTreeMap(int time, String commandIdentifier) {
        modelController.setTimeoutTreeMap(time, treeMapRectangle, commandIdentifier);
    }

    public void closeStatisticsOutput() {
        modelController.closeStatisticsOutput();
    }

    public void newStatisticsOutput(File outputFile, boolean directory) {
        modelController.newStatisticsFile(outputFile, directory);
    }
    
	private DataMap getRootNode(DataFacilitator dataSet, DataMap rootNode) {
		List<String> childrenMap = new ArrayList<>();
		DataMap currentDataMap;
		int maxTime = dataSet.getMaxTime();
		for (int time = 0; time <= maxTime; time++) {
			if(rootNode.getLabel() == "virtualRoot") {
				currentDataMap = dataSet.getData(time);
				for (DataMap currentChildDataMap : currentDataMap.getNextHierChildren()) {
					if (childrenMap == null || !childrenMap.contains(currentChildDataMap.getLabel())) {
						childrenMap.add(currentChildDataMap.getLabel());
					}
				}
			}
			else {
				List<DataMap> thoroughChildren = dataSet.getData(time).getAllChildren();
    			DataMap allChildrenSet = new DataMap("virtuals4", dataSet.getData(time).getTargetSize(),thoroughChildren, null);
    			if(allChildrenSet.hasChild(rootNode.getLabel())) {
    				for (DataMap currentChildDataMap: allChildrenSet.getChild(rootNode.getLabel()).getNextHierChildren()) {
    					String childLabel = currentChildDataMap.getLabel();
        				if (childrenMap == null || !childrenMap.contains(childLabel)) {
        					if(!rootNode.hasChild(childLabel)) {
        						childrenMap.add(childLabel);
        					}
    					}
    				}
    			}
			}
		}
    	
    	
    	for(String childLabel : childrenMap) {
    		for (int time=0; time<=maxTime;time++) {
    			List<DataMap> thoroughChildren = dataSet.getData(time).getAllChildren();
    			DataMap allChildrenSet = new DataMap("virtuals5", dataSet.getData(time).getTargetSize(),thoroughChildren, null);
    			if(allChildrenSet.hasChild(childLabel)) {
    				DataMap allChildrenSetchild = new DataMap(allChildrenSet.getChild(childLabel).getLabel(),allChildrenSet.getChild(childLabel).getTargetSize(),null,allChildrenSet.getChild(childLabel).getColor());
					rootNode.addDatamapSimp(allChildrenSetchild);
    				break;
    			}
    		}
    	}
		if(rootNode.getLabel() != "virtualRoot" && !rootNode.hasChildren()) {
			
		}
		else {
    		for(DataMap ChildMap: rootNode.getNextHierChildren()) {
        		getRootNode(dataSet, ChildMap);
        	}

		}
		return rootNode;
	}
	
	private DataMap decRootNode(DataFacilitator dataSet, DataMap rootNode) {
		int maxTime = dataSet.getMaxTime();
		DataMap currentDataMap;
		for (int time = 0; time <= maxTime; time++) {
			currentDataMap = dataSet.getData(time);
			List<DataMap> thoroughChildren = rootNode.getAllChildren();
			for(DataMap childOfRoot: thoroughChildren) {
				if(currentDataMap.hasChild(childOfRoot.getLabel())) {
					childOfRoot.addToSizeList(currentDataMap.getChild(childOfRoot.getLabel()).getTargetSize());
				}
				else {
					childOfRoot.addToSizeList(0.0);
				}
			}
			List<DataMap> nextHierChildren = rootNode.getNextHierChildren();
			rootNode.addToSizeList(0.0);
			for(DataMap nextHier: nextHierChildren) {
				rootNode.addToSizeList(nextHier.getSizeList().get(time), time);
			}
		}
		
		
		
		return rootNode;
	}
	
    private void runExperiments() {
        String filePath = "E:/MyCode/Treemap/TreemapComparison/TreemapComparison/output/record.txt";

        //used to determine the difference between incremental with and without moves
        String errors = "";

        //timeSteps is not used for real datasets, only when the data is generated.
        int timeSteps = 100;

        TreeMapGenerator generator = new Packing();

        //make it work on both unix and windows
        String seperator = System.getProperty("file.separator");

//            long startTime = System.currentTimeMillis();

        File[] inputFiles = getDataFiles();

        for (int i = startI; i < inputFiles.length; i++) {
            File inputFile = inputFiles[i];
            //reread the data every time such that the data is not unfluenced. Sorting and LM can change the data.
            DataFacilitator facilitator = new DataFileManagerFast(inputFile.getAbsolutePath(), false);
            if (facilitator.getMaxTime() == 0) {
                throw new IllegalStateException("File " + inputFile.getAbsolutePath() + " does not contain any data. Skipping");
            }
            System.out.println("starting with generator: " + generator.getSimpleName() + "; facilitator: " + facilitator.getExperimentName() + " (" + i + "/" + inputFiles.length + ")");

            try {
                FileWriter fw = new FileWriter(filePath, true);
                PrintWriter pw = new PrintWriter(fw);
                pw.close();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            String dataName = facilitator.getDataIdentifier();
            dataName = dataName.substring(dataName.lastIndexOf(seperator) + 1);
            String dataSetFolderOutput = "" + outputFolder.getAbsoluteFile() + seperator + generator.getSimpleName() + seperator + dataName;
            File facOutput = new File(dataSetFolderOutput);

            modelController.newStatisticsFile(facOutput, true);
            //need to reinitialize the generator after every run to make sure it is not persistent
            //if any algorithms changes the datafacilitar (Sorting and LM algo does this)
            generator = generator.reinitialize();

            DataMap rootNode = new DataMap("virtualRoot", 0, null, null);
            rootNode = getRootNode(facilitator, rootNode);
            rootNode = decRootNode(facilitator, rootNode);
            try {
            	long startTime = System.currentTimeMillis();
                Experiment e = new Experiment(facilitator, generator, timeSteps, this, dataSetFolderOutput, rootNode);
                e.runExperiment();
                long endTime = System.currentTimeMillis();
                    System.out.println("Time in total is: " + (endTime - startTime) + " milliseconds");
                if (generateBaseLines) {
                    File baseLineOutputFolder = new File("" + outputFolder.getAbsoluteFile() + seperator + generator.getSimpleName() + seperator + "baseLine" + dataName);
                    baseLineOutputFolder.mkdir();

                    //need to geinitialize the generator for the baseline to ensure data is still correct.
                    DataFacilitator fac = new DataFileManagerFast(inputFile.getAbsolutePath(), false);
                    BaseLineGenerator blg = new BaseLineGenerator();

                    blg.generateBaseLines(fac, facOutput, baseLineOutputFolder);
                }
            } catch (Exception e) {
                System.out.println("e = " + e);
                e.printStackTrace();
                errors += "An error occured in facilitator:" + facilitator.getExperimentName()
                          + " with generator: " + generator.getSimpleName() + "\r\n";
            }
            modelController.closeStatisticsOutput();
            modelController = new ModelController(this);

        }

        System.out.println("Done with generator: " + generator.getSimpleName());
            
//            System.out.println("Time in total is: " + (endTime - startTime) + " milliseconds");

        System.out.println("Done!");
        System.out.println("The following experiments did not succeed:");
        System.out.println(errors);

        System.out.println("It is now safe to exit the program");
    }

    public static List<DataFacilitator> getDataFacilitatorFromFolder(File inputFolder) {
        ArrayList<DataFacilitator> facilitators = new ArrayList();

        for (File f : inputFolder.listFiles()) {
            DataFacilitator df = new DataFileManagerFast(f.getAbsolutePath(), false);
            if (df.getMaxTime() != 0) {
                //there was data
                facilitators.add(df);
            }
        }

        return facilitators;
    }

    private File[] getDataFiles() {
        if (inputFolder == null) {
            throw new IllegalStateException("inputFolder cannot be null when reading from files");
        }
        File[] listFiles = inputFolder.listFiles();
        Arrays.sort(listFiles);//sort on alphabet
        return listFiles;
    }

    private List<DataFacilitator> getDataFacilitators() {
        if (inputFolder != null) {
            return getDataFacilitatorFromFolder(inputFolder);
        }
        String separator = System.getProperty("file.separator");
        List<DataFacilitator> facilitators = new ArrayList();

        DataFacilitator faciliator = new DataFileManager("../Data/Datasets/popularNamesAll.csv", false);
        facilitators.add(faciliator);
        return facilitators;
    }

}
