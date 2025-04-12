import java.io.*;
import java.util.*;

public class Main {

    static BufferedReader br;
    static StringTokenizer st;
    static StringBuilder sb;

    static int Q;
    static TreeMap<Integer, String> valueToName = new TreeMap<>();
    static HashMap<String, Integer> nameToValue = new HashMap<>();

    public static void main(String[] args) throws Exception {
        br = new BufferedReader(new InputStreamReader(System.in));
        sb = new StringBuilder();

        Q = Integer.parseInt(br.readLine());

        for (int i = 0; i < Q; i++) {
            st = new StringTokenizer(br.readLine());
            String cmd = st.nextToken();
            switch (cmd) {
                case "init":
                    init();
                    break;
                case "insert": {
                    String name = st.nextToken();
                    int value = Integer.parseInt(st.nextToken());
                    sb.append(insert(name, value)).append("\n");
                    break;
                }
                case "delete": {
                    String name = st.nextToken();
                    sb.append(delete(name)).append("\n");
                    break;
                }
                case "rank": {
                    int k = Integer.parseInt(st.nextToken());
                    sb.append(rank(k)).append("\n");
                    break;
                }
                case "sum": {
                    int k = Integer.parseInt(st.nextToken());
                    sb.append(sum(k)).append("\n");
                    break;
                }
            }
        }
        System.out.print(sb);
    }

    static void init() {
        valueToName.clear();
        nameToValue.clear();
    }

    static int insert(String name, int value) {
        if (nameToValue.containsKey(name) || valueToName.containsKey(value)) return 0;
        nameToValue.put(name, value);
        valueToName.put(value, name);
        return 1;
    }

    static int delete(String name) {
        if (!nameToValue.containsKey(name)) return 0;
        int value = nameToValue.remove(name);
        valueToName.remove(value);
        return value;
    }

    static String rank(int k) {
        if (valueToName.size() < k) return "None";
        Iterator<Map.Entry<Integer, String>> it = valueToName.entrySet().iterator();
        while (--k > 0 && it.hasNext()) it.next();
        return it.hasNext() ? it.next().getValue() : "None";
    }

    static long sum(int maxValue) {
        long result = 0;
        for (Map.Entry<Integer, String> entry : valueToName.headMap(maxValue + 1).entrySet()) {
            result += entry.getKey();
        }
        return result;
    }
}
