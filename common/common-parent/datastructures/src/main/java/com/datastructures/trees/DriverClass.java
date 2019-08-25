package com.datastructures.trees;

import java.util.ArrayList;
import java.util.List;

public class DriverClass {
    /**
     * #1. Traverse forward and backward
     * #2. forward traversal of each substring sorce(defore)
     * #3. backward traversal of each substring sorce(defore)
     * #3.Befor #3 get Min of previous distance.
     *
     * Time Complexity ~O(n)
     **/
    private void nearestDistanceFromEachSource(String inputSequence, char source) {
        long maxValueBefore = Long.MAX_VALUE;

        List<Long> DISTANCE_LIST = new ArrayList<>();

        //forward from source
        for (int before = 0; before < inputSequence.length(); before++) {
            if (inputSequence.charAt(before) == source) {
                maxValueBefore = before;
            }
            DISTANCE_LIST.add(before - maxValueBefore);
        }

        //reset max distance
        //backward from soucre
        maxValueBefore = Long.MAX_VALUE;
        for (int after = inputSequence.length() - 1; after >= 0; after--) {
            if (inputSequence.charAt(after) == source) {
                maxValueBefore = after;
            }
            DISTANCE_LIST.set(after, Math.min(DISTANCE_LIST.get(after), maxValueBefore - after));
        }
        DISTANCE_LIST.stream().forEach(distance -> {
            System.out.println(distance);
        });
    }

}
