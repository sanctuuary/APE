/*
BSD License

*/


grammar SLTLx;

formula 
	: exists ? proposition 
	;

proposition
   : bool													# boolean
   | '<' module '>' proposition								# toolRef
   | '(' proposition ')'									# backets
   | proposition (AND | OR | IMPL) proposition				# binaryBool		
   | NOT proposition										# unaryBool
   | (SLTL_GLOBALLY | SLTL_FINALLY | SLTL_NEXT) proposition	# unaryModal
   | proposition SLTL_UNTIL proposition						# binaryModal
   | CONSTANT '(' atomic ')'								# function
   ;
   
exists  
	:   EXISTS VARIABLE ( ',' VARIABLE )*
	;

module
	: CONSTANT '(' atomics? '|' atomics? ')' 
	;

atomics
	: atomic ( ',' atomic )*
	;

atomic
	: CONSTANT | VARIABLE  
	;
  
bool
	: 'true'
    | 'false'
	;

VARIABLE
   : '_' CHARACTER*
   ;

CONSTANT
   : '\'' CHARACTER+ '\''
   ;


SLTL_UNTIL
   : 'U'
   ;

SLTL_GLOBALLY
   : 'G'
   ;

SLTL_FINALLY
   : 'F'
   ;

SLTL_NEXT
   : 'X'
   ;

OR
   :'\\/'
   ;
AND
   :'/\\'
   ;
IMPL
   :'->'
   ;
EQUAL
   :'='
   ;
NOT
   :'!'
   ;

EXISTS
   :'Exists'
   ;
CHARACTER
   : [0-9] | [a-z] | [A-Z]
   ;

ENDLINE
   :('\r'|'\n')+
   ;
WHITESPACE
   :(' '|'\t')+->skip
   ;
