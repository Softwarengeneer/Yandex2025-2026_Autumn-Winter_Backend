import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int N = Integer.parseInt(br.readLine().trim());
        String[] parts = br.readLine().trim().split(" ");
        int[] h = new int[N];
        for (int i = 0; i < N; i++) {
            h[i] = Integer.parseInt(parts[i]);
        }

        int[] result = new int[N];
        Arrays.fill(result, -1);

        // обработка отдельно чётных и нечётных индексов
        process(h, result, 0); // чётные
        process(h, result, 1); // нечётные

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < N; i++) {
            if (i > 0) sb.append(" ");
            sb.append(result[i]);
        }
        System.out.println(sb);
    }

    private static void process(int[] h, int[] result, int parity) {
        Deque<Integer> stack = new ArrayDeque<>();
        for (int i = 0; i < h.length; i++) {
            if (i % 2 != parity) continue;
            while (!stack.isEmpty() && h[i] > h[stack.peek()]) {
                int idx = stack.pop();
                result[idx] = i - idx;
            }
            stack.push(i);
        }
    }
}
