package com.github.smivle.tag.extractor.impl.clusterdoc.model;

import static java.lang.Integer.parseInt;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author pc
 */
public class Tag implements Comparable<Tag>{
    private final String docName;
    private final String tag;
    private final List<String> keyWords;
    private double relevance;
    private int index;

    public Tag(int index, String docName, String tag, List<String> keyWords, double relevance) {
        this.docName = docName;
        this.tag = tag;
        this.keyWords = keyWords;
        this.relevance = relevance;
        this.index = index;
    }

    public String getDocName() {
        return docName;
    }
    
    public void addRelevance(double rel){
        relevance += rel;
    }

    public List<String> getKeyWords() {
        return keyWords;
    }

    public double getRelevance() {
        return relevance;
    }

    public String getTag() {
        return tag;
    }
    
    public boolean isValid(){
        return relevance > 0;
    }
    
    public static Tag parse(String str){
        String[] parts = str.split("\t");
        return new Tag(parseInt(parts[0]), parts[1], StringUtils.join(parts, "\t", 4, parts.length).replace("­	", "").replace("	", " ").trim(), Arrays.asList(parts[3].split(" ")), Double.parseDouble(parts[2]));
    }

    @Override
    public String toString() {
        return index + "\t" + docName + "\t" + relevance + "\t" + StringUtils.join(keyWords, " ") + "\t" + tag;
    }

    @Override
    public int compareTo(Tag o) {
        return new Double(o.relevance).compareTo(relevance);
    }

    @Override
    public int hashCode() {
        return (docName + tag).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Tag){
            return (docName + tag).equals(((Tag)obj).docName + ((Tag)obj).tag);
        }
        return false;
    }

    public Integer getIndex() {
        return index;
    }
    
    
}
