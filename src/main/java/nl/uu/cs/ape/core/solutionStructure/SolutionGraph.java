package nl.uu.cs.ape.core.solutionStructure;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.engine.Renderer;
import guru.nidi.graphviz.model.Graph;
import nl.uu.cs.ape.utils.APEUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static guru.nidi.graphviz.model.Factory.graph;

/**
 * The {@code SolutionGraph} class is used to represent the graphical representation of the solution.
 *
 * @author Vedran Kasalica
 */
public class SolutionGraph {

    private Graph graph;

    /**
     * Instantiates a new Solution graph.
     *
     * @param graph the graph
     */
    SolutionGraph(Graph graph) {
        this.graph = graph;
    }

    /**
     * Get the PNG rendered file, based on the workflow structure.
     *
     * @return Theh {@link Renderer} object.
     */
    public Renderer getPNGRenderer() {
        return Graphviz.fromGraph(graph).render(Format.PNG);
    }

    /**
     * Get the PNG file, depicting the workflow structure.
     * @param debug true if the debugging mode is ON
     * 
     * @return The {@link BufferedImage} object that correspond to the workflow.
     */
    public BufferedImage getPNGImage(boolean debug) {
        final Renderer renderer = getPNGRenderer();
        if(true) {
            APEUtils.disableErr();
            }
        final BufferedImage image = renderer.toImage();
        if(true) {
            APEUtils.enableErr();
        }
        return image;
    }

    /**
     * Write to a file the PNG version of the graph.
     *
     * @param file The file that should be written to.
     * @param debug true if the debugging mode is ON
     * @throws IOException Exception in case of error in file handling.
     */
    public void getWrite2File(File file, boolean debug) throws IOException {
        final Renderer renderer = getPNGRenderer();
        if(true) {
        APEUtils.disableErr();
        }
        renderer.toFile(file);
        if(true) {
            APEUtils.enableErr();
        }
    }

    /**
     * Change title of the graph.
     *
     * @param title The new title of the graph.
     * @return A graph with the changed title.
     */
    public SolutionGraph changeTitle(String title) {
        this.graph = graph(title).with(this.graph);
        return this;
    }
}
