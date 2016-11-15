package com.github.smivle.tag.extractor.impl.clusterdoc.model;

import com.github.smivle.common.text.TextUtils;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.TObjectIntHashMap;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author pc
 */
public class Dictionary {
    private Map<String, Integer> wordPos = new HashMap<>();
    private Set<String> filterSet;
    private Map<String, String> formDict = new HashMap<>();
    private Map<String, String> errorDict = new HashMap<>();
    private Map<String, String> reloDict = new HashMap<>();
    private Set<String> stopDict = new HashSet<>();

    public Dictionary(File dictDir) throws IOException {
        init(dictDir);
    }
    
    public Dictionary(File filterSetFile, File dictDir) throws IOException {
        if(filterSetFile.exists()){
            filterSet = new HashSet<>();
            for (String word : FileUtils.readLines(filterSetFile, "utf-8")) {
                filterSet.add(get(word, formDict, reloDict, errorDict));
            }
        }
        init(dictDir);
    }
    
    private void init(File dictDir) throws IOException{
        File formDictFile = new File(dictDir, "formDict.txt");
        File reloDictFile = new File(dictDir, "reloDict.txt"); 
        File errorDictFile = new File(dictDir, "errorDict.txt"); 
        File wordTypeFile = new File(dictDir, "wordTypeDict.txt");
        for (String line : FileUtils.readLines(formDictFile, "utf-8")) {
            String[] words = line.split("\t")[0].split(" ");
            for (int i = 1; i < words.length; i++) {
                formDict.put(words[i], words[0]);
            }
        }
        for (String line : FileUtils.readLines(reloDictFile, "utf-8")) {
            String[] words = line.split(" ");
            for (int i = 1; i < words.length; i++) {
                reloDict.put(words[i], words[0]);
            }
        }
        for (String line : FileUtils.readLines(errorDictFile, "utf-8")) {
            String[] words = line.split(" ");
            for (int i = 0; i < words.length; i++) {
                words[i] = get(words[i], reloDict);
            }
            for (int i = 1; i < words.length; i++) {
                errorDict.put(words[i], words[0]);
            }
        }
        for (String line : FileUtils.readLines(wordTypeFile, "utf-8")) {
            String[] words = line.split(" ");
            if(words[0].equals("V") || words[0].equals("S") || words[0].equals("COM") || words[0].equals("ADV") || words[0].equals("A")){
                continue;
            }
            for (int i = 1; i < words.length; i++) {
                stopDict.add(get(words[i], reloDict, errorDict));
            }
        }
        for (String line : FileUtils.readLines(formDictFile, "utf-8")) {
            String norm = line.split("\t")[0].split(" ")[0];
            norm = get(norm, formDict, reloDict, errorDict);
            if(stopDict.contains(norm) || filterSet != null && filterSet.contains(norm) || wordPos.containsKey(norm)){
                continue;
            }
            wordPos.put(norm, wordPos.size());
        }
    }
    
    private String get(String key, Map<String, String> ... dicts){
        for (Map<String, String> dict : dicts) {
            key = dict.containsKey(key) ? dict.get(key) : key;
        }
        return key;
    }
    
    public int size() {
        return wordPos.size();
    }
    
    public TObjectIntHashMap<String> getWords(String text) {
        TObjectIntHashMap<String> words = new TObjectIntHashMap<>();
        for(String word : TextUtils.splitWords(text, true)){
            if(word.isEmpty()){
                continue;
            }
            word = get(word, formDict, reloDict, errorDict);
            if(indexOf(word) != null){
                words.adjustOrPutValue(word, 1, 1);
            }
        }
        return words;
    }
    
    public String toTSVector(String text) {
        Map<String, List<Integer>> words = getWordPos(text);
        List<String> wordsList = new ArrayList<>();
        for(Entry<String, List<Integer>> entry : words.entrySet()) {
            wordsList.add("'" + entry.getKey() + "':" + StringUtils.join(entry.getValue(), ","));
        }
        return StringUtils.join(wordsList, " ");
    }
    
    public String toTSQuery(String text) {
        Map<String, List<Integer>> words = getWordPos(text);
        return StringUtils.join(words.keySet(), " & ");
    }
    
    public Set<String> getRawKeyword(String query, String text){
        Set<String> raws = new HashSet<>(); 
        Map<String, List<String>> norm2raw = new HashMap();
        for(String word : TextUtils.splitWords(text, false)){
            if(word.isEmpty()){
                continue;
            }
            String norm = get(word.toLowerCase(), formDict, reloDict, errorDict);
            if(!norm2raw.containsKey(norm)){
                norm2raw.put(norm, new ArrayList<>());
            }
            norm2raw.get(norm).add(word);
        }
        for(String word : TextUtils.splitWords(query, true)){
            if(word.isEmpty()){
                continue;
            }
            word = get(word, formDict, reloDict, errorDict);
            if(norm2raw.containsKey(word)){
                raws.addAll(norm2raw.get(word));
            }
        }  
        return raws;
    }
    
    public Map<String, List<Integer>> getWordPos(String text) {
        Map<String, List<Integer>> words = new HashMap<>();
        int i = 1;
        for(String word : TextUtils.splitWords(text, true)){
            if(word.isEmpty()){
                continue;
            }
            word = get(word, formDict, reloDict, errorDict);
            if(!words.containsKey(word)){
                words.put(word, new ArrayList());
            }
            words.get(word).add(i++);
        }
        return words;
    }

    public Integer indexOf(String word) {
        return wordPos.get(word);
    }
}
