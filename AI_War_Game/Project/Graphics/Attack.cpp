#include "Attack.h"
#include "Defense.h"
#include "Groupdef.h"

// enter current state
void Attack::OnEnter(Player* p)
{
	p->setNeed(false);
	p->doSomething();
}

void Attack::Transition(Player* p)
{
	OnExit(p);
	if (p->getHealth() < p->getCowardness() || p->getAmmo() < 1)
	{
		p->setCurrentState(new Defense());
	}

}

// exiting current state
void Attack::OnExit(Player* p)
{
	p->setNeed(true);
}
