import java.io.*;
import java.util.*;

public class Main {
    static final class FastScanner {
        private final InputStream in;
        private final byte[] buf = new byte[1 << 16];
        private int ptr = 0, len = 0;
        FastScanner(InputStream is) { this.in = is; }
        private int read() throws IOException {
            if (ptr >= len) {
                len = in.read(buf);
                ptr = 0;
                if (len <= 0) return -1;
            }
            return buf[ptr++];
        }
        int nextInt() throws IOException {
            int c, sgn = 1, x = 0;
            do c = read(); while (c <= 32);
            if (c == '-') { sgn = -1; c = read(); }
            while (c > 32) { x = x * 10 + (c - '0'); c = read(); }
            return x * sgn;
        }
    }

    static final class Edge {
        int to, id;
        Edge(int to, int id) { this.to = to; this.id = id; }
    }

    public static void main(String[] args) throws Exception {
        FastScanner fs = new FastScanner(System.in);
        int n = fs.nextInt();
        int m = fs.nextInt();

        @SuppressWarnings("unchecked")
        List<Edge>[] g = new ArrayList[n + 1];
        for (int i = 1; i <= n; i++) g[i] = new ArrayList<>();

        int[] eu = new int[m];
        int[] ev = new int[m];
        for (int i = 0; i < m; i++) {
            int u = fs.nextInt(), v = fs.nextInt();
            eu[i] = u; ev[i] = v;
            g[u].add(new Edge(v, i));
            g[v].add(new Edge(u, i));
        }

        // -------- 1) Bridges (iterative Tarjan) --------
        int timer = 0;
        int[] tin = new int[n + 1];
        int[] low = new int[n + 1];
        int[] parent = new int[n + 1];
        int[] parentE = new int[n + 1];
        boolean[] seen = new boolean[n + 1];
        boolean[] isBridge = new boolean[m];

        int[] itIdx = new int[n + 1];
        ArrayDeque<Integer> st = new ArrayDeque<>();

        for (int s = 1; s <= n; s++) {
            if (!seen[s]) {
                parent[s] = 0;
                parentE[s] = -1;
                st.push(s);
                while (!st.isEmpty()) {
                    int v = st.peek();
                    if (!seen[v]) {
                        seen[v] = true;
                        tin[v] = low[v] = ++timer;
                        itIdx[v] = 0;
                    }
                    
                    boolean processedAll = true;
                    for (; itIdx[v] < g[v].size(); itIdx[v]++) {
                        Edge e = g[v].get(itIdx[v]);
                        int to = e.to;
                        if (e.id == parentE[v]) continue; // skip parent edge
                        
                        if (!seen[to]) {
                            parent[to] = v;
                            parentE[to] = e.id;
                            st.push(to);
                            processedAll = false;
                            itIdx[v]++; // increment here since we break
                            break;
                        } else {
                            // Back edge: update low[v] with tin[to] (not low[to])
                            if (tin[to] < tin[v]) {
                                low[v] = Math.min(low[v], tin[to]);
                            }
                        }
                    }
                    
                    if (processedAll) {
                        st.pop();
                        int p = parent[v];
                        if (p != 0) {
                            low[p] = Math.min(low[p], low[v]);
                            if (low[v] > tin[p]) {
                                isBridge[parentE[v]] = true;
                            }
                        }
                    }
                }
            }
        }

        // -------- 2) Сжатие по 2ECC (обход без мостов) --------
        int[] comp = new int[n + 1];
        int comps = 0;
        int[] repAny = new int[n + 1]; // representative for each component
        int[] repFree = new int[n + 1]; // non-bridge-end representative

        ArrayDeque<Integer> q = new ArrayDeque<>();
        for (int i = 1; i <= n; i++) {
            if (comp[i] == 0) {
                comps++;
                q.clear();
                comp[i] = comps;
                repAny[comps] = i;
                repFree[comps] = i; // start with any vertex
                q.add(i);
                
                while (!q.isEmpty()) {
                    int v = q.poll();
                    for (Edge e : g[v]) {
                        if (isBridge[e.id]) continue;
                        int to = e.to;
                        if (comp[to] == 0) {
                            comp[to] = comps;
                            q.add(to);
                        }
                    }
                }
            }
        }

        if (comps == 1) {
            System.out.println(0);
            return;
        }

        // Find bridge ends and mark components that have non-bridge-end vertices
        boolean[] isBridgeEnd = new boolean[n + 1];
        for (int i = 0; i < m; i++) {
            if (isBridge[i]) {
                isBridgeEnd[eu[i]] = true;
                isBridgeEnd[ev[i]] = true;
            }
        }
        
        // Update repFree: find a vertex that is not a bridge end in each component
        for (int i = 1; i <= n; i++) {
            int c = comp[i];
            if (!isBridgeEnd[i]) {
                repFree[c] = i;
            }
        }

        // -------- 3) Дерево мостов и листья --------
        int[] deg = new int[comps + 1];
        for (int i = 0; i < m; i++) {
            if (isBridge[i]) {
                int a = comp[eu[i]], b = comp[ev[i]];
                if (a != b) {
                    deg[a]++;
                    deg[b]++;
                }
            }
        }
        
        List<Integer> leaves = new ArrayList<>();
        for (int c = 1; c <= comps; c++) {
            if (deg[c] == 1) {
                leaves.add(c);
            }
        }

        int L = leaves.size();
        int add = (L + 1) / 2;

        StringBuilder sb = new StringBuilder();
        sb.append(add).append('\n');

        if (L == 0) {
            // Special case: no bridges but multiple components (unlikely but possible)
            // Connect any two components
            int c1 = 1, c2 = 2;
            int u = repAny[c1];
            int v = repAny[c2];
            sb.append(u).append(' ').append(v).append('\n');
            System.out.print(sb.toString());
            return;
        }

        // -------- 4) Соединение листьев --------
        if (L == 2) {
            int c1 = leaves.get(0), c2 = leaves.get(1);
            int u = repFree[c1] != 0 ? repFree[c1] : repAny[c1];
            int v = repFree[c2] != 0 ? repFree[c2] : repAny[c2];
            
            // Check if u and v are already connected by a non-bridge edge
            boolean connected = false;
            for (Edge e : g[u]) {
                if (e.to == v && !isBridge[e.id]) {
                    connected = true;
                    break;
                }
            }
            
            if (!connected) {
                sb.append(u).append(' ').append(v).append('\n');
            } else {
                // Find alternative vertices
                int altU = findAlternativeVertex(g, comp, c1, u, v, isBridge);
                int altV = findAlternativeVertex(g, comp, c2, v, u, isBridge);
                sb.append(altU).append(' ').append(altV).append('\n');
            }
        } else {
            for (int i = 0; i < add; i++) {
                int c1 = leaves.get(i);
                int c2 = leaves.get((i + add) % L);
                
                int u = repFree[c1] != 0 ? repFree[c1] : repAny[c1];
                int v = repFree[c2] != 0 ? repFree[c2] : repAny[c2];
                
                sb.append(u).append(' ').append(v).append('\n');
            }
        }

        System.out.print(sb.toString());
    }
    
    private static int findAlternativeVertex(List<Edge>[] g, int[] comp, int compId, 
                                           int avoid, int target, boolean[] isBridge) {
        // Try to find any vertex in the component that's not connected to target
        for (int i = 1; i < g.length; i++) {
            if (comp[i] == compId && i != avoid) {
                boolean connectedToTarget = false;
                for (Edge e : g[i]) {
                    if (e.to == target && !isBridge[e.id]) {
                        connectedToTarget = true;
                        break;
                    }
                }
                if (!connectedToTarget) {
                    return i;
                }
            }
        }
        // Fallback: return any vertex in the component
        for (int i = 1; i < g.length; i++) {
            if (comp[i] == compId && i != avoid) {
                return i;
            }
        }
        return avoid; // last resort
    }
}
