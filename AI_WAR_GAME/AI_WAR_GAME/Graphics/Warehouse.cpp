#include "Warehouse.h"
#include "glut.h"

Warehouse::Warehouse(double xx, double yy)
{
	x = xx;
	y = yy;
}

void Warehouse::show()
{
	glColor3d(1.0, 0.0, 1.0); // Bright magenta (bold color)
	glBegin(GL_POLYGON);
	glVertex2d(x - 0.5, y);
	glVertex2d(x, y + 0.5);
	glVertex2d(x + 0.5, y);
	glVertex2d(x, y - 0.5);
	glEnd();
}
