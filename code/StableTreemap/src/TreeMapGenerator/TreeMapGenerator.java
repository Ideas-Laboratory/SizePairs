package TreeMapGenerator;

import treemap.DataFaciliation.DataFacilitator;
import treemap.dataStructure.DataMap;
import treemap.dataStructure.Rectangle;
import treemap.dataStructure.TreeMap;

/**
 *
 * @author Max Sondag
 */
public interface TreeMapGenerator {

    public abstract TreeMap generateTreeMap(DataMap dataMap, Rectangle treeMapRectangle, int time);

    public String getParamaterDescription();

    public TreeMapGenerator reinitialize();

    public String getSimpleName();
}
