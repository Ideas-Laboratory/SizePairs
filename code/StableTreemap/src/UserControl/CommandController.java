package UserControl;

import TreeMapGenerator.TreeMapGenerator;
import java.util.Map;
import treemap.DataFaciliation.DataFacilitator;
import treemap.DataFaciliation.DataFileManager;
import treemap.ModelController;
import treemap.dataStructure.DataMap;
import treemap.dataStructure.TreeMap;

/**
 *
 * @author Max Sondag
 */
public abstract class CommandController {

    protected ModelController modelController;

    public CommandController() {
        modelController = new ModelController(this);
    }

    protected void setDataFacilitator(DataFacilitator df) {
        modelController.setDataFacilitator(df);
    }

    protected void setDataFileManager(String fileLocation) {
        DataFileManager dfm = new DataFileManager(fileLocation, false);
        modelController.setDataFacilitator(dfm);
    }

    protected void setTreeMapGenerator(TreeMapGenerator treeMapGenerator) {
        modelController.setTreeMapGenerator(treeMapGenerator);
    }

    protected abstract boolean getTreeMap(int time, boolean useStored, String commandIdentifier, DataMap rootNode);

//    protected abstract boolean getTreeMapAll(boolean useStored, String commandIdentifier, DataFacilitator dataSet);

    /**
     * Updates the current treemap with new weights
     *
     * @param time
     * @return
     */
    protected abstract TreeMap updateCurrentTreeMap(int time);

    public abstract void setStability(Map<String, Double> stabilities);

    public abstract void setAspectRatioBeforeMoves(double maxAspectRatio);

    public abstract void setAspectRatioAfterMoves(double maxAspectRatio);
}
