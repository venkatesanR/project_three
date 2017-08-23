package com.techmania.datastructures;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class Solution {
	private static long totalDistance = 0;
	private static Graph graph = null;
	private static final BigInteger intE = (new BigInteger("10").pow(9)).add(new BigInteger("9"));

	public static void main(String[] args) throws IOException {
		/*
		 * Enter your code here. Read input from STDIN. Print output to STDOUT.
		 * Your class should be named Solution.
		 */
		List<String> dataDetails = FileUtils.readLines(
				new File("D:\\project\\project_three\\common\\common-parent\\common\\src\\main\\resources\\tree.txt"));
		String nodesQ = dataDetails.get(0);
		String[] primLine = nodesQ.split(" ");
		int nodes = Integer.valueOf(primLine[0]);
		int query = Integer.valueOf(primLine[1]);
		graph = new Graph(nodes);
		for (int index = 1; index < nodes; index++) {
			String[] edges = dataDetails.get(index).split(" ");
			Integer e1 = Integer.valueOf(edges[0]);
			Integer e2 = Integer.valueOf(edges[1]);
			graph.addEdge(e1, e2);
		}
		long start=System.currentTimeMillis();
		for (int index = nodes; index < dataDetails.size();) {
			totalDistance = 0;
			int noQ = Integer.valueOf(dataDetails.get(index));
			List<String> quList = new ArrayList<>();
			if (index + 1 < dataDetails.size()) {
				quList = Arrays.asList(dataDetails.get(index + 1).split(" "));
				if (quList != null && quList.size() > 1) {
					combineAndGetSum(quList);
					totalDistance = new BigInteger(String.valueOf(totalDistance)).mod(intE).longValueExact();
				}
				System.out.println(totalDistance);
			}
			index += 2;
		}
		System.out.println("Execution Time :"+(System.currentTimeMillis()-start));
		/*
		 * System.out.println(graph.distance(1, 3));
		 * System.out.println(graph.distance(1, 4));
		 * System.out.println(graph.distance(3,4));
		 */
	}

	private static void combineAndGetSum(List<String> data) {
		if (data.size() == 1) {
			return;
		} else if (data.size() == 2) {
			int source = Integer.valueOf(data.get(0));
			int dest = Integer.valueOf(data.get(1));
			totalDistance = ((source) * (dest) * graph.distance(source - 1, dest - 1));

		} else if (data.size() > 2) {
			processFact(0, data);
		}
	}

	private static void processFact(int start, List<String> data) {
		if (start < data.size()) {
			for (int index = start + 1; index < data.size(); index++) {
				int source = Integer.valueOf(data.get(start));
				int dest = Integer.valueOf(data.get(index));
				totalDistance += ((source) * (dest) * graph.distance(source - 1, dest - 1));
			}
			processFact(start + 1, data);
		}

	}
}
