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
package com.github.stagirs.tagextractor.semantic.impl;

import com.github.stagirs.common.Store;
import com.github.stagirs.common.StoreIterator;
import com.github.stagirs.common.model.Tag;
import com.github.stagirs.tagextractor.semantic.SemanticProcessor;
import gnu.trove.map.hash.TObjectIntHashMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Dmitriy Malakhov
 */
public class ClusterSemanticProcessor implements SemanticProcessor{
    
    @Override
    public void setSemantic(StoreIterator<Tag> tags, Store<Tag> store){
        List<Tag> tagList = tags.toList();
        TObjectIntHashMap term2freq = term2freq(tagList);
        TObjectIntHashMap cluster2length = cluster2length(tagList);
        Map<String, TObjectIntHashMap> cluster2term2freq = cluster2term2freq(tagList);
        TObjectIntHashMap doc2length = doc2length(tagList);
        Map<String, TObjectIntHashMap> doc2term2freq = doc2term2freq(tagList);
        for (Tag tag : tagList) {
            //TODO сделать возможность различных реализаций по определению семантики, например, с помощью tf-idf и tf-icf
            double semantic = 0;
            for(String term : tag.getTerms()){
                double freq = ((double)term2freq.get(term)) / tagList.size();
                double cFreq = ((double)cluster2term2freq.get(tag.getClusterId()).get(term)) / cluster2length.get(tag.getClusterId());
                double dFreq = ((double)doc2term2freq.get(tag.getDocId()).get(term)) / doc2length.get(tag.getDocId());
                semantic += Math.pow(cFreq * dFreq, 0.5) / freq;
            }
            semantic /= tag.getTerms().size();
            tag.setSemantic(semantic);
            store.save(tag);
        }
    }
    
    private TObjectIntHashMap term2freq(List<Tag> tags){
        TObjectIntHashMap term2freq = new TObjectIntHashMap();
        for (Tag tag : tags) {
            for(String term : tag.getTerms()){
                term2freq.adjustOrPutValue(term, 1, 1);
            }
        }
        return term2freq;
    }
    
    private TObjectIntHashMap cluster2length(List<Tag> tags){
        TObjectIntHashMap term2freq = new TObjectIntHashMap();
        for (Tag tag : tags) {
            term2freq.adjustOrPutValue(tag.getClusterId(), 1, 1);
        }
        return term2freq;
    }
    
    private Map<String, TObjectIntHashMap> cluster2term2freq(List<Tag> tags){
        Map<String, TObjectIntHashMap> cluster2term2freq = new HashMap<>();
        for (Tag tag : tags) {
            String clusterId = tag.getClusterId();
            if(!cluster2term2freq.containsKey(clusterId)){
                cluster2term2freq.put(clusterId, new TObjectIntHashMap());
            }
            for(String term : tag.getTerms()){
                cluster2term2freq.get(clusterId).adjustOrPutValue(term, 1, 1);
            }
        }
        return cluster2term2freq;
    }
    
    private TObjectIntHashMap doc2length(List<Tag> tags){
        TObjectIntHashMap term2freq = new TObjectIntHashMap();
        for (Tag tag : tags) {
            term2freq.adjustOrPutValue(tag.getDocId(), 1, 1);
        }
        return term2freq;
    }
    
    private Map<String, TObjectIntHashMap> doc2term2freq(List<Tag> tags){
        Map<String, TObjectIntHashMap> cluster2term2freq = new HashMap<>();
        for (Tag tag : tags) {
            if(!cluster2term2freq.containsKey(tag.getDocId())){
                cluster2term2freq.put(tag.getDocId(), new TObjectIntHashMap());
            }
            for(String term : tag.getTerms()){
                cluster2term2freq.get(tag.getDocId()).adjustOrPutValue(term, 1, 1);
            }
        }
        return cluster2term2freq;
    }
}
