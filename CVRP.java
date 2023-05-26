import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CVRP {
    private static final double[][] DISTANCE_MATRIX = {
            /*Prishtina */    { 0,    41.4, 84.8, 85.3,   41.9,  48.5,  88.6},
            /*Mitrovica*/     { 41.4, 0,    68.9, 103.0,  74.8,  85.0,  84.6},
            /*Peja*/          { 84.8, 68.9,   0,  97.1,   91.3,  123.0, 35.4},
            /*Prizren*/       { 85.3, 103.0,97.1,   0,    64.0,  102.0, 37.1},
            /*Ferizaj*/       { 41.9, 74.8, 91.3,  64.0,  0,     34.0,  88.4},
            /*Gjilan*/        { 48.5, 85.0, 123.0, 102.0, 34.0,    0,   128.0},
            /*Gjakova*/       { 88.6, 84.6, 35.4,  37.1,  88.4,  128.0, 0},

    };

    private static final int[][] ORDERS = {
            { 22, 21, 10, 17, 20, 20, 10, 20 },
            { 17, 14, 19, 10, 10 },
            { 12, 7, 16, 15, 10, 35, 15, 20 },
            { 16, 16, 18, 15, 15 },
            { 36, 17, 11, 18, 9, 21 },
            { 15, 25, 32, 18, 16, 7, 7 },
            { 11, 13, 16, 20, 17, 13, 15, 15 }


    };

    private static final int VEHICLE_CAPACITY = 100;
    private static final int NUM_VEHICLES = 8;
    private static final double INITIAL_TEMPERATURE = 1000;
    private static final double COOLING_RATE = 0.95;
    private static final int NUM_ITERATIONS = 500;



    private int calculateTotalOrders() {
        int totalOrders = 0;
        for (int[] order : ORDERS) {
            for (int quantity : order) {
                totalOrders += quantity;
            }
        }
        return totalOrders;
    }

    private List<List<Integer>> solve() {
        List<List<Integer>> currentSolution = generateInitialSolution(ORDERS.length);
        double currentCost = calculateTotalDistance(currentSolution);
        List<List<Integer>> bestSolution = new ArrayList<>(currentSolution);
        double bestCost = currentCost;
        double temperature = INITIAL_TEMPERATURE;

        for (int i = 0; i < NUM_ITERATIONS; i++) {
            List<List<Integer>> newSolution = generateNeighborSolution(currentSolution);
            double newCost = calculateTotalDistance(newSolution);

            if (acceptanceProbability(currentCost, newCost, temperature) > Math.random()) {
                currentSolution = new ArrayList<>(newSolution);
                currentCost = newCost;
            }

            if (newCost < bestCost) {
                bestSolution = new ArrayList<>(newSolution);
                bestCost = newCost;
            }

            temperature *= COOLING_RATE;
        }

        return bestSolution;
    }

    private List<List<Integer>> generateInitialSolution(int numClients) {
        List<List<Integer>> initialSolution = new ArrayList<>();
        Random random = new Random();

        int totalOrders = calculateTotalOrders();
        int numVehiclesNeeded = (int) Math.ceil((double) totalOrders / VEHICLE_CAPACITY);
        int numVehiclesToUse = Math.min(NUM_VEHICLES, numVehiclesNeeded);

        for (int i = 0; i < numVehiclesToUse; i++) {
            List<Integer> route = new ArrayList<>();
            route.add(1); // Add the depot as the starting point

            while (true) {
                int nextClient = random.nextInt(numClients) + 1;
                if (!route.contains(nextClient)) {
                    route.add(nextClient);
                }

                if (calculateLoad(route) >= VEHICLE_CAPACITY || route.size() == numClients) {
                    break;
                }
            }

            route.add(1); // Add the depot as the ending point
            initialSolution.add(route);
        }

        return initialSolution;
    }

    private List<List<Integer>> generateNeighborSolution(List<List<Integer>> currentSolution) {
        List<List<Integer>> newSolution = new ArrayList<>(currentSolution);
        Random random = new Random();

        // Select a random vehicle route
        int routeIndex = random.nextInt(newSolution.size());
        List<Integer> route = newSolution.get(routeIndex);

        // Select two random clients in the route
        int clientIndex1 = random.nextInt(route.size() - 2) + 1; // Klienti 1
        int clientIndex2 = random.nextInt(route.size() - 2) + 1; // Klienti 2
        // Swap the two selected clients
        int temp = route.get(clientIndex1);
        route.set(clientIndex1, route.get(clientIndex2));
        route.set(clientIndex2, temp);

        return newSolution;
    }

    private double calculateTotalDistance(List<List<Integer>> solution) {
        double totalDistance = 0;

        for (List<Integer> route : solution) {
            if (route.size() <= 1) {
                continue; // Skip routes with only the depot
            }

            double routeDistance = 0;
            int prevNode = route.get(0);

            for (int i = 1; i < route.size(); i++) {
                int currentNode = route.get(i);
                routeDistance += DISTANCE_MATRIX[prevNode - 1][currentNode - 1];
                prevNode = currentNode;
            }

            // Calculate the distance from the last client back to the depot
            int depotNode = route.get(0);
            routeDistance += DISTANCE_MATRIX[prevNode - 1][depotNode - 1];

            totalDistance += routeDistance;
        }

        return totalDistance;
    }

    private int calculateLoad(List<Integer> route) {
        int load = 0;
        for (Integer client : route) {
            load += ORDERS[client - 1].length;
        }
        return load;
    }

    private double acceptanceProbability(double currentCost, double newCost, double temperature) {
        if (newCost < currentCost) {
            return 1.0;
        }
        return Math.exp((currentCost - newCost) / temperature);
    }

    private void printSolution(List<List<Integer>> solution) {
        System.out.println("\nOptimized Solution:");
        double totalDistance = calculateTotalDistance(solution);
        System.out.println("Total Distance: " + totalDistance);

        for (int i = 0; i < solution.size(); i++) {
            List<Integer> route = solution.get(i);
            System.out.print("Route " + (i + 1) + ": ");

            for (int j = 0; j < route.size(); j++) {
                int client = route.get(j);
                System.out.print("Client " + client);

                if (j < route.size() - 1) {
                    System.out.print(" -> ");
                }
            }

            System.out.println();
        }
    }
    public static void main(String[] args) {
        CVRP vrpsa = new CVRP();

        int totalOrders = vrpsa.calculateTotalOrders();
        int numVehiclesNeeded = (int) Math.ceil((double) totalOrders / VEHICLE_CAPACITY);
        if (numVehiclesNeeded > NUM_VEHICLES) {
            System.out.println("Not enough vehicles to fulfill the orders. The number of vehicles needed is " + numVehiclesNeeded + "!");
        } else {
            List<List<Integer>> solution = vrpsa.solve();
            vrpsa.printSolution(solution);
        }
    }
}
