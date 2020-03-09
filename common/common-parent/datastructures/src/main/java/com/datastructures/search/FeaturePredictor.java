package com.datastructures.search;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FeaturePredictor {
    public static void main(String[] args) {
        int numFeatures = 5;
        int topFeatures = 2;
        List<String> possibleFeatures = new ArrayList<>();
        possibleFeatures.add("AMMA");
        possibleFeatures.add("APPA");
        possibleFeatures.add("WIFE");
        possibleFeatures.add("SON");
        possibleFeatures.add("BRO");

        int numFeatureRequests = 5;
        List<String> featureRequests = new ArrayList<>();
        featureRequests.add("I L AMMA AMMA");
        featureRequests.add("APPA");
        featureRequests.add("SON APPA");
        featureRequests.add("WIFE BRO");
        featureRequests.add("SON APPA");
        System.out.println(popularNFeatures(numFeatures, topFeatures, possibleFeatures, numFeatureRequests, featureRequests));
    }

    public static ArrayList<String> popularNFeatures(int numFeatures,
                                                     int topFeatures,
                                                     List<String> possibleFeatures,
                                                     int numFeatureRequests,
                                                     List<String> featureRequests) {


        Set<String> featureSet = possibleFeatures.stream()
                .map(String::toUpperCase)
                .collect(Collectors.toSet());

        Stream<String> filteredInput = featureRequests.stream()
                .flatMap(request -> Stream.of(request.split(" ")).distinct())
                .map(String::toUpperCase)
                .filter(keyword -> featureSet.contains(keyword));


        if (topFeatures > possibleFeatures.size()) {

        }

        Map<String, Long> featureCounter = new HashMap<>();
        filteredInput
                .forEach(keyword -> {
                    long counter = 1;
                    if (featureCounter.containsKey(keyword)) {
                        counter += featureCounter.get(keyword);
                    }
                    featureCounter.put(keyword, counter);
                });

        return (ArrayList<String>) featureCounter.entrySet().stream()
                .sorted((enrty, t1) -> enrty.getValue() < t1.getValue() ? 1 : -1)
                .limit(topFeatures)
                .map(Map.Entry::getKey)
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());
    }
}
