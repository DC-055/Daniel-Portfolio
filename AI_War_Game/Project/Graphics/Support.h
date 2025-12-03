#pragma once
#include "Player.h"
#include "State.h"
#include "Warehouse.h"

const double SSPEED = 0.0001;
const double SMAX_HEALTH = 100;
const double SMAX_AID = 3;
const double SMAX_AMMO = 3;
const double SMIN_AMMO = 0;

class Support : public Player
{
private:
	double aid = SMAX_AID;
	bool needToRestock = false;
public:
	Support(int xx, int yy, int cow, int t);
	int getAid() { return aid; }
	void setAid(int value) { aid = value; }
	bool getNeedToRestock() { return needToRestock; }
	void setNeedToRestock(bool need) { needToRestock = need; }
	double calcDistToWarehouse(int x1, int y1, Warehouse* ws) { return sqrt(pow(ws->getX() - x1, 2) + pow(ws->getY() - y1, 2)); };
	void show(int xx, int yy);
	void doSomething();
};