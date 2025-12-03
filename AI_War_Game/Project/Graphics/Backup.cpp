#include "Backup.h"
#include "Restock.h"
#include "Groupdef.h"

// enter current state
void Backup::OnEnter(Player* p)
{
	p->setNeedToRestock(false);
	p->doSomething();
}

void Backup::Transition(Player* p)
{
	OnExit(p);
	Support* s = static_cast<Support*>(p);
	if (s->getAmmo() <= 0 || s->getAid() <= 0)
	{
		p->setCurrentState(new Restock());
	}
}

// exiting current state
void Backup::OnExit(Player* p)
{
	p->setNeedToRestock(true);
}
