#include "Fighter.h"
#include "Attack.h"
#include "glut.h"
#include "PlayersDef.h"
#include "GroupDef.h"
#include <string>
#include <iostream>

Fighter::Fighter(int xx, int yy, int cow, int t) : Player(xx, yy, FMAX_AMMO, FMAX_HEALTH, cow, t, new Attack()) {}

void Fighter::show(int xx, int yy)
{
	switch (team)
	{
	case 1:
		glColor3d(0.8, 0, 0);
		glRasterPos2d(xx - 3, yy);
		for (char c : "Fighter") glutBitmapCharacter(GLUT_BITMAP_HELVETICA_18, c);
		break;
	case 2:
		glColor3d(0, 0, 0.8);
		glRasterPos2d(xx - 3, yy);
		for (char c : "Fighter") glutBitmapCharacter(GLUT_BITMAP_HELVETICA_18, c);
		break;
	}
	glBegin(GL_POLYGON);
	glVertex2d(x - 0.5, y);
	glVertex2d(x, y + 0.5);
	glVertex2d(x + 0.5, y);
	glVertex2d(x, y - 0.5);
	glEnd();

	// Draw HUD

	if (health > 0)
	{
		glColor3f(0, 1, 0);
		glRasterPos2d(xx - 3, yy - 2);
		for (char c : std::to_string((int)health)) glutBitmapCharacter(GLUT_BITMAP_HELVETICA_18, c);
		glColor3f(0, 0, 0);
		glRasterPos2d(xx, yy - 2);
		for (char c : "|") glutBitmapCharacter(GLUT_BITMAP_HELVETICA_18, c);
		glColor3f(0, 0, 0);
		glRasterPos2d(xx + 1, yy - 2);
		for (char c : std::to_string((int)ammo)) glutBitmapCharacter(GLUT_BITMAP_HELVETICA_18, c);
		if (!needSupply)
		{
			glColor3f(1, 0.5, 0);
			glRasterPos2d(xx, yy - 4);
			for (char c : "Defense") glutBitmapCharacter(GLUT_BITMAP_HELVETICA_18, c);
		}
		else
		{
			glColor3f(0, 0, 0);
			glRasterPos2d(xx, yy - 4);
			for (char c : "Attack") glutBitmapCharacter(GLUT_BITMAP_HELVETICA_18, c);
		}
		glColor3f(0, 0, 0);
		glBegin(GL_LINES);
		glVertex2f(xx, yy - 3);
		glVertex2f(x, y);
		glEnd();
	}
	else
	{
		glColor3f(1, 0, 0);
		glRasterPos2d(xx, yy - 2);
		for (char c : "DEAD") glutBitmapCharacter(GLUT_BITMAP_HELVETICA_18, c);
	}

}

void Fighter::doSomething()
{
	if (!needSupply) // Attack mode
	{
		// Finds closest enemy
		double dist = 0;
		double closestDist = 1000;
		for (Player* po : players)
		{
			if (po->getTeam() != team)
			{
				dist = calcDist(po);
				if (dist <= closestDist)
				{
					closestDist = dist;
					setTarget(po->getX(), po->getY());
				}
			}
		}
		if (closestDist <= 3)
		{
			if (rand() % 100 <= 75)
			{
				int xx = 0;
				int yy = 0;
				while (maze[yy][xx] != SPACE)
				{
					xx = rand() % MSZ;
					yy = rand() % MSZ;
				}
				setTarget(rand() % MSZ, rand() % MSZ);
				AStarTarget();
				return;
			}
		}
		// Corridor check
		int walls = 0;
		if (maze[y][x + 1] == WALL)
			walls++;
		if (maze[y][x - 1] == WALL)
			walls++;
		if (maze[y - 1][x] == WALL)
			walls++;
		if (maze[y + 1][x] == WALL)
			walls++;
		if (walls >= 2)
		{
			AStarTarget();
		}
		else
		{
			double angle = hasClearShot(targetX, targetY);
			if (angle != -1) // Shoot
			{
				if (cooldown == 0)
				{
					if (rand() % 100 <= 5 && ammo >= 5) //grenade
					{
						Grenade* g = new Grenade(y, x, team);
						g->explode();
						ammo = ammo - 5;
					}
					else //bullet
					{
						if (rand() % 100 <= 60 && ammo >= 5)
						{
							bullets.push_back(new Bullet(x, y, angle, team));
							ammo = ammo - 1;
						}
						else
						{
							defenseMove();
						}
					}
					cooldown = FCOOLDOWN;
				}
			}
			else // AStar move to closest enemy
			{
				AStarTarget();
			}
			if (cooldown > 0)
				cooldown--;
		}
	}
	else // Defence mode
	{
		defenseMove();
	}
}

double Fighter::hasClearShot(int xx, int yy) //returns the angle if player has clear shot to position, -1 otherwise (IN RADIANS)
{

	int x1 = x;
	int y1 = y;
	// Bresenham's Line Algorithm to check for walls
	int dx = abs(xx - x1);
	int dy = abs(yy - y1);
	int sx = (x1 < xx) ? 1 : -1;
	int sy = (y1 < yy) ? 1 : -1;
	int err = dx - dy;

	while (true) {
		// If we hit a wall, return -1
		if (maze[y1][x1] == WALL) return -1;

		// If we reached the destination, break
		if (x1 == xx && y1 == yy) break;

		int e2 = 2 * err;
		if (e2 > -dy) {
			err -= dy;
			x1 += sx;
		}
		if (e2 < dx) {
			err += dx;
			y1 += sy;
		}
	}

	// Calculate the angle in radians if no wall is found
	return atan2(yy - y, xx - x);
}

void Fighter::defenseMove()
{
	int minSec = 1000;
	for (int i = -VIEWDISTANCE; i < VIEWDISTANCE; i++)
		for (int j = -VIEWDISTANCE; j < VIEWDISTANCE; j++)
			switch (team)
			{
			case 1:
				if (secMap1[y + i][x + j] < minSec && maze[y + i][x + j] != WALL && inBound(x+j, y+i))
				{
					targetX = x + j;
					targetY = y + i;
				}
				break;
			case 2:
				if (secMap2[y + i][x + j] < minSec && maze[y + i][x + j] != WALL && inBound(x + j, y + i))
				{
					targetX = x + j;
					targetY = y + i;
				}
				break;
			}
	AStarTarget();
}

bool inBound(int x, int y)
{
	return ((abs(y) > 0) && (abs(y) < MSZ) && (abs(x) > 0) && (abs(x) < MSZ));
}
