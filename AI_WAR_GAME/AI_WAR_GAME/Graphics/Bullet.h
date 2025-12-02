#pragma once
#include "definitions.h"
#include "Player.h"
#include "PlayersDef.h"

class Bullet
{
private: 
	double x, y;
	double dir_angle;
	double dirX, dirY;
	double speed;
	bool isMoving;
	int team;
public:
	Bullet(double xx, double yy, double angle, int t);
	void move(int maze[MSZ][MSZ]);
	void show();
	void setIsMoving(bool value) { isMoving = value; }
	bool getIsMoving() { return isMoving; }
	int getX() { return x; }
	int getY() { return y; }
	int getTeam() { return team; }
	double calcDist(Player* p) { return sqrt(pow((x - p->getX()), 2) + pow((y - p->getY()), 2)); }
	void SimulateExplosion(int maze[MSZ][MSZ], double sm[MSZ][MSZ], bool neg);

};

