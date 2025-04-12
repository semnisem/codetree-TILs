import java.io.*;
import java.util.*;

public class Main {

    static BufferedReader br;
    static StringTokenizer st = null;
    static StringBuilder sb;

    static int N, M;
    static int[][] map;
    static int[] dr = {-1, 1, 0, 0}, dc= {0, 0, -1, 1};
    static int[] house = new int[2], park= new int[2];
    static int[][] warriors;
    static int[] status; // 0: 정상, -1:소멸, 1: 얼음
    static Deque<int[]> medusaPath;
    
    static int SUM_MOVE, STONE, ATTACK;
    
    static void findPath() { // bfs로 찾기
    	int r = house[0];
    	int c = house[1];
    	boolean[][] v = new boolean[N][N];
    	int[][][] prev = new int[N][N][2]; // memoization
    	
    	v[r][c]=true;
    	Queue<int[]> queue = new ArrayDeque<>();
    	queue.add(new int[] {r, c});
    	
    	while(!queue.isEmpty()) {
    		int[] curr = queue.poll();
    		r = curr[0];
    		c = curr[1];
    		
    		// 도착지 도착
    		if(r==park[0] && c==park[1]) {
//    			System.out.println("found path\n");
    			
    			// 경로 역추적
    			medusaPath = new ArrayDeque<>();
    			int[] at = {r, c};
    			while (!(at[0]==house[0]&&at[1]==house[1])) {
    				medusaPath.addFirst(new int[] {at[0], at[1]});
    				at = prev[at[0]][at[1]];
    			}
    			// medusaPath.addFirst(new int[] {house[0], house[1]});
    			
    			return;
    		}
    		
    		// 다음 방문
			for(int d=0; d<4; d++) {
	    		int nr = r+dr[d];
	    		int nc = c+dc[d];
	    		if(nr<0 || nc<0 || nr>=N || nc>=N || map[nr][nc]==1 || v[nr][nc]) continue;
	    		
	    		v[nr][nc]=true;
	    		queue.add(new int[] {nr, nc});
	    		prev[nr][nc][0] = r; // (nr,nc)의 이전 좌표 행은 r
	            prev[nr][nc][1] = c; // (nr,nc)의 이전 좌표 열은 c
			}
    	}
    	
    	// 도달 실패
//    	System.out.println("no path: "+"-1");
    }
    
    public static void main(String[] args) throws Exception {
        br = new BufferedReader(new InputStreamReader(System.in));
        sb = new StringBuilder();
        
        // input - 1st
        st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        
        // input - 2nd
        st = new StringTokenizer(br.readLine());
        house[0] = Integer.parseInt(st.nextToken());
        house[1] = Integer.parseInt(st.nextToken());
        park[0] = Integer.parseInt(st.nextToken());
        park[1] = Integer.parseInt(st.nextToken());
        
        // input - 3rd
        st = new StringTokenizer(br.readLine());
        warriors = new int[M][2];
        status = new int[M];
        for(int i=0; i<M; i++) {
        	warriors[i][0] = Integer.parseInt(st.nextToken());
        	warriors[i][1] = Integer.parseInt(st.nextToken());
        }
        
        // input - map
        map = new int[N][N]; // 도로:0, 불가능:1
        for(int i=0; i<N; i++){
        	st = new StringTokenizer(br.readLine());
            for(int j=0; j<N; j++){
                map[i][j]=Integer.parseInt(st.nextToken());
            }
        }
        
        // algo - bfs -> medusaPath
        findPath();
//        medusaPath.forEach(coord -> System.out.printf(Arrays.toString(coord)));
        
        // algo - simulation
        if(medusaPath==null) {
        	sb.append(-1);
        }
        else {        	
        	while(medusaPath.size()>1) {
        		SUM_MOVE=0; STONE=0; ATTACK=0;
        		simulate(medusaPath.pollFirst());
        		sb.append(SUM_MOVE).append(" ").append(STONE).append(" ").append(ATTACK).append("\n");
        		
        	}
        	sb.append(0);
        }
        
        // output
        System.out.println(sb.toString());
    }
    
    static void simulate(int[] curr) { 
    	// 0. 메두사의 이동
    	for(int i=0; i<M; i++) { 		
			if(status[i]==0&&warriors[i][0]==curr[0]&&warriors[i][1]==curr[1]) {
				status[i]=-1;// 소멸
				continue;
			}
		}
    	
//    	System.out.println("\n<< simulate >> medusa: "+Arrays.toString(curr));
    	// 1. 메두사의 시선
		boolean[][] grid = see(curr); 
//		System.out.println("시선 result");
//		for (int i = 0; i < N; i++) {
// 	        for (int j = 0; j < N; j++) {
// 	            System.out.print(grid[i][j] ? "■ " : "□ "); 
// 	        }
// 	        System.out.println();
// 	    }
//		System.out.println();
		
		// 2. 전사들의 이동
		for(int i=0; i<M; i++) { 
			
			if(status[i]==0) {
				SUM_MOVE += step1(grid, curr, warriors[i]); // [i]전사의 1차 움직임
				if(warriors[i][0]==curr[0]&&warriors[i][1]==curr[1]) {
					status[i]=-1; ATTACK++; // 소멸
					continue;
				}
				SUM_MOVE += step2(grid, curr, warriors[i]);  // [i]전사의 2차 움직임
				if(warriors[i][0]==curr[0]&&warriors[i][1]==curr[1]) {
					status[i]=-1; ATTACK++; // 소멸
					continue;
				}
			}
			else if(status[i]==1) { //동작그만을 해제
				status[i]=0;
			}
		}
//		System.out.println("stoned "+STONE
//				+", status: "+Arrays.toString(status)
//				+"\n position: "+Arrays.deepToString(warriors));
//		System.out.println();  // 한 줄 끝
    }
    
    // 상하좌우 가까워지도록
    static int step1(boolean[][] grid, int[] medusa, int[] warrior) {
    	int[] wdr = {-1,1,0,0};
    	int[] wdc = {0,0,-1,1};
    	int min_dist = Math.abs(medusa[0]-warrior[0])+Math.abs(medusa[1]-warrior[1]);
    	int min_d = -1;
    	
    	for(int d=0; d<4; d++) {
    		int nr=warrior[0]+wdr[d];
    		int nc=warrior[1]+wdc[d];
    		if (nr < 0 || nc < 0 || nr >= N || nc >= N || grid[nr][nc]) continue;
    		int dist = Math.abs(medusa[0]-nr)+Math.abs(medusa[1]-nc);
    		if(dist<min_dist) {
    			min_dist=dist;
    			min_d=d;
    		}
    	}
    	
    	if(min_d!=-1) { // move
    		//System.out.println("[1]Before: "+Arrays.toString(warrior));
    		warrior[0]+=wdr[min_d];
    		warrior[1]+=wdc[min_d];
    		//System.out.println("[1]After: "+Arrays.toString(warrior));
    		return 1;
    	}
    	return 0;
    }
    
    // 좌우상하 가까워지도록
    static int step2(boolean[][] grid, int[] medusa, int[] warrior) {
    	int[] wdr = {0,0,-1,1};
    	int[] wdc = {-1,1,0,0};
    	
    	int min_dist = Math.abs(medusa[0]-warrior[0])+Math.abs(medusa[1]-warrior[1]);
    	int min_d = -1;
    	
    	for(int d=0; d<4; d++) {
    		int nr=warrior[0]+wdr[d];
    		int nc=warrior[1]+wdc[d];
    		if (nr < 0 || nc < 0 || nr >= N || nc >= N || grid[nr][nc]) continue;
    		int dist = Math.abs(medusa[0]-nr)+Math.abs(medusa[1]-nc);
    		if(dist<min_dist) {
    			min_dist=dist;
    			min_d=d;
    		}
    	}
    	
    	if(min_d!=-1) { // move
    		//System.out.println("[2]Before: "+Arrays.toString(warrior));
    		warrior[0]+=wdr[min_d];
    		warrior[1]+=wdc[min_d];
    		//System.out.println("[2]After: "+Arrays.toString(warrior));
    		return 1;
    	}
    	return 0;
    }
    
    static boolean[][] see(int[] cur) {
    	
    	int max_watched = 0;
    	int max_d = 0;
    	boolean[][][] grid = new boolean[4][N][N]; // 시선이 닿는 곳
    	
    	for(int d=0; d<4; d++) {
        	// 메두사의 시선
    		mark(cur[0], cur[1], grid[d], d);
        	
        	// 전사들의 그림자
    		for(int i=0; i<M; i++) {
    			int[] w = warriors[i];
    			if(status[i]!=-1&&grid[d][w[0]][w[1]]) {
    				unmark(cur, w, grid[d], d);
				}
    		}
        	
        	// 최종 전사들 수 계산
    		int watched = 0;
    		for(int i=0; i<M; i++) {
    			int[] w = warriors[i];
    			if(status[i]!=-1&&grid[d][w[0]][w[1]]) {
					watched++;
				}
    		}
    		if(max_watched<watched) {
    			max_watched=watched;
    			max_d=d;
    		}
    	}
    	
    	// 최대 전사를 바라보는 방향으로 세팅
    	STONE = max_watched;
    	for(int i=0; i<M; i++) {
			int[] w = warriors[i];
			if(status[i]!=-1&&grid[max_d][w[0]][w[1]]) {
				status[i]=1;
			}
		}		
    	return grid[max_d];
    }
   
    static void unmark(int[] medusa, int[] warrior, boolean[][] grid, int d) {
 	   int depth = 1;
 	   while (true) {
 		   int rr = warrior[0] + dr[d]*depth;
 		   int cc = warrior[1] + dc[d]*depth;
 		   if(rr<0||cc<0||rr>=N||cc>=N) break;
 		   
 		   int offset;
		   if(d==0||d==1) {
			   if(cc==medusa[1]) {
				   offset=0;
			   }
			   else if(cc<medusa[1]) {
				   offset=-depth;
			   }
			   else {
				   offset=depth;
			   }
			   
			   int nr = rr;
			   for(int nc=Math.min(cc, cc+offset); nc<=Math.max(cc, cc+offset); nc++) {
				   if(nr<0||nc<0||nr>=N||nc>=N) continue;
	 			   grid[nr][nc]=false;
	 			   // System.out.println("unmarked ("+nr+","+nc+")");
			   }
		   }
		   else {
			   if(rr==medusa[0]) {
				   offset=0;
			   }
			   else if(rr<medusa[0]) {
				   offset=-depth;
			   }
			   else {
				   offset=depth;
			   }
			   
			   int nc=cc;
			   for(int nr=Math.min(rr, rr+offset); nr<=Math.max(rr, rr+offset); nr++) {
				   if(nr<0||nc<0||nr>=N||nc>=N) continue;
	 			   grid[nr][nc]=false;
	 			   // System.out.println("unmarked ("+nr+","+nc+")");
			   }
		   }
 		   depth++;
 	   }
    }
   static void mark(int r, int c, boolean[][] grid, int d) {
	   int depth = 1;
	   while (true) {
		   int rr = r + dr[d]*depth;
		   int cc = c + dc[d]*depth;
		   if(rr<0||cc<0||rr>=N||cc>=N) break;
		   
		   for(int offset= -depth; offset<=depth; offset++) {
			   int nr, nc;
			   if(d==0||d==1) {
				   nr = rr;
				   nc = cc + offset;
			   }
			   else {
				   nr = rr + offset;
				   nc = cc;
			   }
			   if(nr<0||nc<0||nr>=N||nc>=N) continue;
			   grid[nr][nc]=true;
		   }
		   depth++;
	   }
   }
   
   
}