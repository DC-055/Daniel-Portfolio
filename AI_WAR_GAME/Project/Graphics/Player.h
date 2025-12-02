#pragma once
#include <queue>
#include "State.h"
#include "math.h"
#include "Cell.h"
#include "CompareCells.h"
#include "definitions.h"


class Player
{
protected:
	int x, y;
	double ammo;
	double health;
	int targetX, targetY;
	State* pCurrentState;
	int team;
	int cowardnessFactor;
	std::priority_queue<Cell*, std::vector<Cell*>, CompareCells> pq;
public:
	Player(int xx, int yy, double a, double h, int cow, int t, State* s);
	virtual void show(int xx, int yy);
	virtual void doSomething();
	virtual void setNeed(bool flag);
	virtual void setNeedToRestock(bool flag);
	void setTarget(double x, double y) { targetX = x;  targetY = y; }
	void setCurrentState(State* ps) { pCurrentState = ps; }
	void setAmmo(double a) { ammo = a; }
	void setHealth(double h) { health = h; }
	State* getCurrentState() { return pCurrentState; }
	double getAmmo() { return ammo; }
	double getHealth() { return health; }
	int getTargetX() { return targetX; }
	int getTargetY() { return targetY; }
	int getX() { return x; }
	int getY() { return y; }
	void setX(int xx) { x = xx; }
	void setY(int yy) { y = yy; }
	int getTeam() { return team; }
	int getCowardness() { return cowardnessFactor; }
	double calcDist(Player* p) { return sqrt(pow((x - p->getX()), 2) + pow((y - p->getY()),2)); }
	double calcPointDist(int xx, int yy) { return sqrt(pow((x - xx), 2) + pow((y - yy), 2)); }
	bool checkNeighbour(int row, int col, Cell* pCurrent);
	void AStarTarget();

};