package motifs;

import java.util.Comparator;


import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class Subgraph {
    private IntArrayList nodes;
    private ObjectArrayList<Edge> edges;
    
    private IntArrayList edgesIdList;

    private Int2IntOpenHashMap inDegrees;
    private Int2IntOpenHashMap outDegrees;

    private Int2ObjectOpenHashMap<ObjectArrayList<Edge>> outAdj;
    private Int2ObjectOpenHashMap<ObjectArrayList<Edge>> inAdj;

    private Int2ObjectOpenHashMap<String> nodesOutSignature;
    private Int2ObjectOpenHashMap<String> nodesInSignature;


    public Subgraph() {
        nodes = new IntArrayList();
        edges = new ObjectArrayList<>();

        edgesIdList = new IntArrayList();
    }

    public IntArrayList getNodes() {
        return this.nodes;
    }

    protected void setNodes(IntArrayList nodes) {
        this.nodes = nodes;
    }

    public ObjectArrayList<Edge> getEdges() {
        return this.edges;
    }

    protected void setEdges(ObjectArrayList<Edge> edges) {
        this.edges = edges;
    }


    public IntArrayList getEdgesIdList() {
        return this.edgesIdList;
    }

    public void setEdgesIdList(IntArrayList edgesIdList) {
        this.edgesIdList = edgesIdList;
    }

    public void addNode(int node) {
        if(!this.nodes.contains(node)) {
            this.nodes.add(node);
        }
    }

    public void addEdge(Edge edge) {
        this.edges.add(edge);

        this.edgesIdList.add(edge.getId());
        this.edgesIdList.sort(new MyIntegerComparator());
    }

    public boolean containsNode(int id) {
        return nodes.contains(id);
    }

    public boolean containsEdge(int id) {
        for(Edge edge : edges) {
            if(edge.getId() == id) {
                return true;
            }
        }

        return false;
    }

    public int getNumNodes() {
        return this.nodes.size();
    }

    public int getNumEdges() {
        return this.edges.size();
    }

    public static Subgraph clone(Subgraph subgraph) {
        Subgraph tmp = new Subgraph();
        tmp.nodes = subgraph.getNodes().clone();
        tmp.edges = subgraph.getEdges().clone();
        tmp.edgesIdList = subgraph.getEdgesIdList().clone();

        return tmp;
    }

    public IntArrayList getOrderedEdgesIdList() {
        return edgesIdList;
    }
    

    public boolean equals(Subgraph subgraph) {    
        return (this.getOrderedEdgesIdList().equals(subgraph.getOrderedEdgesIdList()));
    }

    @Override
    public String toString() {
        return "\n{" +
            " nodes='" + getNodes() + "'" +
            ", edges='" + getEdges() + "'" +
            "}";
    }

    public CanonicalSubgraph computeCanonization(boolean isDirected, Int2IntOpenHashMap nodeLabs) {
        // Timestamp standardization
        IntArrayList timestamps = new IntArrayList();

        for(Edge edge : edges) {
            if(!timestamps.contains(edge.getTimestamp())) {
                timestamps.add(edge.getTimestamp());
            }
        }

        timestamps.sort(new MyIntegerComparator());

        ObjectArrayList<Edge> standardizedEdges = new ObjectArrayList<Edge>(); 

        for(Edge edge : edges) {
            Edge standardizedEdge = Edge.clone(edge);

            standardizedEdge.setTimestamp(timestamps.indexOf(edge.getTimestamp()));

            standardizedEdges.add(standardizedEdge);
        }

        edges = standardizedEdges;

        // Initialization
        inDegrees = new Int2IntOpenHashMap();
        outDegrees = new Int2IntOpenHashMap();

        outAdj = new Int2ObjectOpenHashMap<ObjectArrayList<Edge>>();
        inAdj = new Int2ObjectOpenHashMap<ObjectArrayList<Edge>>();

        inDegrees = new Int2IntOpenHashMap();
        outDegrees = new Int2IntOpenHashMap();

        for(int node : nodes) {
            this.outDegrees.put(node, 0);
            this.outAdj.put(node, new ObjectArrayList<Edge>());
            
            if(isDirected) {    
                this.inDegrees.put(node, 0);
                this.inAdj.put(node, new ObjectArrayList<Edge>());
            }
        }

        for(Edge edge: edges) {
            int source = edge.getSource();
            int destination = edge.getDestination();
        
            this.outAdj.get(source).add(edge);
            this.outDegrees.addTo(source, 1);
            
            if(isDirected) {
                this.inAdj.get(destination).add(edge);
                this.inDegrees.addTo(destination, 1);
            } else { // During the subgraph search, of the target is undirected, we consider only one edge. Here we consider tweo edges. (A->B, B->A).
                this.outAdj.get(destination).add(new Edge(edge.getId(), edge.getDestination(), edge.getSource(), edge.getTimestamp(), edge.getLabel()));
                this.outDegrees.addTo(source, 1);
            }
        }

        // Nodes signatures
        TripleComparator tripleComparator = new TripleComparator();
        nodesOutSignature = new Int2ObjectOpenHashMap<String>();
        nodesInSignature = new Int2ObjectOpenHashMap<String>();

        for(int node: nodes) {
            ObjectArrayList<Triple> currentOut = new ObjectArrayList<Triple>();
            
            for(Edge edge : outAdj.get(node)) {
                currentOut.add(new Triple(edge.getTimestamp(), edge.getLabel(), nodeLabs.get(edge.getDestination())));
            }

            currentOut.sort(tripleComparator);

            String currentOutSignature = new String("");

            for(Triple triple: currentOut) {
                currentOutSignature += triple.toString();
            }

            nodesOutSignature.put(node, currentOutSignature);

            if(isDirected) {
                ObjectArrayList<Triple> currentIn = new ObjectArrayList<Triple>();
            
                for(Edge edge : inAdj.get(node)) {
                    currentIn.add(new Triple(edge.getTimestamp(), edge.getLabel(), nodeLabs.get(edge.getSource())));
                }

                currentIn.sort(tripleComparator);

                String currentInSignature = new String("");

                for(Triple triple: currentOut) {
                    currentInSignature += triple.toString();
                }

                nodesInSignature.put(node, currentInSignature);
            }

        }

        // Nodes ordering
        Int2IntOpenHashMap canonMap = new Int2IntOpenHashMap();
        Int2IntOpenHashMap used = new Int2IntOpenHashMap(); // 0 = unused, 1 = used
        IntArrayList setAdiacs = new IntArrayList();

        for(int node: nodes) {
            used.put(node, 0);
            setAdiacs.add(node);
        }

        for(int i = 0; i < nodes.size(); i++) {
            int maxDegOut = -1;
            int maxDegIn = -1;
            int maxLabel = -1;
            int maxId = -1;

            for(int node : setAdiacs) {
                if(outDegrees.get(node) > maxDegOut) { // Out-degree
                    maxDegOut = outDegrees.get(node);
                    maxDegIn = inDegrees.get(node);
                    maxId = node;
                    maxLabel = nodeLabs.get(node);
                } else if(outDegrees.get(node) == maxDegOut) { // In-degree
                    if(inDegrees.get(node) > maxDegIn) {
                        maxDegIn = inDegrees.get(node);
                        maxId = node;
                        maxLabel = nodeLabs.get(node);
                    } else if(inDegrees.get(node) == maxDegIn) { //Labels
                        if(nodeLabs.get(node) > maxLabel) {
                            maxId = node;
                            maxLabel = nodeLabs.get(node);
                        } else if(nodeLabs.get(node) == maxLabel) { // Signature
                            String currentMaxOutSignature = nodesOutSignature.get(maxId);
                            String candidateMaxOutSignature = nodesOutSignature.get(node);
                            
                            if(currentMaxOutSignature.compareTo(candidateMaxOutSignature) < 0) { // currentMaxOut < candidateMaxOut
                                maxId = node;
                            } else if(isDirected && currentMaxOutSignature.compareTo(candidateMaxOutSignature) == 0) { // currentMaxOut == candidateMaxOut
                                String currentMaxInSignature = nodesInSignature.get(maxId);
                                String candidateMaxInSignature = nodesInSignature.get(node);
    
                                if(currentMaxInSignature.compareTo(candidateMaxInSignature) < 0) { // currentMaxOut < candidateMaxOut
                                    maxId = node;
                                }
                            }
                        }
                    } 
                }
            }

            canonMap.put(maxId, i);
            used.put(maxId, 1);
            setAdiacs.clear();

            for(int currentId: canonMap.keySet()) {
                for(Edge edge: outAdj.get(currentId)) {
                    if(used.get(edge.getDestination()) == 0 && !setAdiacs.contains(edge.getDestination())) {
                        setAdiacs.add(edge.getDestination());
                    }
                }

                if(isDirected) {
                    for(Edge edge: inAdj.get(currentId)) {
                        if(used.get(edge.getSource()) == 0 && !setAdiacs.contains(edge.getSource())) {
                            setAdiacs.add(edge.getSource());
                        }
                    }
                }
            }

            if(setAdiacs.isEmpty()) {
                for(int node: nodes) {
                    if(used.get(node) == 0) {
                        setAdiacs.add(node);
                    }
                }
            }
        }

        // Canonical form
        CanonicalSubgraph canonicalSubgraph = new CanonicalSubgraph();

        Int2IntOpenHashMap canonicalNodeLabs = new Int2IntOpenHashMap();
        Int2ObjectOpenHashMap<ObjectArrayList<CanonicalEdge>> canonicalOutAdj = new Int2ObjectOpenHashMap<ObjectArrayList<CanonicalEdge>>();

        CanonicalEdgeComparator canonicalEdgeComparator = new CanonicalEdgeComparator();

        for(int id: canonMap.keySet()) {
            int index = canonMap.get(id);
            int label = nodeLabs.get(id);

            canonicalNodeLabs.put(index, label);

            ObjectArrayList<CanonicalEdge> currentOutAdj = new ObjectArrayList<CanonicalEdge>();

            for(Edge edge: outAdj.get(id)) {
                currentOutAdj.add(new CanonicalEdge(canonMap.get(edge.getDestination()), edge.getTimestamp(), edge.getLabel()));
            }

            currentOutAdj.sort(canonicalEdgeComparator);

            canonicalOutAdj.put(index, currentOutAdj);
        }

        canonicalSubgraph.setNodeLabs(canonicalNodeLabs);
        canonicalSubgraph.setOutAdj(canonicalOutAdj);

        return canonicalSubgraph;
    }

}

class MyIntegerComparator implements IntComparator {
    @Override
    public int compare(int first, int second) {
        return Integer.compare(first, second);
    }

}

class Triple {
    int timestamp;
    int edgeLabel;
    int nodeLabel;

    public Triple() {
    }

    public Triple(int timestamp, int edgeLabel, int nodeLabel) {
        this.timestamp = timestamp;
        this.edgeLabel = edgeLabel;
        this.nodeLabel = nodeLabel;
    }

    public int getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public int getEdgeLabel() {
        return this.edgeLabel;
    }

    public void setEdgeLabel(int edgeLabel) {
        this.edgeLabel = edgeLabel;
    }

    public int getNodeLabel() {
        return this.nodeLabel;
    }

    public void setNodeLabel(int nodeLabel) {
        this.nodeLabel = nodeLabel;
    }

    public Triple timestamp(int timestamp) {
        setTimestamp(timestamp);
        return this;
    }

    public Triple edgeLabel(int edgeLabel) {
        setEdgeLabel(edgeLabel);
        return this;
    }

    public Triple nodeLabel(int nodeLabel) {
        setNodeLabel(nodeLabel);
        return this;
    }

    @Override
    public String toString() {
        return "{" +
            " t" + getTimestamp()  +
            "el" + getEdgeLabel()  +
            "nl" + getNodeLabel()  +
            "}";
    }

}

class TripleComparator implements Comparator<Triple> {
    @Override
    public int compare(Triple firstTriple, Triple secondTriple) {
        // Timestamp
        if(firstTriple.getTimestamp() < secondTriple.getTimestamp()) {
            return -1;
        } else if(firstTriple.getTimestamp() > secondTriple.getTimestamp()) {
            return 1;
        }

        // Edge label
        if(firstTriple.getEdgeLabel() < secondTriple.getEdgeLabel()) {
            return -1;
        } else if(firstTriple.getEdgeLabel() > secondTriple.getEdgeLabel()) {
            return 1;
        }

        // Node label
        if(firstTriple.getNodeLabel() < secondTriple.getNodeLabel()) {
            return -1;
        } else if(firstTriple.getNodeLabel() > secondTriple.getNodeLabel()) {
            return 1;
        }

        // Equals
        return 0;
    }

}
