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
import com.github.stagirs.common.model.DocumentParser;
import com.github.stagirs.common.model.DocumentSerializer;
import com.github.stagirs.lingvo.morpho.MorphoDictionary;
import com.github.stagirs.lingvo.morpho.MorphoDictionaryFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

/**
 *
 * @author Dmitriy Malakhov
 */
public class TFIDFModelTest {
    @Test
    public void test() throws IOException{
        MorphoDictionary md = MorphoDictionaryFactory.get(new File("W:\\apache-tomcat-8.0.37\\work\\stagirs\\dict.opcorpora.xml"));
        
        List<Document> documents = new ArrayList<>();
        for(File file : new File("W:\\apache-tomcat-8.0.37\\work\\stagirs\\docs\\processed").listFiles()){
            documents.add(DocumentParser.parse(file));
        }
        TFIDFModel.fillSementic(documents, md);
        for (Document document : documents) {
            DocumentSerializer.serialize(new File("W:\\apache-tomcat-8.0.37\\work\\stagirs\\docs\\processed"), document);
        }
    }
}
