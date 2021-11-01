package motifs;

import java.util.Comparator;

public class CanonicalEdgeComparator implements Comparator<CanonicalEdge> {
    @Override
    public int compare(CanonicalEdge firstEdge, CanonicalEdge secondEdge) {
        if(firstEdge.getDestinationId() < secondEdge.getDestinationId()) {
            return -1;
        } else if (firstEdge.getDestinationId() > secondEdge.getDestinationId()){
            return 1;
        }

        if(firstEdge.getTimestamp() < secondEdge.getTimestamp()) {
            return -1;
        } else if (firstEdge.getTimestamp() > secondEdge.getTimestamp()){
            return 1;
        }

        if(firstEdge.getLabel() < secondEdge.getLabel()){ // firstEdge.getTimestamp() == secondEdge.getTimestamp()
            return -1;
        } else if(firstEdge.getLabel() > secondEdge.getLabel()){ // firstEdge.getTimestamp() == secondEdge.getTimestamp()
            return 1;
        }

        return 0;
    }

}