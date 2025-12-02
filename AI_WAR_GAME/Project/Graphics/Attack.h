#pragma once
#include "State.h"
#include "Fighter.h"

// Move to nearby enemies

class Attack :
    public State
{
public:
	void OnEnter(Player* p);
	void Transition(Player* p);
	void OnExit(Player* p);
};

