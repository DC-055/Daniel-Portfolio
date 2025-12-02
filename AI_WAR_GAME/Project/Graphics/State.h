#pragma once

class Player;
// pure virtual class (interface)

class State
{
public:
	virtual void OnEnter(Player* p) = 0; //to the next state
	virtual void Transition(Player* p) = 0; //to the current state
	virtual void OnExit(Player* p) = 0; //from the current state
};

