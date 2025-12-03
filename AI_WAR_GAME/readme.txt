Programming Language Interpreter – Final Project

This repository contains a custom programming language interpreter
written in Python as a final project for the Programming Languages
course. The interpreter supports arithmetic operations, boolean logic,
variables, conditionals, and functions (including recursion).

Main Files - basic.py – Core interpreter (lexer, parser, AST, runtime) -
shell.py – Runs the interpreter (interactive + script modes) -
file_handling.py – Validates and runs .lambda script files -
strings_with_arrows.py – Displays exact error positions - program.lambda
– Example script file - BNF_Documentation.pdf – Formal grammar -
LANGUAGE USER GUIDE.pdf – Full usage guide

How to Run

Start Interactive Mode: python shell.py

Run a Script File: basic > -F program.lambda

Exit: basic > QUIT

Language Features - Integer and Boolean variables - Arithmetic: + - * /
% ^ - Comparisons: == != < > <= >= - Logical operators: && || ! -
Variable declaration using VAR - Conditional statements: IF / ELIF /
ELSE / THEN - Named and anonymous functions - Recursive functions -
Interactive REPL and script execution - Detailed syntax and runtime
error messages

Example Usage

Variables: basic > VAR a = 5 basic > VAR b = True

Arithmetic: basic > 5 + 5 10 basic > 8 ^ 2 64

Functions: basic > FUN sum(a,b) -> a + b basic > sum(3,4) 7

Anonymous Function: basic > VAR mul = FUN (a,b) -> a * b basic >
mul(3,5) 15

Recursive Function: basic > FUN counter(x) -> IF x < 10 THEN
counter(x + 1) ELSE x

Daniel Cohen
Final Project – Programming Languages Course
