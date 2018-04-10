# ReinforcementLearning

This program shows how an Q-learning algorithm works in a maze and has a maze-editor to create own test senarios.

Developed with the help of an already existing program: http://www.cs.cmu.edu/~awm/rlsim/

## How to start the application

The application comes with a gradle wrapper, which loads all dependencies and runs the application with the command:
> sh gradlew run


## Q-Learning 

**Q(s,a) = Q(s,a) + &alpha; [r + &gamma; * Q(s',a') - Q(s,a)]**

Legend:
* s - state
* a - action
* s'/a' - next state/action
* r - reward/penalty
* &gamma; - discounting rate
* &alpha; - learning rate
