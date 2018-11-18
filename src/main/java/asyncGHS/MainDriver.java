package asyncGHS;


import edu.princeton.cs.algs4.Edge;
import edu.princeton.cs.algs4.EdgeWeightedGraph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class MainDriver {
    private static Logger log = Logger.getLogger("Main");

    public static EdgeWeightedGraph readInput(String pathToAdjacencyList) throws IOException {
        File file = new File(pathToAdjacencyList);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;
        int noOfNodes = 0;
        int noOfEdges = 0;
        Map<Integer, List<NeighborObject>> adj = new HashMap<>();

        // First line: No of nodes
        if ((st = br.readLine()) != null) {
            noOfNodes = Integer.parseInt(st);
        }

        // Second line: No of nodes
        if ((st = br.readLine()) != null) {
            noOfEdges = Integer.parseInt(st);
        }

        // Next no of edges line contains information regarding edges in format vertex1 vertex2 edgeweight
        EdgeWeightedGraph graph = new EdgeWeightedGraph(noOfNodes);
        for (int i=0;i<noOfEdges;i++) {
            if ((st = br.readLine()) != null) {
                String[] tokens = st.split(" ");
                int vertex1 = Integer.parseInt(tokens[0]);
                int vertex2 = Integer.parseInt(tokens[1]);
                double weight = Double.parseDouble(tokens[2]);
                Edge e = new Edge(vertex1, vertex2, weight);
                graph.addEdge(e);
            }
        }



        /*workers = new String[noOfNodes];
        // second line: worker ids
        if ((st = br.readLine()) != null) {
            workers = st.split("\\s+");
//            log.info("Processes UIDs = " + Arrays.toString(workers));
        }

        // next noOfNodes lines - adjacency matrix
        while ((st = br.readLine()) != null) {
            neighborList = st.split("\\s+");
            List<NeighborObject> neighbors = new ArrayList<>();
            for (int i = 0; i < noOfNodes; i++) {
                if (!neighborList[i].equals("-1")) {
                    neighbors.add(new NeighborObject(Integer.parseInt(workers[i]), Float.parseFloat(neighborList[i])));
                }
            }
            adj.put(Integer.parseInt(workers[count]), neighbors);
            count++;
        }*/
//        log.info("Adjacency list = " + adj);
        return graph;
    }

    public static void main(String[] args) throws IOException {
        EdgeWeightedGraph graph = null;
        GraphGenerator graphGenerator;
        if (args.length < 1) {
//            System.out.println("Format: java MainDriver <input file path>");
//            System.exit(-1);
            graphGenerator = new GraphGenerator();
            System.out.println(graphGenerator.getEdges());
        } else {
            graph = readInput(args[0]);
            graphGenerator = new GraphGenerator(graph);
        }
//        graph.printGraph();
        MasterThread masterThread = new MasterThread("MASTER", 0, graph);
        masterThread.start();
    }

    private static void printEdges(Iterable<Edge> edges) {
        for(Edge edge: edges) {
            System.out.println(edge.toString());
        }
    }
}
