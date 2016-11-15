package com.github.smivle.tag.extractor.impl.clusterdoc;

import com.github.smivle.tag.extractor.TagExtractor;
import com.github.smivle.tag.extractor.impl.clusterdoc.model.Dictionary;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Dmitriy Malakhov
 */
public class ClusterDocTagExtractor implements TagExtractor{
    private int N = 2;
    private ClusterProcessing cp = new ClusterProcessing();
    private KeyWordProcessing kwp = new KeyWordProcessing();

    @Override
    public void extract(File docDir, File tagDir) {
        try {
            ClusterProcessing cp = new ClusterProcessing();
            KeyWordProcessing kwp = new KeyWordProcessing();
            File clusterDir = new File("clusters");
            FileUtils.deleteDirectory(clusterDir);
            clusterDir.mkdirs();
            FileUtils.copyDirectory(docDir, clusterDir);
            process(clusterDir);
            new KeyWordExtractor().process(new File("keys"), clusterDir);
            new TagProcessor().process(new File("dict"), docDir, new File("keys"), new File("tags"));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public void process(File dir) throws IOException{
        if(dir.list().length < 10){
            return;
        }
        cp.process(dir, N, new Dictionary(new File(dir.getParent(), dir.getName() + ".keyword"), new File("dict")));
        kwp.process(dir, new Dictionary(new File("dict")));
        for (int i = 0; i < N; i++) {
            process(new File(dir, String.valueOf(i)));    
        }
    }
}
