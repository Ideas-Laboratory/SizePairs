package treemap.DataFaciliation;

import com.opencsv.CSVReader;
import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import treemap.dataStructure.DataMap;

/**
 *
 * @author Max Sondag
 */
public class DataFileManager implements DataFacilitator {

    File inputFile;
    Map<Integer, DataMap> timeMap;
    private int time = 0;
    private String inputFileLocation;
    private boolean header;

    public DataFileManager(String inputFileLocation, boolean header) {
        this.inputFile = new File(inputFileLocation);
        this.header = header;
        timeMap = new HashMap();
        if (inputFile.toString().endsWith(".csv"));
        {
            readCSVFile();
        }
        this.inputFileLocation = inputFileLocation;
    }

    private void readCSVFile() {
        /**
         * Format of csv files should be as follows: id, parentId(root if not
         * present), size at time 0, size at time 1 ..... First line should
         * contain the headers
         */
        try {
            CSVReader reader = new CSVReader(new FileReader(inputFile), ',');
            String[] nextLine;

            List<StoredData> dataList = new LinkedList();

            //whether color values are included or note
            int hasColor = 0;
            //parse the header if present
            if (header) {
                nextLine = reader.readNext();
                if (nextLine[2].equals("Color")) {
                    hasColor = 1;
                }
            }

            while ((nextLine = reader.readNext()) != null) {
                String id = nextLine[0];
                String parentId = nextLine[1];

                Color color = null;
                if (hasColor != 0) {
                    String hexValue = nextLine[2];
                    color = Color.decode(hexValue);
                }

                List sizes = new LinkedList();
                for (int i = (2 + hasColor); i < nextLine.length; i++) {
                    double size = Double.parseDouble(nextLine[i]);
                    sizes.add(size);
                }

                StoredData data = new StoredData(id, parentId, sizes, color);
                dataList.add(data);
            }

            //Go through all the times, convert them to datamaps and add them to the mapping
            float startHue = 226f / 360f;
            int maxTime = dataList.get(0).getDataAmount();
            for (int time = 0; time < maxTime; time++) {
                DataMap convertToDataMap = convertToDataMap("root", dataList, time, startHue, 0, 0);
                timeMap.put(time, convertToDataMap);
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(DataFileManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DataFileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private int count = 0;

    /**
     *
     * @param id          The id of the datamap we are
     * @param dataList    the complete list of data
     * @param time        the time identifier
     * @param startHue    the starting hue in case no colors were specified
     * @param childCount  how many children the parent had
     * @param childNumber what the number of this child is explicetely
     * @return
     */
    private DataMap convertToDataMap(String id, List<StoredData> dataList, int time, float startHue, int childCount, int childNumber) {
        Color color;
        StoredData currentSD = getStoredDataElement(dataList, id);

        count++;
        if (currentSD == null || currentSD.getColor() == null) {
            color = getColor(startHue, childNumber, childCount);
        } else {
            color = currentSD.getColor();
        }

        //get the children of the currentSD
        ArrayList<StoredData> storedChildren = new ArrayList();
        for (StoredData sd : dataList) {
            //it is a child of this parent and non-zero size
            double size = sd.getSizes().get(time);
            if (sd.getParentId().equals(id) && size != 0) {
                storedChildren.add(sd);
            }
        }

        //recurse in the children of this sd and get the total size.
        ArrayList<DataMap> children = new ArrayList();
        double childSize = 0;
        for (int i = 0; i < storedChildren.size(); i++) {
            StoredData sd = storedChildren.get(i);
            //recurse into the datamap and add it this this parent
            DataMap dm = convertToDataMap(sd.getId(), dataList, time, startHue, storedChildren.size(), i);
            children.add(dm);
            childSize += dm.getTargetSize();
        }

        double size;

        if (children.isEmpty()) {
            //it was a leaf node it has no children to get the size from
            size = currentSD.getSizes().get(time);
        } else {
            size = childSize;
        }

        return new DataMap(id, size, children, color);
    }

    /**
     * Returns the StoredData element with the given id or null if it does not
     * exist
     *
     * @param dataList
     * @param id
     * @return
     */
    private Color getColor(float startHue, int childNumber, int childCount) {
        float number = childNumber;
        float count = childCount - 1;
        float saturationValue = number / count * 0.6f + 0.2f;
        return Color.getHSBColor(startHue, saturationValue, 1f);
    }

    public StoredData getStoredDataElement(List<StoredData> dataList, String id) {
        for (StoredData sd : dataList) {
            if (sd.getId().equals(id)) {
                return sd;
            }
        }
        return null;
    }

    @Override
    public DataMap getData(int time) {
        this.time = time;
        return timeMap.get(time);
    }

    @Override
    public String getDataIdentifier() {
        return inputFile.getAbsolutePath();
    }

    @Override
    public String getParamaterDescription() {
        return "FileName=" + inputFile.getName();
    }

    @Override
    public String getExperimentName() {
        return inputFile.getName();
    }

    @Override
    public DataFacilitator reinitializeWithSeed(int seed) {
        return new DataFileManager(inputFileLocation, header);
    }

    @Override
    public boolean hasMaxTime() {
        return true;
    }

    @Override
    public int getMaxTime() {
        int maxTime = 0;
        for (int t : timeMap.keySet()) {
            maxTime = Math.max(t, maxTime);
        }
        return maxTime;
    }

    @Override
    public int getLastTime() {
        return time;
    }

    @Override
    public void recolor(HashMap<String, Color> colors) {
        for (DataMap tree : timeMap.values()) {
            for (DataMap dm : tree.getAllChildren()) {
                if (colors.get(dm.getLabel()) != null) {
                    dm.setColor(colors.get(dm.getLabel()));
                }
            }
        }
    }

}
