package graph;

import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.objects.*;

import java.util.*;

//Class Graph: implements temporal graphs

public class Graph
{
    private final boolean directed;  //Is the network directed?
    private final Int2IntOpenHashMap nodeLabs; //Map node id to its corresponding label (Labels
    private final ObjectArrayList<int[]> edgeProps;  //Map each edge id (ids starting from 0) to its properties, i.e. the pair (label, timestamp)
    private final Int2ObjectOpenHashMap<Int2ObjectAVLTreeMap<Int2IntOpenHashMap>> outAdjLists;
    //Adjacency list of out-adiacent nodes.
    //Adjacent nodes are grouped by timestamps, so the first map is sorted and maps each node id to a second map, indexed by timestamp.
    //The second map maps each timestamp to the a third map, which maps each adjacent node in that timestamp to the corresponding edge id
    private final Int2ObjectOpenHashMap<Int2ObjectAVLTreeMap<Int2IntOpenHashMap>> inAdjLists; //Adjacency list of in-adiacent nodes
    private final Int2ObjectOpenHashMap<Int2ObjectAVLTreeMap<Int2IntOpenHashMap>> recipAdjLists;
    //Adjacency list of reciprocal-adiacent nodes. This list is used only for undirected networks

    public Graph(boolean dir)
    {
        directed=dir;
        nodeLabs=new Int2IntOpenHashMap();
        edgeProps=new ObjectArrayList<>();
        outAdjLists=new Int2ObjectOpenHashMap<>();
        //If the key is not present in the map, return null
        outAdjLists.defaultReturnValue(null);
        inAdjLists=new Int2ObjectOpenHashMap<>();
        inAdjLists.defaultReturnValue(null);
        recipAdjLists=new Int2ObjectOpenHashMap<>();
        recipAdjLists.defaultReturnValue(null);
    }

    //Is the network directed?
    public boolean isDirected()
    {
        return directed;
    }

    //Number of network nodes
    public int getNumNodes()
    {
        return nodeLabs.size();
    }

    //Number of network edges
    public int getNumEdges()
    {
        return edgeProps.size();
    }

    public Int2IntOpenHashMap getNodeLabs()
    {
        return nodeLabs;
    }

    public ObjectArrayList<int[]> getEdgeProps()
    {
        return edgeProps;
    }

    public Int2ObjectOpenHashMap<Int2ObjectAVLTreeMap<Int2IntOpenHashMap>> getOutAdjLists()
    {
        return outAdjLists;
    }

    public Int2ObjectOpenHashMap<Int2ObjectAVLTreeMap<Int2IntOpenHashMap>> getInAdjLists()
    {
        return inAdjLists;
    }

    public Int2ObjectOpenHashMap<Int2ObjectAVLTreeMap<Int2IntOpenHashMap>> getRecipAdjLists()
    {
        return recipAdjLists;
    }

    //Add a new node to the network with ID id and label lab
    public void addNode(int id, int lab)
    {
        //If present, the node is not added
        nodeLabs.putIfAbsent(id,lab);
        //Initialize adjacency lists of the node
        if(directed)
        {
            outAdjLists.putIfAbsent(id,new Int2ObjectAVLTreeMap<>());
            inAdjLists.putIfAbsent(id,new Int2ObjectAVLTreeMap<>());
        }
        else
            recipAdjLists.putIfAbsent(id,new Int2ObjectAVLTreeMap<>());
    }

    //Add a new edge to the network from source node to dest node having a certain timestamp and label
    public void addEdge(int idSource, int idDest, int timestamp, int lab)
    {
        //Assign a progressive number as id for the edge (starting from 0)
        int idEdge=edgeProps.size();
        //Store edge timestamp and label
        int[] props=new int[2];
        props[0]=timestamp;
        props[1]=lab;
        edgeProps.add(props);
        //Network is directed, update both in- and out- adjacency lists
        if(directed)
        {
            Int2ObjectAVLTreeMap<Int2IntOpenHashMap> mapTimes=outAdjLists.get(idSource);
            Int2IntOpenHashMap mapAdiacs=mapTimes.get(timestamp);
            if(mapAdiacs==null)
            {
                //This is a new timestamp for source node adjacency list
                mapAdiacs=new Int2IntOpenHashMap();
                //Map id of destination node to edge id
                mapAdiacs.put(idDest,idEdge);
                //Map timestamp to dest node and associated edge id
                mapTimes.put(timestamp,mapAdiacs);
            }
            else
                //Timestamp exists. Just map timestamp to dest node and associated edge id
                mapAdiacs.put(idDest,idEdge);
            //Do the same for in-adjacency list of destination node
            mapTimes=inAdjLists.get(idDest);
            mapAdiacs=mapTimes.get(timestamp);
            if(mapAdiacs==null)
            {
                mapAdiacs=new Int2IntOpenHashMap();
                mapAdiacs.put(idSource,idEdge);
                mapTimes.put(timestamp,mapAdiacs);
            }
            else
                mapAdiacs.put(idSource,idEdge);
        }
        else
        {
            //Update only reciprocal adjacency list
            Int2ObjectAVLTreeMap<Int2IntOpenHashMap> mapTimes=recipAdjLists.get(idSource);
            Int2IntOpenHashMap mapAdiacs=mapTimes.get(timestamp);
            if(mapAdiacs==null)
            {
                mapAdiacs=new Int2IntOpenHashMap();
                mapAdiacs.put(idDest,idEdge);
                mapTimes.put(timestamp,mapAdiacs);
            }
            else
                mapAdiacs.put(idDest,idEdge);
            mapTimes=recipAdjLists.get(idDest);
            mapAdiacs=mapTimes.get(timestamp);
            if(mapAdiacs==null)
            {
                mapAdiacs=new Int2IntOpenHashMap();
                mapAdiacs.put(idSource,idEdge);
                mapTimes.put(timestamp,mapAdiacs);
            }
            else
                mapAdiacs.put(idSource,idEdge);
        }
    }

    //Print network information
    public String toString()
    {
        String str="NODES:\n";
        for(int idNode : nodeLabs.keySet())
            str+=idNode+":"+nodeLabs.get(idNode)+"\n";
        str+="\nEDGES:\n";
        Int2ObjectOpenHashMap<Int2ObjectAVLTreeMap<Int2IntOpenHashMap>> adjLists=outAdjLists;
        if(!directed)
            adjLists=recipAdjLists;
        for (Int2ObjectMap.Entry<Int2ObjectAVLTreeMap<Int2IntOpenHashMap>> source : adjLists.int2ObjectEntrySet())
        {
            int idSource=source.getIntKey();
            Int2ObjectAVLTreeMap<Int2IntOpenHashMap> mapTimes=source.getValue();
            for (Int2ObjectMap.Entry<Int2IntOpenHashMap> e : mapTimes.int2ObjectEntrySet())
            {
                Int2IntOpenHashMap mapAdiacs=e.getValue();
                for (Int2IntMap.Entry dest : mapAdiacs.int2IntEntrySet())
                {
                    int idDest=dest.getIntKey();
                    if(directed || idSource<idDest)
                    {
                        int edge=dest.getIntValue();
                        int[] props=edgeProps.get(edge);
                        str+="("+idSource+","+idDest+","+props[0]+":"+props[1]+")\n";
                    }
                }
            }
        }
        str+="\n";
        return str;
    }

    //Check if two nodes are neighbors (i.e. they are connected by an edge with any timestamp)
    public boolean areNeighbors(int idNode1, int idNode2)
    {
        Int2ObjectAVLTreeMap<Int2IntOpenHashMap> mapTimes=outAdjLists.get(idNode1);
        if(mapTimes!=null)
        {
            for(Int2ObjectMap.Entry<Int2IntOpenHashMap> e : mapTimes.int2ObjectEntrySet())
            {
                Int2IntOpenHashMap mapAdiacs=e.getValue();
                if(mapAdiacs.containsKey(idNode2))
                    return true;
            }
        }
        mapTimes=inAdjLists.get(idNode1);
        if(mapTimes!=null)
        {
            for(Int2ObjectMap.Entry<Int2IntOpenHashMap> e : mapTimes.int2ObjectEntrySet())
            {
                Int2IntOpenHashMap mapAdiacs=e.getValue();
                if(mapAdiacs.containsKey(idNode2))
                    return true;
            }
        }
        mapTimes=recipAdjLists.get(idNode1);
        if(mapTimes!=null)
        {
            for (Int2ObjectMap.Entry<Int2IntOpenHashMap> e : mapTimes.int2ObjectEntrySet())
            {
                Int2IntOpenHashMap mapAdiacs=e.getValue();
                if(mapAdiacs.containsKey(idNode2))
                    return true;
            }
        }
        return false;
    }

    //Get the S-relative degree, i.e. the number of nodes in a set S that are connected to node n
    public int getDegree(int node, IntOpenHashSet setRefNodes)
    {
        int deg=0;
        for(int refNode : setRefNodes)
        {
            if(areNeighbors(node,refNode))
                deg++;
        }
        return deg;
    }
}
