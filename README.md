# APE (Automated Pipeline Explorer)

<img src="./res/ape_logo_sqare.png" alt="logo" width="150"/>

APE is a command line tool and API for the automated exploration of possible computational pipelines (workflows) from large collections of computational tools. 

APE relies on a semantic domain model that includes tool and type taxonomies as controlled vocabularies for the description of computational tools, and functional tool annotations (inputs, outputs, operations performed) using terms from these taxonomies. Based on this domain model and a specification of the available workflow inputs, the intended workflow outputs and possibly additional constraints, APE then computes possible workflows. 

Internally, APE uses a component-based program synthesis approach. It translates the domain knowledge and workflow specification into logical formulas that are then fed to a SAT solver to compute satisfying instances. These solutions are then translated into the actual candidate workflows. 

## CLI & API
Automated workflow composition with APE can be performed through its command line interface (CLI) or its application programming interface (API). While the CLI provides a simple means to interact and experiment with the system, the API provides more flexibility and control over the synthesis process. It can be used to integrate APE’s functionality into other systems.

### Command Line Interface (CLI)
When running APE-&lt;version>.jar from the command line, it requires a JSON configuration file given as a parameter and executes the automated workflow composition process accordingly. This configuration file (see [ape cofiguration example](https://github.com/sanctuuary/APE_UseCases/)) provides references to all therefor required information:
1. *Domain model* - annotation of the types and tools in the domain in form of an **ontology** (see [ontology example](https://github.com/sanctuuary/APE_UseCases/) in OWL) and a **tool annotation file** (see [tool annotations example](https://github.com/sanctuuary/APE_UseCases/) in JSON).
2. *Workflow specification* - including a list of **workflow inputs/outputs** and template-based (see [constraint templates](https://github.com/sanctuuary/APE_UseCases/)) **workflow constraints** (see [workflow constraints example](https://github.com/sanctuuary/APE_UseCases/))
3. *Parameters* for the synthesis execution, such as the number of desired solutions, output directory, system configurations, etc.

### Application Programming Interface (API)

Like the CLI, the APE API relies on a configuration file that references the domain ontology, tool annotations, workflow specification and execution parameters. However, the API allows to edit this file programmatically, and thus for instance add constraints or change execution parameters dynamically.

## Demo 
Our use cases are motivated by practical problems in various domains (e.g. bioinformatisc, GIS). Multiple predefined scenarios of scientific workflow synthesis can be found at [GitHub Use Cases Repository](https://github.com/sanctuuary/APE_UseCases).


## Credits
APE has been inspired by the [Loose Programming framework PROPHETS](http://ls5-www.cs.tu-dortmund.de/projects/prophets/index.php). It uses similar mechanisms for semantic domain modeling, workflow specification and synthesis, but strives to provide the automated composition functionality independent from a concrete workflow system.

We thank our brave first-generation users for their patience and constructive feedback that helped us to get APE into shape. 

## License
APE is licensed under the Apache 2.0 license.

####Dependencies

1. **OWL API**	-	LGPL or Apache 2.0

3. **OpenCSV**	-	Apache 2.0

5. **SAT4J**	-	EPL or GNu LGPL

7. **apache-common-lang**	-	Apache 2.0

9. **DOM4J**-	BSD
