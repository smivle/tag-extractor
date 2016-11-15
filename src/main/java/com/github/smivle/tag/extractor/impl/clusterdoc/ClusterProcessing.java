package com.github.smivle.tag.extractor.impl.clusterdoc;

import com.github.smivle.tag.extractor.impl.clusterdoc.model.Dictionary;
import com.github.smivle.tag.extractor.impl.clusterdoc.model.Vector;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;

/**
 *
 * @author pc
 */
public class ClusterProcessing {
    public void process(File dir, int size, Dictionary dict) throws IOException{
        KMeansPlusPlusClusterer clusterer = new KMeansPlusPlusClusterer(size, 100);
        List<Vector> vectors = new ArrayList<>();
        for (File file : dir.listFiles()) {
            vectors.add(new Vector(file.getName(), dict, FileUtils.readFileToString(file, "utf-8")));
        }
        FileUtils.deleteDirectory(dir);
        dir.mkdirs();
        List<CentroidCluster<Vector>> list = clusterer.cluster(vectors);
        for (int i = 0; i < list.size(); i++) {
            File localDir = new File(dir, String.valueOf(i));
            localDir.mkdirs();
            CentroidCluster<Vector> c = list.get(i);
            for (Vector vector : c.getPoints()) {
                FileUtils.write(new File(localDir, vector.getId()), vector.getText(), "utf-8");
            }
        }
    }
}
