package com.datastructures.codility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FriendConnections {
    private List<Integer>[] adj;
    private int V;
    private int E;
    private long[][] candyCanShare = null;

    public FriendConnections(int numberOfFriends) {
        adj = new List[numberOfFriends];
        V = numberOfFriends;
        candyCanShare = new long[numberOfFriends][numberOfFriends];
    }

    public void addEdge(int a, int b, int cost) {
        if (adj[a] == null) {
            adj[a] = new ArrayList<Integer>();
        }
        if (adj[b] == null) {
            adj[b] = new ArrayList<Integer>();
        }
        adj[a].add(b);
        adj[b].add(a);
        candyCanShare[a][b] = cost;
    }

    public List<List<Integer>> findLargestGroups() {
        List<List<Integer>> largeGroups = new ArrayList<>();
        int childs = -1;
        for (List<Integer> child : adj) {
            if (child.size() > childs) {
                childs = child.size();
                largeGroups.add(child);
            }
        }
        return largeGroups;
    }

    public int largestValueOfTwoNodes(List<List<Integer>> largeGroups) {
        int multiplication = -1;
        for (List<Integer> group : largeGroups) {
            Collections.sort(group, Collections.reverseOrder());
            if (group.get(0) * group.get(1) > multiplication) {
                multiplication = group.get(0) * group.get(1);
            }
        }
        return multiplication;
    }
}
