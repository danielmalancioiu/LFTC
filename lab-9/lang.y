%{
#include <stdio.h>
#include <stdlib.h>

#define YYDEBUG 1 
%}

%token PROGRAM
%token VAR
%token IF
%token ELSE
%token FOR
%token WHILE
%token PRINT
%token ARR
%token INPUT

%token plus
%token minus
%token mul
%token division
%token mod
%token lessOrEqual
%token moreOrEqual
%token less
%token more
%token equal
%token different
%token eq



%token leftCurlyBracket
%token rightCurlyBracket
%token leftRoundBracket
%token rightRoundBracket
%token leftBracket
%token rightBracket
%token colon
%token semicolon
%token comma
%token apostrophe
%token quote

%token IDENTIFIER
%token NUMBER_CONST
%token STRING_CONST
%token CHAR_CONST

%start program

%%

program : PROGRAM compound_statement

statement : declaration | assignment_statement | iostmt semicolon | if_statement | while_statement | for_statement | print_statement

statement_list : statement | statement statement_list

compound_statement : leftBracket statement_list rightBracket

expression : expression plus term | expression minus term | term 

term : term mul factor | term division factor | term mod factor | factor 

factor : leftRoundBracket expression rightRoundBracket | IDENTIFIER | constant

constant : NUMBER_CONST | STRING_CONST | CHAR_CONST 

iostmt : INPUT eq IDENTIFIER  | INPUT eq constant

simple_type : VAR

array_declaration : ARR simple_type IDENTIFIER leftBracket rightBracket

declaration : simple_type IDENTIFIER eq expression | array_declaration 

assignment_statement : IDENTIFIER eq expression

if_statement : IF leftCurlyBracket condition rightCurlyBracket compound_statement | IF leftCurlyBracket condition rightCurlyBracket compound_statement ELSE compound_statement

while_statement : WHILE leftCurlyBracket condition rightCurlyBracket compound_statement

for_statement : FOR for_header compound_statement

for_header : leftCurlyBracket simple_type assignment_statement condition assignment_statement rightCurlyBracket

condition : expression relation expression

relation : less | lessOrEqual | equal | different | moreOrEqual | more

print_statement : PRINT leftCurlyBracket constant rightCurlyBracket | PRINT leftCurlyBracket expression rightCurlyBracket 

%%

yyerror(char *s)
{	
	printf("%s\n", s);
}

extern FILE *yyin;

int main(int argc, char **argv)
{
	if(argc > 1) yyin = fopen(argv[1], "r");
	if(argc > 2 && !strcmp(argv[2], "-d")) yydebug = 1;
	if(!yyparse()) fprintf(stderr, "\tProgram is syntactically correct.\n");
	return 0;
}