import java.io.*;
import java.util.*;


public class Main {
	
	static BufferedReader br;
    static StringTokenizer st = null;
    static StringBuilder sb;
    
    static int Q;
    static TreeMap<Integer, Integer> valueCount = new TreeMap<>(); // value -> count
    static TreeMap<Integer, Long> valueSum = new TreeMap<>();      // value -> sum
    static HashMap<String, Integer> nameToValue = new HashMap<>(); // name -> value
    
	public static void main(String[] args) throws Exception {
		// System.setIn(new FileInputStream("src/FW2024/[Testcase]_codetree-db_42.txt"));
		br = new BufferedReader(new InputStreamReader(System.in));
        sb = new StringBuilder();

        Q = Integer.parseInt(br.readLine());
        
        for(int i=0; i<Q; i++) {
        	st = new StringTokenizer(br.readLine());
        	String cmd = st.nextToken();
        	if(cmd.equals("init")) {
        		init();
        	}
        	else if(cmd.equals("insert")) {
        		String name = st.nextToken();
        		int value = Integer.parseInt(st.nextToken());
        		int result = insert(name, value);
        		sb.append(result).append("\n");
        	}
        	else if(cmd.equals("delete")) {
        		String name = st.nextToken();
        		int result = delete(name);
        		sb.append(result).append("\n");
        	}
        	else if(cmd.equals("rank")) {
        		int k = Integer.parseInt(st.nextToken());
        		String result = rank(k);
        		sb.append(result).append("\n");
        	}
        	else if(cmd.equals("sum")) {
        		int k = Integer.parseInt(st.nextToken());
        		long result = sum(k);
        		sb.append(result).append("\n");
        	}
        }
        System.out.println(sb.toString());
	}
	
    static void init() {
        valueCount.clear();
        valueSum.clear();
        nameToValue.clear();
    }

    static int insert(String name, int value) {
        if (nameToValue.containsKey(name) || valueCount.containsKey(value)) return 0;
        nameToValue.put(name, value);
        valueCount.put(value, valueCount.getOrDefault(value, 0) + 1);
        valueSum.put(value, valueSum.getOrDefault(value, 0L) + value);
        return 1;
    }

    static int delete(String name) {
        if (!nameToValue.containsKey(name)) return 0;
        int value = nameToValue.remove(name);
        int count = valueCount.get(value);
        if (count == 1) {
            valueCount.remove(value);
            valueSum.remove(value);
        } else {
            valueCount.put(value, count - 1);
            valueSum.put(value, valueSum.get(value) - value);
        }
        return value;
    }

    static String rank(int k) {
        int count = 0;
        for (Map.Entry<Integer, Integer> entry : valueCount.entrySet()) {
            count += entry.getValue();
            if (count >= k) {
                int targetValue = entry.getKey();
                for (Map.Entry<String, Integer> entry2 : nameToValue.entrySet()) {
                    if (entry2.getValue() == targetValue) return entry2.getKey();
                }
            }
        }
        return "None";
    }

    static long sum(int maxValue) {
        long sum = 0;
        for (Map.Entry<Integer, Long> entry : valueSum.headMap(maxValue + 1).entrySet()) {
            sum += entry.getValue();
        }
        return sum;
    }
}

