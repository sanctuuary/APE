In order to use the APE library, simple run the APE-0.1.jar file using command:

java -jar APE-0.1.jar

while ape.config file needs to be provided in the same folder. In order to be able to run the final products of the demo examples, GMT set of tools needs to be installed.

http://gmt.soest.hawaii.edu/projects/gmt/wiki/Installing

Node: 
Errors regarding SLF4J will not affect the synthesis execution and can be ignored. The same goes for warnings.
_______________________________________________________________________________________________________________________________________________

ape.config is the main configuration file for the library and it consists of the following elements:

	ontology_path 			- 	path to the taxonomy file  (provided demo example taxonomy_demo.owl)
	modulesTaxonomyRoot		-	name of the root tool class
	dataTaxonomyRoot		-	name of the root data taxonomy class
	typesTaxonomyRoot		-	name of the sub-root data type taxonomy class
	formatTaxonomyRoot [optional]	-	name of the sub-root data format taxonomy class
	tool_annotations_path		-	path to the xml file that contains basic tool annotation (provided demo example
						tool_annotations_demo.xml)
	constraints_path		-	path to the csv file containing constraints representing workflow specification (provided 							demo example constraints_demo.csv and constraints template constraints_templates.csv)
	shared_memory			-	true in a case of shared-memory structure, false if the pipeline memory structure should be 							used
	solutions_path			-	path to the file where the workflow solutions will be written
	solution_min_length		-	minimum length from which solutions should be searched
	solution_max_length		-	maximum length to which solutions should be searched, put 0 in case of no limit
	max_solutions			-	max number of solutions that would be returned
	execution_scripts_folder	-	folder where the executable scripts will be generated
	number_of_execution_scripts	-	number of executable scripts that will be generated
	inputs/input			-	each input represent a single instance that will be an input to the program
	inputs/input/type		-	represent the type of the input instance
	inputs/input/format [optional]	-	represent the format of the input instance
	debug_mode			-	true for debug command line output

_______________________________________________________________________________________________________________________________________________


Taxonomy file

Example file: 'GMT_Demo_UseCase.owl'

Used to classify tools and data types into 2 different categories. General structure is that the main class "thing" has 2 subclasses: 
thing
	Tools Taxonomy (name provided as modulesTaxonomyRoot in config file)
	Data Taxonomy (name provided as dataTaxonomyRoot in config file)
		Type Taxonomy (name provided as typesTaxonomyRoot in config file)
		Format Taxonomy (name provided as formatsTaxonomyRoot in config file) [optional]

Tools Taxonomy consists of actual tools from the domain, as well as their abstraction classes.
Type Taxonomy consists of actual data types from the domain, as well as their abstraction classes.
Format Taxonomy consists of actual data Format from the domain, as well as their abstraction classes.

Idea behind using a Format Taxonomy, is that a certain data instance can be defined using a pair, Data Type and Data Format. Thus, Format Taxonomy is optional.


Note:
Encoding supports explicit subclass relations in RDF format. The rest of the OWL file annotations will be omitted.


_______________________________________________________________________________________________________________________________________________

Tool Annotations file

Example file: 'tool_annotations.xml'
The file has the following structure:

functions
	+function
		name
		operation
		?inputs
			+input
				type
				?format
		?outputs
			+output
				type
				?format
		?implementation
			code

where (+) requires 1 or more, (?) requires 0 or 1 and no sign requires existence of exactly 1 such tag.

Regarding the semantics:
	function	-	an implementation/instance of a tool
	name		-	unique name of the tool
	operation	-	name of a tool from Tool Taxonomy
	input		-	a single input to a tool
	type		-	data type of the input (a term used in Type Taxonomy)
	format		-	data format of the input (a term used in Format Taxonomy)
	code		-	code that will be used to implement the workflow as a script

Simplified table representation of our tool annotations is provided in 'res/tool_annotations.png'
_______________________________________________________________________________________________________________________________________________

Constraints File

Example file: 'constraints.csv' and their natural language representation is provided in 'res/Constraints.odt'

The file follows the structure of 2 or more columns where:
1st column represent the Constraint ID (ID of a constraint template from 'res/constraints_template.ods')
2nd column contains the first argument for the constraint (a concept provided in the taxonomy)
3rd column contains the second argument (when applicable) for the constraint (a concept provided in the taxonomy)

Constraint templates can be found in 
_______________________________________________________________________________________________________________________________________________

