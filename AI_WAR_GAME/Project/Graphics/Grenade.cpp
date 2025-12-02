#include "Grenade.h"
#include "GroupDef.h"

Grenade::Grenade(double r, double c, int t)
{
	int i;
	row = r;
	col = c;
	team = t;
	double alpha = 2*PI/NUM_BULLETS; // bullet separation angle
	for (i = 0;i < NUM_BULLETS;i++)
	{
		gBullets[i] = new Bullet(c, r, i * alpha, t);
	}
}

void Grenade::explode()
{
	int i;

	for (i = 0;i < NUM_BULLETS;i++)
	{
		bullets.push_back(gBullets[i]);
	}


}

void Grenade::expand(int maze[MSZ][MSZ])
{
	int i;

	for (i = 0;i < NUM_BULLETS;i++)
	{
		gBullets[i]->move(maze);
	}

}

void Grenade::show()
{
	int i;

	for (i = 0;i < NUM_BULLETS;i++)
	{
		gBullets[i]->show();
	}

}

void Grenade::SimulateExplosion(int maze[MSZ][MSZ], double sm[MSZ][MSZ], bool neg)
{
	int i;

	for (i = 0;i < NUM_BULLETS;i++)
	{
		gBullets[i]->SimulateExplosion(maze,sm, neg);
		free(gBullets[i]);
	}

}
