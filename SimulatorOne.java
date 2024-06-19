import java.io.*;
import java.util.*;

/**
* Class that accept as input a file containing data on roads, shop locations and incoming calls
* and calculates abd output details for the best(shotrest trip) for each call.
*
* @author Nikita Martin
* @version 1.0
* @since 25-03-2024
*/
public class SimulatorOne{
   
   private List<String> shopLocations, clientLocations;
   
   /**
   * Default constructor for SimulatorOne.
   * Initializes the client and shop locations
   */
   public SimulatorOne(){
      shopLocations = new ArrayList<>();
      clientLocations = new ArrayList<>();
      
   }
   
   /**
   * Loads data into a graph, including nodes, edges, shop locations and client locations
   *
   * @param scanner The scanner object used to read data from the file
   * @param graph The graph object to which the data will be added.
   **/
   public void loadFromFile(Scanner scanner, Graph graph){
      // Read the number of nodes from the file
      int numNodes = Integer.parseInt(scanner.nextLine().trim());
      
      // Loop through each node
      for(int i=0; i<numNodes; i++){
         // Read data for edges from the current node
         String[] edgeData = scanner.nextLine().split(" ");//Format:source node, destination node, weight
         String sourceNode =  Integer.toString(i);// each node is indexed from 0
         graph.addVertex(sourceNode);//add the node to the graph
         
         //loop through each destination node and its corresponding cost
         for (int j = 1; j<edgeData.length; j+=2){
            int destNode = Integer.parseInt(edgeData[j]); //extract destination node
            double cost = Double.parseDouble(edgeData[j+1]); //extract edge cost
            graph.addEdge(sourceNode, Integer.toString(destNode), cost); //add edge to the graph
         }
      }
      
      //read the number of shops
      int numShops = Integer.parseInt(scanner.nextLine().trim());
      String[] shopData = scanner.nextLine().split(" ");//read shop locations
      
      for(String shop: shopData){
         shopLocations.add(shop);
      }
      
      //read the number of clients
      int numClients = Integer.parseInt(scanner.nextLine().trim());
      String[] clientData = scanner.nextLine().split(" ");
      
      for(String client: clientData){
         clientLocations.add(client);
      }
      
      scanner.close();
   }
   
   /**
   * Process client calls by finding the nearest taxis and shops for each client location
   *
   * @param graph The graph representing the network of taxis and shops
   **/
   private void processCalls(Graph graph){
      //check if the graph is empty
      if(graph.isEmpty()){
         System.out.println("Graph is Empty");
      }else{
         //Iterate over each client location
         for(String client: clientLocations){
            System.out.println("client " + client);
            
            //find the closest taxis to the client
            List<String> closestTaxis = new ArrayList<>();
            
            // Initialize shortestDistance to the maximum possible value 
            double minTaxiCost = Double.MAX_VALUE;
            
            //obtain the closest taxis to the clients
            for(String taxi: shopLocations){
               //obtain the shortest distance between the taxi and client
               double distanceToClient = graph.getShortestDistance(taxi, client);
               if(distanceToClient < minTaxiCost && distanceToClient!=0.0){
                  minTaxiCost = distanceToClient;//value updates to reflect actual shortest distance between the shop and client
                  closestTaxis.clear();//array cleared to only store the closest taxis to the client in the array
                  closestTaxis.add(taxi);
               }
               else if(distanceToClient == minTaxiCost && distanceToClient != 0.0){
                  //checks if there are multiple taxis close to the client with the same cost
                  closestTaxis.add(taxi);
               }
            }
            
            //Find the closest shops to the client
            List<String> shops = new ArrayList<>();
            double minShopCost = Double.MAX_VALUE;
            
            //obtain the closest shops to the clients
            for (String shop : shopLocations) {
               double shopCost = graph.getShortestDistance(client, shop);
               if(shopCost<minShopCost && shopCost!= 0.0){
                  minShopCost = shopCost;
                  shops.clear();
                  shops.add(shop);
               }else if (shopCost == minShopCost && shopCost!= 0.0){
                  //if there are multiple shops close to the client with the same distance cost
                  shops.add(shop);
               }
            }
            
            //check if a client cannot be picked up or dropped off by checking if the shortest routes to the client and shop exists
            if(minTaxiCost == Double.MAX_VALUE || minTaxiCost == 0 || minShopCost == Double.MAX_VALUE || minShopCost == 0){
               System.out.println("cannot be helped");
               continue;
            }
            
            //process the closest shops to the client
            if(!closestTaxis.isEmpty()){
               Collections.sort(closestTaxis, Comparator.comparingInt(Integer::parseInt));//sorts list in ascending order based on value of its elements.
               for (String taxi : closestTaxis) {
                  //check if multiple paths from a taxi to a client with the same cost
                  List<String> nearestTaxi = graph.getShortestPath(taxi, client);
                  if(!nearestTaxi.isEmpty() && nearestTaxi.size() != 1){
                  
                     //check if multiple paths from a taxi to a client with the same cost
                     graph.dijkstra(taxi);
                     //get the vertex of the client aka target node 
                     Vertex clientVertex = graph.getVertex(client);
                     
                     System.out.println("taxi " + taxi);
                     if(!clientVertex.hasMultipleSolutions){
                        //check of there are multiple paths going to it with the same shortest distance cost
                        for(String paths: nearestTaxi){
                           System.out.print(paths + " ");
                        }
                        System.out.println();
                     }
                     else{
                        System.out.println("multiple solutions cost " + (int)minTaxiCost);
                     }
                  }
               }
            }
                    
            // Process closest shops
            if(!shops.isEmpty()){
               Collections.sort(shops, Comparator.comparingInt(Integer::parseInt));
               for(String closestShop: shops){
                  List<String> nearestShop = graph.getShortestPath(client, closestShop);
                  
                  if(!nearestShop.isEmpty() && nearestShop.size() != 1){
                     System.out.println("shop " + closestShop);
                     
                     graph.dijkstra(client);
                     Vertex shopVertex = graph.getVertex(closestShop);
                     
                     if (!shopVertex.hasMultipleSolutions) {
                        for(String paths: nearestShop){
                           System.out.print(paths + " ");
                        }
                        System.out.println();
                     }else{
                         System.out.println("multiple solutions cost " + (int)minShopCost);
                     }
                  }
               }
            }
         }
      }
   }
         
   public static void main(String [] args){
      SimulatorOne simOne = new SimulatorOne();
      Scanner scanner = new Scanner(System.in);
      Graph graph = new Graph();
      simOne.loadFromFile(scanner, graph);
      simOne.processCalls(graph);
   }
}