package com.github.smivle.tag.extractor.impl.clusterdoc;

import com.github.smivle.tag.extractor.impl.clusterdoc.model.KeyWord;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author pc
 */
public class KeyWordExtractor {
    
    public void process(File dir, File clusterDir) throws IOException{
        Map<String, Map<String, KeyWord>> kws = getKeyWords(clusterDir);
        for (Entry<String, Map<String, KeyWord>> e : kws.entrySet()) {
            List<String> list = new ArrayList<>();
            for(KeyWord kw : e.getValue().values()){
                list.add(kw.toString());
            }
            FileUtils.writeLines(new File(dir, e.getKey()), "utf-8", list);
        }
    }
    
    private Map<String, Map<String, KeyWord>> getKeyWords(File dir) throws IOException{
        Map<String, Map<String, KeyWord>> docId2keyWords = new HashMap<>();
        for (File file : dir.listFiles()) {
            if(file.getName().endsWith(".keyword")){
                continue;
            }
            if(!file.isDirectory()){
                docId2keyWords.put(file.getName(), new HashMap<>());
                continue;
            }
            docId2keyWords.putAll(updateKeyWords(file));
        }
        return docId2keyWords;
    }
    
    private Map<String, Map<String, KeyWord>> updateKeyWords(File dir) throws IOException{
        Map<String, Map<String, KeyWord>> localDocId2keyWords = getKeyWords(dir);
        File keyWordFile = new File(dir.getParent(), dir.getName() + ".keyword");
        if(!keyWordFile.exists()){
            return localDocId2keyWords;
        }
        List<String> keyWordsLines = FileUtils.readLines(keyWordFile);
        for (String keyWordsLine : keyWordsLines) {
            addKeyWord(KeyWord.parse(keyWordsLine), localDocId2keyWords);
        }
        return localDocId2keyWords;
    }
    
    private void addKeyWord(KeyWord keyWord, Map<String, Map<String, KeyWord>> localDocId2keyWords) throws IOException{
        for (Map<String, KeyWord> keyWords : localDocId2keyWords.values()) {
            if(keyWords.containsKey(keyWord.getWord())){
               continue;
            }
            keyWords.put(keyWord.getWord(), keyWord);
        }
    }
}
