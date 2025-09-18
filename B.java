import java.util.*;
import java.io.*;

public class IslandFlooding {
    static int n, m;
    static int[][] heights;
    static int[][] floodTime;
    static int[] dx = {-1, 1, 0, 0}; // вверх, вниз, влево, вправо
    static int[] dy = {0, 0, -1, 1};
    
    static class Cell implements Comparable<Cell> {
        int x, y, time;
        
        Cell(int x, int y, int time) {
            this.x = x;
            this.y = y;
            this.time = time;
        }
        
        @Override
        public int compareTo(Cell other) {
            return Integer.compare(this.time, other.time);
        }
    }
    
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        
        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());
        
        heights = new int[n][m];
        floodTime = new int[n][m];
        
        // Инициализируем время затопления как бесконечность
        for (int i = 0; i < n; i++) {
            Arrays.fill(floodTime[i], Integer.MAX_VALUE);
        }
        
        // Читаем карту высот
        for (int i = 0; i < n; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < m; j++) {
                heights[i][j] = Integer.parseInt(st.nextToken());
            }
        }
        
        // Приоритетная очередь для алгоритма Дейкстры
        PriorityQueue<Cell> pq = new PriorityQueue<>();
        
        // Добавляем в очередь все клетки с высотой 0 (уже затоплены)
        boolean hasWater = false;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (heights[i][j] == 0) {
                    floodTime[i][j] = 0;
                    pq.offer(new Cell(i, j, 0));
                    hasWater = true;
                }
            }
        }
        
        // Если нет клеток с водой (высотой 0), то каждая клетка затапливается через время = её высота
        if (!hasWater) {
            PrintWriter pw = new PrintWriter(System.out);
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    pw.print(heights[i][j]);
                    if (j < m - 1) pw.print(" ");
                }
                pw.println();
            }
            pw.flush();
            return;
        }
        
        // Алгоритм Дейкстры для поиска минимального времени затопления
        while (!pq.isEmpty()) {
            Cell current = pq.poll();
            int x = current.x;
            int y = current.y;
            int currentTime = current.time;
            
            // Если уже обработали эту клетку с лучшим временем, пропускаем
            if (currentTime > floodTime[x][y]) {
                continue;
            }
            
            // Проверяем всех соседей
            for (int dir = 0; dir < 4; dir++) {
                int nx = x + dx[dir];
                int ny = y + dy[dir];
                
                // Проверяем границы
                if (nx >= 0 && nx < n && ny >= 0 && ny < m) {
                    // Время затопления соседа = max(текущее время + 1, высота соседа)
                    int newFloodTime = Math.max(currentTime + 1, heights[nx][ny]);
                    
                    // Если найден более быстрый путь
                    if (newFloodTime < floodTime[nx][ny]) {
                        floodTime[nx][ny] = newFloodTime;
                        pq.offer(new Cell(nx, ny, newFloodTime));
                    }
                }
            }
        }
        
        // Выводим результат
        PrintWriter pw = new PrintWriter(System.out);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                // Если клетка недостижима, то она затапливается через время = её высота
                if (floodTime[i][j] == Integer.MAX_VALUE) {
                    pw.print(heights[i][j]);
                } else {
                    pw.print(floodTime[i][j]);
                }
                if (j < m - 1) pw.print(" ");
            }
            pw.println();
        }
        pw.flush();
    }
}
