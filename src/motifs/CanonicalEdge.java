package motifs;

public class CanonicalEdge {
    int destinationId;
    int timestamp;
    int label;


    public CanonicalEdge() {
    }

    public CanonicalEdge(int destinationId, int timestamp, int label) {
        this.destinationId = destinationId;
        this.timestamp = timestamp;
        this.label = label;
    }

    public int getDestinationId() {
        return this.destinationId;
    }

    public void setDestinationId(int destinationId) {
        this.destinationId = destinationId;
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

    public CanonicalEdge destinationId(int destinationId) {
        setDestinationId(destinationId);
        return this;
    }

    public CanonicalEdge timestamp(int timestamp) {
        setTimestamp(timestamp);
        return this;
    }

    public CanonicalEdge label(int label) {
        setLabel(label);
        return this;
    }

    @Override
    public String toString() {
        return "(id: " + getDestinationId() + " t: " + getTimestamp() + " l: " + getLabel() + ")";
    }

}
