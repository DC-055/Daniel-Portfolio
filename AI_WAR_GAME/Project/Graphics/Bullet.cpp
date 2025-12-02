#include "Bullet.h"
#include <math.h>
#include <cmath>
#include "glut.h"
#include <iostream>

// angle is in radians
Bullet::Bullet(double xx, double yy, double angle, int t)
{
	x = xx;
	y = yy;
	dir_angle = angle;
	dirX = cos(angle);
	dirY = sin(angle);
	speed = 0.3;
	team = t;
	isMoving = true;
}

void Bullet::move(int maze[MSZ][MSZ])
{
	if (isMoving)
	{
		x += speed * dirX;
		y += speed * dirY;
		int x1 = round(x);
		int y1 = round(y);
		if (maze[y1][x1] == WALL)
			isMoving = false;
		if (team != 0)
			for (Player* p : players)
			{
				if (calcDist(p) <= 0.4 && p->getTeam() != team)
				{
					p->setHealth(p->getHealth() - 5);
					isMoving = false;
					break;
				}
			}
	}
}

void Bullet::show()
{
	switch (team)
	{
	case 1:
		glColor3d(1, 0, 0);
		break;
	case 2:
		glColor3d(0, 0, 1);
		break;
	}
	glBegin(GL_POLYGON);
	glVertex2d(x - 0.2, y);
	glVertex2d(x, y + 0.2);
	glVertex2d(x + 0.2, y);
	glVertex2d(x, y - 0.2);
	glEnd();
}

void Bullet::SimulateExplosion(int maze[MSZ][MSZ], double sm[MSZ][MSZ], bool neg)
{
	isMoving = true;
	while (isMoving)
	{
		if (neg)
			sm[(int)y][(int)x] += SECURITY_FACTOR;
		else
			sm[(int)y][(int)x] += SECURITY_FACTOR;
		move(maze);
	}
}
