package treemap.DataFaciliation;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import treemap.dataStructure.DataMap;

public class Dfgenerator implements DataFacilitator{
	List <DataMap> currentData;
	int maxTime;
	public Dfgenerator(List<DataMap> newChildren, int maxTime2) {
		// TODO Auto-generated constructor stub
		currentData = newChildren;
		maxTime = maxTime2;
	}

	@Override
	public DataMap getData(int time) {
		// TODO Auto-generated method stub
		return currentData.get(time);
	}

	@Override
	public String getDataIdentifier() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getExperimentName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getParamaterDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataFacilitator reinitializeWithSeed(int seed) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasMaxTime() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public int getMaxTime() {
		// TODO Auto-generated method stub
		return maxTime;
	}

	@Override
	public int getLastTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void recolor(HashMap<String, Color> colors) {
		// TODO Auto-generated method stub
		
	}

}
