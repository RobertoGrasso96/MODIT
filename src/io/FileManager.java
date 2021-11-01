package io;

import graph.*;
import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class FileManager
{
    //Map node string labels (as reported in the input network file) to integer node labels
    private final Object2IntOpenHashMap<String> mapNodeLabelsToIds;
    //Map node integer labels to original node string labels
    private final ObjectArrayList<String> setNodeLabels;
    //Map edge string labels (as reported in the input network file) to integer edge labels
    private final Object2IntOpenHashMap<String> mapEdgeLabelsToIds;
    //Map edge integer labels to original edge string labels
    private final ObjectArrayList<String> setEdgeLabels;

    public String name;

    public FileManager()
    {
        mapNodeLabelsToIds=new Object2IntOpenHashMap<>();
        setNodeLabels=new ObjectArrayList<>();
        mapEdgeLabelsToIds=new Object2IntOpenHashMap<>();
        setEdgeLabels=new ObjectArrayList<>();
    }

    //Read temporal network (query or target) from file
    //pattern is null if reading a query graph
    public Graph readGraph(String file, boolean directed) throws Exception
    {
        File graphFile = new File(file);
        this.name=graphFile.getName();
        
        //Initialize the temporal graph
        Graph g=new Graph(directed);
      
        BufferedReader br=new BufferedReader(new FileReader(file));
        String str;
        //First line: number of graph nodes
        int numNodes=Integer.parseInt(br.readLine());
        //Read nodes and associated labels
        for(int i=0;i<numNodes;i++)
        {
            str=br.readLine();
            String[] split=str.split("\t");
            int nodeId=Integer.parseInt(split[0]);
            String label=split[1];
            //If the label has never been observed before, map it to a new integer label
            if(!mapNodeLabelsToIds.containsKey(label))
            {
                mapNodeLabelsToIds.put(label,setNodeLabels.size());
                setNodeLabels.add(label);
            }
            int labId=mapNodeLabelsToIds.getInt(label);
           
            g.addNode(nodeId,labId);
        }
        //Read edges
        while((str=br.readLine())!=null)
        {  
            String[] split=str.split("\\s+");  
            int idSource=Integer.parseInt(split[0]);
            int idDest=Integer.parseInt(split[1]);
            //Ignore self edges
            if(idSource!=idDest)
            {
                //Read the list of edges connecting the two nodes
                String[] split2=split[2].split(",");
                for(int i=0;i<split2.length;i++)
                {
                    //For each edge connecting the two nodes, read its timestamp and its label
                    String[] split3=split2[i].split(":");
                    int timestamp=Integer.parseInt(split3[0]);
                    String edgeLab=split3[1];
                    //If the label has never been observed before, map it to a new integer label
                    if(!mapEdgeLabelsToIds.containsKey(edgeLab))
                    {
                        mapEdgeLabelsToIds.put(edgeLab,setEdgeLabels.size());
                        setEdgeLabels.add(edgeLab);
                    }
                    int labId=mapEdgeLabelsToIds.getInt(edgeLab);
                    
                    g.addEdge(idSource,idDest,timestamp,labId);
                }
            }
        }
        br.close();
        return g;
    }

    //Print to standard output the occurrence found (only if dump is enabled)
    public void printOcc(Graph occ)
    {
        //Print occurrence's nodes info
        Int2IntOpenHashMap nodeLabs=occ.getNodeLabs();
        ObjectArrayList<int[]> edgeProps=occ.getEdgeProps();
        Int2ObjectOpenHashMap<Int2ObjectAVLTreeMap<Int2IntOpenHashMap>> outAdjLists=occ.getOutAdjLists();
        IntIterator it=nodeLabs.keySet().iterator();
        int idNode=it.nextInt();
        System.out.print("("+idNode+":"+nodeLabs.get(idNode)+")");
        while(it.hasNext())
        {
            idNode=it.nextInt();
            System.out.print(","+"("+idNode+":"+nodeLabs.get(idNode)+")");
        }
        System.out.print("\t");
        //Print occurrence's edges info
        String edgeStr="";
        for (Int2ObjectMap.Entry<Int2ObjectAVLTreeMap<Int2IntOpenHashMap>> source : outAdjLists.int2ObjectEntrySet())
        {
            int idSource= source.getIntKey();
            Int2ObjectAVLTreeMap<Int2IntOpenHashMap> mapTimes=source.getValue();
            for (Int2ObjectMap.Entry<Int2IntOpenHashMap> time : mapTimes.int2ObjectEntrySet())
            {
                Int2IntOpenHashMap mapAdiacs=time.getValue();
                for (Int2IntMap.Entry dest : mapAdiacs.int2IntEntrySet())
                {
                    int idDest=dest.getIntKey();
                    int edge=dest.getIntValue();
                    int[] props=edgeProps.get(edge);
                    edgeStr+="("+idSource+","+idDest+","+props[0]+":"+props[1]+"),";
                }
            }
        }
        edgeStr=edgeStr.substring(0,edgeStr.length()-1);
        System.out.println(edgeStr);
    }
}
