
import graph.Graph;
import io.FileManager;
import mining.MODITSolver;

//MODIT MAIN CLASS
public class Modit
{
    public static void main(String[] args) throws Exception
    {
        //Input file for target network
        String targetFile=null;
        //Should the networks be undirected?
        boolean directed=true;
        //Maximum allowed difference between timestamps for the retrieved occurrences
        int delta=Integer.MAX_VALUE;
        //Max num of nodes for each subgraph. Default = 5
        int maxNumNodes = 5;
        //Max num of edges for each subgraph. Default = 5
        int maxNumEdges = 5;

        for (int i=0;i<args.length;i++)
        {
            switch (args[i])
            {
                case "-t" -> targetFile= args[++i];
                case "-d" -> delta=Integer.parseInt(args[++i]);
                case "-u" -> directed = false;
                case "-n" -> maxNumNodes = Integer.parseInt(args[++i]);
                case "-e" -> maxNumEdges = Integer.parseInt(args[++i]);
                default -> {
                    System.out.println("Error! Unrecognizable command '" + args[i] + "'");
                    printHelp();
                    System.exit(1);
                }
            }
        }

        //Error in case network file is missing
        if(targetFile==null)
        {
            System.out.println("Error! No target file has been specified!\n");
            printHelp();
            System.exit(1);
        }
      

        FileManager fm=new FileManager();
        System.out.println("\n-----------------------------------------------");
        double inizio=System.currentTimeMillis();
        System.out.println("Reading target graph "+targetFile+"...");
        Graph target=fm.readGraph(targetFile,directed);
        double fine=System.currentTimeMillis();
        double totalTimeReading=(fine-inizio)/1000;
        inizio=System.currentTimeMillis();
        System.out.println("Mining motifs...");
        MODITSolver modit=new MODITSolver(target, fm, maxNumNodes, maxNumEdges);
        
        modit.findMotifs(delta);

        fine=System.currentTimeMillis();
        double totalTimeMatching=(fine-inizio)/1000;
        System.out.println("\nDone!\n");
        System.out.println("Time for reading: "+totalTimeReading+" secs");
        System.out.println("Time for motifs mining: "+totalTimeMatching+" secs");
        System.out.println("Total time: "+(totalTimeReading+totalTimeMatching)+" secs");
        System.out.println();
    }

    public static void printHelp()
    {
        String help = "Usage: java -jar Modit.jar -t <targetFile> "+
                "[-d <deltaThresh> -u -m <maxNumNodes>] \n\n";
        help+="REQUIRED PARAMETERS:\n";
        help+="-t\tTarget network file\n";
        help+="OPTIONAL PARAMETERS:\n";
        help+="-d\tDelta threshold for time window of events (by default delta is infinite)\n";
        help+="-u\tTreat target and query as undirected (by default networks are directed)\n";
        help+="-n\tMax number of nodes for each subgraph (by default it is 5)\n";
        help+="-e\tMax number of edges for each subgraph (by default it is 5)\n\n";
        System.out.println(help);
    }
}
