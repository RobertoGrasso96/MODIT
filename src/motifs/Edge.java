package motifs;

public class Edge {
    private int id;
    private int source;
    private int destination;
    private int timestamp;
    private int label;

    public Edge() {
    }

    
    public Edge(int id, int source, int destination, int timestamp, int label) {
        this.id = id;
        this.source = source;
        this.destination = destination;
        this.timestamp = timestamp;
        this.label = label;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSource() {
        return this.source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public int getDestination() {
        return this.destination;
    }

    public void setDestination(int destination) {
        this.destination = destination;
    }

    public int getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public int getLabel() {
        return this.label;
    }

    public void setLabel(int label) {
        this.label = label;
    }

    public static Edge clone(Edge edge) {
        Edge cloned = new Edge();
        cloned.setId(edge.getId());
        cloned.setLabel(edge.getLabel());
        cloned.setSource(edge.getSource());
        cloned.setDestination(edge.getDestination());
        cloned.setTimestamp(edge.getTimestamp());
        
        return cloned;
    }

    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            " source='" + getSource() + "'" +
            ", destination='" + getDestination() + "'" +
            ", timestamp='" + getTimestamp() + "'" +
            ", label='" + getLabel() + "'" +
            "}";
    }
}
