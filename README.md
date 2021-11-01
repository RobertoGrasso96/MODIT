# MODIT
MODIT (MOtif DIscovery in Temporal Networks) is an algorithm for counting motifs of any size in temporal networks.

## References:
    Grasso R, Micale G, Ferro A, Pulvirenti A (2021). MODIT: MOtif DIscovery in Temporal networks. Submitted to Frontiers in Big Data (2021).

## Requirements:
Java, version 16 or higher (https://www.oracle.com/it/java/technologies/javase-downloads.html)

## REQUIRED PARAMETERS:
```
-t      Target network file
```

## OPTIONAL PARAMETERS:
```
-d      Delta threshold for time window of events (by default delta is infinite)
-u      Treat target and query as undirected (by default networks are directed)
-n      Max number of nodes for each subgraph (by default it is 5)
-e      Max number of edges for each subgraph (by default it is 5)
```

## INPUT FILES FORMAT:

Target network is specified as a text file.
The first row contains the number n of nodes in the network.
The following n rows contain the id of a node followed by its label. Both strings and numbers can be specified as labels. Fields are separated by tab character (\t).
The remaining rows contain the list of edges.
Each edge is specified by the id of the source node, followed by the id of the destination node, the timestamp of the edge and the edge label, according to the following format:

```
idSource	idDest	timestamp:label
```

'idSource', 'idDest' and 'timestamp:label' fields are separated by tab characters (\t).
Both strings and numbers can be specified as labels.
Multiple edges between two nodes but with different timestamps can be specified.

Example:
```
4
1	1
2	1
3	2
4	2
1	3	1245:5
1	4	1244:5
2	3	1245:5
1	3	1248:4
2	4	1246:4
```

## OUTPUT FILE FORMAT:

Output is a .csv file
```
(idNode0:lab0)(idNode1:lab1)...(idNodeN:labN),      [idNode0: (id:destNodeId t:edgeTimestamp l:edgeLabel)...]...[idNodeN: (id:destNodeId t:edgeTimestamp l:edgeLabel)...],      NumOccurrences
```

Each row contains, for a specific motif:
- nodes list(id and label);
- outAdj-list for each node;
- number of occurrences. 

## BIG NETWORKS
For large networks it may be necessary to increase the memory of the JVM. 
To do this, just specify the following parameters: 
```
-XmsXXg -XmxYYg
```
(replace XX and YY with the GBs you want to use)

### DATASET
A dataset can be found at the following link: 
https://studentiunict-my.sharepoint.com/personal/uni370977_studium_unict_it/_layouts/15/onedrive.aspx?id=%2Fpersonal%2Funi370977%5Fstudium%5Funict%5Fit%2FDocuments%2F%5BPUBLIC%5DShared%2FTemporalNetworks