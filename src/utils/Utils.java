package utils;

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import motifs.Subgraph;

public class Utils {
    public static IntArrayList getTimestamps(Int2ObjectAVLTreeMap<Int2IntOpenHashMap> mapTimes) {
        IntArrayList tmp = new IntArrayList();
        for(int timestamp: mapTimes.keySet() ) {
            tmp.add(timestamp);
        }
        return tmp;
    }

    public static IntArrayList getAdjNodesFromMapAdiacs(Int2IntOpenHashMap mapAdiacs) {
        IntArrayList tmp = new IntArrayList();
        for(int timestamp: mapAdiacs.keySet() ) {
            tmp.add(timestamp);
        }
        return tmp;
    }

    public static boolean subgraphListContains( ObjectArrayList<Subgraph> minedSubgraphs, Subgraph subgraph) {
        for(Subgraph currentSubgraph : minedSubgraphs) {
            if(currentSubgraph.equals(subgraph)) {
                return true;
            }
        }
        return false;
    }
}
