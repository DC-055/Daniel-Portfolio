# AI War Game

*Ran on Visual Studio x86*

# AI War Game – Artificial Intelligence & Computer Graphics 

## Project Overview
This project was developed as a final assignment for the **Artificial Intelligence & Computer Graphics** course.  
The goal of the project is to demonstrate **intelligent behavior of computer-controlled characters (AI agents)** in a combat-based game environment.

The game simulates two opposing teams of AI-controlled characters navigating through a maze of rooms and corridors while making real-time decisions based on their goals, resources, and environment.

---

## Game Structure
- The game contains **two competing AI teams**.
- Each team consists of:
  - **Two Fighters**
  - **One Support / Carrier Unit**
- Each team’s objective is to **eliminate the opposing team**.

- All characters move within a **maze composed of rooms and passages**.

---

## Environment & Resources
1. **Ammo and Health Depots**
   - Ammo and medical supply storages are randomly placed throughout the map.
   - Example: 2 ammo stations and 2 medical stations.

2. **Obstacles & Cover**
   - Rooms contain multiple obstacles that provide **cover and hiding areas** during combat.

3. **Character Attributes**
   - Each character has:
     - **Health Points (HP)**
     - **Ammunition Inventory**

---

## AI Goals & Behavior
Each character maintains a set of high-level **goals**:

- Seek and engage enemy characters.
- Survive by:
  - Retreating when health drops below a threshold.
  - Retreating when ammunition drops below a threshold.
- Reload ammunition by requesting supply from the **Support Unit**.
- Restore health by requesting medical supplies from the **Support Unit**.

---

## Decision-Making System
- At any given moment, each character executes **one primary goal** based on its current state.
- The most important goal is selected dynamically.
- Each character is assigned a **randomized personality profile**, for example:
  - Aggressive behavior
  - Defensive or evasive behavior  
- These traits influence decision making and can determine which team wins.
- Once a goal is selected, the agent performs **pathfinding using the A-star algorithm** to reach the relevant target.

The entire decision-making mechanism is implemented using a **Finite State Machine (FSM)** design pattern.

---

## Team Cooperation
- Characters operate as a coordinated team.
- The **Support Unit (Carrier)**:
  - Does not participate in combat.
  - Transfers ammunition and medical supplies between team members.
- If a character requires a resource (ammo or health), it sends a **request** to the Support Unit.

---

## Combat System
- When enemy characters occupy the **same room**, combat is enabled.
- Two types of attacks are supported:
  - **Single bullet shooting**
  - **Grenade throwing**
- Friendly fire is disabled — characters cannot damage teammates.

---

## Dynamic Safety Map
- Character movement is influenced by a **dynamic safety map**.
- The safety map changes whenever a character moves within a combat room.
- Characters adjust their movement paths in real time according to:
  - Enemy positions
  - Available cover
  - Current danger zones

For simplicity, room transitions are used only for movement, not for combat.

---

## Technologies & Concepts Used
- C++
- OpenGL / Graphics Rendering
- Artificial Intelligence
- Finite State Machines (FSM)
- A* Pathfinding Algorithm
- Team-Based AI Coordination
- Dynamic Environment Modeling

---

## Educational Purpose
This project demonstrates:
- Real-time AI decision making
- Multi-agent cooperation
- Tactical navigation in a hostile environment
- Adaptive behavior based on character state and environment
