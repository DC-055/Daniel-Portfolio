#pragma once
#include "definitions.h"

class Warehouse
{
private:
	double x, y;
public:
	Warehouse(double xx, double yy);
	int getX() { return x; };
	int getY() { return y; };
	void show();
};
