package assignment3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Maze{
	
	public static final int HEIGHT = 20;
	public static final int WIDTH = 30;

	public static void main(String[] args) throws Exception{
				
		// reading the file
		BufferedReader in = new BufferedReader(new FileReader("simple_maze_20x30.txt"));

		//create a maze 20x30 of 1 and 0
		int[][] maze = new int[HEIGHT][WIDTH];

		System.out.println("Original maze");

		for(int i = 0; i < HEIGHT; i++){
			String line = in.readLine();
			for(int j = 0; j < WIDTH; j++){
				maze[i][j] = Character.getNumericValue(line.charAt(j));
				System.out.print(maze[i][j]);
			}
			System.out.println();
		}

		in.close();
		
		// creating a maze to hold possible ways
		int[][] wayMaze = new int[HEIGHT][WIDTH];
		for(int i = 0; i < HEIGHT; i++){
			for(int j = 0; j < WIDTH; j++){
				wayMaze[i][j] = -1;
			}
		}
		
		// finding start and finish
		int[] start = startAndFinish(maze, 0);
		int[] finish = startAndFinish(maze, HEIGHT-1);
		
		// create a list to hold current nodes
		List<Integer> nodes = new ArrayList<Integer>();
		nodes.add(start[0]);
		nodes.add(start[1]);
		
		//change wayMaze to mark start node as 1st step
		wayMaze[start[0]][start[1]] = 1;

		int step  = 2;// since 1st step is the start position we'll begin with step == 2
		boolean finishFound = false;
		do{
			
			int[][] neighbours = findNeighbours(maze, wayMaze, nodes );

			wayMaze = updateWayMaze(wayMaze, step, neighbours, nodes);
			nodes.clear();
			nodes = updateNodes(nodes, step, wayMaze);
			if(wayMaze[finish[0]][finish[1]] != -1 || nodes.size() == 0){
				finishFound = true;
			}
			step++;
		}while(!finishFound);
		
		//establish backtrace
		List<Integer> path = new ArrayList<Integer>();
		path = createPath(path, start, finish, wayMaze);
		
		int step_counter = 1;
		int[] meet_at_point = new int[2];
		int meet = (path.size())/4 +1;// /2 - to get the path length and /2 to get the middle point +1 'cause 49/2 = 24+24+1
		System.out.println("meet at "+ meet);
		
		//updating maze
		for(int i = 0; i < path.size(); ){
			maze[path.get(i)][path.get(i+1)] = 4;
			if(step_counter == meet){
				meet_at_point[0] = path.get(i);
				meet_at_point[1] = path.get(i+1);
			}
			i += 2;
			step_counter++;
		}
		//print our maze with path
		System.out.println();
		System.out.println("Maze with path");
		for(int i = 0; i < HEIGHT; i++){
			for(int j = 0; j < WIDTH; j++){
				System.out.print(maze[i][j]);
			}
			System.out.println();
		}
		System.out.println("The length of path is " + path.size()/2 + " steps.");
		System.out.println("In best case parts can meet each other at point: [" + meet_at_point[0] + ", "+
				+ meet_at_point[1] + "].");

	}
	
	public static List<Integer> createPath(List<Integer> path, int[] start, int[] finish, int[][] wayMaze){
		int i = finish[0];
		int j = finish[1];
		path.add(i);
		path.add(j);
		do{
			if(wayMaze[i][j-1] == wayMaze[i][j]-1){
				j -= 1;
				path.add(i);
				path.add(j);
			}
			if(wayMaze[i-1][j] == wayMaze[i][j]-1){
				i -= 1;
				path.add(i);
				path.add(j);
			}
			if(wayMaze[i][j+1] == wayMaze[i][j]-1){
				j += 1;
				path.add(i);
				path.add(j);
			}
			if(wayMaze[i+1][j] == wayMaze[i][j]-1){
				i += 1;
				path.add(i);
				path.add(j);
			}
		}
		while(i != start[0] && j != start[1]);
		path.add(start[0]);
		path.add(start[1]);
		
		return path;
	}
	
	public static List<Integer> updateNodes(List<Integer> nodes, int step, int[][] wayMaze){
		/*write down all nodes which == step
		 * */
		for(int i = 0; i < HEIGHT; i++){
			for(int j = 0; j < WIDTH; j++){
				if(wayMaze[i][j] == step){
					nodes.add(i);
					nodes.add(j);
				}
			}
		}
		
		return nodes;
	}
	
	public static int[][] updateWayMaze(int[][] wayMaze, int step, int[][] neighbours, List<Integer> nodes){
		/*
		 * update wayMaze, mark visited nodes with the step number
		 * */
		int index = 0;
		for(int i = 0; i < neighbours.length; i++){
			for(int j = 0; j < neighbours[0].length; j++){
				if(j == 0 && neighbours[i][j] == 1 && wayMaze[nodes.get(index)-1][nodes.get(index+1)] == -1){
					wayMaze[nodes.get(index)-1][nodes.get(index+1)] = step;
				}
				if(j == 1 && neighbours[i][j] == 1 && wayMaze[nodes.get(index)][nodes.get(index+1)+1] == -1){
					wayMaze[nodes.get(index)][nodes.get(index+1)+1] = step;
				}
				if(j == 2 && neighbours[i][j] == 1 && wayMaze[nodes.get(index)+1][nodes.get(index+1)] == -1){
					wayMaze[nodes.get(index)+1][nodes.get(index+1)] = step;
				}
				if(j == 3 && neighbours[i][j] == 1 && wayMaze[nodes.get(index)][nodes.get(index+1)+1] == -1){
					wayMaze[nodes.get(index)][nodes.get(index+1)+1] = step;
				}
			}
			index += 2;
		}
		
		return wayMaze;
	}
	
	
	public static int[][] findNeighbours(int[][] maze, int[][] wayMaze, List<Integer> nodes ){
		/*
		 * Counter how many 'neighbours' nodes have (or where we can go from certain nodes).
		 * nodes = {1,2,1,3... etc} where numbers are indexes of maze: maze[1][2], maze[1][3] etc.
		 * */
		
		int size = nodes.size()/2;
		
		int[][] neighbours = new int[size][4];// 4 stays here for four neighbours (north, east, south, west)
		
		int counter = 0;
		for(int i = 0; i < size; i++){
			
			if(checkBorders("north", nodes.get(counter)-1) && maze[nodes.get(counter)-1][nodes.get(counter+1)] == 1 && wayMaze[nodes.get(counter)-1][nodes.get(counter+1)] == -1){
				neighbours[i][0] = 1;
			}
			if(checkBorders("east", nodes.get(counter+1)+1) && maze[nodes.get(counter)][nodes.get(counter+1)+1] == 1 && wayMaze[nodes.get(counter)][nodes.get(counter+1)+1] == -1){
				neighbours[i][1] = 1;
			}
			if(checkBorders("south", nodes.get(counter)+1) && maze[nodes.get(counter)+1][nodes.get(counter+1)] == 1 && wayMaze[nodes.get(counter)+1][nodes.get(counter+1)] == -1){
				neighbours[i][2] = 1;
			}
			if(checkBorders("west", nodes.get(counter+1)-1) && maze[nodes.get(counter)][nodes.get(counter+1)-1] == 1 && wayMaze[nodes.get(counter)][nodes.get(counter+1)-1] == -1){
				neighbours[i][3] = 1;
			}
			counter += 2;
		}
		
		return neighbours;
	}
	
	public static boolean checkBorders(String way, int n){
		/*
		 * Check if a node is not at the border of the maze
		 * */
		boolean border = false;
		switch(way){
		case "north":
			if(n >= 0){
				border = true;
			}
			break;
		case "east":
			if(n < WIDTH){
				border = true;
			}
			break;
		case "south":
			if(n < HEIGHT){
				border = true;
			}
			break;
		case "west":
			if(n >= 0){
				border = true;
			}
			break;	
		default:
			break;
		}
		
		return border;
	}
	
	public static int[] startAndFinish(int[][] maze, int rowNumber){
		/*
		 * Find start and finish coordinates
		 * */
		int[] startFinish = new int[2];
		for(int i = 0; i < maze[rowNumber].length; i++){
			if (maze[rowNumber][i] == 1){
				startFinish[0] = rowNumber;
				startFinish[1] = i;
			}
		}
		return startFinish;
	}
	
	

}
