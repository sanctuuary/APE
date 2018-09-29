# APE

Automated Pipeline Program Synthesis and Execution


## Motivation
A short description of the motivation behind the creation and maintenance of the project. This should explain **why** the project exists.

## Build status
Build status of continus integration i.e. travis, appveyor etc. Ex. - 

[![Build Status](https://travis-ci.org/akashnimare/foco.svg?branch=master)](https://travis-ci.org/akashnimare/foco)
[![Windows Build Status](https://ci.appveyor.com/api/projects/status/github/akashnimare/foco?branch=master&svg=true)](https://ci.appveyor.com/project/akashnimare/foco/branch/master)

## Code style
If you're using any code style like xo, standard etc. That will help others while contributing to your project. Ex. -

[![js-standard-style](https://img.shields.io/badge/code%20style-standard-brightgreen.svg?style=flat)](https://github.com/feross/standard)
 
## Screenshots
Include logo/demo screenshot etc.

## Tech/framework used
Ex. -

<b>Built with</b>
- [Electron](https://electron.atom.io)

## Features
What makes your project stand out?

## Code Example
Show what the library does as concisely as possible, developers should be able to figure out **how** your project solves their problem by looking at the code example. Make sure the API you are showing off is obvious, and that your code is short and concise.

## Installation
Provide step by step series of examples and explanations about how to get a development env running.

## API Reference

Depending on the size of the project, if it is small and simple enough the reference docs can be added to the README. For medium size to larger projects it is important to at least provide a link to where the API reference docs live.

## Tests
Describe and show how to run the tests with code examples.

## How to use?
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

## Contribute

Let people know how they can contribute into your project. A [contributing guideline](https://github.com/zulip/zulip-electron/blob/master/CONTRIBUTING.md) will be a big plus.

## Credits
Give proper credits. This could be a link to any repo which inspired you to build this project, any blogposts or links to people who contrbuted in this project. 


## License
A short snippet describing the license (MIT, Apache etc)

MIT © [Yourname]()