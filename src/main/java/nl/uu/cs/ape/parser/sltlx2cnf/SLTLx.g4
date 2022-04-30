/*
BSD License

*/


grammar SLTLx;

condition
   :formula (ENDLINE formula)* ENDLINE* EOF
   ;
   
formula
   : TRUE										# true
   | LPAREN formula RPAREN						# brackets
   | formula BIN_CONNECTIVE formula 			# binaryBool	
   | NOT formula 								# negUnary
   | FORALL LPAREN VARIABLE RPAREN formula 		# forall
   | EXISTS LPAREN VARIABLE RPAREN formula		# exists
   | UN_MODAL formula							# unaryModal
   | '<' module '>' formula						# toolRef
   | formula BIN_MODAL formula					# binaryModal
   | R_REL LPAREN VARIABLE ',' VARIABLE RPAREN	# r_relation
   | CONSTANT '(' VARIABLE ')'					# function
   | VARIABLE EQUAL VARIABLE					# varEq
   ;
   
BIN_CONNECTIVE
   : AND
   | OR
   | IMPL
   | EQUIVALENT
   ;
 
UN_MODAL
   : SLTL_GLOBALLY
   | SLTL_FINALLY
   | SLTL_NEXT
   ;
  
BIN_MODAL
   : SLTL_UNTIL
   ;

module
   : CONSTANT '(' vars ';' vars ')' 
   ;

vars
   : (VARIABLE ( ',' VARIABLE )*)?
   ;

  
TRUE
   : 'true'
   ;

LPAREN
   :'('
   ;
RPAREN
   :')'
   ;
   
VARIABLE
   : '?'  ('0' .. '9' | 'a' .. 'z' | 'A' .. 'Z' | '_')+
   ;

CONSTANT
   : '\''  ('0' .. '9' | 'a' .. 'z' | 'A' .. 'Z' | '_')+ '\''
   ;
   
R_REL
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
   :'<->'
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
   

ENDLINE
   :('\r'|'\n')+
   ;
WHITESPACE
   :(' '|'\t')+->skip
   ;
