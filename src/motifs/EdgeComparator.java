package motifs;

import java.util.Comparator;

public class EdgeComparator implements Comparator<Edge> {
    @Override
    public int compare(Edge firstEdge, Edge secondEdge) {
        if(firstEdge.getTimestamp() < secondEdge.getTimestamp()) {
            return -1;
        } else if (firstEdge.getTimestamp() > secondEdge.getTimestamp()){
            return 1;
        } 
        
        if(firstEdge.getSource() < secondEdge.getSource()){ 
            return -1;
        } else if(firstEdge.getSource() > secondEdge.getSource()){ 
            return 1;
        }

        if(firstEdge.getDestination() < secondEdge.getDestination()){ 
            return -1;
        } else if(firstEdge.getDestination() > secondEdge.getDestination()){ 
            return 1;
        }

        // This case should never happen. There cannot be an edge with the same timestamp, source and destination.
        return Integer.compare(firstEdge.getId(), secondEdge.getId());
    }

}
