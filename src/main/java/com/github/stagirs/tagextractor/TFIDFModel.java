/*
 * Copyright 2016 Dmitriy Malakhov.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.stagirs.tagextractor;

import com.github.stagirs.common.model.Block;
import com.github.stagirs.common.model.Document;
import com.github.stagirs.common.model.Point;
import com.github.stagirs.common.model.Section;
import com.github.stagirs.common.model.Sentence;
import com.github.stagirs.common.model.Text;
import com.github.stagirs.common.text.TextUtils;
import gnu.trove.map.hash.TObjectIntHashMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Dmitriy Malakhov
 */
public class TFIDFModel {
    private Map<String, TObjectIntHashMap<String>> term2doc2count = new HashMap<>();
    private TObjectIntHashMap<String> doc2termcount = new TObjectIntHashMap<>();
    private List<Document> docs;

    private TFIDFModel(List<Document> docs) {
        this.docs = docs;
        for (Document doc : docs) {
            process(doc);
        }
    }
    
    private void process(String docId, String text, int k){
        if(text == null){
            return;
        }
        for(String word : TextUtils.splitWords(text, true)){
            if(!term2doc2count.containsKey(word)){
                term2doc2count.put(word,  new TObjectIntHashMap<>());
            }
            term2doc2count.get(word).adjustOrPutValue(docId, k, k);
            doc2termcount.adjustOrPutValue(docId, k, k);
        }
    }
    
    private void process(Document document){
        process(document.getId(), document.getTitle(), 100);
        for (Block block : document.getBlocks()) {
            if(block instanceof Section){
                process(document.getId(), (Section) block);
            }
            if(block instanceof Point){
                process(document.getId(), (Point) block);
            }
        }
    }
    
    private void process(String docId, Section section){
        process(docId, section.getTitle(), 10);
        for (Block block : section.getBlocks()) {
            if(block instanceof Section){
                process(docId, (Section) block);
            }
            if(block instanceof Point){
                process(docId, (Point) block);
            }
        }
    }
    
    private void process(String docId, Point point){
        for (Sentence sentence : point.getSentences()) {
            for (Text text : sentence.getParts()) {
                if(text.getClassName() != null){
                    continue;
                }
                process(docId, text.getText(), 1);
            }
        }
    }
    
    
    public static void fillSementic(List<Document> docs) {
        TFIDFModel model = new TFIDFModel(docs);
        for (Document doc : docs) {
            model.fillSementic(doc);
        }
    }
    
    private double getSementic(String docId, String text){
        double semantic = 0;
        if(text == null){
            return semantic;
        }
        for(String word : TextUtils.splitWords(text, true)){
            semantic += tf(docId, word) * idf(word);
        }
        return semantic;
    }
    
    private void fillSementic(Document document){
        document.setTitleSemantic(100 * getSementic(document.getId(), document.getTitle()));
        for (Block block : document.getBlocks()) {
            if(block instanceof Section){
                fillSementic(document.getId(), (Section) block);
            }
            if(block instanceof Point){
                fillSementic(document.getId(), (Point) block);
            }
        }
    }
    
    private void fillSementic(String docId, Section section){
        section.setTitleSemantic(10 * getSementic(docId, section.getTitle()));
        for (Block block : section.getBlocks()) {
            if(block instanceof Section){
                fillSementic(docId, (Section) block);
            }
            if(block instanceof Point){
                fillSementic(docId, (Point) block);
            }
        }
    }
    
    private void fillSementic(String docId, Point point){
        for (Sentence sentence : point.getSentences()) {
            double semantic = 0;
            for (Text text : sentence.getParts()) {
                if(text.getClassName() != null){
                    continue;
                }
                semantic += getSementic(docId, text.getText());
            }
            sentence.setSemantic(semantic);
        }
    }
    
    private double tf(String docId, String word){
        return ((double)term2doc2count.get(word).get(docId)) / doc2termcount.get(docId);
    }
    
    private double idf(String word){
        return  Math.log(((double)doc2termcount.size()) / term2doc2count.get(word).size());
    }
}
