<img src="./res/ape_logo_sqare.png" alt="logo" width=20% align="left" />

# APE (Automated Pipeline Explorer)

APE is a command line tool and Java API for the automated exploration of possible computational pipelines (scientific workflows) from large collections of computational tools. 

APE relies on a semantic domain model that includes tool and type taxonomies as controlled vocabularies for the description of computational tools, and functional tool annotations (inputs, outputs, operations performed) using terms from these taxonomies. Based on this domain model and a specification of the available workflow inputs, the intended workflow outputs and possibly additional constraints, APE then computes possible workflows. 

Internally, APE uses a component-based program synthesis approach. It translates the domain knowledge and workflow specification into logical formulas that are then fed to a SAT solver to compute satisfying instances. These solutions are then translated into the actual candidate workflows. 

For [ICCS 2020](https://www.iccs-meeting.org/iccs2020/) we created a video that explains APE in 5 minutes:
<div align="left">
  <a href="https://www.youtube.com/watch?v=CzecqRJXmoM" target="_blank"><img src="./res/youtubeThumbnail.png" alt="APE - Youtube video" width=30%></a>
</div>

## Requirements
To [run](https://github.com/sanctuuary/APE#command-line-interface-cli) APE you need to have [Java 1.8](https://www.oracle.com/java/technologies/javase-jdk8-downloads.html) (or higher) installed on your system. To [build](https://github.com/sanctuuary/APE#how-to-build-ape-from-source) APE from source, [Maven 3.3+](https://maven.apache.org/download.cgi) has to be installed as well.

## Releases
| Date       | Version | Download                                                                             |
|------------|---------|--------------------------------------------------------------------------------------|
| 01-06-2020 | 1.0.0   | [APE-1.0.0.jar](https://github.com/sanctuuary/APE_UseCases/raw/master/APE-1.0.0.jar) |

## How to build APE from source (using Maven)
From the project root, simply launch
```shell
$ mvn -DskipTests=true install
```
to build the APE modules from the source tree and the built files will be generated under the `/target` directory. All the dependencies will be gathered by Maven and the following stand-alone module will be generated: `APE-<version>-jar-with-dependencies.jar`

## Using APE
Automated workflow composition with APE can be performed through its command line interface (CLI) or its application programming interface (API). While the CLI provides a simple means to interact and experiment with the system, the API provides more flexibility and control over the synthesis process. It can be used to integrate APE’s functionality into other systems.

### Command line interface (CLI)
When running APE-&lt;version>.jar from the command line, it requires a JSON configuration file given as a parameter and executes the automated workflow composition process accordingly. This configuration file (see [APE cofiguration example](https://github.com/sanctuuary/APE_UseCases/blob/master/SimpleDemo/ape.configuration) and [APE configuration documentation](https://github.com/sanctuuary/APE_UseCases#configuration-file)) provides references to all therefor required information:
1. *Domain model* - classification of the types and operations in the domain in form of an **ontology** (see [ontology example](https://github.com/sanctuuary/APE_UseCases/blob/master/SimpleDemo/GMT_Demo_UseCase.owl) in OWL) and a **tool annotation file** (see [tool annotations example](https://github.com/sanctuuary/APE_UseCases/blob/master/SimpleDemo/tool_annotations.json) in JSON).
2. *Workflow specification* - including a list of **workflow inputs/outputs** and template-based (see [constraint templates](https://github.com/sanctuuary/APE_UseCases/blob/master/SimpleDemo/constraint_templates.json)) **workflow constraints** (see [workflow constraints example](https://github.com/sanctuuary/APE_UseCases/blob/master/SimpleDemo/constraints.json))
3. *Parameters* for the synthesis execution, such as the number of desired solutions, output directory, system configurations, etc.

To run the APE CLI use:

```shell
java -jar APE-<version>.jar configuration.json
```

For more details check the [simple demo](https://github.com/sanctuuary/APE_UseCases/tree/master/SimpleDemo).

### Application programming interface (API)

Like the CLI, the APE API relies on a configuration file that references the domain ontology, tool annotations, workflow specification and execution parameters:

```java
// set up the framework
APE ape = new APE("path/to/setup-configuration.json");

// run the synthesis
SATsolutionsList solutions = ape.runSynthesis("path/to/run-configuration.json");
```

However, the API allows to edit this file programmatically, and thus for instance add constraints or change execution parameters dynamically:

```java
// set up the framework
JSONObject setupConfig = ...
APE ape = new APE(setupConfig);

// run the synthesis
JSONObject runConfig = ...
SATsolutionsList solutions1 = ape.runSynthesis(runConfig);

// run the synthesis again with altered parameters
runConfig.put("use_workflow_input", "ONE");
SATsolutionsList solutions2 = ape.runSynthesis(runConfig);
```

## Use cases and demos
Our use cases are motivated by practical problems in various domains (e.g. bioinformatisc, GIS). Different examples are available at [GitHub Use Cases Repository](https://github.com/sanctuuary/APE_UseCases).

For one of the bioinformatics use cases our intern Karl Allgaeuer developed a prototype of a web-based interface to APE. It is available at http://ape.science.uu.nl/ (beta).
A Docker version of this demonstrator is available at https://github.com/sanctuuary/Burke_Docker

## The APE team
* Vedran Kasalica (v.kasalica@uu.nl), lead developer
* Maurin Voshol, student developer
* Anna-Lena Lamprecht, project initiator and principal investigator

## Contact
For any questions concerning APE please get in touch with Vedran Kasalica (v.kasalica@uu.nl).

## Contributions
We welcome contributions (bug reports, bug fixes, feature requests, extensions, use cases, ...) to APE. Please get in touch with Vedran Kasalica (v.kasalica@uu.nl) to coordinate your contribution. We expect all contributors to follow our [Code of Conduct](https://github.com/sanctuuary/APE/blob/master/CODE_OF_CONDUCT.md).

## Credits
APE has been inspired by the [Loose Programming framework PROPHETS](http://ls5-www.cs.tu-dortmund.de/projects/prophets/index.php). It uses similar mechanisms for semantic domain modeling, workflow specification and synthesis, but strives to provide the automated exploration and composition functionality independent from a concrete workflow system.

We thank our brave first-generation users for their patience and constructive feedback that helped us to get APE into shape. 

## License
APE is licensed under the [Apache 2.0](https://github.com/sanctuuary/APE/blob/master/LICENSE) license.

#### Maven dependencies

1. [**OWL API**](https://mvnrepository.com/artifact/net.sourceforge.owlapi/owlapi-distribution) - LGPL or Apache 2.0
2. [**SAT4J**](https://mvnrepository.com/artifact/org.apache.logging.log4j/log4j-core) - EPL or GNu LGPL
3. [**apache-common-lang**](https://mvnrepository.com/artifact/org.apache.commons/commons-lang3) - Apache 2.0
4. [**graphviz-java**](https://mvnrepository.com/artifact/guru.nidi/graphviz-java) - Apache 2.0
5. [**org.json**](https://mvnrepository.com/artifact/org.json/json) - [JSON license](https://www.json.org/license.html)
