package com.github.smivle.tag.extractor.impl.clusterdoc.model;

import static java.lang.Integer.parseInt;

/**
 *
 * @author pc
 */
public class KeyWord implements Comparable<KeyWord>{
    private final String word;
    private final int docWithWordInCluster;
    private final int docInCluster;
    private final int docWithWordAll;
    private final int docAll;

    public KeyWord(String word, int docWithWordInCluster, int docInCluster, int docWithWordAll, int docAll) {
        this.word = word;
        this.docWithWordInCluster = docWithWordInCluster;
        this.docInCluster = docInCluster;
        this.docWithWordAll = docWithWordAll;
        this.docAll = docAll;
    }

    public boolean isValid(){
        return ((double)docWithWordInCluster) / docInCluster > ((double)docWithWordAll) / docAll;
    }
    
    public int getDocAll() {
        return docAll;
    }

    public int getDocInCluster() {
        return docInCluster;
    }

    public int getDocWithWordAll() {
        return docWithWordAll;
    }

    public int getDocWithWordInCluster() {
        return docWithWordInCluster;
    }

    public String getWord() {
        return word;
    }


    @Override
    public String toString() {
        return word + "\t" + docWithWordInCluster + "\t" + docInCluster + "\t" + docWithWordAll + "\t" + docAll; 
    }
    
    public static KeyWord parse(String str){
        String[] parts = str.split("\t");
        return new KeyWord(parts[0], parseInt(parts[1]), parseInt(parts[2]), parseInt(parts[3]), parseInt(parts[4]));
    }

    @Override
    public int hashCode() {
        return word.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof KeyWord ? word.equals(((KeyWord)obj).word) : false; 
    }

    @Override
    public int compareTo(KeyWord o) {
        return word.compareTo(o.word);
    }
}
