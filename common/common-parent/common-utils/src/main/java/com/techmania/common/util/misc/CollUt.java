package com.techmania.common.util.misc;

import com.techmania.entity.dsmodels.CacheAbleInformation;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CollUt {
    public Map<String, CacheAbleInformation> cacheableInformationMap = new HashMap<>();

    public static void main(String[] args) {
        CollUt ut = new CollUt();
        ut.put("CR01", "QC001", "15000");
        ut.put("CR01", "QC001", "15000");
        ut.put("CR03", "QC001", "15000");
        System.out.println("Filtered: " + ut.get("QC001", "15000").get());
    }

    public void put(String craneId, String areaId, String match) {
        CacheAbleInformation craneCacheInfo = new CacheAbleInformation(areaId, match, System.nanoTime());
        cacheableInformationMap.put(craneId, craneCacheInfo);
    }

    public Optional<CacheAbleInformation> get(String areaId, String match2) {
        return cacheableInformationMap
                .entrySet()
                .stream()
                .map(entry -> entry.getValue())
                .peek(data -> System.out.println(data.toString()))
                .max(Comparator.comparing(CacheAbleInformation::getTimestamp));
    }
}
