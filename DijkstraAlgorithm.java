
public class DijkstraAlgorithm {
	static int n = 5;
    static int INF = Integer.MAX_VALUE;
    static double[][] graph = {
            {0, 41.3, 84.8, 88.6, 85.3},
            {41.3, 0, 68.9, INF, INF},
            {84.8, 68.9, 0, 35.4, 97.1},
            {88.6, INF, 35.4, 0, 40.1},
            {85.3, INF, 97.1, 40.1, 0}
    };
    static boolean[] visited = new boolean[n];
    static int[] path = new int[n];
    static int[] bestPath = new int[n];
    static double bestDist = INF;

    static void tsp(int pos, double dist, int visitedCount) {
        if (visitedCount == n) {
            if (graph[pos][0] != INF && dist + graph[pos][0] < bestDist) {
                System.arraycopy(path, 0, bestPath, 0, n);
                bestDist = dist + graph[pos][0];
            }
            return;
        }
        for (int i = 1; i < n; i++) {
            if (!visited[i] && graph[pos][i] != INF) {
                visited[i] = true;
                path[visitedCount] = i;
                tsp(i, dist + graph[pos][i], visitedCount + 1);
                visited[i] = false;
            }
        }
    }
    static String getCityName(int i) {
        if (i == 0) {
            return "Prishtina";
        } else if (i == 1) {
            return "Mitrovica";
        } else if (i == 2) {
            return "Peja";
        } else if (i == 3) {
            return "Gjakova";
        } else {
            return "Prizren";
        }
    }

    public static void main(String[] args) {
        visited[0] = true;
        path[0] = 0;
        tsp(0, 0, 1);
        System.out.print("Best path: Prishtina -> ");
        for (int i = 0; i < n - 1; i++) {
            System.out.print(getCityName(bestPath[i]) + " -> ");
        }
        System.out.println("Prishtina");
        System.out.println("Distance: " + bestDist + " km");
    }
}
