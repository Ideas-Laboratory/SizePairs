package statistics.Stability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import treemap.dataStructure.Rectangle;
import treemap.dataStructure.TreeMap;

/**
 *
 * @author max
 */
public class RelativeQuadrantStability {

    public double getBaselineInstability(TreeMap oldTm, TreeMap newTm, TreeMap baseTm) {
        //identical for baseline and new
        List<TreeMap> oldRemaining = getOldRemainingLeafs(oldTm, newTm);

        //edge cases
        if (oldRemaining.isEmpty()) {
            //nothing left due to insertions and deltions. So completely instable
            return 0;
        }
        if (oldRemaining.size() == 1) {
            //Only 1 node in the old treemap left.
            return 1;
        }

        //holds the stability score in the end. It is composed of the average stability
        //score of all items
        double instability = 0;

        HashMap<String, TreeMap> newMapping = new HashMap();
        for (TreeMap tmNew : newTm.getAllLeafs()) {
            newMapping.put(tmNew.getLabel(), tmNew);
        }
        HashMap<String, TreeMap> baseLineMapping = new HashMap();
        for (TreeMap tmBase : baseTm.getAllLeafs()) {
            baseLineMapping.put(tmBase.getLabel(), tmBase);
        }

        for (TreeMap tmOld1 : oldRemaining) {
            TreeMap tmNew1 = newMapping.get(tmOld1.getLabel());
            TreeMap tmBase1 = baseLineMapping.get(tmOld1.getLabel());
            double tmStabNew = 0;
            double tmStabBase = 0;
            for (TreeMap tmOld2 : oldRemaining) {
                //find the one with the correct label
                if (tmOld1.getLabel().equals(tmOld2.getLabel())) {
                    continue;
                }
                //get the corresponding new treemap
                TreeMap tmNew2 = newMapping.get(tmOld2.getLabel());
                TreeMap tmBase2 = baseLineMapping.get(tmOld2.getLabel());

                double itemInstabilityNew = getQuadrantInstability(tmOld1, tmOld2, tmNew1, tmNew2);
                tmStabNew += itemInstabilityNew;

                double itemInstabilityBase = getQuadrantInstability(tmOld1, tmOld2, tmBase1, tmBase2);
                tmStabBase += itemInstabilityBase;
            }

            //calculate baseline score outside of the loop. In case it dissappeared, baseline score is equal to the original score.
            instability += Math.max(0, tmStabNew - tmStabBase);//itemStability;
        }
        //Normalize the stability score
        instability = instability / (Math.pow(oldRemaining.size(), 2) - oldRemaining.size());

        return instability;
    }

    public double getInstability(TreeMap oldTm, TreeMap newTm) {

        List<TreeMap> oldRemaining = getOldRemainingLeafs(oldTm, newTm);

        //edge cases
        if (oldRemaining.isEmpty()) {
            //nothing left due to insertions and deltions. So completely instable
            return 0;
        }
        if (oldRemaining.size() == 1) {
            //Only 1 node in the old treemap left.
            return 1;
        }

        //holds the instability score in the end. It is composed of the average stability
        //score of all items
        double instability = 0;

        HashMap<String, TreeMap> newMapping = new HashMap();
        for (TreeMap tmNew : newTm.getAllLeafs()) {
            newMapping.put(tmNew.getLabel(), tmNew);
        }
        for (TreeMap tmOld1 : oldRemaining) {
            TreeMap tmNew1 = newMapping.get(tmOld1.getLabel());
            for (TreeMap tmOld2 : oldRemaining) {
                if (tmOld1.getLabel().equals(tmOld2.getLabel())) {
                    continue;
                }
                //get the corresponding new treemap
                TreeMap tmNew2 = newMapping.get(tmOld2.getLabel());

                if (tmNew1 == null || tmNew2 == null) {
                    System.out.println("");
                }

                if (tmNew1.getRectangle() == null || tmNew2.getRectangle() == null) {
                    System.out.println("");
                }

                double itemInstability = getQuadrantInstability(tmOld1, tmOld2, tmNew1, tmNew2);
                instability += itemInstability;
            }
        }
        //Normalize the instability score
        instability = instability / (Math.pow(oldRemaining.size(), 2) - oldRemaining.size());

        return instability;
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

    private List<TreeMap> getNewRemainingLeafs(TreeMap oldTm, TreeMap newTm) {
        List<TreeMap> remainingLeafs = new ArrayList();
        for (TreeMap tm1 : newTm.getAllLeafs()) {
            TreeMap tm2 = oldTm.getChildWithLabel(tm1.getLabel());
            if (tm2 != null) {
                //tm1 was present in both
                remainingLeafs.add(tm1);
            }
        }
        return remainingLeafs;
    }

    /**
     * Returns an array of size 8 containing the percentage percentage of r2
     * that lies in the respective quadrant. Quadrants are encoded as follows:
     * 0:east,1norhteast,2north,3northwest,4west,5southwest,6south,7southeast
     *
     * @param tmOld
     * @param tmOld2
     * @return
     */
    private Double[] getQuadrantPercentages(Rectangle r1, Rectangle r2) {
        //check in which quardrants it lies
        double E = 0, NE = 0, N = 0, NW = 0, W = 0, SW = 0, S = 0, SE = 0;
        if (r2.getX() >= r1.getX2()) {
            //Strictly east
            if (r2.getY() < r1.getY()) {
                //at least partially in NE

                //get the percentage that r2 is in NE
                NE = (r1.getY() - r2.getY()) / r2.getHeight();
                NE = Math.min(1, NE);
            }
            if (r2.getY2() > r1.getY2()) {
                //at least partiall in SE
                SE = (r2.getY2() - r1.getY2()) / r2.getHeight();
                SE = Math.min(1, SE);
            }
            //remainder is in east
            E = 1 - NE - SE;

        } else if (r2.getX2() <= r1.getX()) {
            //strictly west
            if (r2.getY() < r1.getY()) {
                //at least partially in NW

                //get the percentage that r2 is in NW
                NW = (r1.getY() - r2.getY()) / r2.getHeight();
                NW = Math.min(1, NW);
            }
            if (r2.getY2() > r1.getY2()) {
                //at least partiall in SW
                SW = (r2.getY2() - r1.getY2()) / r2.getHeight();
                SW = Math.min(1, SW);
            }
            //remainder is in west
            W = 1 - NW - SW;
        } else if (r2.getY2() <= r1.getY()) {
            //strictly North
            if (r2.getX() < r1.getX()) {
                //at least partially in NW

                //get the percentage that r2 is in NW
                NW = (r1.getX() - r2.getX()) / r2.getWidth();
                NW = Math.min(1, NW);
            }
            if (r2.getX2() > r1.getX2()) {
                //at least partiall in SW
                NE = (r2.getX2() - r1.getX2()) / r2.getWidth();
                NE = Math.min(1, NE);
            }
            //remainder is in west
            N = 1 - NW - NE;
        } else {
            //strictly south
            if (r2.getX() < r1.getX()) {
                //at least partially in SW

                //get the percentage that r2 is in NW
                SW = (r1.getX() - r2.getX()) / r2.getWidth();
                SW = Math.min(1, SW);
            }
            if (r2.getX2() > r1.getX2()) {
                //at least partiall in SE
                SE = (r2.getX2() - r1.getX2()) / r2.getWidth();
                SE = Math.min(1, SE);
            }
            //remainder is in west
            S = 1 - SW - SE;
        }

        Double[] quadrant = new Double[8];
        quadrant[0] = E;
        quadrant[1] = NE;
        quadrant[2] = N;
        quadrant[3] = NW;
        quadrant[4] = W;
        quadrant[5] = SW;
        quadrant[6] = S;
        quadrant[7] = SE;
        return quadrant;
    }

    /**
     * returns the instability of the movement between quadrants
     *
     * @param oldPercentages
     * @param newPercentages
     * @return
     */
    private double getQuadrantInstability(Double[] oldPercentages, Double[] newPercentages) {
        double instability = 0;
        for (int i = 0; i < oldPercentages.length; i++) {
            double oldPercentage = oldPercentages[i];
            double newPercentage = newPercentages[i];

            instability += Math.abs(oldPercentage - newPercentage) / 2;
        }
        //handle rounding errors
        instability = Math.min(1.0, Math.max(instability, 0.0));
        return instability;
    }

    private double getQuadrantInstability(TreeMap tmOld1, TreeMap tmOld2, TreeMap tmNew1, TreeMap tmNew2) {

        if (tmNew1 == null) {
            System.out.println("tmNew1 is null.");
            System.out.println("tmOld1.getLabel() = " + tmOld1.getLabel());
            System.out.println("tmOld1.getTargetSize() = " + tmOld1.getTargetSize());
        }
        if (tmNew2 == null) {
            System.out.println("tmNew2 is null.");
            System.out.println("tmOld2.getLabel() = " + tmOld2.getLabel());
            System.out.println("tmOld2.getTargetSize() = " + tmOld2.getTargetSize());
        }

        Double[] oldPercentages = getQuadrantPercentages(tmOld1.getRectangle(), tmOld2.getRectangle());
        Double[] newPercentages = getQuadrantPercentages(tmNew1.getRectangle(), tmNew2.getRectangle());

        double itemStability = getQuadrantInstability(oldPercentages, newPercentages);
        return itemStability;
    }
}
