### APE 1.1.12 - log4j version fix Latest
- Changed log4j version dependency to 2.17.0, due to security issues.
- Updated other mvn dependencies

### APE 1.1.11 - DOI release
- Fix needed to obtain the DOI

### APE 1.1.9 - Export abstract and executable CWL
- Export abstract and executable CWL
- Improved synthesis execution flags



### APE 1.1.8 - Improved Class structure and naming
Improved class structure and naming to support extensions with additional solving techniques. The new structure allows for easy extensions with other (non-SAT) solvers.



### APE 1.1.7 - Adding synthesis execution flags
- Added synthesis execution flag (class SATsolutionsList)
- Improved SolutionWorkflow class by adding methods for PNG retrieval of the workflows (e.g., getDataflowGraphPNG(..))



### APE 1.1.5 - Adding long predicate labels
- Added method PredicateLabel.getLongLabel()
- Improved ModuleNode.getNodeFullLabel() method and renamed to ModuleNode.getNodeLongLabel()



### APE 1.1.4 - Improving SolutionWorkflowNode (updated)
- Introduced method SolutionWorkflowNode.getNodeFullLabel()
- Improved method descriptions


### APE 1.1.3 - Improved synthesis run handling
Implemented:

- Cleaning temp files used for synthesis encoding after each SAT run
- Introduced "timeout(sec)" configuration field


### APE 1.1.2 - Constraint parsing fix
Parsing of constraints got fixed. The error occurred in case of defining constraints over concrete operations that are not part of the ontology.


### APE 1.1.1 - Strict taxonomy hiararchy
Improvements:

- Implemented stricter tool annotations and added the corresponding core configuration field (strict_tool_annotations)
- Removed message passing approach
- Improved APE API (added new methods and improved documentation)


### APE 1.0.3 - Interface improvements
- Provided interface for building constraints from JSON objects
- Tested new functionalities
- Improved constraint descriptions (ConstraintTemplate in ConstraintFactory)
- Improved constraint printouts in debug mode


### APE 1.0.2 - constraint improvements
- Improved constraint formatting
- Refactored interface for auxiliary predicated
- Improved documentation and testing


### The first stable version of APE 1.0.1
APE is a command line tool and Java API for the automated exploration of possible computational pipelines (scientific workflows) from large collections of computational tools.

The first stable version of the software includes:

- APE-1.0.1.jar (the library jar)
- APE-1.0.1-executable.jar (command line executable jar, that includes all the dependencies)
- APE-1.0.1-javadoc.jar
- APE-1.0.1-sources.jar

