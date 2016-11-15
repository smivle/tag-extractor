package com.github.smivle.tag.extractor.impl.clusterdoc;

import com.github.smivle.tag.extractor.impl.clusterdoc.model.Dictionary;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author pc
 */
public class KeyWordProcessing {
    public static double N = 0.5;
    
    class WordModel{
        private final String key;
        private int[] freq = new int[]{0, 0};
        private Map<String, int[]> freqs = new HashMap<>();
        private int clusterSize = 0;

        public WordModel(String key) {
            this.key = key;
        }
        
        public void add(String key){
            if(!freqs.containsKey(key)){
                freqs.put(key, new int[]{0, 0});
            }
            freqs.get(key)[0]++;
            freq[0]++;
        }
        
        public void setSize(int size){
            clusterSize = size;
        }
        
        public void addSize(String key, int size){
            if(freqs.containsKey(key)){
                freqs.get(key)[1] = size;
            }
            freq[1] += size;
        }
        
        public Map<String, int[]> getClusters(){
            Map<String, int[]> map = new HashMap<>();
            for (Map.Entry<String, int[]> entry : freqs.entrySet()) {
                if(((double)entry.getValue()[0]) / entry.getValue()[1] > ((double)freq[0]) / freq[1]){
                    map.put(entry.getKey(), new int[]{entry.getValue()[0], entry.getValue()[1], freq[0], freq[1]});
                }
            }
            return map;
        }
    }
    
    public void process(File dir, Dictionary dict) throws IOException{
        Map<String, WordModel> model = getModel(dir, dict);
        Map<String, Set<String>> cluster2keywords = getClusterId2keywords(model);
        for (Map.Entry<String, Set<String>> entry : cluster2keywords.entrySet()) {
            File file = new File(dir, entry.getKey() + ".keyword");
            FileUtils.write(file, String.join("\n", entry.getValue()));
        }
    }
    
    private Map<String, Set<String>> getClusterId2keywords(Map<String, WordModel> model){
        Map<String, Set<String>> cluster2keywords = new HashMap<>();
        for (Map.Entry<String, WordModel> entry : model.entrySet()) {
            for(Entry<String, int[]> e : entry.getValue().getClusters().entrySet()){
                if(!cluster2keywords.containsKey(e.getKey())){
                    cluster2keywords.put(e.getKey(), new HashSet<String>());
                }
                cluster2keywords.get(e.getKey()).add(entry.getKey() + "\t" + e.getValue()[0] + "\t" + e.getValue()[1] + "\t" + e.getValue()[2] + "\t" + e.getValue()[3]);
            }
        }
        return cluster2keywords;
    }
    
    private Map<String, WordModel> getModel(File dir, Dictionary dict) throws IOException{
        Map<String, WordModel> model = new HashMap<>();
        int csize = dir.list().length;
        for (File clusterDir : dir.listFiles()) {
            String clusterName = clusterDir.getName();
            int size = clusterDir.list().length;
            for (File file : clusterDir.listFiles()) {
                for (String word : dict.getWords(FileUtils.readFileToString(file, "utf-8")).keySet()) {
                    if(!model.containsKey(word)){
                        model.put(word, new WordModel(word));
                    }
                    model.get(word).add(clusterName);
                }
            }
            
            for (Map.Entry<String, WordModel> entry : model.entrySet()) { 
                entry.getValue().addSize(clusterName, size);
            }
        }
        for (Map.Entry<String, WordModel> entry : model.entrySet()) { 
            entry.getValue().setSize(csize);
        }
        return model;
    }
}
