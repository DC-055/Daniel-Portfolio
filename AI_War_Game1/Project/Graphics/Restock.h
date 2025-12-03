#pragma once
#include "State.h"
#include "Support.h"
// Go to nearby storage

class Restock :
	public State
{
public:
	void OnEnter(Player* p);
	void Transition(Player* p);
	void OnExit(Player* p);
};