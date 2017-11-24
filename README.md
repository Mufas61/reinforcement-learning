# ReinforcementLearning

This program shows how an Q-learning algorithm works in a maze and has a maze-editor to create own test senarios.

Developed with the help of an already existing program: http://www.cs.cmu.edu/~awm/rlsim/

## Q-Learning 

**Q(s,a) = Q(s,a) + &alpha; [r + &gamma; * Q(s',a') - Q(s,a)]**

Legend:
* s - state
* a - action
* s'/a' - next state/action
* r - reward/penalty
* &gamma; - discounting rate
* &alpha; - learning rate
