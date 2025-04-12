import java.io.*;
import java.util.*;


public class Main {
	
	static BufferedReader br;
    static StringTokenizer st = null;
    static StringBuilder sb;
    
    static int Q;
    static TreeMap<Integer, String> valueToName = new TreeMap<>();
    static HashMap<String, Integer> nameToValue = new HashMap<>();
    
	public static void main(String[] args) throws Exception {
		br = new BufferedReader(new InputStreamReader(System.in));
        sb = new StringBuilder();
        
        nameToValue = new HashMap<>();
        valueToName = new TreeMap<>();

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
        		int result = sum(k);
        		sb.append(result).append("\n");
        	}
        }
        System.out.println(sb.toString());
	}
	
	static void init() {
		/**
		 * 이는 table에 포함된 모든 데이터를 삭제합니다. 해당 쿼리는 가장 처음에 항상 주어지 며, 최대 50번 주어집니다.
		 */
		nameToValue.clear();
        valueToName.clear();
	}
	
	static int insert(String n, int v) {
		if (nameToValue.containsKey(n) || valueToName.containsKey(v)) return 0;
	    nameToValue.put(n, v);
	    valueToName.put(v, n);
	    return 1;
	}
	
	
	static int delete(String n) {
		if (!nameToValue.containsKey(n)) return 0;
	    int val = nameToValue.remove(n);
	    valueToName.remove(val);
	    return val;
	}
	
	static String rank(int k) {
		if (valueToName.size() < k) return "None";
	    Iterator<Integer> it = valueToName.navigableKeySet().iterator();
	    for (int i = 1; i < k; i++) it.next();
	    return valueToName.get(it.next());
	}
	
	static int sum(int max_value) {
		int sum = 0;
	    for (Map.Entry<Integer, String> e : valueToName.headMap(max_value + 1).entrySet()) {
	        sum += e.getKey();
	    }
	    return sum;
	}
}
