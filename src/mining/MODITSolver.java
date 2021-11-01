package mining;

import com.google.common.collect.TreeMultiset;
import graph.*;
import io.FileManager;
import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import utils.Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import motifs.Edge;
import motifs.Subgraph;
import motifs.EdgeComparator;

public class MODITSolver {
	// Target temporal network
	private final Graph targetGraph;
	// Object that eventually handles the storage of query occurrences into output file
	private final FileManager fm;

	private final int maxNumNodes; // max number of nodes for each subgraph
	private final int maxNumEdges; // max number of edges for each subgraph

	private Int2IntOpenHashMap nodeLabs;
	private ObjectArrayList<int[]> edgeProps;

	private Int2ObjectOpenHashMap<Int2ObjectAVLTreeMap<Int2IntOpenHashMap>> adjLists;
	private Int2ObjectOpenHashMap<Int2ObjectAVLTreeMap<Int2IntOpenHashMap>> inAdjLists; // Just for directed targets

	private int[] orderedNodeKeys;

	private Int2ObjectOpenHashMap<ObjectArrayList<Edge>> edgeLists;
	private Int2ObjectOpenHashMap<ObjectArrayList<Edge>> inEdgeLists; // Just for directed targets
	private EdgeComparator edgeComparator;


	private Object2IntOpenHashMap<String> minedSubgraphsMap;
	private Object2IntOpenHashMap<String> motifsMap;
	

	// Set of timestamps of edges that have been already visited
	private TreeMultiset<Integer> timestampSet;
	private int minTime;
	private int maxTime;
	private int diffTime;

	public MODITSolver(Graph targetGraph, FileManager fm, int maxNumNodes, int maxNumEdges) {
		this.targetGraph = targetGraph;
		this.fm = fm;
		this.edgeComparator = new EdgeComparator();
		this.maxNumNodes = maxNumNodes;
		this.maxNumEdges = maxNumEdges;
	}

	protected void updateBounds(int delta) {
		// Update diffTime, which is the maximum extension value of the time window
		minTime = timestampSet.firstEntry().getElement();
		maxTime = timestampSet.lastEntry().getElement();

		// If delta is not defined, there is no limit to the extension of the temporal
		// window
		if (delta == Integer.MAX_VALUE){
			diffTime = Integer.MAX_VALUE;
		} else{
			diffTime = delta - maxTime + minTime;
		}
	}

	public void findMotifs(int delta) {	
		motifsMap = new Object2IntOpenHashMap<String>();
		
		// Nodes and Edges labels
		nodeLabs = targetGraph.getNodeLabs();
		edgeProps = targetGraph.getEdgeProps();

		// Adj-lists
		if(!targetGraph.isDirected()) {
			adjLists=targetGraph.getRecipAdjLists();
			inAdjLists = null;
		} else {
			adjLists = targetGraph.getOutAdjLists();
			// If the graph is directed, we must also consider inAdj-lists
			inAdjLists = targetGraph.getInAdjLists();
		}

		// Number of nodes
		int numNodes = targetGraph.getNumNodes();

		// Ordered node IDs
		orderedNodeKeys = nodeLabs.keySet().toIntArray();
		Arrays.sort(orderedNodeKeys);

		// Map to get the index from the ID
		Int2IntOpenHashMap mapKeyToIndex = new Int2IntOpenHashMap();

		for (int i = 0; i < numNodes; i++) {
			mapKeyToIndex.put(orderedNodeKeys[i], i);
		}

		// For each node, accessing through the ID, it builds a list of edges
		edgeLists = new Int2ObjectOpenHashMap<ObjectArrayList<Edge>>();
		
		// If the target graph is direct, we must also consider inAdj-lists
		inEdgeLists = new Int2ObjectOpenHashMap<ObjectArrayList<Edge>>();


		for (int i = 0; i < numNodes; i++) {
			// Edges grouped by timestamps
			Int2ObjectAVLTreeMap<Int2IntOpenHashMap> mapTimes = adjLists.get(orderedNodeKeys[i]);

			// Timestamp set
			IntArrayList ts = new IntArrayList();

			for (int time : Utils.getTimestamps(mapTimes)) {
				ts.add(time);
			}

			// Initialization of the edges list for the current node
			edgeLists.put(orderedNodeKeys[i], new ObjectArrayList<Edge>());

			// For each timestamp
			for (int timestamp : ts) {
				// Edges (destNode, edgeId) having the current timestamp
				for (int destNode : mapTimes.get(timestamp).keySet()) {
					int edgeId = mapTimes.get(timestamp).get(destNode);
					int label = edgeProps.get(edgeId)[1];

					edgeLists.get(orderedNodeKeys[i])
							.add(new Edge(edgeId, orderedNodeKeys[i], destNode, timestamp, label));
				}

				// Edges list ordering
				edgeLists.get(orderedNodeKeys[i]).sort(edgeComparator);
			}

		}

		// Same operations done in the previous point but for inAdj-lists
		if(targetGraph.isDirected()) {
			for (int i = 0; i < numNodes; i++) {
				// Edges grouped by timestamps
				Int2ObjectAVLTreeMap<Int2IntOpenHashMap> mapTimes = inAdjLists.get(orderedNodeKeys[i]);
	
				// Timestamp set
				IntArrayList ts = new IntArrayList();
	
				for (int time : Utils.getTimestamps(mapTimes)) {
					ts.add(time);
				}
	
				// Initialization of the edges list for the current node
				inEdgeLists.put(orderedNodeKeys[i], new ObjectArrayList<Edge>());
	
				// For each timestamp
				for (int timestamp : ts) {
					// Edges (sourceNode, edgeId) having the current timestamp
					for (int sourceNode : mapTimes.get(timestamp).keySet()) {
						int edgeId = mapTimes.get(timestamp).get(sourceNode);
						int label = edgeProps.get(edgeId)[1];
	
						inEdgeLists.get(orderedNodeKeys[i])
								.add(new Edge(edgeId, sourceNode, orderedNodeKeys[i], timestamp, label));
					}
	
					// Edges list ordering
					inEdgeLists.get(orderedNodeKeys[i]).sort(edgeComparator);
				}
	
			}
		}

		// System.out.println("\tVisiting the graph...");
		
		// Init visited subgraphs list
		minedSubgraphsMap = new Object2IntOpenHashMap<String>();

		Subgraph currentSubgraph;

		for (int index = 0; index < numNodes; index++) {
			// System.out.println("\t\t" + (index + 1) + "/" + numNodes);
			for(Edge edge : edgeLists.get(orderedNodeKeys[index])) {
				// Init current subgraph
				currentSubgraph = new Subgraph();
				currentSubgraph.addNode(orderedNodeKeys[index]);

				// Init timestamps multiset of visited edges
				timestampSet = TreeMultiset.create();
	
				minTime = -1;
				maxTime = -1;
				diffTime = -1;
	
				// Seed timestamp
				int baseTimestamp = 0;

				// All subsequent timestamps must have timestamps> = seed
				baseTimestamp = edge.getTimestamp();


				// We add the current node and the current edge to the current subgraph
				currentSubgraph.addNode(edge.getDestination());
				currentSubgraph.addEdge(edge);

				// Check that the current subgraph has not already been found and, if necessary, add it to the list of subgraphs found and perform the recursion
				if(!minedSubgraphsMap.containsKey(currentSubgraph.getOrderedEdgesIdList().toString())) {
					// Add the current timestamp to timestamps set
					timestampSet.add(baseTimestamp);

					// Update the list of subraphs
					minedSubgraphsMap.put(currentSubgraph.getOrderedEdgesIdList().toString(), 0);

					// Uncomment this code block to include the simpliest motif (2 nodes and 1 edge)
					// String canon = currentSubgraph.computeCanonization(targetGraph.isDirected(), targetGraph.getNodeLabs()).toString();
					// if(motifsMap.containsKey(canon)) {
					// 	motifsMap.addTo(canon, 1);
					// } else {
					// 	motifsMap.put(canon, 1);
					// }

					// Update diffTime, which is the maximum extension value of the time window
					updateBounds(delta);

					// Perform the recursion by exploring the current subgraph. It continues on both the new node and the current node
					recusiveSearch(Subgraph.clone(currentSubgraph), orderedNodeKeys[index], baseTimestamp, delta);
					recusiveSearch(Subgraph.clone(currentSubgraph), edge.getDestination(), baseTimestamp, delta);
				}

			}

			// Same operations done in the previous point but for inAdj-lists
			if(targetGraph.isDirected()) {
				for(Edge edge : inEdgeLists.get(orderedNodeKeys[index])) {
					// Init current subgraph
					currentSubgraph = new Subgraph();
					currentSubgraph.addNode(orderedNodeKeys[index]);
	
					// Init timestamps multiset of visited edges
					timestampSet = TreeMultiset.create();
		
					minTime = -1;
					maxTime = -1;
					diffTime = -1;
		
					// Seed timestamp
					int baseTimestamp = 0;
	
					// All subsequent timestamps must have timestamps> = seed
					baseTimestamp = edge.getTimestamp();
	
					// We add the current node and the current edge to the current subgraph
					currentSubgraph.addNode(edge.getSource());
					currentSubgraph.addEdge(edge);
	
					// Check that the current subgraph has not already been found and, if necessary, add it to the list of subgraphs found and perform the recursion
					if(!minedSubgraphsMap.containsKey(currentSubgraph.getOrderedEdgesIdList().toString())) {
						// Add the current timestamp to timestamps set
						timestampSet.add(baseTimestamp);

						// Update the list of subraphs
						minedSubgraphsMap.put(currentSubgraph.getOrderedEdgesIdList().toString(), 0);

						// Uncomment this code block to include the simpliest motif (2 nodes and 1 edge)
						// String canon = currentSubgraph.computeCanonization(targetGraph.isDirected(), targetGraph.getNodeLabs()).toString();
						// if(motifsMap.containsKey(canon)) {
						// 	motifsMap.addTo(canon, 1);
						// } else {
						// 	motifsMap.put(canon, 1);
						// }
	
						// Update diffTime, which is the maximum extension value of the time window
						updateBounds(delta);
						
						// Perform the recursion by exploring the current subgraph. It continues on both the new node and the current node
						recusiveSearch(Subgraph.clone(currentSubgraph), orderedNodeKeys[index], baseTimestamp, delta);
						recusiveSearch(Subgraph.clone(currentSubgraph), edge.getSource(), baseTimestamp, delta);	
					}
	
				}
			}
		}
	
		/*** OUTPUT */
		System.out.println("\tWriting results ...");
		File directory = new File("out");
		
		if(!directory.exists()) {
			directory.mkdirs();
		}


		String d = delta!=Integer.MAX_VALUE ? String.valueOf(delta):"inf";
		File output = new File("out/[D"+d+"-N"+maxNumNodes+"-E"+ maxNumEdges+"]" + fm.name + ".csv");


		try {
			FileWriter fw = new FileWriter(output.getAbsoluteFile());
        	BufferedWriter bw = new BufferedWriter(fw);
        	bw.write("NODES, EDGES, NUM_OCC\n");

			for(String key: motifsMap.keySet()) {
				bw.write(key);
				bw.write(", " + motifsMap.getInt(key));
				bw.write("\n");
			}

        	bw.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public void recusiveSearch(Subgraph lastSubgraph, int currentNode, int baseTimestamp, int delta) {
		if(lastSubgraph.getNumEdges() >= maxNumEdges) {
			return;
		} 

		Subgraph currentSubgraph;
		for(Edge edge : edgeLists.get(currentNode)) {
			// Verify that the edge respects time constraints
			if (
				(
					baseTimestamp <= edge.getTimestamp()
					&& edge.getTimestamp() >= (minTime - diffTime)
					&& edge.getTimestamp() <= (maxTime + diffTime)
					&& !lastSubgraph.containsEdge(edge.getId()) // without this check there may be duplicates
				) ||
				(
					baseTimestamp <= edge.getTimestamp() 
					&& delta == Integer.MAX_VALUE
					&& !lastSubgraph.containsEdge(edge.getId()) // without this check there may be duplicates
				)
				) { // without this check there may be duplicates	
					// Has a new node been found?
					boolean newNodeFound = !lastSubgraph.containsNode(edge.getDestination());
					
					currentSubgraph = Subgraph.clone(lastSubgraph);
					if(newNodeFound) {
						if(lastSubgraph.getNumNodes() >  maxNumNodes - 1) { // Limit on the number of nodes
							// BACKTRACKING 
							continue;
						}
						currentSubgraph.addNode(edge.getDestination());
					} 

					
					// Adds the edge to the current subgraph
					currentSubgraph.addEdge(edge);
				
				
				// Check that the current subgraph has not already been found and, if necessary, add it to the list of subgraphs found and perform the recursion
				if(!minedSubgraphsMap.containsKey(currentSubgraph.getOrderedEdgesIdList().toString())) {
					// Update the list of subgraphs found
					minedSubgraphsMap.put(currentSubgraph.getOrderedEdgesIdList().toString(), 0);

					// Standardize the timestamps, build the canonical form, and update the current motif counter
					String canon = currentSubgraph.computeCanonization(targetGraph.isDirected(), targetGraph.getNodeLabs()).toString();
					if(motifsMap.containsKey(canon)) {
						motifsMap.addTo(canon, 1);
					} else {
						motifsMap.put(canon, 1);
					}
					
					// Add the current timestamp to timestamps set
					timestampSet.add(edge.getTimestamp());
		
					// Update diffTime, which is the maximum extension value of the time window
					updateBounds(delta);

					// Perform the recursion by exploring the current subgraph. It continues on both the new node and the current node
					recusiveSearch(Subgraph.clone(currentSubgraph), currentNode, baseTimestamp, delta);
					recusiveSearch(Subgraph.clone(currentSubgraph), edge.getDestination(), baseTimestamp, delta);

					// BACKTRACKING 
		
					// Remove the current timestamp from the set timestamps set
					timestampSet.remove(edge.getTimestamp());
		
					// Update diffTime, which is the maximum extension value of the time window
					updateBounds(delta);
				} 
			}
		}

		// Same operations done in the previous point but for inAdj-lists
		if(targetGraph.isDirected()) {
			for(Edge edge : inEdgeLists.get(currentNode)) {
					// Verify that the edge respects time constraints
				if (
						(
							baseTimestamp <= edge.getTimestamp()
							&& edge.getTimestamp() >= (minTime - diffTime)
							&& edge.getTimestamp() <= (maxTime + diffTime)
							&& !lastSubgraph.containsEdge(edge.getId()) // without this check there may be duplicates
						) ||
						(
							baseTimestamp <= edge.getTimestamp() 
							&& delta == Integer.MAX_VALUE
							&& !lastSubgraph.containsEdge(edge.getId()) // without this check there may be duplicates
						)
					
					) { 
				
						// Has a new node been found?
						boolean newNodeFound = !lastSubgraph.containsNode(edge.getSource());
						currentSubgraph = Subgraph.clone(lastSubgraph);

						if(newNodeFound) {
							if(lastSubgraph.getNumNodes() >  maxNumNodes - 1) { // Limit on the number of nodes
								// BACKTRACKING 
								continue;
							}

							currentSubgraph.addNode(edge.getSource());
						} 
		
						// Adds the edge to the current subgraph
						currentSubgraph.addEdge(edge);
					
					
					// Check that the current subgraph has not already been found and, if necessary, add it to the list of subgraphs found and perform the recursion
					if(!minedSubgraphsMap.containsKey(currentSubgraph.getOrderedEdgesIdList().toString())) {
						// Update the list of subgraphs found
						minedSubgraphsMap.put(currentSubgraph.getOrderedEdgesIdList().toString(), 0);

						// Standardize the timestamps, build the canonical form, and update the current motif counter
						String canon = currentSubgraph.computeCanonization(targetGraph.isDirected(), targetGraph.getNodeLabs()).toString();
						if(motifsMap.containsKey(canon)) {
							motifsMap.addTo(canon, 1);
						} else {
							motifsMap.put(canon, 1);
						}
						
						// Add the current timestamp to timestamps set
						timestampSet.add(edge.getTimestamp());
			
						// Update diffTime, which is the maximum extension value of the time window
						updateBounds(delta);
						
						// Perform the recursion by exploring the current subgraph. It continues on both the new node and the current node
						recusiveSearch(Subgraph.clone(currentSubgraph), currentNode, baseTimestamp, delta);
						recusiveSearch(Subgraph.clone(currentSubgraph), edge.getSource(), baseTimestamp, delta);
		
						// BACKTRACKING 
			
						// Remove the current timestamp from the set timestamps set
						timestampSet.remove(edge.getTimestamp());
			
						// Update diffTime, which is the maximum extension value of the time window
						updateBounds(delta);
					} 
				}
			}
		}
	}
}