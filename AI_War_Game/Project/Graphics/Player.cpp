#include "Player.h"
#include "Grenade.h"
#include "glut.h"
#include "definitions.h"
#include <iostream>

Player::Player(int xx, int yy, double a, double h, int cow, int t, State* s)
{
	x = xx;
	y = yy;
	ammo = a;
	health = h;
	team = t;
	cowardnessFactor = cow;
	pCurrentState = s;
}

void Player::doSomething()
{
}

void Player::setNeed(bool flag)
{
}

void Player::setNeedToRestock(bool flag)
{
}


bool Player::checkNeighbour(int row, int col, Cell* pCurrent)
{
	if ((row == targetY) && (col == targetX))
	{
		Cell* pPrev = pCurrent;
		while (pCurrent->getParent() != nullptr)
		{
			pPrev = pCurrent;
			pCurrent = pCurrent->getParent();
		}
		setX(pPrev->getCol());
		setY(pPrev->getRow());
		return true;
	}
	else
	{
		double sec;
		if (team == 1)
			sec = secMap1[row][col];
		else
			sec = secMap2[row][col];
		pq.push(new Cell(row, col, targetY, targetX, pCurrent->getG() + sec + 1, pCurrent));
		return false;
	}
}

void Player::AStarTarget()
{
	if (x == targetX && y == targetY)
		return;

	// setup tempMaze
	int tempMaze[MSZ][MSZ] = { 0 };
	int curr;
	for (int i = 0; i < MSZ; i++)
		for (int j = 0; j < MSZ; j++)
		{
			curr = maze[i][j];
			if (curr != WALL)
			{
				tempMaze[i][j] = SPACE;
			}
			else
				tempMaze[i][j] = WALL;
		}

	tempMaze[y][x] = START;
	tempMaze[targetY][targetX] = TARGET;

	bool runAStar = true;
	pq.push(new Cell(y, x, targetY, targetX, 0, nullptr));
	Cell* pCurrent = nullptr;
	Cell* pTest;
	int row, col;
	while (runAStar)
	{
		while (true)
		{
			if (pq.empty())
			{
				runAStar = false;
				break;
			}
			if (tempMaze[pq.top()->getRow()][pq.top()->getCol()] == BLACK)
			{
				delete pq.top();
				pq.pop();
			}
			else
				break;
		}
		if(runAStar) // grays is not empty
		{
			pCurrent = pq.top();
			pq.pop(); // extract the first element from grays
			// 1. paint pCurrent black
			row = pCurrent->getRow();
			col = pCurrent->getCol();
			if (tempMaze[row][col] != START)
				tempMaze[row][col] = BLACK;
			// 2. Check all neighbors of pCurrent

			// go up
			if ((tempMaze[row + 1][col] == SPACE) || (tempMaze[row + 1][col] == TARGET))
			{
				if (checkNeighbour(row + 1, col, pCurrent))
				{
					runAStar = false;
					break;
				}
			}
			// go down
			if ((tempMaze[row - 1][col] == SPACE) || (tempMaze[row - 1][col] == TARGET))
			{
				if (checkNeighbour(row - 1, col, pCurrent))
				{
					runAStar = false;
					break;
				}
			}
			// go left
			if ((tempMaze[row][col - 1] == SPACE) || (tempMaze[row][col - 1] == TARGET))
			{
				if (checkNeighbour(row, col-1, pCurrent))
				{
					runAStar = false;
					break;
				}
			}
			// go right
			if ((tempMaze[row][col + 1] == SPACE) || (tempMaze[row][col + 1] == TARGET))
			{
				if (checkNeighbour(row, col+1, pCurrent))
				{
					runAStar = false;
					break;
				}
			}
		}
	}
	while (!pq.empty())
	{
		delete pq.top();
		pq.pop();
	}
}

void Player::show(int xx, int yy)
{
}
