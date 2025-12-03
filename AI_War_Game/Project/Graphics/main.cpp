
#include <stdlib.h>
#include <time.h>
#include <math.h>
#include "glut.h"
#include <queue>
#include "Cell.h"
#include <iostream>
#include<chrono>
#include<thread>
#include "Room.h"
#include "Warehouse.h"
#include "CompareCells.h"
#include "Bullet.h"
#include "Grenade.h"
#include "Fighter.h"
#include "Support.h"
#include "definitions.h"
#include "GroupDef.h"
#include "PlayersDef.h"

using namespace std;

const int WIDTH = 1200;
const int HEIGHT = 1200;

const int NUM_ROOMS = 10;

const double WALL_COST = 5;
const double SPACE_COST = 1;

Room* rooms[NUM_ROOMS];
vector <Warehouse*> warehouses;
vector<Player*> players;
vector<Bullet*> bullets;
vector<Grenade*> grenades;

const int CTR = 2;
int counter = 0;
int winCondition = 0;
int maze[MSZ][MSZ] = { 0 };
double secMap1[MSZ][MSZ] = { 0 };
double secMap2[MSZ][MSZ] = { 0 };

// TEST

void RestorePath(Cell* pc)
{
	int r, c;
	while (pc != nullptr)
	{
		r = pc->getRow();
		c = pc->getCol();
		if (maze[r][c] == WALL)
			maze[r][c] = SPACE;
		pc = pc->getParent();
	}
}

// row, col are the indices of neighbor cell
void AddNeighbor(int r, int c, Cell* pCurrent, priority_queue<Cell*, vector<Cell*>, CompareCells>& pq, vector <Cell>& grays, vector <Cell>& black) // blacks shouldn't be changed
{
	double newg, cost;
	vector<Cell>::iterator itGray;
	vector<Cell>::iterator itBlack;


	if (maze[r][c] == WALL) cost = WALL_COST;
	else cost = SPACE_COST;
	newg = pCurrent->getG() + cost;
	Cell* pNeighbor = new Cell(r, c, pCurrent->getTargetRow(), pCurrent->getTargetCol(),
		newg, pCurrent);
	// check what to do with the neighbor Cell
	// 1. if the neighbor is black: do nothing
	// 2. if the neighbor is white: add it to PQ and to grays
	// 3. if it is gray:
	// 3.1 if F of neighbor is not below the neighbor copy that is stored in PQ then do nothing
	// 3.2 otherwise then we must update the PQ and grays
	itGray = find(grays.begin(), grays.end(), *pNeighbor);
	itBlack = find(black.begin(), black.end(), *pNeighbor);

	if (itBlack == black.end()) // then it is not black
	{
		if (itGray == grays.end()) // then it is not gray => it is white
		{
			// paint it gray
			pq.push(pNeighbor);
			grays.push_back(*pNeighbor);
		}
		else // it is gray
		{
			if (pNeighbor->getF() < itGray->getF()) // then we found a better path and we have to exchange it
			{
				grays.erase(itGray);
				grays.push_back(*pNeighbor);

				// and do the same with PQ
				vector<Cell*> tmp;
				while (!pq.empty() && !((*pq.top()) == (*pNeighbor)))
				{
					tmp.push_back(pq.top()); // save the top element in tmp
					pq.pop(); // remove top element from pq
				}
				if (pq.empty()) // ERROR!
				{
					cout << "PQ is empty\n";
					exit(2);
				}
				else // we have found the neighbor cell in PQ
				{
					pq.pop(); // remove old neighbor from pq
					pq.push(pNeighbor);
					// now restore pq
					while (!tmp.empty())
					{
						pq.push(tmp.back());
						tmp.pop_back();
					}
				}
			}
		}
	}


}

// run A* from room at index1 to room at index2
void BuildPath(int index1, int index2)
{
	int r, c, tr, tc;

	r = rooms[index1]->getCenterY();
	c = rooms[index1]->getCenterX();
	tr = rooms[index2]->getCenterY();
	tc = rooms[index2]->getCenterX();
	Cell* pCurrent;
	Cell* start = new Cell(r, c, tr, tc, 0, nullptr);
	priority_queue<Cell*, vector<Cell*>, CompareCells> pq;
	vector <Cell> grays;
	vector <Cell> black;
	vector<Cell>::iterator itGray;

	pq.push(start);
	grays.push_back(*start);
	// pq shouldn't be empty because we are going to reach the target beforehand
	while (!pq.empty())
	{
		pCurrent = pq.top();
		if (pCurrent->getH() < 0.001) // this is a targt cell
		{
			RestorePath(pCurrent);
			return;
		}
		else // target hasn't been reached
		{
			// 1. remove pCurrent from pq 
			pq.pop();
			// 2. find and remove pCurrent from grays
			itGray = find(grays.begin(), grays.end(), *pCurrent);
			if (itGray == grays.end()) // pCurrent wasn't found
			{
				cout << "Error: pCurrent wasn't found in grays\n";
				exit(1);
			}
			grays.erase(itGray);
			// 3. paint pCurrent black
			black.push_back(*pCurrent);
			// 4. take care of neighbors
			r = pCurrent->getRow();
			c = pCurrent->getCol();
			// up
			if (r + 1 < MSZ)
				AddNeighbor(r + 1, c, pCurrent, pq, grays, black);
			// down
			if (r - 1 >= 0)
				AddNeighbor(r - 1, c, pCurrent, pq, grays, black);
			// left
			if (c - 1 >= 0)
				AddNeighbor(r, c - 1, pCurrent, pq, grays, black);
			// right
			if (c + 1 < MSZ)
				AddNeighbor(r, c + 1, pCurrent, pq, grays, black);
		}

	}
	cout << "Error pq is empty, target hasn't been found\n";
}

void BuildPathBetweenTheRooms()
{
	int i, j;

	for (i = 0; i < NUM_ROOMS; i++)
		for (j = i + 1; j < NUM_ROOMS; j++)
		{
			BuildPath(i, j); // A*
			cout << "The path from " << i << " to " << j << " has been paved\n";
		}
}

void SetupDungeon()
{
	int i, j;
	int cx, cy, w, h;
	bool hasOverlap;

	for (i = 0; i < NUM_ROOMS; i++)
	{
		do
		{
			hasOverlap = false;
			w = 6 + rand() % (MSZ / 5);
			h = 6 + rand() % (MSZ / 5);
			cx = 2 + w / 2 + rand() % (MSZ - w - 4);
			cy = 2 + h / 2 + rand() % (MSZ - h - 4);
			for (j = 0; j < i && !hasOverlap; j++)
				hasOverlap = rooms[j]->Overlap(cx, cy, w, h);
		} while (hasOverlap); // check the validity of the room

		rooms[i] = new Room(cx, cy, w, h, maze);

		if (i == 0) //Add team 1
		{
			maze[cx + 2][cy] = SPACE;
			maze[cx - 2][cy] = SPACE;
			maze[cx][cy - 2] = SPACE;
			players.push_back(new Fighter(cx + 2, cy, 40, 1));
			players.push_back(new Fighter(cx - 2, cy, 30, 1));
			players.push_back(new Support(cx, cy - 2, 50, 1));
		}
		if (i == NUM_ROOMS - 1) //Add team 2
		{
			maze[cx + 2][cy] = SPACE;
			maze[cx - 2][cy] = SPACE;
			maze[cx][cy - 2] = SPACE;
			players.push_back(new Fighter(cx + 2, cy, 20, 2));
			players.push_back(new Fighter(cx - 2, cy, 40, 2));
			players.push_back(new Support(cx, cy - 2, 60, 2));
		}
		//Add warehouse in each room
		warehouses.push_back(new Warehouse(cx, cy));
	}

	for (i = 0; i < 100; i++)
		maze[rand() % MSZ][rand() % MSZ] = WALL;
	BuildPathBetweenTheRooms();
}

void GenerateSecurityMap()
{
	int numSimulations = 25;
	Grenade* g = nullptr;

	for (int i = 0; i < numSimulations; i++)
	{
		g = new Grenade(rand() % MSZ, rand() % MSZ, 0);
		g->SimulateExplosion(maze, secMap1, false);
		free(g);
		g = new Grenade(rand() % MSZ, rand() % MSZ, 0);
		g->SimulateExplosion(maze, secMap2, false);
		free(g);
	}
	for (Player* p : players)
	{
		if (p->getTeam() == 1)
			for (int i = 0; i < numSimulations; i++)
			{
				g = new Grenade(p->getY(), p->getX(), 0);
				g->SimulateExplosion(maze, secMap2, false);
				free(g);
				g = new Grenade(p->getY(), p->getX(), 0);
				g->SimulateExplosion(maze, secMap1, true);
				free(g);
			}
		else
		{
			for (int i = 0; i < numSimulations; i++)
			{
				g = new Grenade(p->getY(), p->getX(), 0);
				g->SimulateExplosion(maze, secMap1, false);
				free(g);
				g = new Grenade(p->getY(), p->getX(), 0);
				g->SimulateExplosion(maze, secMap2, true);
				free(g);
			}
		}
	}
	for (Bullet* b : bullets)
	{
		if (b->getTeam() == 1)
			for (int i = 0; i < numSimulations; i++)
			{
				g = new Grenade(b->getY(), b->getX(), 0);
				g->SimulateExplosion(maze, secMap2, false);
				free(g);
			}
		else
		{
			for (int i = 0; i < numSimulations; i++)
			{
				g = new Grenade(b->getY(), b->getX(), 0);
				g->SimulateExplosion(maze, secMap1, false);
				free(g);
			}
		}
	}
}

void init()
{
	glClearColor(0.3, 0.3, 0.4, 0);// color of window background
	glOrtho(0, MSZ, 0, MSZ, -1, 1); // set the coordinates system

	srand(time(0));
	GenerateSecurityMap();
	SetupDungeon();
}

void ShowDungeon()
{
	int i, j;
	double s;

	for (i = 0; i < MSZ; i++)
		for (j = 0; j < MSZ; j++)
		{
			//1. set color of cell
			switch (maze[i][j])
			{
			case SPACE:
				glColor3d(1, 1, 1 ); // gray
				break;
			case WALL:
				glColor3d(0.3, 0.3, 0.4); // dark gray
				break;
			}
			// show cell
			glBegin(GL_POLYGON);
			glVertex2d(j - 0.5, i - 0.5);
			glVertex2d(j - 0.5, i + 0.5);
			glVertex2d(j + 0.5, i + 0.5);
			glVertex2d(j + 0.5, i - 0.5);
			glEnd();
		}
}

void checkVictory()
{
	int team1 = 0;
	int team2 = 0;
	for (Player* p : players) //moving players
	{
		switch (p->getTeam())
		{
		case 1:
			team1++;
			break;
		case 2:
			team2++;
			break;
		}
	}
	if (team1 == 0 && team2 == 0)
		winCondition = 3;
	if (team1 == 0)
		winCondition = 2;
	if (team2 == 0)
		winCondition = 1;
	if (team1 != 0 && team2 != 0)
		winCondition = 0;
}

void display()
{
	glClear(GL_COLOR_BUFFER_BIT); // clean frame buffer

	ShowDungeon();
	for (size_t i = 0; i < bullets.size(); ++i) //show bullets
	{
		bullets[i]->show();
	}
	for (size_t i = 0; i < players.size(); ++i) //show players
	{
		players[i]->show((MSZ / 7) * (i + 1), MSZ - 2);
	}
	for (size_t i = 0; i < warehouses.size(); ++i) //show players
	{
		warehouses[i]->show();
	}
	if (winCondition != 0)
	{
		glColor3d(0, 0, 0);
		glBegin(GL_POLYGON);
		glVertex2d(MSZ / 2 + 20, MSZ / 2 - 5);
		glVertex2d(MSZ / 2 + 20, MSZ / 2 + 5);
		glVertex2d(MSZ / 2 - 5, MSZ / 2 + 5);
		glVertex2d(MSZ / 2 - 5, MSZ / 2 - 5);
		glEnd();
		switch (winCondition) //shows if game ended
		{
		case 1:
			glColor3f(1, 0, 0);
			glRasterPos2d(MSZ / 2, MSZ / 2);
			for (char c : "TEAM 1 WON!!!") glutBitmapCharacter(GLUT_BITMAP_HELVETICA_18, c);
			break;
		case 2:
			glColor3f(0, 0, 1);
			glRasterPos2d(MSZ / 2, MSZ / 2);
			for (char c : "TEAM 2 WON!!!") glutBitmapCharacter(GLUT_BITMAP_HELVETICA_18, c);
			break;
		case 3:
			glColor3f(0, 1, 0);
			glRasterPos2d(MSZ / 2, MSZ / 2);
			for (char c : "DRAW") glutBitmapCharacter(GLUT_BITMAP_HELVETICA_18, c);
			break;
		}
		for (size_t i = 0; i < warehouses.size(); ++i)
		{
			warehouses[i]->show();
		}
	}
	glutSwapBuffers(); // show all
}

void idle()
{
	if (winCondition == 0)
	{
		if (counter <= 0)
		{
			GenerateSecurityMap();
			counter = CTR;
		}
		else
			counter--;
		for (size_t i = 0; i < bullets.size(); ++i) //moving bullets
		{
			bullets[i]->move(maze);
		}
		for (auto it = players.begin(); it != players.end(); ) //moving players
		{
			Player* p = *it;
			p->getCurrentState()->OnEnter(p);
			p->getCurrentState()->Transition(p);

			if (p->getHealth() < 0) {
				it = players.erase(it);  // Remove and get the next valid iterator
				delete p;                // Free memory if dynamically allocated
			}
			else {
				++it; // Only increment if no deletion happened
			}
		}
		bullets.erase(std::remove_if(bullets.begin(), bullets.end(), [](Bullet* bullet) {return !bullet->getIsMoving(); }), bullets.end());
		grenades.clear();
		checkVictory();
	}
	glutPostRedisplay(); // indirect call to display
	std::this_thread::sleep_for(std::chrono::milliseconds(40));
}

void menu(int choice)
{
}

void mouse(int button, int state, int x, int y)
{
}

void main(int argc, char* argv[])
{
	glutInit(&argc, argv);
	// definitions for visual memory (Frame buffer) and double buffer
	glutInitDisplayMode(GLUT_RGB | GLUT_DOUBLE);
	glutInitWindowSize(WIDTH, HEIGHT);
	glutInitWindowPosition(600, 20);
	glutCreateWindow("SHOOTER");

	// display is a refresh function
	glutDisplayFunc(display);
	// idle is a update function
	glutIdleFunc(idle);

	glutMouseFunc(mouse);

	// menu
	glutCreateMenu(menu);
	glutAttachMenu(GLUT_RIGHT_BUTTON);


	init();

	glutMainLoop();
}
