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
import com.github.stagirs.common.StoreIterator;
import com.github.stagirs.common.model.doc.Document;
import java.io.File;
import java.io.IOException;
import org.junit.Test;

/**
 *
 * @author Dmitriy Malakhov
 */
public class TagExtractorTest {
    @Test
    public void test() throws IOException{
        new TagExtractor().extract(new StoreIterator<>(new File("docs"), Document.class), new Store<>(new File("tagsWithoutSemantic")));
    }
}
