import java.util.*;
import java.io.*;

public class TreasureIsland {
    static int n, m;
    static int[] values;
    static List<List<Integer>> graph;
    static int[][] memo;
    
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        
        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());
        
        values = new int[n + 1];
        st = new StringTokenizer(br.readLine());
        for (int i = 1; i <= n; i++) {
            values[i] = Integer.parseInt(st.nextToken());
        }
        
        graph = new ArrayList<>(n + 1);
        for (int i = 0; i <= n; i++) {
            graph.add(new ArrayList<>());
        }
        
        for (int i = 0; i < m; i++) {
            st = new StringTokenizer(br.readLine());
            int a = Integer.parseInt(st.nextToken());
            int b = Integer.parseInt(st.nextToken());
            graph.get(a).add(b);
            graph.get(b).add(a);
        }
        
        // DP с битовыми масками: memo[island][mask] = максимальная сумма
        // начиная с острова island при посещенных островах mask
        memo = new int[n + 1][1 << n];
        for (int i = 0; i <= n; i++) {
            Arrays.fill(memo[i], -1);
        }
        
        // Дино всегда начинает с острова номер 1
        int maxTreasure = dfs(1, 1 << 0);
        
        System.out.println(maxTreasure);
    }
    
    static int dfs(int current, int visited) {
        if (memo[current][visited] != -1) {
            return memo[current][visited];
        }
        
        int result = values[current];
        int maxNext = 0;
        
        // Пробуем переходить на все соседние непосещенные острова
        for (int next : graph.get(current)) {
            int nextBit = 1 << (next - 1);
            if ((visited & nextBit) == 0) { // Если остров еще не посещен
                maxNext = Math.max(maxNext, dfs(next, visited | nextBit));
            }
        }
        
        result += maxNext;
        memo[current][visited] = result;
        return result;
    }
}
