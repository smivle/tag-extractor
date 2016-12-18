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

import com.github.stagirs.common.model.Document;
import com.github.stagirs.common.model.DocumentUtils;
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
    private Map<String, List<Sentence>> doc2list = new HashMap<>();

    private TFIDFModel(List<Document> docs) {
        for (Document doc : docs) {
            doc2list.put(doc.getId(), new ArrayList<>());
            DocumentUtils.fillSentences(doc, doc2list.get(doc.getId()));
            for(Sentence sentence : doc2list.get(doc.getId())){
                for (Text text : sentence.getParts()) {
                    if(text.getClassName() != null){
                        continue;
                    }
                    for(String word : TextUtils.splitWords(text.getText(), true)){
                        if(!term2doc2count.containsKey(word)){
                            term2doc2count.put(word,  new TObjectIntHashMap<>());
                        }
                        term2doc2count.get(word).adjustOrPutValue(doc.getId(), 1, 1);
                        doc2termcount.adjustOrPutValue(doc.getId(), 1, 1);
                    }
                }
            }
        }
    }

    public static void fillSementic(List<Document> docs) {
        TFIDFModel model = new TFIDFModel(docs);
        for (Map.Entry<String, List<Sentence>> entry : model.doc2list.entrySet()) {
            for(Sentence sentence : entry.getValue()){
                double semantic = 0;
                for (Text text : sentence.getParts()) {
                    if(text.getClassName() != null){
                        continue;
                    }
                    for(String word : TextUtils.splitWords(text.getText(), true)){
                        semantic += model.tf(entry.getKey(), word) * model.idf(word);
                    }
                }
                sentence.setSemantic(semantic);
            }
        }
    }
    
    private double tf(String docId, String word){
        return ((double)term2doc2count.get(word).get(docId)) / doc2termcount.get(docId);
    }
    
    private double idf(String word){
        return  Math.log(((double)doc2termcount.size()) / term2doc2count.get(word).size());
    }
}
