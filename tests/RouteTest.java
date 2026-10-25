import java.util.*;

public class RouteTest {
    static int fails = 0;

    static void check(boolean ok, String msg) {
        if (!ok) { fails++; System.out.println("  FAIL: " + msg); }
    }

    static void run(String name, int n, int[][] edges, int source, int target) {
        Map<String, Object> r = Algorithms.dagCore(n, edges, source);
        Long[] dist = (Long[]) r.get("distance");
        int[] pe = (int[]) r.get("parent_edge");
        List<Integer> route = Algorithms.reconstructRoute(source, target, edges, pe);
        System.out.println(name);
        System.out.println("  distance=" + Arrays.toString(dist) + " parent_edge=" + Arrays.toString(pe));
        System.out.println("  reconstructRoute(source=" + source + ", target=" + target + ") = " + route);
        if (dist[target] == null) {
            check(route.isEmpty(), "unreachable target must give an empty list");
            System.out.println("  unreachable target -> empty list");
            return;
        }
        check(!route.isEmpty() && route.get(0) == source && route.get(route.size() - 1) == target,
                "route must run from source to target");
        long sum = 0;
        List<Integer> ids = new ArrayList<>();
        for (int i = 1; i < route.size(); i++) {
            int id = pe[route.get(i)];
            check(id >= 0 && edges[id][0] == route.get(i - 1) && edges[id][1] == route.get(i),
                    "edge " + id + " does not go " + route.get(i - 1) + " -> " + route.get(i));
            if (id >= 0) { sum += edges[id][2]; ids.add(id); }
        }
        System.out.println("  edge ids=" + ids + ", weight sum=" + sum + ", distance=" + dist[target]);
        check(sum == dist[target], "weight sum must equal distance");
    }

    public static void main(String[] args) {
        int[][] ex = {{0, 1, 3}, {0, 2, 8}, {1, 2, -7}, {2, 3, 4}};
        run("A: reachable target (example from the assignment)", 5, ex, 0, 3);
        run("B: target is the source", 5, ex, 0, 0);
        run("C: unreachable target", 5, ex, 0, 4);
        run("D: parallel edges with different weights", 2,
                new int[][]{{0, 1, 5}, {0, 1, 2}, {0, 1, 2}}, 0, 1);
        run("E: tied shortest routes (0->2 and 0->1->2 both cost 6)", 3,
                new int[][]{{0, 1, 5}, {0, 1, 5}, {1, 2, 1}, {0, 2, 6}}, 0, 2);
        System.out.println(fails == 0 ? "ALL ROUTE CHECKS PASSED" : fails + " CHECK(S) FAILED");
        if (fails != 0) System.exit(1);
    }
}