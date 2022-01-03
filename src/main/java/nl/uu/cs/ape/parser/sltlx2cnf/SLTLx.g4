/*
BSD License

*/


grammar SLTLx;

condition
   :formula (ENDLINE formula)* ENDLINE* EOF
   ;
   
formula
   : bool										# boolean
   | LPAREN formula RPAREN						# brackets
   | formula bin_connective formula 			# binaryBool	
   | NOT formula bin_connective formula			# negBinaryBool
   | NOT formula 								# negUnary
   | FORALL LPAREN variable RPAREN formula 		# forall
   | EXISTS LPAREN variable RPAREN formula		# exists
   | un_modal formula							# unaryModal
   | '<' module '>' formula						# toolRef
   | formula bin_modal formula					# binaryModal
   | SLTL_R LPAREN variable ',' variable RPAREN	# function
   | CONSTANT '(' variable ')'					# function
   | variable EQUAL variable					# varEq
   ;
   
bin_connective
   : AND
   | OR
   | IMPL
   | EQUIVALENT
   ;
 
un_modal
   : SLTL_GLOBALLY
   | SLTL_FINALLY
   | SLTL_NEXT
   ;
  
bin_modal
   : SLTL_UNTIL
   ;

module
   : CONSTANT '(' vars? ';' vars? ')' 
   ;

vars
   : variable ( ',' variable )*
   ;

  
bool
   : 'true'
   | 'false'
   ;

LPAREN
   :'('
   ;
RPAREN
   :')'
   ;
separator
   :','
   ;

variable
   : '?' CHARACTER+
   ;

CONSTANT
   : '_' CHARACTER+
   ;

SLTL_R
   : 'R'
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
   :'|'
   ;
AND
   :'&'
   ;
IMPL
   :'->'
   ;
EQUIVALENT
   :'<=-'
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
 
FORALL
   :'Forall'
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
