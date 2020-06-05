package nl.uu.cs.ape.sat.core;

import nl.uu.cs.ape.sat.models.AllModules;
import nl.uu.cs.ape.sat.models.Module;

import java.util.List;

/**
 * The {@code SolutionInterpreter} class defines objects used to interpret the solutions given in native formats (e.g. SAT output).
 *
 * @author Vedran Kasalica
 */
public abstract class SolutionInterpreter {

    public abstract String getSolution();

    public abstract String getRelevantSolution();

    public abstract List<Module> getRelevantSolutionModules(AllModules allModules);

    public abstract boolean isSat();
}
