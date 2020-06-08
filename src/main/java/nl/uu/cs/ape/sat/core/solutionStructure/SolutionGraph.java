package nl.uu.cs.ape.sat.core.solutionStructure;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.engine.Renderer;
import guru.nidi.graphviz.model.Graph;

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
     *
     * @return The {@link BufferedImage} object that correspond to the workflow.
     */
    public BufferedImage getPNGImage() {
        return Graphviz.fromGraph(graph).render(Format.PNG).toImage();
    }

    /**
     * Write to a file the PNG version of the graph.
     *
     * @param file The file that should be written to.
     * @throws IOException Exception in case of error in file handling.
     */
    public void getWrite2File(File file) throws IOException {
        Graphviz.fromGraph(graph).render(Format.PNG).toFile(file);
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
