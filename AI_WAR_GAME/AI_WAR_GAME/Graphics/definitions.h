#pragma once

const int MSZ = 100;

const int WALL = 0;
const int SPACE = 1;
const int START = 2;
const int TARGET = 3;
const int BLACK = 4;
const double PI = 3.14;
const double SECURITY_FACTOR = 0.01;
const int VIEWDISTANCE = 50;

extern int maze[MSZ][MSZ];
extern double secMap1[MSZ][MSZ];
extern double secMap2[MSZ][MSZ];