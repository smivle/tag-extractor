package com.github.smivle.tag.extractor.impl.clusterdoc.model;

import gnu.trove.map.hash.TObjectIntHashMap;
import org.apache.commons.math3.ml.clustering.Clusterable;

/**
 *
 * @author pc
 */
public class Vector implements Clusterable{
    private final String id;
    private final String text;
    private int length = 0;
    private final TObjectIntHashMap<String> words;
    private final double[] points;
    
    public Vector(String id, Dictionary dict, String text){
        this.id = id;
        this.text = text;
        this.words = dict.getWords(text);
        for (Integer count : words.values()) {
            length += count;
        }
        points = new double[dict.size()];
        words.forEachEntry((word, count) -> {
            points[dict.indexOf(word)] = ((double) count) / Math.log(length);
            return true;
        });
    }

    @Override
    public double[] getPoint() {
        return points;
    }

    public String getText() {
        return text;
    }

    public String getId() {
        return id;
    }
}
