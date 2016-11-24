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

import com.github.stagirs.common.Store;
import com.github.stagirs.common.HashUtils;
import com.github.stagirs.common.model.doc.Document;
import com.github.stagirs.common.model.Tag;
import com.github.stagirs.common.model.doc.Point;
import com.github.stagirs.common.model.doc.Section;
import java.util.Iterator;
/**
 * 
 * @author Dmitriy Malakhov
 */
public class TagExtractor {
    
    public void extract(Iterator<Document> docs, Store<Tag> store){
        while(docs.hasNext()){
            Document doc = docs.next();
            for (Section section : doc.getSections()) {
                for (Point point : section.getPoints()) {
                    store.save(new Tag(point.getHash(), point.text(), doc.getId()));
                    for (String s : point.getSentences()) {
                        store.save(new Tag(HashUtils.hash(s), s, doc.getId()));
                    }
                }
            }
        }
    }
}
