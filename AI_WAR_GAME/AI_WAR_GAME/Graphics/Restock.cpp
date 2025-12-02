#include "Restock.h"
#include "Backup.h"
#include "Groupdef.h"

// enter current state
void Restock::OnEnter(Player* p)
{
		p->setNeedToRestock(true);
		p->doSomething();
}

void Restock::Transition(Player* p)
{
	OnExit(p);
	//check if the ammo and aid is on max
	//then go to Backup
	Support* s = static_cast<Support*>(p);
		if (s->getAid() == SMAX_AID && s->getAmmo() == SMAX_AMMO)
			p->setCurrentState(new Backup());
	
}

// exiting current state
void Restock::OnExit(Player* p)
{
	p->setNeedToRestock(false);
}

