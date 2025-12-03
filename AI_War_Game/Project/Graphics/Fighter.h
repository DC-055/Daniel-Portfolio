#pragma once
#include "Player.h"
#include "State.h"
const double FSPEED = 0.01;
const double FMAX_HEALTH = 100;
const double FMAX_AMMO = 60;
const double FMIN_AMMO = 0;
const int FCOOLDOWN = 10;

class Fighter : public Player
{
private:
	bool needSupply = false;
	int cooldown = FCOOLDOWN;
public:
	Fighter(int xx, int yy, int cow, int t);
	bool getNeed() { return needSupply; }
	void setNeed(bool need) { needSupply = need; }
	void show(int xx, int yy);
	void doSomething();
	double hasClearShot(int xx, int yy);
	void defenseMove();
};
bool inBound(int x, int y);
