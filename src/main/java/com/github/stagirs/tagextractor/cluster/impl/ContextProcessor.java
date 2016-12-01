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
package com.github.stagirs.tagextractor.cluster.impl;

import com.github.stagirs.clustering.CentroidCluster;
import com.github.stagirs.clustering.Cluster;
import com.github.stagirs.clustering.Clusterable;
import com.github.stagirs.clustering.KMeansPlusPlusClusterer;
import com.github.stagirs.clustering.context.ContextClusterer;
import com.github.stagirs.common.Store;
import com.github.stagirs.common.StoreIterator;
import com.github.stagirs.common.model.Tag;
import com.github.stagirs.common.model.doc.Document;
import com.github.stagirs.tagextractor.cluster.ClusterProcessor;
import gnu.trove.impl.hash.TIntDoubleHash;
import gnu.trove.map.hash.TIntDoubleHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * @author Dmitriy Malakhov
 */
public class ContextProcessor implements ClusterProcessor{
    class Vector implements Clusterable{
        private final Tag tag;
        private final TIntDoubleHashMap points = new TIntDoubleHashMap();

        public Vector(Tag tag, TObjectIntHashMap<String> term2index, TObjectIntHashMap<String> term2count){
            this.tag = tag;
            tag.termsMap().forEachEntry((term, count) -> {
                points.put(term2index.get(term),((double) count) / Math.log(term2count.get(term)));
                return true;
            });
        }

        @Override
        public TIntDoubleHashMap getPoint() {
            return points;
        }

        public Tag getTag() {
            return tag;
        }

    }
    
    @Override
    public void setClusters(StoreIterator<Tag> tags, Store<Tag> store){
        List<Tag> tagList = tags.toList();
        ContextClusterer<Vector> clusterer = new ContextClusterer();
        List<? extends Cluster<Vector>> list = clusterer.cluster(getVectors(tagList));
        for (int i = 0; i < list.size(); i++) {
            for (Vector point : list.get(i).getPoints()) {
                point.getTag().addCluster(i);
            }
        }
    }
    
    private List<Vector> getVectors(List<Tag> tags){
        List<Vector> vectors = new ArrayList<>();
        TObjectIntHashMap<String> term2index = new TObjectIntHashMap<>(); 
        TObjectIntHashMap<String> term2size = new TObjectIntHashMap<>();
        for (Tag tag : tags) {
            for (String term : tag.getTerms()) {
                if(!term2index.containsKey(term)){
                    term2index.put(term, term2index.size());
                }
                term2size.adjustOrPutValue(term, 1, 1);
            }
        }
        for (Tag tag : tags) {
            vectors.add(new Vector(tag, term2index , term2size));
        }
        return vectors;
    }
}
