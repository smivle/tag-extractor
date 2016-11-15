package com.github.smivle.tag.extractor.impl.clusterdoc;

import com.github.smivle.common.text.SentenceExtractor;
import com.github.smivle.tag.extractor.impl.clusterdoc.model.Dictionary;
import com.github.smivle.tag.extractor.impl.clusterdoc.model.KeyWord;
import com.github.smivle.tag.extractor.impl.clusterdoc.model.Tag;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author pc
 */
public class TagProcessor {
    
    public void process(File dictDir, File docDir, File keyDir, File tagDir) throws IOException{
        Map<String, Map<String, KeyWord>> docId2keyWords = getKeyWords(keyDir);
        Dictionary dict = new Dictionary(dictDir);
        for (File  docFile : docDir.listFiles()) {
            if(!docId2keyWords.containsKey(docFile.getName())){
                continue;
            }
            FileUtils.writeLines(new File(tagDir, docFile.getName()), "utf-8", processDoc(docFile, docId2keyWords.get(docFile.getName()), dict));
        }
    }
    
    private TreeSet<Tag> processDoc(File docFile, Map<String, KeyWord> keyWords, Dictionary dict) throws IOException{
        List<String> sentences = SentenceExtractor.get().extract(FileUtils.readFileToString(docFile, "utf-8"));
        TreeSet<Tag> tags = new TreeSet<>();
        for (int i = 0; i < sentences.size(); i++) {
            Tag tag = new Tag(i,
                    docFile.getName(), 
                    (i > 0 ? sentences.get(i - 1) : "") + sentences.get(i) + (i < sentences.size() - 1 ? sentences.get(i + 1) : ""), 
                    new ArrayList<String>(), 
                    0);
            dict.getWords(tag.getTag()).forEachEntry((word, count)->{
                if(!keyWords.containsKey(word)){
                    return true;
                }
                KeyWord kw = keyWords.get(word);
                tag.addRelevance(count / (1 + Math.log(kw.getDocWithWordInCluster())));
                tag.getKeyWords().add(word);
                return true;
            });
            if(tag.isValid()){
                tags.add(tag);
            }
        }
        return tags;
    }

    private Map<String, Map<String, KeyWord>> getKeyWords(File keyDir) throws IOException {
        Map<String, Map<String, KeyWord>> map = new HashMap<>();
        for (File file : keyDir.listFiles()) {
            Map<String, KeyWord> docMap = new HashMap<>();
            List<String> lines = FileUtils.readLines(file, "utf-8");
            for (String line : lines) {
                KeyWord kw = KeyWord.parse(line);
                docMap.put(kw.getWord(), kw);
            }
            map.put(file.getName(), docMap);
        }
        return map;
    }
}
