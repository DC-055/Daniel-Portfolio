#pragma once
#include "State.h"
#include "Support.h"
// Stand near fighters

class Backup :
	public State
{
public:
	void OnEnter(Player* p);
	void Transition(Player* p);
	void OnExit(Player* p);
};