import java.io.*;
import java.util.*;

public class Main {
    static class DSU {
        int[] parent, size;
        DSU(int n) {
            parent = new int[n];
            size = new int[n];
            for (int i = 0; i < n; i++) {
                parent[i] = i;
                size[i] = 1;
            }
        }
        int find(int x) {
            if (parent[x] != x) parent[x] = find(parent[x]);
            return parent[x];
        }
        void union(int a, int b) {
            a = find(a);
            b = find(b);
            if (a == b) return;
            if (size[a] < size[b]) {
                int t = a; a = b; b = t;
            }
            parent[b] = a;
            size[a] += size[b];
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int N = Integer.parseInt(br.readLine().trim());

        // Сначала собираем все слова и даём им id
        Map<String, Integer> wordId = new HashMap<>(N * 10);
        List<int[]> queries = new ArrayList<>(N);
        int idCounter = 0;

        for (int i = 0; i < N; i++) {
            int M = Integer.parseInt(br.readLine().trim());
            String[] words = br.readLine().trim().split(" ");
            int[] ids = new int[words.length];
            for (int j = 0; j < words.length; j++) {
                String w = words[j];
                Integer id = wordId.get(w);
                if (id == null) {
                    id = idCounter++;
                    wordId.put(w, id);
                }
                ids[j] = id;
            }
            queries.add(ids);
        }

        // DSU на словах
        DSU dsu = new DSU(idCounter);

        for (int[] ids : queries) {
            for (int j = 1; j < ids.length; j++) {
                dsu.union(ids[0], ids[j]);
            }
        }

        // считаем компоненты
        int numComponents = 0;
        int maxSize = 0;
        for (int i = 0; i < idCounter; i++) {
            if (dsu.find(i) == i) {
                numComponents++;
                maxSize = Math.max(maxSize, dsu.size[i]);
            }
        }

        System.out.println(numComponents + " " + maxSize);
    }
}
