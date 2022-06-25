package TreeMapGenerator;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import treemap.dataStructure.DataMap;
import treemap.dataStructure.Rectangle;
import treemap.dataStructure.TreeMap;
import treemap.DataFaciliation.*;

public class Packing implements TreeMapGenerator{
	private boolean leafNodesFreelyFlip = false;
	private boolean controledLeafNodes = true;
	private String splitSymbol = "ymt612pyt";
	private int maxTime = 0;
	private DataMap rootParent = null;
	private double alpha = 0.2;
	public TreeMap generateTreeMap(DataMap dataMap, Rectangle inputR, int curTime) {
		leafNodesFreelyFlip = false;
		controledLeafNodes = false;
		maxTime = dataMap.getSizeList().size() - 1;
		TreeMap returnTreeMap;
        if (curTime == 0) {       	
        	rootParent = getPacking(dataMap);        //Packing
        	Map<DataMap, Rectangle> initMapping = generateInitLevel(rootParent, inputR);
        }
//        if(dataMap==null||dataMap.getTargetSize() <= 0.1) {
//			TreeMap returnTreeMap1 = new TreeMap(inputR, dataMap.getLabel(), dataMap.getColor(), dataMap.getTargetSize(), null);
//	        return returnTreeMap1;
//		}
        Map<DataMap, Rectangle> mapping = generateLevel(rootParent, inputR, curTime);
        //recursively go through the children to generate all treemaps
        List<TreeMap> treeChildren = new ArrayList();
        for (DataMap dm : mapping.keySet()) {
        	if(dm.getTargetSize() > 0) {
        		if(mapping.get(dm).getHeight()!=0 && mapping.get(dm).getWidth()!=0) {
        			TreeMap tm = new TreeMap(mapping.get(dm), dm.getLabel(), dm.getColor(), dm.getTargetSize(),null);
                    treeChildren.add(tm);
        		}
        	}
        	else {
        	}
        }
        returnTreeMap = new TreeMap(inputR, dataMap.getLabel(), dataMap.getColor(), dataMap.getTargetSize(), treeChildren);
        return returnTreeMap;
	}
	
	/**
    *
    * Generate the current level. Recursively split the datamap into two
    * roughly equal sized partition while preservering the order. Cuts the
    * inputR over the longest side
    *
    * @param dataMaps
    * @param inputR
    * @return
    */
   private Map<DataMap, Rectangle> generateLevel(DataMap packingTreeMap, Rectangle inputR, int time) {	   
       double totalSize = packingTreeMap.getSizeList().get(time);
       Map<DataMap, Rectangle> mapping = new HashMap<DataMap, Rectangle>();
       List<DataMap> list = new ArrayList<DataMap>(packingTreeMap.getChildren());
       
       if (list.size() == 1) {
    	   
    	   if (!list.get(0).hasChildren()) {
    		   mapping.put(list.get(0), inputR);
    	   }
    	   else {
    		   mapping.putAll(generateLevel(list.get(0), inputR, time));
    	   }
       }
       
       else {
           if(packingTreeMap.getTargetSize() - list.get(0).getTargetSize()-list.get(1).getTargetSize()>1) {
        	   System.err.println("something is wrong");
           }
    	   if(totalSize == 0.0) {
        	   Rectangle rEmpty = new Rectangle(0, 0, 0, 0);
        	   mapping.put(list.get(0), rEmpty);
        	   mapping.put(list.get(1), rEmpty);
           }
    	   else {
    		   Rectangle r1, r2;
               double lengthPercentageR1 = list.get(0).getSizeList().get(time) / totalSize;
               double x1 = inputR.getX();
               double y1 = inputR.getY();
               double height = inputR.getHeight();
               double width = inputR.getWidth();
        	   //allow leafNodes to flip
        	   if(leafNodesFreelyFlip) {
        		   if(packingTreeMap.getLabel().split(splitSymbol).length==2 && !packingTreeMap.getChildren().get(0).hasChildren()) {
            		   if (inputR.getHeight() >= inputR.getWidth()) {
                           r1 = new Rectangle(x1, y1, width, lengthPercentageR1 * height);
                           r2 = new Rectangle(x1, y1 + lengthPercentageR1 * height, width, height - lengthPercentageR1 * height);
                           packingTreeMap.setOrder(true);
                       } else {
                           r1 = new Rectangle(x1, y1, lengthPercentageR1 * width, height);
                           r2 = new Rectangle(x1 + lengthPercentageR1 * width, y1, width - lengthPercentageR1 * width, height);
                           packingTreeMap.setOrder(false);
                       }
            	   }
            	   else {
            		   if(packingTreeMap.getOrder()) {
                		   r1 = new Rectangle(x1, y1, width, lengthPercentageR1 * height);
                           r2 = new Rectangle(x1, y1 + lengthPercentageR1 * height, width, height - lengthPercentageR1 * height);
                	   }
                	   else {
                		   r1 = new Rectangle(x1, y1, lengthPercentageR1 * width, height);
                           r2 = new Rectangle(x1 + lengthPercentageR1 * width, y1, width - lengthPercentageR1 * width, height);
                	   }
            	   } 
        	   }
        	   else if(controledLeafNodes) {
        		   if(packingTreeMap.getLabel().split(splitSymbol).length==2 && !packingTreeMap.getChildren().get(0).hasChildren()) {
        			   
        			   if(packingTreeMap.getOrder()) {
        				   if (inputR.getHeight() >= 0.8*inputR.getWidth()) {
                               r1 = new Rectangle(x1, y1, width, lengthPercentageR1 * height);
                               r2 = new Rectangle(x1, y1 + lengthPercentageR1 * height, width, height - lengthPercentageR1 * height);
                               packingTreeMap.setOrder(true);
                           } else {
                               r1 = new Rectangle(x1, y1, lengthPercentageR1 * width, height);
                               r2 = new Rectangle(x1 + lengthPercentageR1 * width, y1, width - lengthPercentageR1 * width, height);
                               packingTreeMap.setOrder(false);
                           } 
        			   }
        			   else {
        				   if (inputR.getHeight() >= 1.2*inputR.getWidth()) {
                               r1 = new Rectangle(x1, y1, width, lengthPercentageR1 * height);
                               r2 = new Rectangle(x1, y1 + lengthPercentageR1 * height, width, height - lengthPercentageR1 * height);
                               packingTreeMap.setOrder(true);
                           } else {
                               r1 = new Rectangle(x1, y1, lengthPercentageR1 * width, height);
                               r2 = new Rectangle(x1 + lengthPercentageR1 * width, y1, width - lengthPercentageR1 * width, height);
                               packingTreeMap.setOrder(false);
                           } 
        			   }
            	   }
            	   else {
            		   if(packingTreeMap.getOrder()) {
                		   r1 = new Rectangle(x1, y1, width, lengthPercentageR1 * height);
                           r2 = new Rectangle(x1, y1 + lengthPercentageR1 * height, width, height - lengthPercentageR1 * height);
                	   }
                	   else {
                		   r1 = new Rectangle(x1, y1, lengthPercentageR1 * width, height);
                           r2 = new Rectangle(x1 + lengthPercentageR1 * width, y1, width - lengthPercentageR1 * width, height);
                	   }
            	   } 
        	   }
        	   else {
    			   if(packingTreeMap.getOrder()) {
            		   r1 = new Rectangle(x1, y1, width, lengthPercentageR1 * height);
                       r2 = new Rectangle(x1, y1 + lengthPercentageR1 * height, width, height - lengthPercentageR1 * height);
            	   }
            	   else {
            		   r1 = new Rectangle(x1, y1, lengthPercentageR1 * width, height);
                       r2 = new Rectangle(x1 + lengthPercentageR1 * width, y1, width - lengthPercentageR1 * width, height);
            	   }
        	   }
               
               //recursively map the rectangles until there's no children or we have reached a hierarchical treemap's base
               //we are in the basecase and the mapping is known
               if (!list.get(0).hasChildren()) {
        		   mapping.put(list.get(0), r1);
        	   }
        	   else {
        		   mapping.putAll(generateLevel(list.get(0), r1, time));
        	   }
               
               if (!list.get(1).hasChildren()) {
        		   mapping.put(list.get(1), r2);
        	   }
        	   else {
        		   mapping.putAll(generateLevel(list.get(1), r2, time));
        	   }
    	   }
           
       }
       

       return mapping;
   }
	/**
   *
   * Generate the init level. Recursively split the datamap into two
   * roughly equal sized partition while preservering the order. Cuts the
   * inputR over the longest side
   *
   * @param dataMaps
   * @param inputR
   * @return
   */  
   private Map<DataMap, Rectangle> generateInitLevel(DataMap packingTreeMap, Rectangle inputR) {
       Map<DataMap, Rectangle> mapping = new HashMap<DataMap, Rectangle>();
       List<DataMap> list = new ArrayList<DataMap>(packingTreeMap.getChildren());      

       if (list.size() == 1) {
    	   
    	   if (!list.get(0).hasChildren()) {
    		   mapping.put(list.get(0), inputR);
    	   }
    	   else {
    		   mapping.putAll(generateInitLevel(list.get(0), inputR));
    	   }
       }
       
       else {
           Rectangle r1, r2;
           double x1 = inputR.getX();
           double y1 = inputR.getY();
           double height = inputR.getHeight();
           double width = inputR.getWidth();
           double AllChildSize0 = list.get(0).getSizeSum();
           double AllChildSize1 = list.get(1).getSizeSum();
           double lengthPercentageR1 = AllChildSize0 / (AllChildSize1 + AllChildSize0);
    	   if (inputR.getHeight() >= inputR.getWidth()) {
               r1 = new Rectangle(x1, y1, width, lengthPercentageR1 * height);
               r2 = new Rectangle(x1, y1 + lengthPercentageR1 * height, width, height - lengthPercentageR1 * height);
               packingTreeMap.setOrder(true);
           } else {
               r1 = new Rectangle(x1, y1, lengthPercentageR1 * width, height);
               r2 = new Rectangle(x1 + lengthPercentageR1 * width, y1, width - lengthPercentageR1 * width, height);
               packingTreeMap.setOrder(false);
           }
           //recursively map the rectangles until there's no children or we have reached a hierarchical treemap's base
           if (!list.get(0).hasChildren()) {
    		   mapping.put(list.get(0), r1);
    	   }
    	   else {
    		   mapping.putAll(generateInitLevel(list.get(0), r1));
    	   }
           
           if (!list.get(1).hasChildren()) {
    		   mapping.put(list.get(1), r2);
    	   }
    	   else {
    		   mapping.putAll(generateInitLevel(list.get(1), r2));
    	   }
       }
       return mapping;
   }
   
	@Override
	public String getParamaterDescription() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public TreeMapGenerator reinitialize() {
		leafNodesFreelyFlip = true;
		controledLeafNodes = false;
		
		maxTime = 0;
		/**
		 *For convenience, I ignored treemaps with multi levels. (It may work anyway)
		 */
		rootParent = null;
		return this;
	}
	
	private double abs(double a) {
		return (a<0) ? -a : a;
	}
	
	/**
	 * 
	 * @param DataMap
	 * @return a list contains all DataMap that has appeared in dataSet
	 */
	
	private DataMap updatePackingTreeSize(DataMap packingTree, DataMap currentDataMap, DataMap allChildrenSet, List<String> currentDataLabel){
		String[] allChildrenLabel;
		allChildrenLabel = packingTree.getLabel().split(splitSymbol);
		
		
		double currentTargetSize = 0;
		for (int i = 0; i < allChildrenLabel.length; i++) {
			if(currentDataLabel.contains(allChildrenLabel[i])) {
				currentTargetSize += allChildrenSet.getChild(allChildrenLabel[i]).getTargetSize();
			}
		}
		packingTree.setTargetSize(currentTargetSize);
		
		if(packingTree.hasChildren()) {
			List <DataMap> allChildren = packingTree.getNextHierChildren();
			
			for(int i = 0; i < allChildren.size(); i++) {
				updatePackingTreeSize(allChildren.get(i), currentDataMap, allChildrenSet, currentDataLabel);
			}
		}
		
		return packingTree;
	}
	
	/**
	 * get the changing of two elements' combination (packing)
	 */
	private double getChanging(String dataMap1Label, String dataMap2Label, DataMap rootNode, double alpha) {
		double changing = 0;
		double delta1_2 = 0;
		double cost = 0;
		double currentSize1 = 0;
		double currentSize2 = 0;
		double lastSize1 = 0;
		double lastSize2 = 0;
		
		/**
		 * initial lastSize
		 */
		lastSize1 = rootNode.getChild(dataMap1Label).getSizeList().get(0);
		lastSize2 = rootNode.getChild(dataMap2Label).getSizeList().get(0);
		
		/**
		 * calculate the data changing through traverse
		 */
		for (int time = 1; time <= maxTime; time++) {
			currentSize1 = rootNode.getChild(dataMap1Label).getSizeList().get(time);
			currentSize2 = rootNode.getChild(dataMap2Label).getSizeList().get(time);
			
			if(currentSize1 + currentSize2 + lastSize1 + lastSize2 != 0) {
				changing += abs((currentSize1 + currentSize2 - lastSize1 - lastSize2) / (currentSize1 + lastSize1 + currentSize2 + lastSize2)) / (maxTime+1);
			}
			if(currentSize1 + currentSize2 != 0) {
				delta1_2 += abs((currentSize1 - currentSize2)/(currentSize1+ currentSize2)) / (1+maxTime);
			}
			
			lastSize1 = currentSize1;
			lastSize2 = currentSize2;
			currentSize1 = 0;
			currentSize2 = 0;
		}
		
		cost = alpha * changing + (1 - alpha) * delta1_2;
		return cost;
	}
	
	/**
	 * get the changing of a single element
	 */
	private double getChanging(String dataMapLabel, DataMap rootNode) {
		double changing = 0;
		double currentSize = 0;
		double lastSize = 0;
		/**
		 * initial lastSize
		 */
		lastSize = rootNode.getChild(dataMapLabel).getSizeList().get(0);
		
		
		
		/**
		 * calculate the data changing through traverse
		 */
		for (int time = 1; time <= maxTime; time++) {
			
			currentSize = rootNode.getChild(dataMapLabel).getSizeList().get(time);

			if(currentSize + lastSize != 0) {
				changing += abs((currentSize - lastSize) / (currentSize + lastSize)) / (maxTime+1);	
			}
			lastSize = currentSize;
			currentSize = 0;
		}
		
		return changing;
	}
	
	/**
	 * Packing data in pairs
	 * @return 
	 */
	boolean metaPack = true;
	private DataMap getPacking(DataMap rootNode) {
		List <String> dataMaps = new ArrayList<>();
		for(DataMap childofRootNode: rootNode.getChildren()) {
			dataMaps.add(childofRootNode.getLabel());
		}
		if(metaPack) {
			metaPack = false;
			for(DataMap childNode : rootNode.getChildren()) {
				if(childNode.hasChildren()) {
					metaPack = true;
					DataMap subRoot = getPacking(childNode);
					List <DataMap> removeList = new ArrayList<>();
					for(DataMap subsubChild: childNode.getChildren()) {
						removeList.add(subsubChild);
					}
					childNode.removeDataMaps_pack(removeList);
					childNode.addDatamapSimp(subRoot);
					metaPack = false;
				}
			}
		}
		
		List <String> parentDataMaps = new ArrayList<>();
		int childNum = dataMaps.size();                          //childNum: the number of the elements		
		DataMap rootDataMap = null;
		double [][] allChanging = new double [childNum][childNum];
		boolean [] canbeUsed = new boolean [childNum];  // mark nodes that violets size restrictions and has been packed
		for (int i=0;i<childNum;i++) {
			canbeUsed[i] = true;
		}
		//Preprocess canUsed to impose restrictions  similar to approximation
		canbeUsed = preprocessCanbeUsed(canbeUsed, dataMaps, rootNode);
		/**
		 * initialize the changing 
		 */
		for (int i=0;i<childNum;i++) {
			if(!canbeUsed[i]) continue;
			for (int j=i+1;j<childNum;j++) {
				allChanging[i][j] = getChanging(dataMaps.get(i), dataMaps.get(j), rootNode, alpha);
			}
		}
		for (int i=0;i<childNum;i++) {                              // get the changing of possible signle packages
			allChanging[i][i] = getChanging(dataMaps.get(i), rootNode) + 900000000;
		}
		
		for(int i=0;i<childNum;i++) {                               // put extra large packages( > 1/3*total size) directly into the upper level
			if(!canbeUsed[i]) {
				DataMap oldparentMap = rootNode.getChild(dataMaps.get(i));
				DataMap parentMap = new DataMap(oldparentMap.getLabel(),oldparentMap.getTargetSize(),oldparentMap.getChildren(),oldparentMap.getColor());
				parentMap.setSizeList(oldparentMap.getSizeList());
				parentDataMaps.add(parentMap.getLabel());
			}
		}
		
		while(checkIfAllUsed(canbeUsed)) {
			double min = 1000000000;
			int currenti = -1;
			int currentj = -1;
			ArrayList<DataMap> children = new ArrayList();
			
			for (int i=0; i<childNum; i++) {
				if (!canbeUsed[i]) continue;
				for (int j=i; j<childNum; j++) {
					if (!canbeUsed[j]) continue;
					if (min>allChanging[i][j]) {
						min = allChanging[i][j];
						currenti = i;
						currentj = j;
					}
				}
			}
			canbeUsed[currenti] = false;
			canbeUsed[currentj] = false;
			double parentSize = 0;
			if (currentj == currenti) {
				DataMap oldparentMap = rootNode.getChild(dataMaps.get(currenti));
				DataMap parentMap = new DataMap(oldparentMap.getLabel(),oldparentMap.getTargetSize(),oldparentMap.getChildren(),oldparentMap.getColor());
				parentMap.setSizeList(oldparentMap.getSizeList());
				parentDataMaps.add(parentMap.getLabel());
				
			}

			else {
				String parentLabel = dataMaps.get(currenti) + splitSymbol + dataMaps.get(currentj);
				parentSize += rootNode.getChild(dataMaps.get(currenti)).getTargetSize() + rootNode.getChild(dataMaps.get(currentj)).getTargetSize();
				DataMap newchild1 = new DataMap(rootNode.getChild(dataMaps.get(currenti)).getLabel(),rootNode.getChild(dataMaps.get(currenti)).getTargetSize(),rootNode.getChild(dataMaps.get(currenti)).getChildren(),rootNode.getChild(dataMaps.get(currenti)).getColor());
				DataMap newchild2 = new DataMap(rootNode.getChild(dataMaps.get(currentj)).getLabel(),rootNode.getChild(dataMaps.get(currentj)).getTargetSize(),rootNode.getChild(dataMaps.get(currentj)).getChildren(),rootNode.getChild(dataMaps.get(currentj)).getColor());
				newchild1.setSizeList(rootNode.getChild(dataMaps.get(currenti)).getSizeList());
				newchild2.setSizeList(rootNode.getChild(dataMaps.get(currentj)).getSizeList());
				children.add(newchild1);
				children.add(newchild2);
				DataMap parentMap = new DataMap(parentLabel, parentSize, children, null);
				parentMap.mergeSizeList(newchild1.getSizeList(), newchild2.getSizeList());
				rootNode.removeDataMap_pack(children.get(0));
				rootNode.removeDataMap_pack(children.get(1));
				rootNode.addDatamapSimp(parentMap);
				parentDataMaps.add(parentMap.getLabel());
			}
		}
		DataMap newrootDataMap = null;
		if (parentDataMaps.size() > 1) {
			return getPacking(rootNode);    //Recursively packing until there's only one element left
		}
		else {
			rootDataMap = rootNode.getChild(parentDataMaps.get(0));
			newrootDataMap = new DataMap(rootDataMap.getLabel(),rootDataMap.getTargetSize(),rootDataMap.getChildren(),rootDataMap.getColor());
			newrootDataMap.setSizeList(rootDataMap.getSizeList());
			metaPack = true;
		}
		return newrootDataMap;
	}	
	
	/**
	 * Check if a certain value exists in a list
	 * @return 
	 */
	private static boolean checkIfAllUsed(boolean[] arr) {
        for (boolean s : arr) {
            if (s)
                return true;
        }
        return false;
    }
	/**
	 * Preprocess canUsed to impose restrictions for 
	 * @return 
	 */
	private boolean[] preprocessCanbeUsed(boolean[] canbeUsed, List <String> dataMaps, DataMap rootNode) {
        int packageNum = dataMaps.size();
        if(packageNum<=2) {
        	return canbeUsed;
        }
        double[] packageSize = new double[packageNum];
        double totalSize = 0;
    	for (int i=0; i < packageNum; i++) {
    		packageSize[i] = rootNode.getChild(dataMaps.get(i)).getSizeSum();
			totalSize += packageSize[i];   		
    	}
        for (int i=0; i<packageNum; i++) {
        	if(packageSize[i]>totalSize / 3) {
        		canbeUsed[i] = false;
        	}
        }
        if(packageNum == 3) {
        	int maxIndex = 0;
        	for(int k=0;k<3;k++) {
        		canbeUsed[k] = true;
        		if(packageSize[k] > packageSize[maxIndex]) {
        			maxIndex = k;
        		}
        	}
        	canbeUsed[maxIndex] = false;
        }
        return canbeUsed;
    }

	@Override
	public String getSimpleName() {
		return "Packing";
	}
}
