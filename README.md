# APE (Automated Pipeline Explorer)

APE is a command line tool and API for the automated exploration of possible computational pipelines (workflows) from large collections of computational tools. 

APE relies on a semantic domain model that includes tool and type taxonomies as controlled vocabularies for the description of computational tools, and functional tool annotations (inputs, outputs, operations performed) using terms from these taxonomies. Based on this domain model and a specification of the available workflow inputs, the intended workflow outputs and possibly additional constraints, APE then computes possible workflows. 

Internally, APE uses a component-based program synthesis approach. It translates the domain knowledge and workflow specification into logical formulas that are then fed to a SAT solver to compute satisfying instances. These solutions are then translated into the actual candidate workflows. 

## Installation
...

## Demo Project 
...

## Domain Model
Taxonomies/ontologies and tool annotations.

## Workflow Specification
...

In order to specify the constraints, one of the following approaches should be followed:

* Use terminal to specify the constraints one at the time
* Import a file with all the constraints

Each of the constrains has to be of the following format:

`constraintID par1 par2 par3 ... parN`

where the number of parameters depends on the constraint specified. Constrains that can be used are specified in the following section.

### Constraint formats

ID: 1___desc: If we use module <b>parameters[0]</b>, then use <b>parameters[1]</b> consequently.___no. of parameters: 2

ID: 2___desc: If we use module <b>parameters[0]</b>, then do not use <b>parameters[1]</b> consequently.___no. of parameters: 2

ID: 3___desc: If we use module <b>parameters[0]</b>, then we must have used <b>parameters[1]</b> prior to it.___no. of parameters: 2

ID: 4___desc: If we use module <b>parameters[0]</b>, then use <b>parameters[1]</b> as a next module in the sequence.___no. of parameters: 2

ID: 5___desc: Use module <b>parameters[0]</b> in the solution.___no. of parameters: 2

ID: 6___desc: Do not use module <b>parameters[0]</b> in the solution.___no. of parameters: 2

ID: 7___desc: Use <b>parameters[0]</b> as last module in the solution.___no. of parameters: 2

ID: 8___desc: Use type <b>parameters[0]</b> in the solution.___no. of parameters: 2

ID: 9___desc: Do not use type <b>parameters[0]</b> in the solution.___no. of parameters: 2

## Credits
APE has been inspired by the [Loose Programming framework PROPHETS](http://ls5-www.cs.tu-dortmund.de/projects/prophets/index.php). It uses similar mechanisms for semantic domain modeling, workflow specification and synthesis, but strives to provide the automated composition functionality independent from a concrete workflow system.

We thank our brave first-generation users for their patience and constructive feedback that helped us to get APE into shape. 

## License
APE is licensed under the Apache 2.0 license.

####Dependencies

**OWL API**	-	LGPL or Apache 2.0

**OpenCSV**	-	Apache 2.0

**SAT4J**	-	EPL or GNu LGPL

**apache-common-lang**	-	Apache 2.0

**DOM4J**-	BSD
