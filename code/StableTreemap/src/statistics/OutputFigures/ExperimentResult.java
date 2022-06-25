/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package statistics.OutputFigures;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import statistics.Baseline.TreeMapReader;
import statistics.Stability.CornerTravelDistance;
import statistics.Stability.RelativeQuadrantStability;
import statistics.StatisticalTracker;
import treemap.dataStructure.Rectangle;
import treemap.dataStructure.TreeMap;

/**
 *
 * @author MaxSondag
 */
public class ExperimentResult {

    public String algorithm;
    public String dataset;
    public String classificationString;

    protected List<TreeMap> outputTreeMaps = new ArrayList();
    protected List<TreeMap> baseLineTreemaps = new ArrayList();

    int fileAmount;

    double meanMeanAspectRatio;
    double meanMedianAspectRatio;
    double medianMeanAspectRatio;
    double medianMedianAspectRatio;

    StringBuilder baselineCTDStringBuilder = new StringBuilder();
    
    /**
     * Sondag stability score
     */
    double ssv;

    /**
     * Corner travel change score.
     */
    double ctd;

    double drift;
    /**
     * Sondag stability score with baseline measure
     */
    double baselineSSV;

    /**
     * Corner travel score with baseline measure
     */
    double baselineCT;

    public ExperimentResult(String algorithm, String dataset, double meanMeanAspectRatio, double meanMedianAspectRatio, double ssv, double ctd, double baselineSSV, double baselineCT) {
        this.algorithm = algorithm;
        this.dataset = dataset;
        this.meanMeanAspectRatio = meanMeanAspectRatio;
        this.meanMedianAspectRatio = meanMedianAspectRatio;
        this.ssv = ssv;
        this.ctd = ctd;
        this.baselineSSV = baselineSSV;
        this.baselineCT = baselineCT;
    }

    /**
     * Both files should be sorted on time
     *
     * @param normalFiles
     * @param baselineFiles
     */
    public ExperimentResult(List<File> normalFiles, List<File> baselineFiles) {
        TreeMapReader tmr = new TreeMapReader();

        for (File f : normalFiles) {
            TreeMap tm = tmr.readTreeMap(f);
            outputTreeMaps.add(tm);
        }
        for (File f : baselineFiles) {
            TreeMap tm = tmr.readTreeMap(f);
            baseLineTreemaps.add(tm);
        }
        calculateStatistics();
//        calculateNewStatistics();
        //remove the lists as this is too much data when going through all files.
        //leaving them in for now for testing purposes only, otherwise it could be arguments.
        outputTreeMaps = null;
        baseLineTreemaps = null;
    }

    protected void calculateStatistics() {
        fileAmount = outputTreeMaps.size();
        calculateAspectRatios();
        calculateLocationDrift();
        calculateSSV();
        calculateCornerTravel();
        calculateBaselineSSV();
        calculateBaselineCornerTravel();
        String s = baselineCTDStringBuilder.toString();
        System.out.println("s = " + s);

    }
    protected void calculateNewStatistics() {
        fileAmount = outputTreeMaps.size();
//        calculateFrequency();
//        calculateTransformScore();
//        calculateTransformScoreAllPairs();
//        calculateAspectRatios();
//        calculateLastAspectRatios();
        calculateLocationDrift();
//        calculateSSV();
//        calculateCornerTravel();
//        calculateBaselineSSV();
//        calculateBaselineCornerTravel();
//        String s = baselineCTDStringBuilder.toString();
//        System.out.println("s = " + s);

    }

//    protected void calculateUserstudyCases() {
//        fileAmount = outputTreeMaps.size();
//        
//
//    }
    
    protected void calculateAspectRatioChange() {
        double aspectRatioChange = 0;
        double aspectRatioChangeMedian = 0;
    	for (int i = 0; i < (outputTreeMaps.size() - 1); i++) {
    		TreeMap oldTm = outputTreeMaps.get(i);
            TreeMap newTm = outputTreeMaps.get(i + 1);
            List <Double> ARC = new ArrayList<>();
            List<TreeMap> oldRemaining = getOldRemainingLeafs(oldTm, newTm);
            if (oldRemaining.isEmpty()) {
            	aspectRatioChange += 0;
            	continue;
            }
            HashMap<String, TreeMap> newMapping = new HashMap();
            for (TreeMap tmNew : newTm.getAllLeafs()) {
                newMapping.put(tmNew.getLabel(), tmNew);
            }

            for (TreeMap tmOld : oldRemaining) {
                TreeMap tmNew = newMapping.get(tmOld.getLabel());
                double aspectRatioOld = tmOld.getRectangle().getAspectRatio();
                double aspectRatioNew = tmNew.getRectangle().getAspectRatio();
                if(aspectRatioOld!=0 && oldRemaining.size()!=0) {
                	aspectRatioChange += Math.abs(aspectRatioOld- aspectRatioNew) / oldRemaining.size();
                	ARC.add(Math.abs(aspectRatioOld- aspectRatioNew)  / oldRemaining.size());
                }
            }
            aspectRatioChangeMedian += getMedian(ARC);
        }
    	aspectRatioChange = aspectRatioChange / (outputTreeMaps.size()-1);
    	aspectRatioChangeMedian = aspectRatioChangeMedian / (outputTreeMaps.size()-1);
    	meanMeanAspectRatio = aspectRatioChange;
    	medianMedianAspectRatio = aspectRatioChangeMedian;
    }
    protected void calculateFrequency() {
    	double PerBothSideSameD5 = 0;
    	double PerReverseD5 = 0;
        double PerOneSideUnchanged5 = 0;
        double PerUnchanged5 = 0;
        double PerUnchanged10 = 0;
        double PerBothSideSameD10 = 0;
        double PerOneSideUnchanged10 = 0;
        double PerReverseD10 = 0;
    	for (int i = 0; i < (outputTreeMaps.size() - 1); i++) {
    		TreeMap oldTm = outputTreeMaps.get(i);
    		for(int j = i + 1; j <(outputTreeMaps.size() - 1); j++) {
    			TreeMap newTm = outputTreeMaps.get(j);
                List<TreeMap> oldRemaining = getOldRemainingLeafs(oldTm, newTm);
                if (oldRemaining.isEmpty()) {
                	PerBothSideSameD5 += 0;
                	PerBothSideSameD10 += 0;
                	PerOneSideUnchanged5 += 0;
                	PerOneSideUnchanged10 += 0;
                	PerReverseD5 += 0;
                	PerReverseD10 += 0;
                	continue;
                }
                HashMap<String, TreeMap> newMapping = new HashMap();
                for (TreeMap tmNew : newTm.getAllLeafs()) {
                    newMapping.put(tmNew.getLabel(), tmNew);
                }
                List<Double> score = new ArrayList<>();
                double BothSideSameD5 = 0;
                double BothSideSameD10 = 0;
                double OneSideUnchanged5 = 0;
                double OneSideUnchanged10 = 0;
                double Unchanged5 = 0;
                double Unchanged10 = 0;
                double ReverseD5 = 0;
                double ReverseD10 = 0;
                for (TreeMap tmOld : oldRemaining) {
                    TreeMap tmNew = newMapping.get(tmOld.getLabel());
                    double widthOld = tmOld.getRectangle().getWidth();
                    double heightOld = tmOld.getRectangle().getHeight();
                    double widthNew = tmNew.getRectangle().getWidth();
                    double heightNew = tmNew.getRectangle().getHeight();
                    if(Math.abs((heightNew - heightOld) / heightOld) < 0.1 && Math.abs((widthNew - widthOld) / widthOld) > 0.1) {
                    	OneSideUnchanged10 += 1;
                    }
                    else if(Math.abs((heightNew - heightOld) / heightOld) > 0.1 && Math.abs((widthNew - widthOld) / widthOld) < 0.1) {
                    	OneSideUnchanged10 += 1;
                    }
                    else if(Math.abs((heightNew - heightOld) / heightOld) < 0.1 && Math.abs((widthNew - widthOld) / widthOld) < 0.1) {
                    	Unchanged10 += 1;
                    }
                    else if((heightNew - heightOld) / heightOld > 0 && (widthNew - widthOld) / widthOld > 0) {
                    	BothSideSameD10 += 1;
                    }
                    else if((heightNew - heightOld) / heightOld < 0 && (widthNew - widthOld) / widthOld < 0) {
                    	BothSideSameD10 += 1;
                    }
                    else {
                    	ReverseD10 += 1;
                    }
                    
                    if(Math.abs((heightNew - heightOld) / heightOld) < 0.05 && Math.abs((widthNew - widthOld) / widthOld) > 0.05) {
                    	OneSideUnchanged5+=1;
                    }
                    else if(Math.abs((heightNew - heightOld) / heightOld) > 0.05 && Math.abs((widthNew - widthOld) / widthOld) < 0.05) {
                    	OneSideUnchanged5+=1;
                    }
                    else if(Math.abs((heightNew - heightOld) / heightOld) < 0.05 && Math.abs((widthNew - widthOld) / widthOld) < 0.05) {
                    	Unchanged5 += 1;
                    }
                    else if((heightNew - heightOld) / heightOld > 0 && (widthNew - widthOld) / widthOld > 0) {
                    	BothSideSameD5 += 1;
                    }
                    else if((heightNew - heightOld) / heightOld < 0 && (widthNew - widthOld) / widthOld < 0) {
                    	BothSideSameD5 += 1;
                    }
                    else {
                    	ReverseD5 += 1;
                    }
                    
                }
                int numberofCombinations = oldRemaining.size();
                PerBothSideSameD5 += BothSideSameD5 / numberofCombinations;
                PerReverseD5 += ReverseD5 / numberofCombinations;
                PerOneSideUnchanged5 += OneSideUnchanged5 / numberofCombinations;
                PerBothSideSameD10 += BothSideSameD10 / numberofCombinations;
                PerReverseD10 += ReverseD10 / numberofCombinations;
                PerOneSideUnchanged10 += OneSideUnchanged10 / numberofCombinations;
                PerUnchanged5 += Unchanged5 / numberofCombinations;
                PerUnchanged10 += Unchanged10 / numberofCombinations;
    		}
        }
    	int numberofCombinations2 = (outputTreeMaps.size() - 1) * outputTreeMaps.size() / 2;
    	PerBothSideSameD5 = PerBothSideSameD5 / numberofCombinations2;
        PerReverseD5 = PerReverseD5 / numberofCombinations2;
        PerOneSideUnchanged5 = PerOneSideUnchanged5 / numberofCombinations2;
        PerBothSideSameD10 = PerBothSideSameD10 / numberofCombinations2;
        PerReverseD10 = PerReverseD10 / numberofCombinations2;
        PerOneSideUnchanged10 = PerOneSideUnchanged10 / numberofCombinations2;
        PerUnchanged5 = PerUnchanged5 / numberofCombinations2;
        PerUnchanged10 = PerUnchanged10 / numberofCombinations2;
        meanMeanAspectRatio = PerBothSideSameD5;
        meanMedianAspectRatio = PerReverseD5;
//        medianMeanAspectRatio = PerOneSideUnchanged5;
        medianMedianAspectRatio = PerBothSideSameD10;
        ssv = PerReverseD10;
        ctd = PerOneSideUnchanged10;
        baselineCT = PerUnchanged5;
    	baselineSSV = PerUnchanged10;
    }
    
    protected void calculateTransformScore() {
        double transformScore = 0;
        double transformScoreMedian = 0;
    	for (int i = 0; i < (outputTreeMaps.size() - 1); i++) {
    		TreeMap oldTm = outputTreeMaps.get(i);
            TreeMap newTm = outputTreeMaps.get(i + 1);
            List<TreeMap> oldRemaining = getOldRemainingLeafs(oldTm, newTm);
            if (oldRemaining.isEmpty()) {
            	transformScore += 0;
            	continue;
            }
            HashMap<String, TreeMap> newMapping = new HashMap();
            for (TreeMap tmNew : newTm.getAllLeafs()) {
                newMapping.put(tmNew.getLabel(), tmNew);
            }
            List<Double> score = new ArrayList<>();
            for (TreeMap tmOld : oldRemaining) {
                TreeMap tmNew = newMapping.get(tmOld.getLabel());
                double areaOld = tmOld.getRectangle().getArea();
                double areaNew = tmNew.getRectangle().getArea();
                double widthOld = tmOld.getRectangle().getWidth();
                double heightOld = tmOld.getRectangle().getHeight();
                double widthNew = tmNew.getRectangle().getWidth();
                double heightNew = tmNew.getRectangle().getHeight();
                double widthBaseline = widthOld * areaNew / areaOld;
                double heightBaseline = heightOld * areaNew / areaOld;
                //new metrics
                double heightChangePercentage = Math.abs(heightBaseline - heightNew) / heightBaseline;
                double widthChangePercentage = Math.abs(widthBaseline - widthNew) / widthBaseline;
                transformScore += Math.min(heightChangePercentage, widthChangePercentage);
                score.add(Math.min(heightChangePercentage, widthChangePercentage));
            }
            transformScoreMedian += getMedian(score);
        }
    	transformScore = transformScore / (outputTreeMaps.size()-1);
    	transformScoreMedian  = transformScoreMedian / (outputTreeMaps.size()-1);
    	ssv = transformScore;
    	ctd = transformScoreMedian;
    }
    
    protected void calculateLocationDrift() {
        double LocationDrift = 0;
        double LocationDriftVariance = 0;
        double LocationDriftVarianceUnnormed = 0;
        int totalNumber = 0;
        List<String> alreadyVisited = new ArrayList<>();
        List<Double> LocationDriftList = new ArrayList<>();
        List<Double> LocationDriftListUnnormed = new ArrayList<>();
        for (int i = 0; i < outputTreeMaps.size(); i++) {
        	TreeMap currentTm = outputTreeMaps.get(i);
        	List<TreeMap> currentLeafs = currentTm.getAllLeafs();
        	for (TreeMap leafX: currentLeafs) {
        		if(alreadyVisited.contains(leafX.getLabel())) {
        			continue;
        		}
        		else {
        			double LocationDriftTemp = 0;
        			double LocationDriftTemp2 = 0;
        			totalNumber += 1;
        			List<TreeMap> leafGroup = new ArrayList<>();
        			alreadyVisited.add(leafX.getLabel());
        			for (int j = 0; j < outputTreeMaps.size(); j++) {
        				List<TreeMap> searchLeafs = outputTreeMaps.get(j).getAllLeafs();
        				for (TreeMap leafY: searchLeafs) {
        					if(leafY.getLabel().equals(leafX.getLabel())) {
        						leafGroup.add(leafY);
        					}
        				}
                	}
        			int numberOfExists = leafGroup.size();
        			double CoGravityX = 0;
        			double CoGravityY = 0;
        			for (int k = 0; k < numberOfExists; k++) {
        				CoGravityX += leafGroup.get(k).getRectangle().getCenterX() / numberOfExists;
        				CoGravityY += leafGroup.get(k).getRectangle().getCenterY() / numberOfExists;
        			}
        			for (int all = 0; all < numberOfExists; all++) {
        				Rectangle currentR = leafGroup.get(all).getRectangle();
        				LocationDrift += Math.sqrt( Math.pow(currentR.getCenterX() - CoGravityX,2) + Math.pow(currentR.getCenterY() - CoGravityY, 2) ) / (1000 * Math.sqrt(2) * numberOfExists);
//        				LocationDriftTemp += Math.sqrt( Math.pow(currentR.getCenterX() - CoGravityX,2) + Math.pow(currentR.getCenterY() - CoGravityY, 2) ) / (1000 * Math.sqrt(2) * numberOfExists);
//        				LocationDriftTemp2 += Math.sqrt( Math.pow(currentR.getCenterX() - CoGravityX,2) + Math.pow(currentR.getCenterY() - CoGravityY, 2) ) / numberOfExists;

        			}
        			LocationDriftList.add(LocationDriftTemp);
        			LocationDriftListUnnormed.add(LocationDriftTemp2);
        		}
        	}
        }
        
        LocationDrift = LocationDrift / totalNumber;
//        LocationDriftVariance += POP_Variance(LocationDriftList);
//        LocationDriftVarianceUnnormed += POP_Variance(LocationDriftListUnnormed);
        medianMeanAspectRatio = LocationDrift;
//        meanMedianAspectRatio = LocationDriftVariance;
//        medianMeanAspectRatio = LocationDriftVarianceUnnormed;
    }
    
    double POP_Variance(List<Double> locationDriftList) {
        double variance = 0;
        for (int i = 0; i < locationDriftList.size(); i++) {
            variance = variance + (Math.pow((locationDriftList.get(i) - Mean(locationDriftList)), 2));
        }
        variance = variance / locationDriftList.size();
        return variance;
    }

    double Mean(List<Double> locationDriftList) {
        double mean = 0;
        mean = Sum(locationDriftList) / locationDriftList.size();
        return mean;
    }
    
    double Sum(List<Double> locationDriftList) {
        double sum = 0;
        for (int i = 0; i < locationDriftList.size(); i++)
            sum = sum + locationDriftList.get(i);
        return sum;
    }
    
    protected void calculateTransformScoreAllPairs() {
        double transformScore = 0;
        double transformScoreMedian = 0;
    	for (int i = 0; i < (outputTreeMaps.size() - 1); i++) {
    		TreeMap oldTm = outputTreeMaps.get(i);
    		for (int j = i + 1; j < (outputTreeMaps.size() - 1); j++) {
    			TreeMap newTm = outputTreeMaps.get(j);
                List<TreeMap> oldRemaining = getOldRemainingLeafs(oldTm, newTm);
                if (oldRemaining.isEmpty()) {
                	transformScore += 0;
                	continue;
                }
                HashMap<String, TreeMap> newMapping = new HashMap();
                for (TreeMap tmNew : newTm.getAllLeafs()) {
                    newMapping.put(tmNew.getLabel(), tmNew);
                }
                List<Double> score = new ArrayList<>();
                for (TreeMap tmOld : oldRemaining) {
                    TreeMap tmNew = newMapping.get(tmOld.getLabel());
                    double areaOld = tmOld.getRectangle().getArea();
                    double areaNew = tmNew.getRectangle().getArea();
                    double widthOld = tmOld.getRectangle().getWidth();
                    double heightOld = tmOld.getRectangle().getHeight();
                    double widthNew = tmNew.getRectangle().getWidth();
                    double heightNew = tmNew.getRectangle().getHeight();
                    double widthBaseline = widthOld * areaNew / areaOld;
                    double heightBaseline = heightOld * areaNew / areaOld;
                    //new metrics
                    double heightChangePercentage = Math.abs(heightBaseline - heightNew) / heightBaseline;
                    double widthChangePercentage = Math.abs(widthBaseline - widthNew) / widthBaseline;
                    transformScore += Math.min(heightChangePercentage, widthChangePercentage);
                    score.add(Math.min(heightChangePercentage, widthChangePercentage));
                }
                transformScoreMedian += getMedian(score);
    		}
            
        }
    	int numberofCombinations2 = (outputTreeMaps.size() - 1) * outputTreeMaps.size() / 2;
    	transformScore = transformScore / numberofCombinations2;
    	transformScoreMedian  = transformScoreMedian / numberofCombinations2;
    	
    	baselineCT = transformScore;
    	baselineSSV = transformScoreMedian;
    }
    
    private List<TreeMap> getOldRemainingLeafs(TreeMap oldTm, TreeMap newTm) {
        List<TreeMap> remainingLeafs = new ArrayList();
        Set<String> labelsInNew = new HashSet();
        for (TreeMap tm : newTm.getAllLeafs()) {
            labelsInNew.add(tm.getLabel());
        }

        for (TreeMap tm : oldTm.getAllLeafs()) {
            if (labelsInNew.contains(tm.getLabel())) {
                //tm was present in both
                remainingLeafs.add(tm);
            }
        }
        return remainingLeafs;
    }

    protected void calculateAspectRatios() {
        StatisticalTracker st = new StatisticalTracker(null);
        List<Double> meanAspectRatios = new ArrayList();
        List<Double> medianAspectRatios = new ArrayList();
        for (TreeMap tm : outputTreeMaps) {
            meanAspectRatios.add(st.getMeanAspectRatioLeafs(tm));
            medianAspectRatios.add(st.getMedianAspectRatioLeafs(tm));
            System.out.print(st.getMeanAspectRatioLeafs(tm) + ",");
        }
        meanMeanAspectRatio = getMean(meanAspectRatios);
//        meanMedianAspectRatio = getMean(medianAspectRatios);
//        
//        medianMeanAspectRatio = getMedian(meanAspectRatios);
//        medianMedianAspectRatio = getMedian(medianAspectRatios);
    }
    
    protected void calculateLastAspectRatios() {
        StatisticalTracker st = new StatisticalTracker(null);
        List<Double> meanAspectRatios = new ArrayList();
        List<Double> medianAspectRatios = new ArrayList();
        for(int i = (int) Math.floor(outputTreeMaps.size() / 2.0); i < outputTreeMaps.size(); i++) {
        	meanAspectRatios.add(st.getMeanAspectRatioLeafs(outputTreeMaps.get(i)));
        	medianAspectRatios.add(st.getMedianAspectRatioLeafs(outputTreeMaps.get(i)));
        }
        meanMeanAspectRatio = st.getMeanAspectRatioLeafs(outputTreeMaps.get(outputTreeMaps.size()-1));
        meanMedianAspectRatio = st.getMedianAspectRatioLeafs(outputTreeMaps.get(outputTreeMaps.size()-1));
        
        medianMeanAspectRatio = getMean(meanAspectRatios);
        medianMedianAspectRatio = getMean(medianAspectRatios);
    }

    protected void calculateSSV() {
        ssv = 0;
        for (int i = 0; i < (outputTreeMaps.size() - 1); i++) {
            TreeMap tm1 = outputTreeMaps.get(i);
            TreeMap tm2 = outputTreeMaps.get(i + 1);
            RelativeQuadrantStability rqs = new RelativeQuadrantStability();
            double stability = rqs.getInstability(tm1, tm2);
            ssv += stability;
        }
        ssv = ssv / (double) (outputTreeMaps.size() - 1);
        if (Double.isNaN(ssv)) {
            System.out.println("ssv = " + ssv);
        }
    }

    protected void calculateCornerTravel() {
        ctd = 0;
        for (int i = 0; i < (outputTreeMaps.size() - 1); i++) {
            TreeMap tm1 = outputTreeMaps.get(i);
            TreeMap tm2 = outputTreeMaps.get(i + 1);
            CornerTravelDistance ctd = new CornerTravelDistance();
            double stability = ctd.getInstability(tm1, tm2);
            System.out.println(stability);
            this.ctd += stability;
        }
        ctd = ctd / (double) (outputTreeMaps.size() - 1);
    }

    protected void calculateBaselineSSV() {
        baselineSSV = 0;
        for (int i = 0; i < (outputTreeMaps.size() - 1); i++) {
            //compare every treemap to it's baseline
            TreeMap oldTm = outputTreeMaps.get(i);
            TreeMap newTm = outputTreeMaps.get(i + 1);
            TreeMap baseTm = baseLineTreemaps.get(i);
            RelativeQuadrantStability rqs = new RelativeQuadrantStability();
            double instability = rqs.getBaselineInstability(oldTm, newTm, baseTm);
            baselineSSV += instability;
        }
        baselineSSV = baselineSSV / (double) (outputTreeMaps.size() - 1);
    }

    protected void calculateBaselineCornerTravel() {
        baselineCT = 0;
        for (int i = 0; i < (outputTreeMaps.size() - 1); i++) {
            //compare every treemap to it's baseline
            TreeMap oldTm = outputTreeMaps.get(i);
            TreeMap newTm = outputTreeMaps.get(i + 1);
            TreeMap baseTm = baseLineTreemaps.get(i);
            CornerTravelDistance ctd = new CornerTravelDistance();
            double stability = ctd.getBaselineInstability(oldTm, newTm, baseTm,baselineCTDStringBuilder);
            
            baselineCT += stability;
        }
        baselineCT = baselineCT / (double) (outputTreeMaps.size() - 1);
    }

    //return the mean from a list of doubles
    protected double getMean(List<Double> list) {
        double sum = 0;
        for (Double d : list) {
            sum += d;
        }
        return sum / (double) list.size();
    }

    //return the median from a list of doubles.
    protected double getMedian(List<Double> list) {
        List<Double> copyList = new ArrayList(list);
        copyList.sort((Double d1, Double d2) -> (Double.compare(d1, d2)));
        return copyList.get(copyList.size() / 2);
    }

    public String getResults() {
        String returnString = "";
        returnString += algorithm + ";";
        returnString += dataset + ";";
        returnString += meanMeanAspectRatio + ";";
        returnString += meanMedianAspectRatio + ";";
        returnString += medianMeanAspectRatio + ";";
        returnString += medianMedianAspectRatio + ";";
        returnString += ssv + ";";
        returnString += ctd + ";";
        returnString += baselineCT + ";";
        returnString += baselineSSV + ";";
        return returnString;
    }

    public boolean hasNaN() {
        if (Double.isNaN(meanMeanAspectRatio)) {
            return true;
        }
        if (Double.isNaN(meanMedianAspectRatio)) {
            return true;
        }
        if (Double.isNaN(medianMeanAspectRatio)) {
            return true;
        }
        if (Double.isNaN(medianMedianAspectRatio)) {
            return true;
        }
        if (Double.isNaN(ssv)) {
            return true;
        }
        if (Double.isNaN(ctd)) {
            return true;
        }
        if (Double.isNaN(baselineSSV)) {
            return true;
        }
        if (Double.isNaN(baselineCT)) {
            return true;
        }
        return false;
    }

    public void setClassificationString(String classificationString) {
        this.classificationString = classificationString;
    }

    public String getClassificationString() {
        return classificationString;
    }
}
