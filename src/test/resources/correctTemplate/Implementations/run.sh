#!/bin/bash
unset noclobber

sudo chmod u+x workflow*

./workflowSolution_0.sh
echo "Workflow 0 executed"

./workflowSolution_1.sh
echo "Workflow 1 executed"

./workflowSolution_2.sh
echo "Workflow 2 executed"

./workflowSolution_3.sh
echo "Workflow 3 executed"

./workflowSolution_4.sh
echo "Workflow 4 executed"






