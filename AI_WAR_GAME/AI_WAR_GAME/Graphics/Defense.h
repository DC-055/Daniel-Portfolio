#pragma once
#include "State.h"

// Move to safe space using security map

class Defense :
    public State
{   
public:
	void OnEnter(Player* p);
	void Transition(Player* p);
	void OnExit(Player* p);
};

