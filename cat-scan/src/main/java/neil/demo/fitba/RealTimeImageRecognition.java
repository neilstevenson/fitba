package neil.demo.fitba;
/*
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import boofcv.abst.scene.ImageClassifier.Score;
import boofcv.deepboof.ImageClassifierVggCifar10;
import boofcv.gui.ImageClassificationPanel;
import boofcv.gui.image.ShowImages;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.Planar;

import com.hazelcast.jet.datamodel.Tuple3;
import com.hazelcast.jet.datamodel.WindowResult;
import com.hazelcast.jet.pipeline.ContextFactory;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sink;

import javax.swing.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Timestamp;
import java.util.Comparator;
import java.util.Map.Entry;

import static com.hazelcast.jet.Util.entry;
import static com.hazelcast.jet.aggregate.AggregateOperations.maxBy;
import static com.hazelcast.jet.datamodel.Tuple3.tuple3;
import static com.hazelcast.jet.function.ComparatorEx.comparingDouble;
import static com.hazelcast.jet.pipeline.SinkBuilder.sinkBuilder;
import static com.hazelcast.jet.pipeline.WindowDefinition.tumbling;
import static java.util.Collections.singletonList;

/**
 * An application which uses webcam frame stream as the input and classifies the
 * frames with a model pre-trained with CIFAR-10 dataset.
 * <p>
 * Second-worth of frames will be aggregated to find the classification with
 * maximum score that will be sent to a GUI sink to be shown on the screen.
 *
 * The DAG used to model image recognition calculations can be seen below :
 *
 *              ┌───────────────────┐
 *              │Webcam Video Source│
 *              └─────────┬─────────┘
 *                        │
 *                        v
 *        ┌────────────────────────────────┐
 *        │Classify Images with pre-trained│
 *        │     machine learning model     │
 *        └───────────────┬────────────────┘
 *                        │
 *                        v
 *            ┌───────────────────────┐
 *            │Calculate maximum score│
 *            │    in 1 sec windows   │
 *            └───────────┬───────────┘
 *                        │
 *                        v
 *              ┌───────────────────┐
 *              │Show results on GUI│
 *              └───────────────────┘
 */
public class RealTimeImageRecognition {
	
	public static final String MODEL = "likevgg_cifar10";

    /**
     * Builds and returns the Pipeline which represents the actual computation.
     * @throws FileNotFoundException 
     */
    protected static Pipeline buildPipeline() {
        Pipeline pipeline = Pipeline.create();
        
        pipeline.drawFrom(WebcamSource.webcam(500))
        .withIngestionTimestamps()
        .mapUsingContext(classifierContext(MODEL),
                (ctx, img) -> {
                    Entry<String, Double> classification = classifyWithModel(ctx, img);
                    return tuple3(img, classification.getKey(), classification.getValue());
                }
        )
        .window(tumbling(1000))
        .aggregate(maxBy(comparingDouble(Tuple3::f2)))
        .drainTo(buildGUISink());
        
        return pipeline;
    }


    /**
     * A GUI Sink which will show the frames with the maximum classification scores.
     */
    private static Sink<WindowResult<Tuple3<BufferedImage, String, Double>>> buildGUISink() {
        return sinkBuilder("GUI", (instance) -> createPanel())
                .<WindowResult<Tuple3<BufferedImage, String, Double>>>receiveFn((panel, tsItem) -> {
                    BufferedImage image = tsItem.result().f0();
                    Score score = new Score();
                    score.set(tsItem.result().f2(), 0);
                    panel.addImage(
                            image,
                            new Timestamp(tsItem.end()).toString(),
                            singletonList(score),
                            singletonList(tsItem.result().f1())
                    );
                    scrollToBottomAndRepaint(panel);
                })
                .build();
    }

    /**
     * Scrolls the GUI panel to the bottom to show latest result
     * whenever a new image added to the GUI panel
     */
    @SuppressWarnings("rawtypes")
	private static void scrollToBottomAndRepaint(ImageClassificationPanel panel) {
        Component[] components = panel.getComponents();
        for (Component component : components) {
            if (component instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) component;
                JList list = (JList) scrollPane.getViewport().getView();
                int size = list.getModel().getSize();
                list.setSelectedIndex(Math.max(size - 2, list.getLastVisibleIndex()));
                JScrollBar vertical = scrollPane.getVerticalScrollBar();
                vertical.setValue(vertical.getMaximum());
                panel.repaint(scrollPane.getBounds());
            }
        }
    }

    /**
     * Creates and returns image result GUI panel
     */
    private static ImageClassificationPanel createPanel() {
        ImageClassificationPanel panel = new ImageClassificationPanel();
        ShowImages.showWindow(panel, "Results", true);
        return panel;
    }

    /**
     * The actual classification of the images by using the pre-trained model.
     */
    private static Entry<String, Double> classifyWithModel(ImageClassifierVggCifar10 classifier, BufferedImage image) {
        Planar<GrayF32> planar = new Planar<>(GrayF32.class, image.getWidth(), image.getHeight(), 3);
        ConvertBufferedImage.convertFromPlanar(image, planar, true, GrayF32.class);
        classifier.classify(planar);
        return classifier.getAllResults().stream()
                .map(score -> entry(classifier.getCategories().get(score.category), score.score))
                .max(Comparator.comparing(Entry::getValue)).get();
    }

    /**
     * Loads the pre-trained model from the specified path
     *
     * TODO: ImageClassifierVggCifar10.loadModel() doesn't work with resources,
     * so won't look inside the Jar file.
     * 
     * @param modelPath path of the model
     */
    private static ContextFactory<ImageClassifierVggCifar10> classifierContext(String modelPath) {
        return ContextFactory.withCreateFn(jet -> {
            ImageClassifierVggCifar10 classifier = new ImageClassifierVggCifar10();
            classifier.loadModel(new File(modelPath));
            return classifier;
        });
    }
}
