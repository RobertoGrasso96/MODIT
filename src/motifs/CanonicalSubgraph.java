package motifs;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.Arrays;

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;


public class CanonicalSubgraph {

   Int2IntOpenHashMap nodeLabs;
    Int2ObjectOpenHashMap<ObjectArrayList<CanonicalEdge>> outAdj;


    public CanonicalSubgraph() {
    }

    public CanonicalSubgraph(Int2IntOpenHashMap nodeLabs, Int2ObjectOpenHashMap<ObjectArrayList<CanonicalEdge>> outAdj) {
        this.nodeLabs = nodeLabs;
        this.outAdj = outAdj;
    }

    public Int2IntOpenHashMap getNodeLabs() {
        return this.nodeLabs;
    }

    public void setNodeLabs(Int2IntOpenHashMap nodeLabs) {
        this.nodeLabs = nodeLabs;
    }

    public Int2ObjectOpenHashMap<ObjectArrayList<CanonicalEdge>> getOutAdj() {
        return this.outAdj;
    }

    public void setOutAdj(Int2ObjectOpenHashMap<ObjectArrayList<CanonicalEdge>> outAdj) {
        this.outAdj = outAdj;
    }

    public CanonicalSubgraph nodeLabs(Int2IntOpenHashMap nodeLabs) {
        setNodeLabs(nodeLabs);
        return this;
    }

    public CanonicalSubgraph outAdj(Int2ObjectOpenHashMap<ObjectArrayList<CanonicalEdge>> outAdj) {
        setOutAdj(outAdj);
        return this;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        int[] keySet = nodeLabs.keySet().toIntArray();
        Arrays.sort(keySet);
        
      for(int key: keySet) {
            sb.append("(id: " + key + " l: " + nodeLabs.get(key) + ")");
        }

        sb.append(", ");

        for(int key: keySet) {
            ObjectArrayList<CanonicalEdge> currentList = outAdj.get(key);
            
            if(currentList.size() == 0) {
                continue;
            }

            sb.append("[" + key + ": ");
            for(CanonicalEdge canonicalEdge : currentList) {
                sb.append(canonicalEdge.toString());
            }
            sb.append("]");
        }

        return sb.toString();
    }


}
