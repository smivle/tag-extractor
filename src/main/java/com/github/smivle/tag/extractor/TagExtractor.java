package com.github.smivle.tag.extractor;

import java.io.File;

/**
 *
 * @author Dmitriy Malakhov
 */
public interface TagExtractor {
    /**
     * 
     * @param docDir дирректория, файлы которой содержат тексты документов
     * @param tagDir дирректория, в которой должны появиться файлы соответствующие файлам документов, содержащие теги этих документов
     * формат файла тегов:
     * <порядковый номер тега><термины тегов разделенные пробелом>\t<значение функции семантики>
     */
    public void extract(File docDir, File tagDir);
}
