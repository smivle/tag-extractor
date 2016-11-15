package com.github.smivle.tag.extractor.impl.clusterdoc.model;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author pc
 */
public class ClusterWords {
    private final String word;
    private final int[] freq = new int[]{0, 0};
    private final Map<String, int[]> clusterFreqs = new HashMap<>();
    private int clusterCount = 0;

    public ClusterWords(String word) {
        this.word = word;
    }

    public void addClusterWithWord(String clusterId){
        if(!clusterFreqs.containsKey(clusterId)){
            clusterFreqs.put(clusterId, new int[]{0, 0});
        }
        clusterFreqs.get(clusterId)[0]++;
        freq[0]++;
    }

    public void addClusterSize(String clusterId, int size){
        if(clusterFreqs.containsKey(clusterId)){
            clusterFreqs.get(clusterId)[1] = size;
        }
        freq[1] += size;
    }

    public Map<String, KeyWord> getClusters(){
        Map<String, KeyWord> map = new HashMap<>();
        for (Map.Entry<String, int[]> entry : clusterFreqs.entrySet()) {
            KeyWord kw = new KeyWord(word, entry.getValue()[0], entry.getValue()[1], freq[0], freq[1]);
            if(kw.isValid()){
                map.put(entry.getKey(), kw);
            }
        }
        return map;
    }
    
    public void setClusterCount(int clusterCount){
        this.clusterCount = clusterCount;
    }
}
