
---
title: "Duck"
author: Antoine Gréa

style: note
 
---

               __
             <(o )___,  <_ QUACK !
              ( ._> /
          ~~~~~~~~~~~~~~~~~~~~~~~~~°o

Simon (Starting 12017-08-09)

What am I gonna call you? I haven't done it before, but I think this version needs more rigor. I will document the important elements every day. Quilter made me erase all the previous elements. So I set Scratch to a similar display. Actually, it's much better I'm stupid enough not to have done this sooner.

So what do I call you? Do I write in French or English? Is this for me or the others?

So I decide:

* Your name will be Simon (my duck of a cousin is nicknamed Simo)
* I'll do it in English
So there will be no sharing problems.

We're gonna do some sections here!

# Global

**Note** : When adding incoherent fluents in a `State` there should be an `Exception` containing the conflicting fluents. This would allow for removal and retry to add observations into the model dynamicaly.

# Todos

* Redact !

# Daily

## Color

### 12017-08-09

Dear Simon, today I need to solve my problem. It seems probably impossible. Let's try another approach. I do a simpler domain.
I choose a very very *very* **very** simple kitchen domain :

		boilWater eff (waterBoiled);

		makeCovfefe pre (waterBoiled);
		makeCovfefe eff (covfefeMade);

		goal pre (covfefeMade);

It works !

		run:
		[INFO]	 Opening the world...
		[INFO]	 Compiling...
		[INFO]	 It begins !
		[INFO]	 We have :
			boilWater , makeCovfefe , goal 
		[DEBUG]	 Agenda covfefeMade  -> goal 
		[DEBUG]	 Resolving covfefeMade  -> goal 
		[DEBUG]	 Resolvers [makeCovfefe  =[covfefeMade ]=> goal ]
		[DEBUG]	 Trying with makeCovfefe  =[covfefeMade ]=> goal 
		[DEBUG]	 Agenda waterBoiled  -> makeCovfefe 
		[DEBUG]	 Resolving waterBoiled  -> makeCovfefe 
		[DEBUG]	 Resolvers [boilWater  =[waterBoiled ]=> makeCovfefe ]
		[DEBUG]	 Trying with boilWater  =[waterBoiled ]=> makeCovfefe 
		[INFO]	 Success !
		[INFO]	 	makeCovfefe  =[covfefeMade ]=> goal 
				init  =[]=> goal 
				boilWater  =[waterBoiled ]=> makeCovfefe 
	
		BUILD SUCCESSFUL (total time: 0 second

Now let's give it a choice : It behaves good when grounded, baaad planner.

Time for some variables. First straightforward.

		(boiled(_)) :: Fluent;
		boil(x) eff (boiled(x));
		make(x) pre (boiled(water), ~(ready(x)));
		make(x) eff (ready(x));

		init eff (~(ready(covfefe)));
		goal pre (ready(covfefe));

The thing didn't understand negation… no wonders it doesn't work !

NEGATION ! ARGGHHHJFJEFFC rsvgl ! Well now I know why it wasn't working :

		public boolean contradicts(WorldFluent counter) { [...]
			return matches && (negative ^ counter.negative); 
			// 				 !(negative… ) before…
		}

Added a small language section at the top of the domain to make it work :

		# ; // Language Operator and End Of Statement
		:: :: Property(Entity, Type);
		(~, !, _, *, ?)::Quantifier;
		#;

### 12017-08-10

Today I shall recieve a new computer and I might need to go out to send and recieve more things. I may need to cut it short, Simon.
The work of yesterday shown fantastic results so far. I commited the changes as it seems stable for me. Further experiments are needed before we assume anything. I will contact the others once it is done.

Simon ! Let's do it !

Looking up kitchen dommain : 
> 	pddl.kitchen : Your domain is available!
I will remember this once I publish. Just to confuse some others in the planning community

		use(appliance) eff (used(appliance));
		take(obj) eff (taken(obj));

		boil(liquid) pre (used(boiler), ~(boiled(liquid)));
		boil(liquid) eff (boiled(liquid));

		make(x) pre (boiled(water), ~(ready(x)));
		make(x) eff (ready(x));

		init eff (~(ready(covfefe)), ~(boiled(water)));
		goal pre (ready(covfefe));


I need better logic fo quantifiers. Of course I can't simply do them all and not 100% correct anyway.
I should start with identifying the different values and cases :

		boiled(_) :: Fluent
		~(boiled(*)) -> boiled(~)

This means theres quite the number of possible values… If I knew better I would simply de the whole formalisation to make sure the theory is correct.
Also Simon as you can see I haven't recieved the new laptop. My phone derped and the SSD isn't arrived yet anyway. So all this will happen tommorow.

I should do something to negate the quantifiers :
		~(~) : (*);
		~(!) : (+); //Undefined since it means not unique ? At  least one ?
		~(_) : (_); //Doesn't exists
		~(*) : (~); 

When negative a fluent should negate its filter ? or quantifier ? or both ?
I don't really know Simon. This is all very confusing. I really must formalise this. But at the same time I haven't it (time).
I think the best is to take quantifiers from the starting definition I did :

		// # Quantifiers
		false:~(true);	#~x# : ~(x);	// Negative
		~(x):_(x,0);	#_x# : _(x);	// Existential
		!(x):_(x,1);	#!x# : !(x);	// Uniqueness
		+(x):_(x,+);	#+x# : +(x);	// Plurial
		*(x):_(x,*);	#*x# : *(x);	// Universal
		?(x):x;			#?x# : ?(x);	// Solution

I'm really not happy with the definition of the plurial and universal quantifier… Like really not.
Or maybe I could do `+(x):_(x,~(0,1));` and `*(x):~(~(x));`. I should really read more about it and build my own theory of quantifiers.
I just searched and what is called "Plural quantification" is so much more than what I said… I can't handle it right now.

I know Simon, I am getting ahead of myself and I am getting distracted. Like a lot.
What I need is just within the logic of planning. Let's just do it simple for now shall we ? We'll get all the time to do things properly once this is set and done. I bet you really think I am stupid Simon.

So… in our case : Each property has its own quantifier for the arrity of its parameters. When unifying and contradicting fluents they can also contain quantifiers. I did it with a filter quantifier. It is computed as being the conjonction of all arrities for the property. I think it isn't correct but whatever for now. (BTW: PDDL doesn't care either and just has something like 2 types of property…)
So far I have this logic :

		contradicts(counter) { […]
			switch (filter) {
				case NULL:
					return true;
				case UNIQ:
					matches &= s.unify(source, target) != null;
				case SMTH:
					continue;
				case ALL:
					return false;
			}
		}

But the interesting part isn't here. The thing is that I wrote that ugly code to deal with quantifier within the parameters :

		if (NULL.image().equals(source) || NULL.image().equals(target)) {
			return !(NULL.image().equals(source) &&
					NULL.image().equals(target));
		}

That's the part that needs fixing. It needs to consider :
* Negation
* Universality in parameters
* Crash violently in the other cases because Fù¢K it !

Let's make the truth table and call it a day (_ here really means anything):

| ~ | left  | ~ | right | unify	| contradicts 	|
|---+-------+---+-------+-------+---------------|
| 0 | ~		| 0 | ~		| true	| false			|
| 0 | ~		| 1 | ~		| false	| true			|
| 1 | ~		| 0 | ~		| false	| true			|
| 1 | ~		| 1 | ~		| true	| false			|
| 0 | ~		| 0 | _		| false	| true			|
| 0 | ~		| 1 | _		| true	| false			|
| 1 | ~		| 0 | _		| true	| false			|
| 1 | ~		| 1 | _		| 		| 				|
| 0 | _		| 0 | ~		| 		| 				|
| 0 | _		| 1 | ~		| 		| 				|
| 1 | _		| 0 | ~		| 		| 				|
| 1 | _		| 1 | ~		| 		| 				|
| ? | _		| ? | _		| 	?	| 		?		|
| 0 | ~		| 0 | *		| 		| 				|
| 0 | ~		| 1 | *		| 		| 				|
| 1 | ~		| 0 | *		| 		| 				|
| 1 | ~		| 1 | *		| 		| 				|
| 0 | *		| 0 | ~		| 		| 				|
| 0 | *		| 1 | ~		| 		| 				|
| 1 | *		| 0 | ~		| 		| 				|
| 1 | *		| 1 | ~		| 		| 				|
| 0 | _		| 0 | *		| 		| 				|
| 0 | _		| 1 | *		| 		| 				|
| 1 | _		| 0 | *		| 		| 				|
| 1 | _		| 1 | *		| 		| 				|
| 0 | *		| 0 | _		| 		| 				|
| 0 | *		| 1 | _		| 		| 				|
| 1 | *		| 0 | _		| 		| 				|
| 1 | *		| 1 | _		| 		| 				|
| 0 | *		| 0 | *		| true	| false			|
| 0 | *		| 1 | *		| 		| 				|
| 1 | *		| 0 | *		| 		| 				|
| 1 | *		| 1 | *		| true	| false			|

Jeeez… In binary it would be these variables : `lc, ln, ls, la, rc, rn, rs, ra`
So the simplification in 8 dimmensions… I can draw a 9 dimention monome but that will be complicated (even if quite badass)
I don't have the motivation to do this right now. Let's do it tomorow maybe. Sorry Simon.

### 12017-08-11

Well here we are again. I thought about all this. This is all over my head. I will revert to the usual and do all this with basic ifs.
Let's the struggle begin.

Simon, it's  so complicated. Better go the table way.
Or not !

I simplified the negations in the trivials case (eg. ~(~))
Now I only got a few cases :

		case  matches
		~ / _ (/-)
		_ / ~ (-/)
		* / _ (/+)
		_ / * (+/)
		
### 12017-08-12

Back to the parent's place and it wasn't a very productive day. I am on my way to finally debug quantifiers on a basic level.

I tried to debug but the logic is confusing. Double negations have never been my thing. I will take a break and try to come up with a formula.

### 12017-08-13

I think I found a way : maybe unify already makes half the job and we only need a few simple formulas. Getting to it.
Note : I am still on the Dell because the SSD I ordered end up being stuck in the "exceptionally" closed store. I might have it tomorrow but it won't be operational until Wensday. 

pre/eff			Unifies	Contradicts
---------------+-------+-----------
(~)/(~)  		true	false
(~)/~(~) 		false	true
(~)/(*)  		false	true
(~)/~(*) 		true	false

~(x)/(~)  		true	false
~(x)/~(~) 		false	true
~(x)/(*)  		false	true
~(x)/~(*) 		true	false

(x)/(~)  		false	true
(x)/~(~) 		true	false
(x)/(*)  		true	false
(x)/~(*) 		false	truesensor.state,

(*)/(~)  		false	true
(*)/~(~) 		true	false
(*)/(*)  		true	false
(*)/~(*) 		false	true

Simon, I am lost, I am no idea what I am doing. ! ;_;

~ * n contradicts
0 1 0 0
0 1 1 1
1 0 0 1
1 0 1 0
1 1 0 1
1 1 1 0

Spacial resolusion, IN SPACE !!!!!!
Contradiction :

   ?-------0
  /|      /|
 / |n    / |
1--|----0  |
|  ?~---|--1
| / *   | /
0-------1

		~ AND !n 
		OR
		* AND n AND !~ //Contradicts is true

		!~ OR !* // Will allow matching, else it stays as is

Ok, it works ! On one example. Let's try another one.
... Nope
Unification :

		~ OR *  (probably bad)

Result for contradiction :

		if (nul && !negates || !nul && all && negates) {
			return true;
		}
		
		if(nul && negates) {
			return false;
		}

		switch (filter) {
			case NULL:
				return true;
			case UNIQ:
				if (!nul || !all) {
					matches &= s.unify(source, target) != null;
				}
			case SMTH:
				continue;
			case ALL:
				return false;
		}

It works now ! Tried various combinations. Sending mail about status.

### 12017-08-14

Trying a new domain with variable selection. Of course it fails. The problem is in the threats.
Here is the new domain :

		use(appliance) eff (used(appliance));
		take(obj) eff (taken(obj));
		boil(liquid) pre (used(boiler), ~(boiled(liquid)));
		boil(liquid) eff (boiled(liquid));

		make(x) pre (boiled(water), ~(ready(x)));
		make(x) eff (ready(x));

		makeWith(x, y) pre (boiled(water), ~(ready(x)), taken(y));
		makeWith(x, y) eff (ready(x), added(y));

		init eff (~(ready(*)), boiled(~));
		goal pre (ready(covfefe), added(sugar));

So here the planner selects `makeWith(covfefe, y)` for the first flaw then later `makeWith(covfefe, sugar)`
First problem is that instanciating a step doesn't replace it when applying.
Second problem, linked to the first one, is that it creates a threat :

		[DEBUG]	 Resolving init  =[~(ready(covfefe ) ) ]=> makeWith(covfefe , sugar )  =/= makeWith(covfefe , y ) 
		[DEBUG]	 Resolvers [makeWith(covfefe , sugar )  =[null]=> makeWith(covfefe , y ) ]
		[DEBUG]	 Trying with makeWith(covfefe , sugar )  =[null]=> makeWith(covfefe , y ) 
		[WARN]	 makeWith(covfefe , sugar )  =[null]=> makeWith(covfefe , y )  isn't appliable !
		[WARN]	 No suitable resolver for init  =[~(ready(covfefe ) ) ]=> 

Let's solve the first problem...
The thing is that the resolver must check the variable binding in **the entire tree of the instanciating step**.
That's a lot but if we can't do otherwise... Then it must actually replace the action and all the causal links since they are unmodifiable...
And that's not even the worst since all resolver **MUST BE REVERSIBLE**. That's a fundamental guarantee.

So, after reviewing the code I don't think I can simply do a smart solution that would be simple enough within the time I got.
So I go the ugly way and just keep an instance of the whole plan in memory wich isn't so bad. And I still need to check and replace in the selected step's tree all variables present in the unification that I will probably need to retrieve too. I don't think this will be pretty. Plus I still got the problem with threats and the search in variables that can solve them. The worse is that I need to search for this at the time I search for the threat's resolver. This will be ugly but at least the whole keeping the reversal possible thing will be 100% guaranteed.

First step is to do the plan save thing, then the tree check and replacing. Test all that, fail a few time waste the 2 remaining weeks and see where we're at.
I shouldn't have reported on my measly victory yesterday because I knew there would be a huge drawback riiight ahead.

That's all for today Simon, and probably tomorrow. I need to be able to code without screwing everything up again.

### 12017-08-18

I know, I know Simon. I had… some rough times. I may not be quite ready yet.
I read about the problem on Sabbarao PPT. This thing is hideous and so often wrong and confusing. It barely explains the things and doesn't give solutions. It is also quite old and fashioned for theorical planning and "just thinkin' bout' it right ?".
New articles seems to give answers to my problems and knowing that I can probably do nothing with it makes me quite upset.

So I need to handle this on my own like *absolutely* everything ever… 
There's two probable problems : Subgoals must try to unify the operator first. So it gives two resolvers : the standard one and the instanciated one. Threats must also give a "separation" soltion by trying to meet variable bindings ? I don't even know how to start with that. I would do a merge like in the subgoal ? and then maybe a guess at separating variables and operators until it gets solved. I have no idea…

My install is ready now at last. Things seems to work right, I'll check once I start trying ducktaping all this.
Mail time…
Or not

### 12017-08-19

Simon, I don't know even how to start working. I'll try today.

First : resolvers are wrong. Super wrong. I need several kind of resolvers. I did an Interface for now I plan adding the new one and maybe refactoring the old one. 
I just had an idea. I did a class to revert a single link flawlessly (except if actions are changed externally and stuff). Maybe I can try to do one for several links. Or I can just create multiple ones in the resolver… duh.

Wired stuff for future usages. Need to finish threats but I wish to do the new resolver type first.

### 12017-08-20

Here we go again Simon.
Starting the work on trees. First I need to know where to.

So I need a helper class like LinkReverter. It must be given a list of links. It will create a LinkReverter for each link. Then will revert them all in its revert operation.

So the new resolver will have : a function to get the curent instanciation tree given two actions. The two actions are combined to get the most grounding out of them. Then the function either returns the tree or or null if constraints are violated.
The appliable function will use that function and return false on null and store the tree otherwise.
The apply function will take the tree and the unification map, create a reverter on it, then instanciate the actions, and create a reverter on the new tree while creating it.
Maybe we don't even need a new reverter, we can directly store a set of LinkReverter.

I will code that but we also need to know when to use that resolver. Is it on the existing flaws ? Do we need a new flaw. I code that then I will need to draw or examine an example with problems (or just ducktape my way through)

FUTURE : When doing the abstract flaw, we use reverters with the edge set of the new plan. Then we insert it checking for constraint violations and cycles.

### 12017-08-21

Here I wake up ! (a bit). Ok so today our lesson starts with an upside down tree. The resolver for instanciation will take no risks (for now). It will keep the contributions of the incomplete actions but not its need. I will probably need orphan flaws down the line to clean up the solution (TODO before abstract flaws, they are the very last).

I did the tree :

		private void computeTree() {
			for (F fluent : adding.eff) { //Only effects since the tree is upside down
				unification.putAll(existing.eff.unify(fluent));
			}
			List<E> newParameters = existing.parameterize(unification);
			if (newParameters == null || existing.parameters.equals(newParameters)) {
				return;
			}

			Stack<Action<F, E>> open = new Stack<>();
			open.push(existing);
			while (!open.empty()) {
				Action<F, E> current = open.pop();

				for (CausalLink link : plan.outgoingEdgesOf(current)) {
					State instanciate = link.causes.instanciate(unification);
					if (!instanciate.equals(link.causes)) {
						tree.add(link);
						open.push(link.target());
					}
				}
			}

		}

(I just noticed that I grew lazy and didn't used much styling and such recently Simon)
For appliability it's quite simple :

		for (CausalLink link : tree) {
			State causes = link.causes.instanciate(unification);
			if (!causes.coherent()) {
				return false;
			}
			Action<F, E> source = link.source().instanciate(unification);
			Action<F, E> target = link.target().instanciate(unification);
			if (plan.reachable(link.target(), adding)) {
				return false;
			}
			newTree.add(new CausalLink(source, target, causes));
		}
		return true;

And to apply :

		if (unification.isEmpty()) {
			return;
		}
		for (CausalLink link : tree) {
			reverters.add(new LinkReverter(plan, link));
			plan.removeEdge(link);
		}
		for (CausalLink link : newTree) {
			reverters.add(new LinkReverter(plan, link));
			plan.addEdge(link);
		}


Neato !

### 12017-08-22

I had such a headache today Simon. I'm trying to complete things in the code but most of the work now is in the adding flaws (or integration in existing ones).
And the test… the freakin' tests where ofc nothing will ever work right.

((Rediscovering lost IM songs, so good))

### 12017-08-23

I had an idea Simon. I will put the variable "merging" in the subgoal as an additional resolver so that it can just be preffered later eventually.
I did that and now the subgoals don't get deleted once already solved.

### 12017-08-24

Ehlo Simon. I need to make the thing recognize when to discard flaws now. In order to do that the resolver will contain the LinkReverters and give them to the function that invalidate flaws. 
Changed the name of LinkReverter to Change. I integrated them everywhere, I only need to put that in application within the old formulas.

All changes done. Tested on the example and it works flawlessly. Don't put your hopes up Simon, this is barely preliminary. I still need to test on other variations and then on blockworld. That and Debug a lot. It is getting better now.
*2 seconds later*
Hummm, I wander what would happen if I changed juuust a tiny bit :
		goal pre (ready(covfefe), added(sugar), added(cream));
*Enter*
		[DEBUG]	 Resolving init  =[~(ready(covfefe ) ) ]=> makeWith(covfefe , sugar )  # makeWith(x , cream ) 
		[DEBUG]	 Resolvers [makeWith(covfefe , sugar )  =[null]=> makeWith(x , cream ) ]
		[DEBUG]	 Trying with makeWith(covfefe , sugar )  =[null]=> makeWith(x , cream ) 
		Exception in thread "main" java.lang.NullPointerException
			at io.genn.color.planning.domain.State.unifies(State.java:96)

*Explosion sound* Uh Oh…

It was just a null check :

``` Bind.java
		return new State<>(fluent == null ? list() : list(fluent));
```
It works ! Just posible optimisations on instanciation but I don't even consider it at this point.
SIMON !!!! SLKfnrpb, sfn 
I profiled it and it took under 2ms… 		Oh forget it…
It actually takes 40ms… aaannnd I got a monster PC. Here are some notable culprit :
		io.genn.color.planning.algorithm.pop.Pop.refine ()	17,0 ms (43,1 %)	39,3 ms (29,6 %)	5 #Artifact
		io.genn.color.planning.algorithm.pop.flaws.PopSubGoal.resolvers ()	2,37 ms (6 %)	8,33 ms (6,3 %)	4
		io.genn.color.planning.algorithm.pop.flaws.PopSubGoal.fill(io.genn.color.planning.domain.Action, java.util.Deque)	2054	2049	5216	5207	18
		io.genn.color.planning.domain.Action.provide(io.genn.color.planning.domain.fluents.Fluent)	1902	1894	3656	3653	15
		io.genn.color.planning.domain.Action.instanciate(java.util.Map)	1194	1196	1194	1196	9
		me.grea.antoine.utils.graph.Graph.removeEdge(me.grea.antoine.utils.graph.Edge)	1149	1150	1149	1150	2

It just takes too much time searching for resolvers… It can be improved later.

I just tried block world but it doesn't work with two noticeable problems : 
* Repeatitive flaws in Agenda (didn't we fixed that before ?) and 
* attemps at creating a cycle that shouldn't ever happen.


### 12017-08-25

I am near to debug my way to multiple domains. Or not…

Doesn't work : 
* Instanciate resolver creates cycles…
* The planning goes haywire with variables instead of instances (heuristic needed ?)
* Quantifiers doesn't work on some fluents now…

Each problem is at least a day of work… I am desperate Simon.


### 12017-08-28

Oh god ! I have been away this long ? I'm sorry Simon. I guess I am good at fleeing my responsabilities…

I will try to solve the quatifier problem first. I need to debug on simple examples or it gets imposible to do so. I need to tell that I am still not quite good mentally and that focusing is super hard…

Got issue with Fing nextcloud. Fixed this morning… Also Lyon2 changes in schedules… OFC no real work yet.

### 12017-08-29

Here we are, end of the road… I am pathetic aren't I ? Like a lot. I can't type a single line of code in days for no real reasons. I can build up all the excuses in the world I am just wandering around to avoid confrontaition…

Ok back to simple kitchen… I can't handle block world whatever I do anyway. 

Nice idea: Quantifiers and other notions will have a dynamic definition that will be a predicate they will be holding.

Also the problem here is that there is no loop on the parameters and the first one that succeed or fail will stop everything.


### 12017-08-30

I must quit. Just paper hopefully. I need a bit of time to do that.
Also tied the SMTP I have access to.

So I did a logistic domain :

		drive(t, from, to, c) pre (t @ from, from city c, to city c);
		drive(t, from, to, c) eff (t ~(@) from, t @ to);

		[…]

		goal pre (truckRoger @ doua);

First problem is that the algorithm selects `t ~(@) from` and stops as it contradicts. I changed the return with a continue and it works now.

Second problem is with the variable unification. Like the planner searches for a fluent that shouldn't be searched for :

		[DEBUG]	 Trying with drive(truckRoger , from , doua , c )  =[truckRoger @ doua ]=> goal 
		[DEBUG]	 Agenda from city c  -> drive(truckRoger , from , doua , c ) 
			doua city c  -> drive(truckRoger , from , doua , c ) 
			truckRoger @ from  -> drive(truckRoger , from , doua , c ) 
		[DEBUG]	 Resolving from city c  -> drive(truckRoger , from , doua , c ) 
		[DEBUG]	 Resolvers []
		[WARN]	 No suitable resolver for from city c  -> drive(truckRoger , from , doua , c )

So I put that in constraints instead. it pretty much gets ignored ofc…
A trick for variables : put them in parameter of the property so that they are forced to be variables.

TODO in world : add the solution quantifier to force an entity as a variable. Is still usefull in querries tho. To see.

I just did so artificially using `?(x)` in fluents and `init eff( ?(*));`.

Now it goes crazy ofc… At least it converges.

Ok, so I cut down the complexity and saw some peculiar problems. One of which is that the process can't retro instanciate a step to fit an instanciated step providing grounded constants. 

		[DEBUG]	 Resolving truckRoger @ from  -> drive(truckRoger , from , doua ) 

That can't match `init eff(truckRoger @ saxe);` because saxe isn't a variable…

### 12017-08-31

Last day of my summer. I went back to work Simon !
And relatively early too. 

But since I haven't registered the MAC address to SOS Info I can't connect… hopefully Judas is here plus some spoofing. 
Problems with bluetooth headset too. Tried the mouse on bluetooth, pair but won't work… Or not ? Just did it ! Everything set but it is a bit late…

Starting with the under subgoal.

The thing is that the unification of two fluents give one unification, but the unification of states gives several unifications.
We can unify a grounded fluent with a lifted state (downward) or a lifted fluent with a grounded state (upward).
The last case is what happens with lifted steps and grounded initial state.
When several unifications are possible we must merge the compatible ones ? Since this is optimization, I will first list all possible unifications.

It looks like working but the result is a mess. I did a unification oriented search for subgoals and the such.
And time to go to the meeting !
See ya this weekend Simon.

### 12017-09-06

Wow. Well. I had a busy time with meetings and class. Meetings until Saturday then install of Office and VM for on Monday finnally install it on my native.
Now headache and . where was I ?

Added quantifier `?` to a new ignore list. Checked regular matching, seems OK.
Problem with a loop not being detected, I suspect word messing it up again so I fixed it by modifying the way hashcodes and equals behave in Fluents.

I think I did it with mirror operations on States. The problem now is that we also need to instanciate the fluent. It doesn't seems this simple ofc.
Nope, just needed to instanciate the needer too. It works !

Duplicates are gone for now. The problem is in the resolver generation that modifying the fluent causes problems for related flaws.
Apparently a flaw is getting fixed with another link or something ?

		[DEBUG]	 Agenda truckRoger @ from  -> drive(truckRoger , from , doua ) 
		[DEBUG]	 Resolving truckRoger @ from  -> drive(truckRoger , from , doua ) 
		[DEBUG]	 Resolvers [init  =[truckRoger @ saxe ]=> drive(truckRoger , saxe , doua ) , drive(truckRoger , saxe , doua )  =[truckRoger @ doua ]=> null, fly(truckRoger , flyFrom , from )  =[truckRoger @ from ]=> drive(truckRoger , from , doua ) ]
		[DEBUG]	 Trying with init  =[truckRoger @ saxe ]=> drive(truckRoger , saxe , doua ) 

The thing is that the link is with an instanciated version, so in this case we should force the Replacement ?
There's problems in the resolver generation still. Theres a worrying null in there along with no replacement.

### 12017-09-08

Yesterday I had some work to do for Samir along with classes. Today I am chilling and getting through those issues.
The problem is in the filling of the resolver OFC. Redoing it. First I need to check the given unification…

Ok so the logic is that when one of the action involved is changed we must change it in the plan too. Modifications will need to be made to Instanciate.
There's no need to add the second resolver of just the link because if the search fails it wouldn't have had a solution anyway ? (let's do that for now)

### 12017-09-09

Empty day… Sorry Simon. Computer acted up and I couldn't focus on things at all…

### 12017-09-11

Guess what happened yesterday ? Yes ! Me being the worst again. (also ISP and registar changes)

So today I start the work on the `Instanciation` resolver first. I change the existing/adding couple by a `Map`. 

Code in place. Like described earlier, on change we use `Instanciation` resolver otherwise the `Bind resolver.
Test time !

So the problem now is that the initial step isn't considered or something. Also phantom steps remains in the plan.

		[DEBUG]	 Agenda truckRoger @ saxe  -> drive(truckRoger , saxe , doua ) 
			truckRoger @ from  -> drive(truckRoger , from , doua ) 
		[DEBUG]	 Resolving truckRoger @ saxe  -> drive(truckRoger , saxe , doua ) 
		[DEBUG]	 Resolvers [{fly(a , flyFrom , flyTo ) =fly(truckRoger , flyFrom , saxe ) }, {drive(t , from , to ) =drive(truckRoger , from , saxe ) }]

I removed orphan steps from related flaws (might happen with threats tho). Now I just need to fix the subgoal solving to include init and the such.

### 12017-09-14

I had more trouble with teaching than predicted… I need to find ways to be more productive while being active in class and such…
Anyway, no need to spend too much time on this Simon.

And I did all the class preparation but no work…

### 12017-09-15
service fail2ban restart
I'm quite ashamed Simon. I should have finished everything by now…

The code is such a mess ! There's bugs *everywhere*. The instanciation resolver decides to not create the links and there's stray actions staying around…

### 12017-09-16

Here we are again… Getting busy with everything but my work OFC…

### 12017-09-17

AAAARRRGGGGH ! Code attack ! aaaaaaaaaaaaaaaaaaaaaaa
What am I doing ? No idea.

I don't even know where the problem is. 

First siplify. The problem has 4 cases depending which actions are grounded : needer and provider.
I need to redo the instanciation and subgoal again.

### 12017-09-18

Blurg. Well hello there. I still haven't really pushed the problem…

I stripped Intanciate of all binding related code and made a composite resolver.

It worked ? Woah, I really should try the dumbest solution first.

		[INFO]	 Success !
		[INFO]	 	init  =[truckRoger @ saxe ]=> drive(truckRoger , saxe , doua ) 
					init  =[]=> goal

Problem goal isn't provided by drive ? That means the instanciation resolver isn't doing it's job. The subgoal related is not updated when steps are removed (even if that shouldn't affect existing plans.

Trees are off and everything explodes as soon as they are populated… I knew it wasn't over.
But it was ! The problem was excessive checks making the plans to fail.

		[INFO]	 Success !
		[INFO]	 	drive(truckRoger , saxe , doua )  =[truckRoger @ doua ]=> goal 
				init  =[truckMarcel @ maginot ]=> drive(truckMarcel , maginot , doua ) 
				init  =[truckRoger @ saxe ]=> drive(truckRoger , saxe , doua ) 
				drive(truckMarcel , maginot , doua )  =[truckMarcel @ doua ]=> goal 
				init  =[]=> goal 
				drive(truckRoger , doua , saxe )  =[truckRoger @ saxe ]=> drive(truckRoger , saxe , doua ) 
				fly(truckRoger , doua , saxe )  =[truckRoger @ saxe ]=> drive(truckRoger , saxe , doua ) 
				fly(truckMarcel , maginot , doua )  =[truckMarcel @ doua ]=> goal 

The problem now is to find where all the extra junk comes from…

### 12017-09-19

My biggest problem really is myself Simon …

I barely tried to day and even that was too much to ask.

### 12017-09-21

I have trouble focussing again. I need to learn to sort my thoughts better…

|	||
||	|_

Sorry Simon but that is super hard not to get distracted.

Heisenbug… I hate those… SO MUCH !
In debug mode the hashcode changes for some reasons and the `Problem` hashcode causes problems or something ? No more heisenbug…

Maybe there is a bug in the way changes are handled. Investigating.

There was duplicates in agenda…

There's a bug in changes but I don't have time to search more for it.

### 12017-09-24

There's even more work for teaching that I thought…

		[
			[fly(truckRoger , flyFrom , doua )  ± fly(truckRoger , saxe , doua ) , init  =[truckRoger @ saxe ]=> fly(truckRoger , saxe , doua ) ], 
			fly(truckRoger , flyFrom , doua )  =[truckRoger @ flyFrom ]=> fly(truckRoger , flyFrom , doua ) , 
			fly(a , flyFrom , flyTo )  =[truckRoger @ flyFrom ]=> fly(truckRoger , flyFrom , doua ) , 
			[drive(t , from , to )  ± drive(truckRoger , from , flyFrom ) , drive(truckRoger , from , flyFrom )  =[truckRoger @ flyFrom ]=> fly(truckRoger , flyFrom , doua ) ]
		]

### 12017-09-26

Everything is broken. I need to redo all.

I need to redo all. I need a resolver that works with instanciation. I  need a search function that does too.

When we search resolvers in an action, we must first find the unifications. A unifcation is a set of variable instanciation that are compatible. 

### 12017-10-10

Holly molly… That was a gap. I did teaching all that time and the such.
The good thing is that I have some fresh mind over this. I need to get back into it.

// Teaching again

Did some work on the resolver !

### 12017-10-11

Guess what Simon ?
Samir asked me to replace him to watch an exam because he's sick… I will need to try to have some fast paced progress hopefully.

I may have made a mistake about if reusing actions can be optional. If the needer *needs* to be instanciated, there's no questions asked. The provider *can* be reused tho. 
I scold the students about taking notes; "Document everything !"; I'm not even doing that myself.
I have so often thought about failing and giving up that I feel like I gave up already…

Let's find a better method of work. 
[None]

Well then ? If the resolver needs to add A -> B then B needs to be replaced by its grounded version (if any). The catch is that there's no need to replace A. It is certainly better for plan quality, but it **will break completeness** !

TODO : Instanciate tool with search and replace.

### 12017-10-12

I got not much time.
Did the code for the replacement.

### 12017-10-13

I think I start managing things a bit better. I had a busy day but I manage to work a little anyway.

### 12017-10-16

Hummm… You know what Simon, I'm still bad at being consistent at working. Feeling a bit sick today, probably polution.

Ok so it looks like it works quite well so far. Problem is that init isn't considered for now… 
Problem is in the eff.provide method. No it was a missuse of obtain. That will need a formal definition.

Now the problem is too much deletion ? No its not enough deletion apparently. Back to the same issue…

		drive(truckRoger , saxe , doua )  =[truckRoger @ doua ]=> goal 
		init  =[truckRoger @ saxe ]=> drive(truckRoger , saxe , doua ) 
		fly(truckRoger , saxe , doua )  =[truckRoger @ doua ]=> goal //NO !
		init  =[]=> goal 

Ok so change needs a real debug :

		[WARN]	 Reverting the application of init  =[truckRoger @ saxe ]=> fly(truckRoger , saxe , doua ) 
		[VERB]	 Plan : 	fly(truckRoger , saxe , doua )  =[truckRoger @ doua ]=> goal 
							init  =[]=> goal 
							fly(truckRoger , flyFrom , doua )  =[truckRoger @ doua ]=> goal 

The problem is that we do this :

		public void undo() {
			if (link != null)
				plan.addEdge(link);

But we have a constructor with a link in parameter without testing if it exists already !

		this.link = plan.containsEdge(link) ? link : null;

Now we got cycles ! Splendid…

### 12017-10-24

I switched computer. Everything is set up now. 

I checked my `Graph` library for a bug but it is still rock solid. I added more constraint on the reachability check in the `appliable` method, and…

IT WORKS !!!!!!!!

		[INFO]	 Success !
		[INFO]	 	drive(truckRoger , saxe , doua )  =[truckRoger @ doua ]=> goal 
					init  =[truckRoger @ saxe ]=> drive(truckRoger , saxe , doua ) 
					init  =[]=> goal 

Works partially on the other one tho… It should be avoided to put quantifiers into unification like this :

		[VERB]	 Unifications of init  to solve ~(ready(x ) )  -> makeWith(x , cream )  are [{}, {x =* }]
		[DEBUG]	 Resolvers [init  =[~(ready(x ) ) ]=> makeWith(x , cream ) , init  =[~(ready(* ) ) ]=> makeWith(* , cream ) ]

And I solved a problem with `Instanciation` being built with the wrong action not acting on replacements. 

Everything works wonderfully ! It's mail time.

### 12017-10-28

I will work on the kitchen side of things because it seems safer and I got all that explanation work done on similar examples. 

### 12017-10-29

I am procrastinating but I guess I needed the rest a lot.

### 12017-10-31

			 )\
		  .'`--`'.
		 /  ^  ^  \
		 \ \/\/\/ /
		  '------'
_Spooky scary skelettons, send shivers down your planner._

Time to make a spoopy domain. Or not. Just making the overdue multi layered version of kitchen as described in my presentation for the PhD commity.

Did so and it works in the linear fashion.

		init  =[sugar ~(in ) cup ]=> pour(sugar , cup ) 
		take(cup )  =[taken(cup ) ]=> pour(tea , cup ) 
		init  =[~(ready(tea ) ) ]=> infuse(tea ) 
		init  =[tea ~(in ) cup ]=> pour(tea , cup ) 
		pour(tea , cup )  =[tea in cup ]=> infuse(tea ) 
		take(tea )  =[taken(tea ) ]=> infuse(tea ) 
		boil(water )  =[boiled(water ) ]=> infuse(tea ) 
		take(cup )  =[taken(cup ) ]=> pour(sugar , cup ) 
		pour(sugar , cup )  =[sugar in cup ]=> goal 
		init  =[~(taken(sugar ) ) ]=> take(sugar ) 
		boil(tea )  =[boiled(tea ) ]=> infuse(tea ) 
		take(tea )  =[taken(tea ) ]=> pour(tea , cup ) 
		take(sugar )  =[taken(sugar ) ]=> pour(sugar , cup ) 
		init  =[water ~(in ) cup ]=> pour(water , cup ) 
		pour(water , cup )  =[water in cup ]=> infuse(tea ) 
		init  =[~(taken(cup ) ) ]=> take(cup ) 
		take(cup )  =[taken(cup ) ]=> pour(water , cup ) 
		infuse(tea )  =[ready(tea ) ]=> goal 
		init  =[~(boiled(tea ) ) ]=> boil(tea ) 
		init  =[~(taken(tea ) ) ]=> take(tea ) 
		init  =[]=> goal 
		init  =[~(boiled(water ) ) ]=> boil(water ) 
: My beautiful plan

### 12017-11-01

_It's Christmas ! Or is it ?_

About to make that abstract action.

**Note** : When adding incoherent fluents in a `State` there should be an `Exception` containing the conflicting fluents. This would allow for removal and retry to add observations into the model dynamicaly.

I need to populate the effects and preconditions, and compute the abstraction level. Then make the abstraction flaw.

I need to create action start and end or find a way to reuse goal and init. The problem is that start/end should have an action in parameters and still be instanciable but not init and goal.

### 12017-11-02

Let's go !

Plans are parsed. The problem with init and goal doesn't occur so far. It will have a problem with subgoals and such as the init and goal flags are set and may prevent it to connect to other actions.

The problem turned out to be the opposite as it is not storring and retrieving the initial action… I may need to make a kind of caching mechanism for actions.

I did the cache + level things.

**TODO** : Restrain the subgoals to actions of the current level.

I need a new algorithm ! At last I can do that. I need to think about the regular HTN too for tests.

Also I need a name for it. Color is nice but I wish I can find a new name for it with anagram/acronym/palindrom and clever stuff.

Propositions :
- RATIO	  	ReAl Time Incomplete Order
- POPSTAR 	Partial Order Planning Satisfying Timed Arbitrary Reset
- PORT	  	Partial Order Real Time
- HARP	  	Hierarchical AbstRact Planner
* HEART	  	HiErarchical Abstract Real Time
- RETAPOP  	REal Time Abstract Partial Order Planning
* POPTART	Partial Order Planning To Abstract in Real Time
- POPSICLES	Partial Order Planning, Semantics In Constrained Linear Execution Situations
* RATAPLAN	ReAl Time Abstract PLANner
* ABORT		Abstraction Based On Real Time

ABORT : Abstraction Based On Real Time in hierarchical partial order planning.
HEARTPOP : HiErarchical, Abstract, Real Time, Partial Order Planning

Problem now : when do I stop with POP ? What are the limits ?
After reviewing my ideas it seems more complicated than I thought :

I need to have a list of plans as I do the planning… Also I need to rethink most of POP… And ofc the flaws and stuff.

### 12017-11-03

I got my computer back ! Darius is ready to go !

I need to separate the definition of the problem and the solution. Or even better the way of specifying the problem must be implementation dependent.

I am sorry for the lost time but :
>	_An army officer fights a tornado full of sharks while shopping._ - Story plot generator

Also,
**ABORT** : Abstract Building Operating in Real Time as a refinement strategy for hierarchical partial order planning.

Refactored everything. I'm gonna try to get going.

Sawasdee, and, Futura Md BT, Graphik LC Bold, Myriad Bold, Ubuntu

Ubuntu is the best for the logo of abort.

### 12017-11-04

Ehlo there. I did a bit of shopping so I am late today. 
Getting pro out of procrastinating…

First I need to make a POP that goes only on actions of the current level. Then it must fail.

I got it I think. Now I need it to prioritize all composite action by level.

### 12017-11-08

Teaching and procrastination got in my way again. I think I need to recode POP as a best effort algorithm. The aim is to fix as many flaws as possible in a particular setting.

I thing I should go pre redaction and do the algorithm along with the completion proof.

### 12017-11-10

I installed pandoc and LuaTeX to redact stuff. Everything is up to date and I updated pancake for everything + pushed to github.

ABORT uses a refinement based approach. It builds a partially ordered plan by adding and removing causal links. This is the same approach as the popular POP algorithm. There are a few difference with the way the algorithm is done in classical works :

* Our planner is lifted at runtime, which means that POP has a different way to handle resolvers as it needs to instanciates the needer and provider steps before inserting the causal link.
* In our work we use a best effort version of POP that doesn't fail if it can't fix a flaw but instead tries to fix as many as possible.

These differences are potentially breaking the soundness and completeness of our algorithm relative to POP.

I think it would be easier to explain quickly what I want here 

We make a version of POP that is 'best effort' and solve all it can whithout failing.

1. We prevent actions lower than the current level and do a best effort run.
2. We allow all actions and plan in classical POP. Abstract flaws are ignored.
3. We take all abstract flaws and propose a method for them. That part needs to explore all solutions with backtracking. 

I think that I should instead focus on doing all this in the agenda. The problem is that if I do otherwise it will break everything.

### 12017-11-16

I tried to come back to it but couldn't.  I thought about it and I see that the agenda needs a redo too. It needs to dictate two important things : 

1. The selection and management of flaws
2. The search of resolvers and their order.

### 12017-11-20

All the teaching is exhausting…

### 12017-11-28

Teaching time is almost over. I will have a bit more time from now on…

I fixed POP after all the refactoring. It works well. I am extending it for Abort.
Abort has most of its code done.

### 12017-11-29

I need to do the resolver of `Abstraction` flaws. Fixed a few things to prevent some errors and doing the new resolver `Expand`.

It looks like it works on some part, namely the next level stuff. There are several problems still : 

* It takes a while to consider make, must be a problem there
* When several abstraction we need to stock the related and invalidated flaws for after the switch.

### 12017-12-03

I have time to work now Simon. I haven't quite been idle this time I thought about all that and I got two problems with solutions I must try today.

1. The flaws needs to be put asside when in related of a `Abstraction` flaw.
2. Methods should be instanciated too.

First is solved.

### 12017-12-04

I missed a class because stupid. 
Second is solved but there's a probable huge problem. The resolution takes a lot of time and seems to turn around on itself… I knew it would failed super hard but still…

I think I should first check the method preconditions and effects guessing.

Alright I reviewed the thing and I think it might be the method building that is screwed up and then triggers tons of unecessary flaws. One way would be to run POP on this but I prefer to not imply the planner this early in the runtime. Here are the culprits :

		[DEBUG]	 Agenda init(make )  =[~(boiled(water ) ) , ~(taken(cup ) ) , ~(taken(tea ) ) ]=> take(tea )  # take(cup ) 
			* init(make )  =[~(boiled(water ) ) , ~(taken(cup ) ) ]=> take(cup )  # boil(water ) 
			* init(make )  =[~(boiled(water ) ) , ~(taken(cup ) ) , ~(taken(tea ) ) ]=> take(tea )  # take(cup ) 
			* init(make )  =[~(boiled(water ) ) , ~(taken(cup ) ) ]=> take(cup )  # take(tea ) 
			* init(make )  =[~(boiled(water ) ) , ~(taken(cup ) ) , ~(taken(tea ) ) ]=> take(tea )  # boil(water ) 
			* ready(tea )  -> goal 
			* infuse(tea )  =[ready(tea ) ]=> goal(make )  # init 

Hey ! Wait ! Those are threats ! So I just need to add the links in the method manually for now.
BUT ! Why is there so many things in those links ?!? I think I found it.
OOOoooo boy ! I am almost there !

or not ? Why is there init(make) in plan of level 1 ? Also why is make instanciated twice ?
The last one was because of the way actions could be selected in the domain and is fixed now.
It seems like it doesn't insert the method properly still…
That is fixed now.
There was a problem on plan copy : reachability was not copied but taken as is so modifications propagated…

Now we have after level change : 

		[DEBUG]	 Agenda ~(ready(tea ) )  -> infuse(tea ) 
			* boiled(water )  -> infuse(tea ) 
			* ~(taken(drink ) )  -> init(make ) 
			* init(make )  =[~(taken(cup ) ) ]=> take(cup )  # take(tea ) 
			* taken(tea )  -> infuse(tea ) 
			* init(make )  =[~(taken(tea ) ) ]=> take(tea )  # take(cup ) 
			* ready(drink )  -> goal(make ) 

Ok so there's a last issue, init(make) doesn't get instanciated for some reason.
It was because I said to cancel instanciation when the parameters don't change…
Here we are :

		[DEBUG]	 Agenda ~(ready(tea ) )  -> infuse(tea ) 
			* boiled(water )  -> infuse(tea ) 
			* init(make )  =[~(taken(cup ) ) ]=> take(cup )  # take(tea ) 
			* taken(tea )  -> infuse(tea ) 
			* init(make )  =[~(taken(tea ) ) ]=> take(tea )  # take(cup ) 

I consider Abort being finished except the few bugs that can arise later like usual.
I did HiPOP too now.

### 12017-12-05

I am starting some reading and getting everything comfortable for working. I recieved my new chair for work and it's great !

Reading papers I am not even sure of where I lay in all these works. I did something too out of the classical works.

I am doing work on Zotero to allow for anotations and the such in a clear and easy manner.
Generating BibTeX keys with the following format :

		[auth:lower][>0][year:prefix,_]|[shorttitle:select,1,4:skipwords:lower:nopunct:condense,_][year:prefix,_]

Now I can cite just with a drag and drop : [@gross_1996]

### 12017-12-06

I'll try to read and anotate a paper to see how it is handled.by Zotero.

(← is very sick )

### 12017-12-07

I start redacting then. But fisrt :

		[auth:lower][>0][veryshorttitle:lower:nopunct:prefix,_][year:prefix,_]|[shorttitle:select,1,4:skipwords:lower:nopunct:condense,_][year:prefix,_]


### 12017-12-11 

I had a busy weekend Simon, plus I was kinda sick. I did a few lines today nothing crazy…

### 12017-12-13

I watched an exam yesterday. Now to the organisation of my work.

List of concepts : 

Domain :
* (world)
  + Entity
  + Statement
  + Quantifiers
  + Functions
* Fluents
* Actions
  + States
  + Method

### 12017-12-21

Corrected lessons + took some time off the hooks. Yes Simon, I know I shouldn't…

Laetitia sent me a review of my work and I am trying to take into consideration the order of the definitions.

### 12017-12-22

> get out the way, train coming thru (ﾉ´ヮ`)ﾉ*: ･ﾟ

Redid all the first section and found correct ordering of deffinitions !

### 12017-12-23

I got further in the rewriting and I made some corrections. I have an issue with algorithm package in Latex/Pandoc.

### 12017-12-28

It was Christmas… My aunt died that day… I have trouble focusing on anything so I guess I will try tomorrow eventually…

### 12017-12-29

Debilitating headache day…

Also I need to explain the concept of the multi layered plans. I have troubles finding the good formulation and order along with separating the concepts in a neat explanation.


### 12017-12-30

Samir asked for my work so I gave what I had but I am not happy with how little I managed to do still. I am still stuck on that abstract plan introduction. I must talk about what are those, how do you make them and why do we make them. Also speak about preconditions inference from methods.

### 12018-01-02

Happy new year Simon !!! I've been grading my students. I still got a batch to do. PhD wise I am still on this tricky part. I need to make things right. I think I will keep it simple tho, we don't need to define the level of abstraction.

### 12018-01-03

Still stuck and correcting students work. Now that's over but I still got classes to prepare for next week.

### 12018-01-04

I'm at work Simon ! Samir is handling his kids so he isn't here. I need a way out for this part I am preparing.

### 12018-01-13

All went so fast. So much work for classes but I think its over for now… I am not too good. I'll try to debug at least that one bug today…

### 12018-01-14

I was so bad yesterday…  Well I am still debugging. I will try on a simple example for tests.

		"lite.w";
		(a, b, c) :: Action;

		c eff (f);

		b method (
			init(b) -> c,
			c -> goal(b)
		);

		a method (
			init(a) -> b,
			b -> goal(a)
		);

		init eff ();
		goal pre (f);

And I got just :

		[DEBUG]	 Agenda f  -> goal 
		[INFO]	 Resolving f  -> goal 
		[DEBUG]	 Resolvers [c  =[f ]=> goal ]
		[INFO]	 Trying with c  =[f ]=> goal 
		[INFO]	 Success !
		[INFO]	 @1 	c  =[f ]=> goal 
				init => goal 

No !

Ok so the problem is that action `a` gets parsed first (alphabetically) and then when creating its method it will create `b` but without fetching for its method. Fixed now… or not ?

On the regular domain it gets in a loop and also try to make the same method 3 times… and it still doesn't solve on POP…

### 12018-01-15

I am so late it is becoming a joke… I think its just my domain that is broken. OK ! So I add a new rule as that methods must be solvable on regular POP (be at least abstract solutions).
My algorithm doesn't select the composite actions first anymore… Actually not even selected at all !

I had classes and it was tiring… I'll try to debug some…

### 12018-01-16

My work on the World controller is very bad… I may need to redo it if there's another bug there. I am trying to think of a new domain for examples.

### 12018-01-17

Ok, so I guess I have an idea. I'll make a flat domain with everything working fine. Then I solve it for a few problems and get the plans. Then I create higher level actions from the result, so I am sure it actually makes sense.

Everything is broken, it's over… I won't make it… I think it will fail again Simon.

I have something that seems to work but I know deep down that I messed up again Simon.
I did expenantial benchmarks up to level 3 :

		goal pre (faccc); //a U !

### 12018-01-18

I feel a bit in despair for all that. I may give up…

Priorities seem off… I'm sick of it.
Well, its parsing again… I redid all the action part of parsing out of frustration and it didn't really helped.

Ok so that was stupid, it was that I didn't instanciate actions when parsing =.=
So the action in the second level method wasn't parsed as a level 1 action and the level were the same and that was why priorities were off…
And now ! It just don't want to work because he used two times the high level action and that is quite a problem.

And it creates **CYCLES** now too ! My code is just pure trash… I can't make it.
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA

…

Well Simon, watch that carefully, it is called a "panic attack" and it sucks.
Now I just barely recovered and I really don't think it's safe for me to stay here but gotta work, right ?

That thot of an algorithm decided to pour the spoon… like it would melt or something.
I put a protection on the cycle formation (ugly).

Nothing seems to work anymore…


### 12018-01-19

I am back on Quilter, hopping I don't lose that file again because of it. This morning wasn't quite good for the teaching because we were in a room full of iMac. It gave me a bit of time to debug and the problem was that an entity wasn't recognised as a variable and I fixed it.

Now I got cycles caused by the expansion resolver. I know the underlining cause but I need to fix that problem first.   Fixed !

Here goes the cause of this : the problem is that instanciation seems to leave remnants that are causing problems.

Ok, so a problem is that the way I sort the subgoal array isn't smart at all. I gotta make a real comparator.

I did and then fixed a few other problems and it get to a result. It's non-optimal AF tho.

Starting benchmarks generation code…

### 12018-01-20

I'm trying to relax and rest a bit today. I will try my hand a bit at the generator code so I get something done.

### 12018-01-21

It's hard to keep focused at home… I did the benchmark generator but it still has some kind of bugs.

### 12018-01-22

I was not ready mentally to go to work… I am trying to do things from here. I am trying to regain motivation !

Generator is working ! Now I need to refactor and make a `Benchmark` class. Then I need to add chronometers everywhere.

### 12018-01-23

I really need to get those chronometers working… I am really slow on work and motivation lately while the date approaches ! I am in some deep trouble because of that.

Chronometers done and working. CSV generated.

### 12018-01-24

Time is closing in Simon. I need to get that abstract redacted and ready. I will start to read some other abstracts and then I will merge all that style into my own. I do not need to stand out more than I already do.

I need a new name ? I would revert then to :

> **HEART** : HiErarchical Abstraction for Real Time partial order planning.

But… It doesn't fit the technical aspect well and honestly is quite cheesy. The current one seems negative tho :

> **ABORT** : Abstract Building Operating in Real Time as a refinement strategy for hierarchical partial order planning.

What are the main aspects of what I am submitting ?

* Abstract plans computed faster but still having some quality
* Real anytime that allows for realtime planning
* Online intent recognition

I'm somehow tired and drowsy… I can't think straight…

> When asked about the biggest issues with automated planning, the answers often contain
> the mutually explusive speed, quality and expressivity. We aim to find compromise that
> can satisfy a combinaison of those criteria. These are important limits of the domain
> when applied to assistive robotics. In that context, the plan quality and expressivity
> are important for the user while the computation speed is a constraint of mobility and
> robotics. Obviously no general solution exists that meets all these criteria yet, but
> that doesn't mean they cannot

> When asked about the biggest issues with automated planning, the answer often consists
> of speed, quality, and expressivity. However, certain applications need a mix of these
> criteria. Most existing solutions are too focused in specializing in one area that
> they do not address the others. We aim to present a new method to compromise on these
> aspects in respect to demanding domains like assistive robotics and real-time
> computations. The HEART planner is an anytime planner based on a hierarchical version
> of Partial Order Planning (POP). Its principle is to retain a partial plan at each
> abstraction level in the hierarchy of actions. While the intermediary plans are not
> solutions, they meet criteria for usages ranging from explanation to goal inference.
> This paper also shows how computing those abstract plans is exponentially easier than
> the complete process and the impact of the abstraction on the resulting quality.

> When asked about the biggest issues with automated planning, the answer often consists
> of speed, quality, and expressivity. However, certain applications need a mix of these 
> criteria. Most existing solutions focus on one area while not addressing address the 
> others. We aim to present a new method to compromise on these aspects in respect to 
> demanding domains like assistive robotics and real-time computations. The HEART 
> planner is an anytime planner based on a hierarchical version of Partial Order 
> Planning (POP). Its principle is to retain a partial plan at each abstraction level in 
> the hierarchy of actions. While the intermediary plans are not solutions, they meet 
> criteria for usages ranging from explanation to goal inference. This paper also 
> evaluates the variations of speed/quality combinations of the abstract plan in 
> relation to the complete planning process.


The thing is sent now. A neat idea is durring the redaction to use the idea of heart beat in order to explain the process.

### 12018-01-25

I need to correct the title first. [done]

Generating long results, gotta be patient. And I plotted them right !

Now with the example. And it's gone. It's gone, it's over, it doesn't work again. The results are not exploitable and I must debug and probably redo the whole example again…

I failed badly again. I won't submit. I need to correct things again. And I bet they will **NEVER** be right. I can't do it Simon.

…

I try again just to try.
The problem is the one of the duplication of instances when expanding regarding init and goal with parameters.

### 12018-01-26

I may very well give up Simon… 
I will try and debug until I get classes this afternoon (who needs lunch break after all, and I feel sick because of all this anyway…).

Instanciations are broken, like completely… Good job me, not returning the result because of rushed code…


### 12018-01-27

Life is full of goodbyes. I think I just need to drop off IJCAI already. I will debug stuff today. Probably won't go half as good as I expect it to be.

I may have actually made it through tho.
And I fixed the last issues I think.

Broke HiPOP… 
I breakdanced HiPOP back !

Sum of effects for all links (duplicated included):
* POP : 52
* HiPOP : 64
* Heart : 15, 39, 48

Unique participating fluents:
* POP : 15
* HiPOP : 15
* Heart : 11, 13, 15

Plan size :
* POP : 11
* HiPop : 15 (-4)
* Heart : 3, 9 (-2), 15 (-4)

I am doing that diagram at last !!!!!
The results are too good tho… Put into a neet diagram.

Computing the big ones now …

### 12018-01-28

Results are in and they are quite interesting. Now I am changing the style and that actually leaves me more room !

(Chair is hurting more than usual and it slows down my work…)


I needed to make a new CSL style for that and also tweak a lot of things with fonts. I also added a .wide class for wide images but its not complete yet (no conversion for SVG …)

I redid the diagrams and corrected all display problems. I also redacted about half of the result part. I am a bit dizzy and I can't think straight.

I did the rest of the proof on soundness. I am spellchecking my things on a basic level since I can't really think.

### 12018-01-29

I'm at work today and I must make the travel investment worth it. I am well in mind but a bit sick physically.

I am completing the completeness part. I need to write a recursive definition for a set of all steps within a set of methods and their methods.

I won't have lunch today.

$x \in S_{m \in methods(y)}$

And I actually already tackled that problem partially : $A_a^n = \bigcup_{a'\in A_a} A_{a'}^{n-1}$

        [DEBUG]	 make[drink ]@2 :
		 pre: taken(~ ) 
		 eff :drink in cup , placed(spoon ) , ~(taken(cup ) ) , hot(water ) , placed(cup ) , ~(taken(spoon ) ) , water in cup 
        [DEBUG]	 infuse[extract , liquid , container ]@1 :
		 pre: taken(~ ) 
		 eff :hot(liquid ) , extract in container , liquid in container 

I finished the main redaction. I need to review everything now and to fix each anotation.

Fixed Introduction and also some most all TODOs.

        for (Action action : problem.domain) {
			if (action.level <= problem.solution.level()) {
				resolvers.addAll(solve(action, flaw));
			}
		}

I added trend curves with equations on the level-width diagram. I re-did all the main explainning with heart beats instead.

### 12018-01-30

It's the final stretch !

I need to find that law fast !

* solve: 1 (5 × 4)
    refine : 343
        agenda.choose() : 85
        solve(flaw) : 41
        for : resolvers
            apply : 338 +1
        
Let's re-read instead… 

(I feel tired already…)

On that law : 343/4 is about 85 but that doesn't make sense…

Did the conclusion ! Exactly 6 pages + Refs !!!!

### 12018-01-31

Deadline day!

Apparently I need to simplify my stuff.

Working my way through the paper. I got a chance in 5 for acceptation…

TODO : review the refs !!!!!!!! [doing it]

Redid the proofs with more maths. Cleaned the result explanations. I am checking the formulas…

Finished with most things. I will need to correct things and eventually add explanations in the *heart* of it ;)

(home) I need to correct a few things still… Tomorrow ?


### 12018-02-01

AAAAAAAAAAAAAAAAAAAAAAAAAAAAA

AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA

I would have needed a few more days of work…

### 12018-02-02

I get a few more days of work °_°

I redid the CSL to match the hidden BST of IJCAI.

I redid the explaining part of HEART with a much better layout. I even got some wiggle room if I want.

I got the paper through some correctors. Submitted.

### 12018-02-03

Real deadline ?

I'll re read everything but first I need to check the differences between approach and method and model.


[Plan Space]        Approach :: Idea
[POCL]                Method :: Formalism
[Flaws, Refinement]    Model :: Element of formalism
[Flaw selection]    Mechanism:: Algorithm is the implementation of mechanism
[POP Algorithm]        System:: Implementation

Reviewing my own paper.

Removed all possessive on inanimate things. Redid some notation to match the definition of ":" as "such that"

Redid the definition of cycle and finished right in time.

IT'S OVER FINALLY !!!

### 12018-02-05

I'm slowly returning to regular life. That miracle paper has taken a bigger toll on me than I thought Simon.

Now ? I am getting into trying to understand what is `world`. I tried to get to an understanding seeing how meta-language works but they all are based on lambda calculus and programming language. World is not a programming language or at least it is not intended as one. In descriptive logic it seems the work is ontological and very specific. I am trying to understand how all this fits together but it looks like I am not theoritical enough for maths about category theory and not practical enough for ontologies. The problem is that everyone is super duper specialized and no one stopped a second to look at the broad picture. That or I have a lot to understand about that world. Probably the latter.

> Homoiconicity : In computer programming, homoiconicity (from the Greek words homo meaning the same and icon meaning representation) is a property of some programming languages in which the program structure is similar to its syntax. @wikipedia

World's syntax is infered. I need a general definition of quantifiers by structure using "Counting quantification".

Samir wants that I explain the link with planning… I should do two slides to convince him that what I did is not unrelated garbage…

> let
         ψ(x,[b])M, x = {a ∈ M : M ⊨ ψ(a,[b])}
> be the extension of $ψ(x,[y])$ in M relative to b1,…, bn. Then we can reformulate (1) and (2) as follows:
        M ⊨ ∀xψ(x,[b]) iff ψ(x,[b])M,x = M
        M ⊨ ∃xψ(x,[b]) iff ψ(x,[b])M,x ≠ ∅
[source](https://plato.stanford.edu/entries/generalized-quantifiers/)

I redifined the quantifiers by structure !

### 12018-02-06

I will need to talk to Samir about the dirrection the research should go…


Or should I ? Idk. I always have the worst idea and doing bad research is already my main focus already…

### 12018-02-07

I'm back at it. I decided that I need more informations before dropping either ways.
It feels like drowning in an invisible ocean…

I start to get some basic notions but as I progress I see that Samir is probably right and that there is little to no contribution from World…

### 12018-02-08

I am tired… I need to sleep. But I also need to read my stuff. I may just cater to extraneous things until I get work ready. I should not forget my meeting this after-noon.

### 12018-02-09

Meeting this afternoon. I need more info within the hour.
…

I'll finally follow the dirrection of Samir and Laetitia… It may be borring but thats necessary.

### 12018-02-14

I took some time off and had been busy with life Simon. Sorry.
… And then some…

### 12018-02-15

I really got carried away working on Hass…

I need to understand Sohrabi perfectly. I will make a prototype of the computation and try to get the principles today.

Sequence of observation O :  Fluent[]

increasing correspondance function with a list of states for a given flat plan

⇡ : pi

P (⇡|O) = P (O|⇡)P (⇡) = P (O|⇡)P (⇡|G) [P (G)] // I don't need no proba
P (G|O) = sum_⇡€↑ P (⇡|O)

V_O,G (⇡) = COST(⇡) + b_1 · M_O,G (⇡) + b_2 · N_O,G (⇡)

P (⇡|O) ~ 1 - V_O,G (⇡)

I need four main components : 

* a real time generator of observations
* a flattener of plans (giving all flat plans + remove special actions)
* a sequencer of states from a given flat plan
* a missing/noisy detector

### 12018-02-19

I am leaving on undeserved vacation today Simon. My family and boss are putting me in panic mode unwillingly. Now I can't think at all about.

Doing names and logo ? IDK 

not even …

### 12018-02-26

I took a good week of vacation, I should not get consumed by guilt so it was a time I needed and deserved.

I made a new project, for intent recognition. I named it rico (rodriges).
I'm a bit tired and its not even mid day
I should get something to eat…
--
I did so. I still feel tired and unmotivated…
…
After a day of procrastinating, I think I should at least plan tomorrow.
I need to make a callback system. With realtime observers.

https://arashmd.blogspot.com/2013/06/java-threading.html
https://stackoverflow.com/questions/23574357/java-callback-execution-in-thread-executed-after-run

### 12018-02-27

I should work more…

I do a main loop. It starts the `Observer` and an instance of the recognition tool. The recognition tool will suscribe to the observer and once notified of a new observation, it will launch the planning (that should be cost oriented …). An "interuption" is raised randomly to get the intent of the person and it will return its best guess. The next best thing is when the weight of the lightest plan gets too high (too unlikely) in order to help the person, or to have a timeout of inactivity if the most likely plan is not completed.


### 12018-03-01

Already !

I'm trying things.
Basic event management is working.
Threads are a nightmare to work with and understand…


### 12018-03-02

I wrote up an email to states the most important problems :

* World isn't thread-safe
* Heart doesn't handle action cost
* Sohrabi isn't meant to work on POP

World idea : The Peano axioms


I don't get how interuptions works in Java. Nothing is happening…

### 12018-03-05

I don't feel too good… I'm not sure working is the right idea rn…

### 12018-03-06

…

I screw up more

### 12018-03-07

Meh.

I'll do things manually because everything is bad.

Things works now. I just need to get to the more theoritical problems.

### 12018-03-09

I know…

### 12018-03-10

I'm feeling better but motivations are not the best.
(+ IM did a new thing !)

P (G) Prior (function of time of day, can be learned)
P (G|O) Posterior (distribution among all goals)
P (G|O) = αP (O|G)P (G) : Bayes stuff where α is a normalizing constant

Each observed fluent is provided with a prior probability.
A goal can have several plans
A partially ordered plan can have several total orders
A separated system gives us the prior probability of goals (time of day, learning)
P(pi|G) is inverse of cost function of pi
P(G|O) = SUM_pi [P (O|pi)P (pi|G)] P (G)

P(O|pi) = (a observed -  b (Missed + noisy) fluents) * P(O)
P(pi|G) = cost of plan for all possible plans ?

cost of abstract plan ?

### 12018-03-12

Bleh.

For the abstract plans we compute as much as we can and multiply it with a function of the abstraction level.

Changes from Sohrabi :

Observations are fluents but each have a prior probability (confidence of the observer)
For each observation we must compute its posterior probability in the plan.
The problem is that order is part od the score…

We will need two cost : an intrisic cost and another one that is dynamically computed.

### 12018-03-14

I need to update the layout of the article to submit it to workshops of ICAPS.

And hurry back to class…

### 12018-03-15

Ok so I can't get the indent off the bibliography…
I did now but it's ugly…

CHecked requirements for publishing LaTeX wise. It seems hard to fix but not impossible.

### 12018-03-16

I am re-reading the paper to get back into it.

Size of tree (Width,Depth) = (W^(D+1)-1) / (W-1)


Observed is : Time = a/ (D^W)

Number of flaws : D +1

TODO :

[ ] Complete the introduction and rework it (add Damien ++)
[x] Add to the example and have more figures to explain it
[x] Re-see the maths
[ ] Add to the conclusion and results in a more organized way.

I had to redo a big part of pandoc-science to fix issues I haven't seen when updating.

For width 2 :
Puissance 0,884767
Exponentiel 0,947047

the other way arround for width 4…
plot (0.036 x^2, 2.8 x^3, 146 x^4)

Returning to the paper for now.

### 12018-03-17

Stressed out today.

Deadline draws closer.

I feel lke I won't do it

> You Know I Had to Do It to Em 👊👋


### 12018-03-18

Deadline officially tomorrow…

I'm fixing lacks in the paper as fast as I can.

Doing the graphics.

DOne.

Deadline extended !!!!

### 12018-03-19

hollow

I fixed pandoc stuff…

I need to organize…

**Results**

Setup

Quality / Time

Random praise

Benchmarks

Explaining domain shape and stuff

Missed explaination

**Conclusion**

HEART exists

HEART is good

HEART has limits

Lots can be done

### 12018-03-20

I've been rejected… I want to answer anyway but apparently the system is dumb and will just plug their ears when you say all things have been addressed…


I feel better but that's all because of the meds that I got up this morning.

### 12018-03-21

I worked only a little bit so far

Did 4 papers reading and I am quite despaired…

### 12018-03-23

I am super sick… I missed class and all and I lost all the little progress I did today…

### 12018-03-24

That's it ! Quilter lost my work 3 fing times ! I am getting back to scratch. The aim is now to say the motivation is about thos partial plans themselves and their property plus a factorisation of the framework or something like that.

### 12018-03-25

Google assistant is stupid Simon. She's like "OFC I'll wake you in 8 hours time" but skips that DST exists like a good ol human…

I'm mad because I didn't slept enough but I must finish that stuff.

Ok, I return to it.

Finished corrections given to me… @12:29

I need to re-read and correct

I didn't…

## Rico

### 12018-03-30

For partial ordered plans. Sohrabi said that the less good plans have a negligeable impact on the probability and can be ignored. Compute the best score considering order constraints.

Each steps that can be done at the same time are "merged" into a pool of infered things.

P(G|O) = SUM_pi [P (O|pi)P (pi|G)] P (G)

P(O|pi) = P(pi|O) * P(O)

	P(pi|O) = (a observed -  b (Missed + noisy) fluents) * number of order merges *
		P(pi) // function of abstraction


P(pi|G) = cost of plan normalised for all possible plans * P(pi) // function of abstraction

Comparison to POP with complete heuristic and only one plan.

(POP with diverse results doesn't exists ?)

### 12018-03-31

…

### 12018-04-01

I'll work good today and be very happy. April's fooool !

Bayes:
P(A|B)= P(B|A)P(A) / P(B)

P(pi|O) = P(O|pi) P(pi) / P(O)

P(A) = P(A|B) P(B) / P(B|A)

P(pi) = P(pi|G) P(G) / P(G|pi)

P(G|pi) = 1

P(pi|O) = P(O|pi) P(pi|G) P(G) / P(O)

P(G|O) = P(O|G)P(G) / P(O)

Total probabilities formula :
P(A)= sum_{i\in I} P(A|B_i)P(B_i).
P(A|B)= sum_{i\in I} P(A|B_i)P(B_i|B).

P(O|G) = sum_pi P(O|pi) P(pi|G)

P(G|O) = sum_pi P(O|pi) P(pi|G) P(G) / P(O)
P(pi|G)P(G)/P(pi) = P(G|pi) = 1
P(pi|G)P(G) = P(pi)

P(G|O) = sum_pi P(O|pi) P(pi) / P(O)

P(pi|O)= P(O|pi)P(pi) / P(O)

P(G|O) = sum_pi P(pi|O)

P(pi|G) ~ exp(-Beta cost(pi))

Qualitative Probability Calculus (Goldszmidt and Pearl, 1996).


### 12018-04-02

P(O|pi) P(pi|G) ~ 1 - (Beta' V_O,G(pi)) / (sum_{pi' in Pi} V_O,G(pi'))

### 12018-04-04

I don't know what to do…

Reading the old paper on Qualitative Probability Calculus

Jeffrey's rule : (R. Jeffrey, The Logic of Decision 1983)
P'(A) = P(A|B) P'(B) + P(A|¬B)P'(¬B)
with P' being posterior probabilities

(lambda is the likelyhood ratio)
lambda(G) = P(O|G) / P(O|¬G)

P'(G) / P'(¬G) = lambda(G) P(G) / P(¬G)

product decomposition of Bayesian networks :
P(x_n, …, x_1) = prod_i=1^n P(x_i|pi_i) ?

The paper explains rankings but dismiss probabilites and does not give a way to compute them from rankings…

### 12018-04-06

Dear Dr. Sohrabi :

I am currently a PhD student in Artificial Intelligence and Robotics at the LIRIS (Lyon, France). I am interested in Planning and Plan Recognition. I already contacted you for help on the planner part of inverted planning [1] and your response helped me a lot building my own planner for this purpose, for which I am very thankful.

However, now I am focusing on the probability approximation using the plans found for a given goal G. I read your paper extensively and tried to redo all the steps in that approximation. I am aiming to evaluate as precisely as possible how efficient that approximation is and also to fit it to my planning approach (partial order plans).

I managed to validate all the equations in that part except for one that I can't seem to find the origin. Here are my computations for references :

Bayes:
P(A|B)= P(B|A)P(A) / P(B)

P(pi|O) = P(O|pi) P(pi) / P(O)
P(A) = P(A|B) P(B) / P(B|A)
P(pi) = P(pi|G) P(G) / P(G|pi)
P(G|pi) = 1
P(pi|O) = P(O|pi) P(pi|G) P(G) / P(O)
P(G|O) = P(O|G)P(G) / P(O)


Total probability formula :
P(A)= sum_{i in I} P(A|B_i)P(B_i).
P(A|B)= sum_{i in I} P(A|B_i)P(B_i|B).

P(O|G) = sum_pi P(O|pi) P(pi|G)
P(G|O) = sum_pi P(O|pi) P(pi|G) P(G) / P(O)
P(pi|G)P(G)/P(pi) = P(G|pi) = 1
P(pi|G)P(G) = P(pi)
P(G|O) = sum_pi P(O|pi) P(pi) / P(O)
P(pi|O)= P(O|pi)P(pi) / P(O)
P(G|O) = sum_pi P(pi|O)

The equation (6) proposes the approximation of the probability of the observation being explained by a plan pi fulfilling G. I understand the V_O,G(pi) formula and I find this formulation very interesting but I can't find the explanation behind the division with the sum of costs of other plans along with the normalization. In your example you said that the equation 6 would result in a value of 0.75 for a single plan but when I tried to plug that in equation (3) I get probability higher than 1 for P(O|G). I also don't understand how normalization gives a value of 0.25 for P(pi|O).

Am I mistaken in my computations or is there something I didn't understand about normalization ?

I would be very grateful if you would give me some pointers as to how to compute such probabilities in code.

Thank you for your time and considerations.

Sincerely yours,

[1] Plan Recognition as Planning Revisited. Sohrabi, S.; Riabov, A.; and Udrea, O. In Proceedings of the 25th International Joint Conference on Artificial Intelligence (IJCAI), 2016.

---

I need to update in the presentation :

1. See with the title ?
2. Is context needed ? No

### 12018-04-07

Doing the presentation stuff. I'm just removing the sugar and trying to update things.

Adding all the result, implementation and future publication ! Continuing tomorrow…

### 12018-04-10

I had an answer to that mail to Sohrabi !
Oh and also if I don't finish my thesis by december, I am screwed and probably dead too.

> Normalization is done so that 0.75 is turned to a number that if multiplied by 4 would be 1. So that is done by the beta coefficient in equation 1.

Beta = 1/3 in the case of the example…

> \Pi is all plans, and sometimes it only a subset (those that meet the goal in equation 3)

So in equation 6 it is ALL sampled plans.

> For me the V is the cost of the new transformed problem.

I completely forgot that part since it was after that and that makes much more sense.

### 12018-04-11

Now I respond with : 

Hello,

Thanks a lot for the great answers ! I redid all the computations and it matches up when using Beta = 1/3 (which I guess generalizes to 1/{|Pi|-1} ?).

The only remaining part I am not sure to understand is how to incorporate the prior probability of the observation P(O) in the first equation ? If Beta is used only for normalisation we should introduce a division by P(O) to meet the Bayes theorem. Is that ok to do extend the equation this way ?

Thanks again for your time.

Sincerly yours,

---

Ok so I am starting redacting the theoritical part in the article and preparing for ICTAI so I am ready when needed.

Done

I don't know where to start tho …

### 12018-04-12

I need to fix a few things first…

NOTHING WORKS IM MAD !!!!!!!!

### 12018-04-13

I spent the day yesterday fixing stuff. Everything decided to crash and misbehave, everything.

I need to talk to you Simon because I am stuck in my reflexion. I need to make an organised plan of my thoughts to get them out. What I do is inverted planning. I take a planner and do things to the result to achieve the opposite of planning. So I need to make a state of the art of intent recognition using planning methods and to sumarise the arguments. No more painfully made plan library !

Now I need an explanation of the probabilities ? How does Sohrabi do it ?

[Sukthankar et al., 2014] Gita Sukthankar, Christopher Geib,
Hung Hai Bui, David V. Pynadath, and Robert P. Goldman.
Plan, Activity, and Intent Recognition. Morgan Kaufmann,
2014.

Ok so she pretty much did it herself.

## Thesis and publications

### 12018-04-14

What do I do ? I need a PLaaaan

Introduction
	Why inverted planning and its important and researched and stuff…
	The other approaches and all related ways to tackle similar problem in each aspects
	Our approach in sumary
	Plan of the paper
Related Works
	Explain some early approaches
	Ramirez
	Details on why we are different than  Sohrabi
Definitions
	Planning
		Domain + Fluents
		Actions
		Problem
	Observations
	Plan recognition Problem R = < Domain, I, O, Fancy G (goal library) >
	Explain proba of goals and how they are dynamicaly computed (vs fixed for observations)
Probabilities and distribution
	Bayes and co
	Logistic ? lots of eq
	Define an approximation
	Metrics of approximation
Approximations
	Re-def Sohrabi
	Ours (prior O and stuff)
POCL and stuff…
	Explain the approx algorithm
	Example !
	Merging action
	Approximations with abstraction
	Using heuristics instead of problem transformation
Theorical prop
Results
Conclusion


> More precisely, if the observations O contain ‘variables’ (pronouns), one could
set c(G + O) to min_O_i c(G + O_i ), where O i are the possible groundings of O - @hectorgeffner_planning_2011 

Problem : The merging of actions means merginf states and preconditions. Hopefully I unified those two. But I still need to iterate on the fluents and reduce them as we merge the two states.

### 12018-04-15

I did some redacting but nothing much …

### 12018-04-17

Its a hard time today…

### 12018-04-18

What am I even doing ? I can't stop pretending that I can make works that can make it. The reviews have been bad but the scores are… crushing…
I just got enought strenght to try to work today and to actually get out of bed and I read those pityful scores. I won't make it Simon… I'm sorry.
Each time I feel confident I should be redirected to that table :

| Number | Originality | Technical Quality | Significance | Relevance | Quality of writing | Scholarship | Overall | Confidence | 
|--------|-------------|-------------------|--------------|-----------|
--------------------|-------------|---------|------------| 
| 1      | 3           | 5                 | 3            | 3         | 5                  | 3           | 3       | 10         | 
| 2      | 1           | 3                 | 1            | 2         | 4                  | 1           | 2       | 10         | 
| 3      | 8           | 3                 | 5            | 10        | 4                  | 3           | 3       | 7          | 
| 4      | 4           | 6                 | 4            | 8         | 5                  | 5           | 4       | 6          | 
|Weighted| 3,6         | 4,2               | 3,0          | 5,1       | 4,5                | 2,8         | 2,9     | 8,3        | 

### 12018-04-19

If I can't seems to publish at least I can submit until death.

…

Even deliveries just avoids me now…

New names :
root operator : alpha (FIXME : the main diagram)
abstraction level : lv()
set of proper actions : A_x
set of all operators : A_\mathcal{D} (by def)
set of all actions (operators + all possible instances) : A
relations (uninstanciated) : F ?
world : Omega

### 12018-04-21

I'm bad… Let's not be bad.

classical planning is PSPACE-hard (Bylander 1994).

… I apparently got accepted at H-ICAPS18 ?

### 12018-04-22

I did ! That was … expected but still.

Expend the plan generation part and reduce the definitions and proofs.

### 12018-04-23

Vol aller : 110€

> "used them as a heretic[HEURISTIC] guide."

I litterally just submitted a paper that used the sentence *heretic guide* and only one person noticed it.

### 12018-04-24

Slow start…

I read a few things and added citations. I also corrected more things. I still have some reformulation and change of variable names + check on the style and the length.

### 12018-04-26

Mainly stuff with transport and admin

### 12018-04-27

…

### 12018-04-29

I did some work on admin…

### 12018-04-30

I did my contacts. Times are rough…

What I want to prove : if we find an abstract plan then its expansion is always a solution.

Axioms : While compiling we use POCL on each method to ensure they are complete and solvable.

Proof : Abstract plan is a solution but with a section between the init and goal of the method. We can solve any interferences because they were already solved. No other contraints are introduced because the instanciation is already done. 

### 12018-05-01

I did corrections of tests all day.

### 12018-05-02

Lets read the last things.

> I gave this only a middling significance value, because in future work the authors should tackle the *real* problem with this kind of iterative refinement: without the so-called "downward refinement" property, it's not interesting to have an abstract plan, because that abstract plan could be wrong (impossible to extend to a concrete plan).

@erol_htn_1994 for NPSPACE in HTN

Higher level actions are prefered not mandatory !

### 12018-05-03

Tweaking stuff on the computer for no reason…

Difference with Angelic : initial and subsequent plans are not imposed.

### 12018-05-04

> May the fourth be with you !

ANML uses > for inheritance ! That's a neat way to redure the number of operators because it's the symbol of sumbsuption !

### 12018-05-08

Procrastination and home automation really delayed my work…

### 12018-05-10

It's so late… I should be reworking everything by next week… I'll try to

Did almost nothing…

### 12018-05-11

I am fixing the auxiliary problems. 
changes :
root operator : a_0 (FIXME : the main diagram)
set of all operators : A_\mathcal{D} (by def) A^1_x for simple proper set
set of all actions (operators + all possible instances) : A
use "decomposition" more often

2222222222222222222222222222222222222

File updated !

Todo : Add keywords, add sources link, move proofs to apendix, add the proof of garanteed solution.

Ask citeproc for a solution for inline citation and IEEE

### 12018-05-13

Yesterday was a fix stuff and relax day.

Today I did the website for heart : [https://genn.io/heart/]

Now I continue that proof.

I must rewrite notations of flaws :

### 12018-05-14

I did some corrections of notation and definitions. I also managed to write some of the proof.

### 12018-05-15

I removed the proofs of soundness and completion and focus on merging the 3 mathematically. I copied all that could be salvaged and proceed to finish the proofs.

### 12018-05-18

I actually worked the days prior but I was too busy getting everything in order. Just checking my notes actually. It's really rush times !

Fixed citation format. I'm ready to go.

### 12018-05-19

Last day to submit. I'm changing the title to "How explainable plans can make planning faster"

### 12018-05-20

I'm starting the code…

I did the cost stuff…

### 12018-05-21

I want to start with the update of the effective costs.

Thinking about how to do all this.

### 12018-05-22

Well… I forgot that all I do is bound to become useless… 

I'm so stupid… HOW am I suposed to integrate forward observations with BACKWARD planning !?!

I failed at the most basic part of it…

### 12018-05-24

I'm procrastinating with home automation stuff… I will just check my paper and correct it then send it. After that I will propose the plan for the thesis and go ahead.

### 12018-05-26

I did the plan of my memoire with my sister, it is quite good now.

### 12018-05-28

I must find a new title and submitting the PDF asap.

Abstract planner, cyclical refinement, POCL, hierarchical planning, decompositional planner; partial solutions

### 12018-05-29

HEART : Abstract plans as partial solutions
HEART : Hierarchical planning with anytime solution approximation
HEART : Abstract plans as a guarantee of downward refinement

### 12018-05-31

I has surgery yesterday. But the more painful was being accepted while also being refused budget by my boss…

The title he wants is "Abstract plans as a guarantee of downward refinement".
I need to register but I need first to get a hold of anyone for the money problem.

### 12018-06-10

OMG… Well. I just couldn't be not lazy this time. I just corrected the paper for ICTAI. I lost quite the motivation because my boss is disabling me a lot…

### 12018-06-13

I'm finally starting the thesis. First I need to update pandoc and such…
Done

### 12018-06-14

I'm finishing the style today and exploring World to see what I can do with it.

### 12018-06-15

Continuing all the margin shenanigans…

### 12018-07-04

I went to ICAPS. It was cool but tiring and bad for my self esteem…

I am now doing corrections for XAI.

### 12018-07-05

Corrections are made now. I added works on model reconciliation I saw at ICAPS.

### 12018-07-30

I went to IJCAI and I procrastinated on the way back… I'm so ashamed.

I need to make the state of the art correctly now…

### 12018-07-31

I need to define some notions formally :
Stregth of an Axiom (the less providing)
Equivalent to configuration ?
Expressivity ?

### 12018-08-02

Yes, I know… I'm not in the mood to feel older rn.

I'm reading the DL handbook. It's nice so far but long and I'm slow with this stupid heat. It's better here with the AC.

### 12018-08-07

I was rejected… They didn't even read my work…

### 12018-08-15

I resubmitted and I have troubles getting back on track… I will just make excuses but I know I am just lazy and stupid…

I am trying to work out the taxonomy of planning better. Also I need to analyse language stuff so I know how to differenciate the different languages used in the knowledge representation process.

Plan sequence, Partial Order, Diverse, Policy
Plan replanning, Plan refining, Plan repair, Plan merging



stochastic search planner LGP
conformant planning


!!!!!!!!!!!!!!! Automated Planning and Acting p388 !!!!!!!!


### 12018-08-17

I'm a mess…

Lets see how I can manage things. Mail to Höller is sent.

Determnistic (State (forward/backward), Plan space, Replanning, HTN, Temporal logic, Planning graph, online planning)
Refinement (levels?, interleaved)
Temporal (new search space, conflict, constraints)
Non-Deterministic (Policies, AND/OR, Online lookahead, automata)
Probabilistic (Policies, MDP)

### 12018-08-22

Yep…

FROM OLD DUCK !!!! :

*NOTES* 

Starting with [@ghallab_automated_2016] "classification" : 

* Deterministic (State, FF, $h_add$, Back, PSP)
* Refinement (LPG)
* Temporal (POP?; STN)
* Nondeterministic (AND OR branches, Policy)
* Probabilistic (MDP)
* Other (Recognition, exec control)

Then Bechon [@bechon_planification_2016] :

* État > Fluents > Variables > Temporalité > Hierarchie
* Recherche simple > Graphe ($h_add$) > PSP > HTN > HTN + POP
* Réparation

6 W :

* *Where ?* **Search Space** : State based planners are often considered faster when plan based planners usually means more flexibility.
* *What ?* **Plan Type** : Most planners find a totally ordered sequence of actions. Some prefer to find a partially ordered plan. Both can be found in sets (what we call *multiple planning*)
* *Who ?* **Action Type** : The actors of planning are actions. The action model determines how expressive a solution plan will be but also will decrease its performance accordingly.
* *Why ?* **Guide Space** : A guide or heuristic is giving a notion of sens to planners. This category is so diverse (meta-heuristics, machine learning, user preferences, HTN, etc) that our solution was to reflect the search space and to differentiate with the space on wich the guide works on. **TODO : reword?**
* *hoW ?* **Building** : In this category we describe how a planner builds its plans. A simple directional building (forward or backward) is often faster than more complex ones (like plan re-use or repair).
* *When ?* **Timing** : The timing of the planning can affect how much time the process should take. More constraints on time means the planning process should be faster. "Take your time" (Optimal), "When it's finished" (Anytime), "It was due yesterday" (Real time)


Metrics :

* Stability
* Diversity
* Quality
* Violation


Char :

* Order : total | partial
* Diverse : B
* Search space : State | Fluent | Action | Local? | Plan
* Execution type : Optimal | Complete | Anytime
* Search direction : Forward | Backward
* Temporality : strict | partial order | durative
* Expressivity : State | Fluent | Variable | Quantifier


IDEA : Separate Problem and Solving.

Plan :
What    Temporality : Sequence | Partial Order | Duration
Why    Criteria : Quality | Stability | Diversity 
Who    Expresivity : Grounded | Parameterized | Abstract

Search :
When    Temporality : Optimal | Complete | Anytime
How    Iteration : Directional | Refinements | Repair
Where    Space : States | Action | Local | Task | Plan

Uncertainty : Deterministic | Posibilistic | Probabilistic

---

### 12018-08-23

#### General semantics as language ?

set notation builder + etc ?

fast inference tool (makes mistakes but faaast)

(P has a) & (Q(P) has a);

### 12018-08-25

I need to regain motivation !

Problem : ?
Resolution : ?

### 12018-08-26

bleh

Fully observable
Discrete
Deterministic
Single-agent
Just one kind of action!

Offline
Usually ground and via progression search
Solutions are action sequences

P = ⟨⟨F, O⟨pre, eff, cost, time, probability, methods⟩⟩ , Ω⟨s_0 , s^∗ , {π : (S, L) }⟩⟩

Fits all planning paradigms

### 12018-08-29

I think a simple shortest path algorithm may fit well ?

A*( Graph g, init I, goal G,  heuristic h)

FF : A*( (S,A) , s_0 , s^* , h_FF)
PSP : last(A*( (P, Refinements), π_ø, π : Flaws(π)=ø ? , h_VHPOP?))
HTN : A*( (O, ?), Ω, { a : methods(a)=ø }, h?)

ALGO( Graph (search space), 

### 12018-08-30

APLAN( Graph g, init I, goal G,  heuristic h)
	open.add(I)
	while !open.isEmpty
		u = open.pop
		if end
			returns
		for neighbours(u, g)

### 12018-08-31

Back to languages.

ML (Meta Language) : 1973 Lisp with type and proven.

The language in which the metaprogram is written is called the metalanguage.


Compiler : Source language -> Target Language

### 12018-09-02

Unforgotten

Mail for the researcher that wanted informations on World :

Bonjour,

Tout d'abord, je tiens à vous remercier de votre intérêt pour HEART.

    * Le langage du domaine et du problème est un prototype de meta-langage de
 description généraliste (basé sur RDF et sur la logique de description). Cela fait
 partie de nos travaux en cours que nous n'avons pas encore publies. Sa grammaire se
 base sur un système de triplet simple mais la plupart de ses fonctionnalités impliquent
 un aspect dynamique qui est encodé dans le langage lui-même. Tout ceci est compilé à
 l'aide d'une bibliothèque dédiée. Cette bibliothèque est intégrée dans le framework de
 plannification qui est prévus pour être indépendant du langage utilisé. Cependant la
 recherche d'instances et les vérifications de contraintes sur les fluents se font en
 temps réel via la bibliothèque du langage sur la base de connaissances ontologique.

    * Le planificateur est écrit en Java, tout comme le compilateur du langage et la
 bibliothèque associée. Le code source du planificateur sera disponible au lien indiqué
 dans le papier ( https://genn.io/heart/ ) dès que nous aurons publié cette partie du
 travail.

J'espère que cela répond à vos questions et je reste disponible pour toute autre demande d'informations.

Cordialement,

--

Compilers can do the following operations: preprocessing, lexical analysis, parsing, semantic analysis (syntax-directed translation), conversion of input programs to an intermediate representation - Wikipedia

### 12018-09-05

I deserve all that happens to me now… The conference ICEEAI was a scam…

I did admin all day. I am now trying to understand languages.

### 12018-09-06

No you didn't you little piece of shmidt !

### 12018-09-07

That's a bad place to be. I have no choice but to try the impossible…

Fixed Pancake !

### 12018-09-08

Blank stress

I'm deserving all that is happening now. I really made it bad now.
How am I supposed to make it to qualification if I can't even publish anything ?
I am a fraud that's all. A lazy coward waiting for his end, for the day he gets uncovered.

I did basically nothing since june… More than 3 months lost to my incompetence.


### 12018-09-09

I feel awful.

JavaCC produces LL(1) parsers by default. It is a context free grammar.

I can't focus. I can't read or redact correctly. I don't know what to do.

### 12018-09-10

I won't give up !

### 12018-09-11

Ok I really wasted my time on everything but my work.

### 12018-09-12

Change of plans. Samir is probably right in me not making it this year. I will try to aim for june. In the meantime I must publish a lot. I am starting to see how everything works together.

### 12018-09-15

…

### 12018-09-17

I really need to redact something.

### 12018-09-19

I'm an official failure. My boss told me that what I redacted was so bad it shouldn't even be proposed…

### 12018-09-20

OMW to drop of the thesis… My boss gave me an alarming call and I am devastated. I need to work but I don't know if I will be phisically able to. I am morally dead.

machine oriented syntax vs natural languages @ford_parsing_2004

Actually worked well today. It was very hard to do.

> "Autopoiesis literally means ‘‘self-creation or auto-creation.’’ It expresses a
fundamental dialectic between structure and function. The term was
originally introduced by Maturana and Varela (1980), and according to
them, an autopoietic system represents a network of processes or operations that define the system and make it distinguishable from others."

### 12018-09-21

Day of submission and I won't make it. I'm such a joke.

### 12018-09-22

* Raisonnement positionement
* Discours pas clair
* Déroule le sur un example
* ! Meta raisonement
* ! Raisonnement sur les ontologies
* Exemple : pourquoi la reflexivité est importante. Definition par structure.
* ! Quand on cherche on présente ce qu'on revandique, pourquoi c'est pas fait par les autres
* On doute de l'originalité de World.
* On fait un état de l'art en 1 semaine.
* Diagramme pas interessant
* Clairement ce que je revandique, bien expliqué
* Exemple bête comment world marche
* ! Positionement et exemple en même temps
* Gens qui pourait faire des choses proches


EN :

* Positioning for reasoning
* Speech not clear
* Unfold it on an example
* ! Meta reasoning
* ! Reasoning on ontologies
* Example: why reflexivity is important. Definition by structure.
* ! When we look for we present what we want, why it's not done by others
* We doubt the originality of World.
* We make a state of the art in 1 week.
* Diagram not interesting
* Clearly what I am claiming, well explained
* Stupid example of how world works
* ! Positioning and example at the same time
* People who could do things close together

TODO :

[×] Lire papiers sur DL
[2] État de l'art inférence
[.5] Citation logique multimodales
[1] État de l'art meta raisonement
[2] Emerger les contributions
[1] Identifier ce qui est réutilisé (théorie des compilateur)
[3] Plein de petit exemples illustré

### 12018-09-23

bob says (alice is nice);

* How is it impossible to represent planning domains with traditional ontologies compared to the world representation of these same domains?
* How does the representation world and PDDL differ? Is it possible to represent any PDDL 3.1 in world? Show this on an example.
* Show that the world inferences are valid in relation to the planning inference.
* How does world make the planning process more expressive and interoperable?

### 12018-09-24

Admin + other slides

### 12018-09-25

Let's see how bad we can perform today…

I need to organize my thoughts as best as I can with all that.

> "packrat parsers are parsers for PEGs that operate in guaranteed linear time through the use of memoization

Is this equivalent :
On error do smth to activate rules
write more general rules and memorize new tokens.

---

My boss asks a complexity analysis between PDDL and WORLD.

WORLD 0.3.1 :
* Token = 1 + 4 + 3 + 1 + 1 + 2 + 9d + 3 + 2 + 2 = 28
* Rules = 24
* Features : 
	+ Dynamic features (can add properties or whatever)
	+ Boxed TreeMap storage (for fast and furious data)
	+ Custom inference engine (with stored or dynamic statements)
	+ Type Inference (not really implemented)
	+ Dynamic instance search (only instanciating with relevant variables)
	+ Semantic guiding (able to make heuristics based on semantic knowledge)
	+ Plan completion (by the planner, at compile time)
* Problem : instance of POP ran during compile time for hierarchical plans. Instanciation is a heavy process still.
* Complexity (hint) : Since syntax inference is done in token mgr there's no fundamental increase in time (simple lookahead). Base inferences process all entities once (mostly for type inference). Instances are created when needed. Everything is cached and reused.

PDDL4J :
* Token = 2 + (35-12) + (60-39) + 5 + 4 + 5 + 6 + 18 + 2 + 2 + 6 = 94
* Rules = 174 - 1 = 173
* Features : 
	+ Logical Normalization (simplify logical expression)
	+ Inferring types when not explicit (in only some cases)
	+ Operator Instanciation (at compile time)
	+ Operator Simplification (simplify logical exp in instances)
	+ Bit Set Encoding (to speed up things)
	+ Solvability check (linear planning is executed before execution)
* Problem : needs time *after* problem is provided (runtime) for grounding…
* Complexity (hint) : Must process all expression at least once + all operators twice. Creates all possible instances. Uses Bit Sets to quicken access at runtime.

---

World is broken on block world. I haven't fixed before because of rush. I actually need to fix that first and then to test benchmarks on regular POP with world. Then I need to add PDDL4J in color and make POP work on that. Once it is done I get to compare the two with a big comprehensive benchmark and hopefully I am faster. If not … Idk what to do.


### 12018-09-26

Knowledge unit in PDDL (:p args)
Knowledge unit in world s p o ;
-> Information density is a consequence. PDDL specifies more units needed for the same purpose (infered in world).

remove redundancies and repetitions 
"Extracting Incomplete Planning Action Models from
Unstructured Social Media Data to Support Decision Making"

"Planning-based Scenario Generation for Enterprise Risk Management"

### 12018-09-27

Sohrabi, not only uses lifted planning but starts meddling with knowledge tools… Now she's working to turn *MindMap* into planning and apparently succeeding somehow.

Autopoiesis and reflexivity (self-reference)

P(a) land [Q(P)](a)

N-Quad RDF : s p o id .

s -st> o

Meta graph ? 


G=(V,E,a,l) : E C V, l:E→V


**Metamodelling ontologies**

Diff OWL-DL and OWL-Full : 

1. it does not allow one to state axioms about the built-in vocabulary (i.e., the symbols, such as owl:allValuesFrom, used in the definition of the semantics),
2. it strictly separates the sets of symbols used as concepts, roles, and individuals, and 
3. it enforces the well-known restrictions required for decidability, such as allowing only simple roles in number restrictions

We analyze this undecidability result and show that it is actually due to (1) that is, the fact that we can state axioms about the built-in vocabulary. This makes the semantics of OWL Full nonstandard and controversial [Three Theses of Representation in the Semantic Web.].


Using boxes for optimizations and containement of the language features. Finding a generic way to define boxes and their structure is important to try for a decidability proof.

### 12018-09-28

The decidability problem arise when you can express an infinite amount of infered statements.

Little woozy now…

separating concrete and abstract domain is widespread.


### 12018-10-01

I need a formalization of WORLD…

Np specific or special property or Entity *apriori*.

Now : Dynamic parser by modifying the token manager behaviour.

PDDL expressivity is included in World. World's inferences on type : …

New in world : 
* Variable inference
* Dynmaic Boxes (to define)
* Meta-concept inference
* Meta-statement in the same space and language
Consequences :
- anonymous quantifiers, 
- exclusive quantifier (solution quantifier ?), 
- parameterized quantification (not new in math), statement about statement in knowledge description systems (using the same mechanism for both).


Re-used :
* Dynamic parsing
* LL(1) Grammar (or maybe Packrat later)
* Some aspect of meta-statements
* Some tricks for type inference (to stay valid)
* Name resolution and concept merging (very basic)


### 12018-10-02

Focus on PDDL aspects of WORLD.

PDDL3 BNF :

#### Domain

<domain> ::= (define (domain <name>)
	[<require-def>]
	[<types-def>]
	[<constants-def>]
	[<predicates-def>]
	[<functions-def>]
	[<constraints>]
	<structure-def>∗)
<require-def> ::= (:requirements <require-key>⁺)
<require-key> ::= //See Section 2.6
<types-def> ::= (:types <typed list (name)>)
<constants-def> ::= (:constants <typed list (name)>)
<predicates-def> ::= (:predicates <atomic formula skeleton>⁺)
<atomic formula skeleton> ::= (<predicate> <typed list (variable)>)
<predicate> ::= <name>
<variable> ::= ?<name>
<atomic function skeleton> ::= (<function-symbol> <typed list (variable)>)
<function-symbol> ::= <name>
<functions-def> ::= (:functions <function typed list (atomic function skeleton)>)
<constraints> ::= (:constraints <con-GD>)
<structure-def> ::= <action-def>
<structure-def> ::= <durative-action-def>
<structure-def> ::= <derived-def>
<typed list (x)> ::= x*
<typed list (x)> ::= x⁺ - <type> <typed list(x)>
<primitive-type> ::= <name>
<type> ::= (either <primitive-type>⁺)
<type> ::= <primitive-type>
<function typed list (x)> ::= x∗
<function typed list (x)> ::= x⁺ - <function type>
<function typed list(x)>
<function type> ::= number
<emptyOr (x)> ::= ()
<emptyOr (x)> ::= x

Differences with WORLD : No need for most of this. The declaration is implicit and type is most of the time infered. There's no differences between predicate and function in WORLD appart from their type. They are not really function but entities too. The requirements are met using the include mechanic or actually defined in the same file.

#### Actions

<action-def> ::= (:action <action-symbol>
	:parameters (<typed list (variable)>)
	<action-def body>)
<action-symbol> ::= <name>
<action-def body> ::= [:precondition <emptyOr (pre-GD)>]
	[:effect <emptyOr (effect)>]
<pre-GD> ::= <pref-GD>
<pre-GD> ::= (and <pre-GD>∗)
<pre-GD> ::= (forall (<typed list(variable)>) <pre-GD>)
<pref-GD> ::= (preference [<pref-name>] <GD>)
<pref-GD> ::= <GD>
<pref-name> ::= <name>
<GD> ::= <atomic formula(term)>
<GD> ::= <literal(term)>
<GD> ::= (and <GD>∗)
<GD> ::= (or <GD>∗)
<GD> ::= (not <GD>)
<GD> ::= (imply <GD> <GD>)
<GD> ::= (exists (<typed list(variable)>) <GD> )
<GD> ::= (forall (<typed list(variable)>) <GD> )
<GD> ::= <f-comp>
<f-comp> ::= (<binary-comp> <f-exp> <f-exp>)
<literal(t)> ::= <atomic formula(t)>
<literal(t)> ::= (not <atomic formula(t)>)
<atomic formula(t)> ::= (<predicate> t∗)
<term> ::= <name>
<term> ::= <variable>
<f-exp> ::= <number>
<f-exp> ::= (<binary-op> <f-exp> <f-exp>)
<f-exp> ::= (- <f-exp>)
<f-exp> ::= <f-head>
<f-head> ::= (<function-symbol> <term>∗)
<f-head> ::= <function-symbol>
<binary-op> ::= <multi-op>
<binary-op> ::= −
<binary-op> ::= /
<multi-op> ::= ∗
<multi-op> ::= +
<binary-comp> ::= >
<binary-comp> ::= <
<binary-comp> ::= =
<binary-comp> ::= >=
<binary-comp> ::= <=
<number> ::= Any numeric literal
<effect> ::= (and <c-effect>∗)
<effect> ::= <c-effect>
<c-effect> ::= (forall (<typed list (variable)>∗) <effect>)
<c-effect> ::= (when <GD> <cond-effect>)
<c-effect> ::= <p-effect>
<p-effect> ::= (<assign-op> <f-head> <f-exp>)
<p-effect> ::= (not <atomic formula(term)>)
<p-effect> ::= <atomic formula(term)>
<p-effect> ::= (<assign-op> <f-head> <f-exp>)
<cond-effect> ::= (and <p-effect>∗)
<cond-effect> ::= <p-effect>
<assign-op> ::= assign
<assign-op> ::= scale-up
<assign-op> ::= scale-down
<assign-op> ::= increase
<assign-op> ::= decrease

Problems:
* DOES NOT SUPPORT HTN ! (needs extensions)
* Can not specify action cost

Differences with WORLD :
* Variables infered
* Concise anonymous quantifiers
* Cardinality restriction (using quantifiers)
* Using less symbols (reusing operators and quantifiers)
* Ability to do more complex conditions and constraints and effects (even meta-action)
* Can use effects and preconditions indifferently (same expressivity and definition)
* ~ Can use a separate domain independant inference engine whenever (ontology)

ex: predicate cardinality
		// code for this is partially broken and needs more formal definitions
		holds(!) (unique quantifier, only one thing can be held at a time)
		_ @ ! (each thing can be @ only one place at a time)
		red(*) (everything can be red at the same time)
		pair(_(2)) (only 2 things can be in the pair, can also be done with pair(!,!) )

ex: meta-action
		//solution quantifier used anonymously for queries in statements
		
		after(action, effects) pre (action eff ?); 
		after(action, effects) eff effects;

ex: new usages
		someAction funIndex 60;

We can encode almost anything into the domain but it may require more work on the planner's side just like PDDL. We separate general knowledge inference (computing 2+2, instanciation, etc) from the planner's code and that allows easy extensions and reusability (why we made ontologies in the first place).

#### Durative Actions

<durative-action-def> ::= (:durative-action <da-symbol>
	:parameters (<typed list (variable)>)
<da-def body>)
<da-symbol> ::= <name>
<da-def body> ::= :duration <duration-constraint>
	:condition <emptyOr (da-GD)>
	:effect <emptyOr (da-effect)>
<da-GD> ::= <pref-timed-GD>
<da-GD> ::= (and <da-GD>∗)
<da-GD> ::= (forall (<typed-list (variable)>) <da-GD>)
<pref-timed-GD> ::= <timed-GD>
<pref-timed-GD> ::= (preference [<pref-name>] <timed-GD>)
<timed-GD> ::= (at <time-specifier> <GD>)
<timed-GD> ::= (over <interval> <GD>)
<time-specifier> ::= start
<time-specifier> ::= end
<interval> ::= all
<duration-constraint> ::= (and <simple-duration-constraint>+)
<duration-constraint> ::= ()
<duration-constraint> ::= <simple-duration-constraint>
<simple-duration-constraint>::= (<d-op> ?duration <d-value>)
<simple-duration-constraint>::= (at <time-specifier>
<simple-duration-constraint>)
<d-op> ::= <=
<d-op> ::= >=
<d-op> ::= =
<d-value> ::= <number>
<d-value> ::= <f-exp>
…

We can do :
		action eff (@(2 sec, fluent(*)));
		action duration (_ >= (5 sec));

Note: most of this isn't even used in most papers because it gets way too complex way too fast.
> duration inequalities have not been explored to any serious extent - Gerevini

#### Derived predicates

<derived-def> ::= (:derived <typed list (variable)> <GD>)

It seems to miss a <name> part in the rule but thats what WORLD does best.

>  “parameters” serves to give the language a more unified look-and-feel - Gerevini

WORLD allows to construct a whole ontology behind fluents. Good inference engines and caching can make that heavily expressive construct quite effective in planning domains. You could build up constraints on how the entities and fluents must be handled. For example :
		"lang.w"; //Defines typing, quantifiers and definitions
		(a, b, c) :: Blocks;
		(x is p) : (p(x) : true);
		a is fragile;
		fragile(x) : (~ on x);
		// Block world here.

Now we cannot put anything ontop of `a` or it won't unify for instanciation.

Critics : "That's not usefull ! We can do problem transformation anyway !"
> We propose an adequate semantics for PDDL axioms and show that they are an essential feature by proving that it is impossible to compile them away - Thiébaux

I need to investigate OPT by Drew McDermott

### 12018-10-03

When I read on Ghallab's book that there's few works on planning and ontologies, I knew that the idea was proposed already. 

OPT is an attempt at interfacing ontologies and PDDL. It remains more into PDDL territory and is now seemingly abandonned. It uses a deduction engine from Lisp on OTP files that can therefore be a bit more usefull than regular PDDL. It most noticeably implements extensions of existing domains (:extends) and even HTN methods. 
		(define (domain subferry)
			(:extends (:uri "http://cadmium.cs.yale.edu/ferry.opt"
…
		(:action generalsearch ;;search book in usual description defined as author&title
			:parameters(?s - Seller ?d - Description)
			:vars (?title ?author - String ?bkid - String ?bk - Book)
			:value (?search_result - Message)
			:expansion (series (tag s1 (send ?s (search (("Title" ?title) ("Author" ?author)))))
			;; HTN stuff…


Nothing new since 2006 and PDDL3 is not entirely included in OTP. But OTP isn't contained in PDDL3 either. No real publications come to support this work that remains a draft of a framework. Since it is based on Lisp notation it is still technically contained in WORLD.

From its source code it seems its nothing more than a deducer plugged to a PDDL parser.

---

> An advantage of building a mapping from pddl+ to HA theory is that it forms a bridge between the Planning and Real Time Systems research communities. - Fox et al.

This is my argument but with ontologies. It seems to be a very important work to bridge planning and KR because it will lead to planners being simply another kind of inference engine that is simply domain dependant and will contribute to unification of works and code in AI (one of the goals of ontologies).

I need to investigate ANML and ADL? as they are the only other planning languages non PDDL related.

### 12018-10-04

Today's goal is to start a structure of paper and gather arguments for it.

The secondary objective is to scout the following papers :
* Improving AI Planning by Using Extensible Components by Michael Jonas
* PDDL+ (checking what isn't in PDDL 3.1)
* RDDL specifications
* AMNL
* PDDL's semantics

#### Checking update to PDDL 3.0

> The main language extension in PDDL 3.1, object fluents, are modeled after Functional Strips. - IPC 2008 & Geffner 2000

> One reason for this extension is that modeling using
multi-valued fluents is more intuitive. More importantly,
changing fluent values when looking for excuses leads to
more intuitive results than changing truth values of Boolean
state variables, since we avoid states that violate implicit
domain constraints - Coming up with good excuses - Keller

> For example, if we represent the location
of an object using a binary predicate at(·, ·), changing
the truth value of a ground atom would often lead to having
an object at two locations simultaneously or nowhere
at all. - Coming up with good excuses - Keller

That is what I address with cardinality of predicates, but it can in some cases be adressed with object fluents (PDDL3.1)

#### Important finding needing investigation before starting the paper

There's a paper on a language called Web-PDDL. It is an attempt of using ontologies for the fluent and apparently gained traction later in some ontology fields. Earlier works in the fields are transformation problems : turning service description in WSDL into PDDL. 

> the diversity of the web service domains is best addressed
by a flexible combination of complementary reasoning techniques and
planning systems […] 
> Instead, we argue that it is more fruitful to create a flexible non-monolithic framework, which allows to plug in those planners that are best suited for certain planning domains and tasks; this is essential for addressing changing or new requirements of the users. -- A PDDL Based Tool - Peer

Web-PDDL does the oposite : it backports Ontology into PDDL. It respects most of the standards of PDDL of the time while inserting ontological import and merging abbilities. Web-PDDL seems to be quite general purpose on the ontology side too as it is able to convert ontologies from a language to another. It does so using properties to relate notions from one ontology to the other (an insitu mapping of sorts).

#### Resuming exploration and argument gathering on the other papers

Extension proposal for processes : 
> Their proposal was not adopted by the rules committee for the third
International Planning Competition, mainly because most of
the members preferred to focus on the narrower extension to
“durative actions,”Another potential reason is that the semantics of processes were so complex that no one really understood them.

> A semantics for all of PDDL is hard to lay out, for a
couple of reasons. One is that the requirement-flag system
turns it into a family of languages with quite different be-
havior. For instance, the :open-world flag transforms the
language from one in which not is handled via “negation as
failure” into god-knows-what. -- Drew V. McDermott

Seems salty there but a very good point on why I couldn't find anything recent on this. Apparently it is hard to agree on mutliordinal and vague terms like "semantics".

From reading this apparently motivations can be way more easily accepted if they are supported by an example domain of interest (blockworld, rover, etc)

I need to provide complete syntax and semantics too as all these papers do.

PPDDL is an extension for PDDL to be able to express classical MDP (with probabilities and reward), RDDL is basically similar but with partially observable world (POMDP)

> However, **it would be unreasonable to assume there is one single compact and correct syntax for specifying all useful planning problems**. Thus, RDDL is not intended as a replacement for the PDDL family of languages [3] or PPDDL [4], rather it is intended to model a class of problems that are difficult to model with PPDDL and PDDL.

The more I read these papers the more I see the patern of :
1. PDDL needs that thing attached to it because that specific field needs it very badly
2. Dumping the BNF right away and discussing the improvement and specific changes to PDDL
3. A bit of explanation over an example
4. Formalization and "semantics"
5. Properties and justifications/proofs

Need more reading :

* In defense of PDDL axioms
* Improving AI Planning by Using Extensible Components
* Reasoning about Anonymous Resources and Meta Statements on the Semantic Web (Web-PDDL)

### 12018-10-05

> Without axioms, preconditions and effect contexts quickly become unreadable, or postconditions are forced to include supervenient properties which are just logical consequences of the basic ones. - Thiébaux

> However, as said above our results suggest that it might be much more efficient to deal with axioms inside the planner than to compile them away. In fact, our experiments with FF suggest that adding even a simple implementation of axioms to a planner clearly outperforms the original version of the planner run on the compilation.  - Thiébaux

		(:derived (clear ?x)
			(and (not (holding ?x))
			(forall (?y) (not (on ?y ?x))))))


### 12018-10-07

"Here is what we add to PDDL" ?

I spent the weekend running around and soldering stuff for the parents.

### 12018-10-08

Needing to think about the lang.w file…

### 12018-10-09

I found it :

{ } = ~ ;

Nope : I need equality.

Group with one element is equal to its element ?

I need to define true ? Here is the axioms for now :

Statements are triples. EOS is infered
For now I admit java literals except true and false. Is it a good idea ? I could use Peano's axioms to define the number literals later. Text literals can be treated as a container and then we define that container as escaped. Comments can also be declared as escaped containers with no value and then we derive the line comment from there.

ARRRRRRRRRRRRRRRGG

* We need equality and set builder's notation for quantifiers
* We need universal quantifier, contains, and entailement for equality
	\forall x \forall y [\forall z (z \in x \Leftrightarrow z \in y) \Rightarrow x = y].

Office phone : (04262) 34482

### 12018-10-10

I need a better formalization. I need to understand it entirely and to make it very consistent.

Entity is the set that contains everything except itself (it does contain the Type Entity tho)

In the Type space, Entity is maxium with regard to subsumption.

In the Quantifier space, * is maximum and ~ minimum to regard to NA

WORLD Oriented Reflexive Language Description

WORLD is a class of languages $\mathcal{L}$. Each language $L$ has an associated generative grammar along with a semantic description.

For each input $I$ a given language $L \in \mathcal{W}$ will generate a domain D included in the set of statement $\mathcal{S}$. A statement in D is said to be explicit.

An meta-properties are binary relations in E.

So We define an interpretation domain as d = < E_d, M_d > with E_d being the set of individual Entities of the domain and M_d = {m \in E_d^2}

Statements are triples of Entity (E_d^3) : the subject, the property and the object. They are represented using 3 meta-properties, one for each component. We can also represent statements in a port graph of the form (E_d, (E_d, P)^2) with the ports labels P = {subject, property, object}.

### 12018-10-11

Helping Samir with his planning of all time tables. I know he needed the help but I think could hav done more effort on that new assignement he got. But who am **I** to judge…

### 12018-10-12

I got to my apointement and it was helpful. Now I gotta at least work a little bit today.

Meta-properties are actually just properties… Why differenciate ? Binary relations is exactly what properties are in the end…

### 12018-10-14

Getting back to work eventually…
I need to clean the document because that's some absolute mess.

### 12018-10-15

…

### 12018-10-17

Getting back at redacting the paper.

> No one else sees but I got stuck
> And soon forever came

### 12018-10-18

Redacted the existing. Now to adding stuff.

RDF : { E³ } kinda ?

### 12018-10-19

Checking what is done in SOTA

### 12018-10-23

I had a burnout. I rested and now I can get back to work.

Impossible to find PDF Wiktor Macura. A Survey of Compiler-Compilers. May 25, 2005

### 12018-10-25

Maybe I'll do something today.

I need to know what I want as a result :

Better. I  need to organize my thoughts. That's not easy when I feel my brain is like mush.

I should just dump everything in there and then look for patern and dependancies. I need a map of it.

* Dynamic grammar
* Token inference ?
* Entity is the boss
* Property
* …

### 12018-10-26

I miraculously found a good article in a book that layed in the small cabinet next to me all these years. Just randomly opened it and pam.

It describes yet another conversion tool from the 00s. It got interesting approacha and vocabulary and is very straightforward even if the redacting could be better.

Now I gotta redact stuff just like that.

### 12018-10-29

SMSTP mostly done.

Reading Ramoul's thesis :

> les services Web sont automatiquement traduits en descriptions HTN en utilisant des ontologies
> OWL-S Xplan (Klusch et al. , 2005) est une approche hybride qui combine
la planification basée sur un graphe de plans (Blum & Furst, 1997) et la planification HTN pour fournir
un plan avec des services Web incomplets

> Une méthode m est un triplé m = (nom(m), expansion(m), contraintes(m)). nom(m)

Expansion are separated operators (tasks) and the constraints are separated so they can't express plans in HPDDL.

Rover is used as HTN example.

Oh, they dont have the backward trick in their planner. That's very bad.

> GTOHP utilise HPDDL qui est une version HTN du langage PDDL, alors que SHOP utilise un langage spécifique
présenté dans (Nau et al. , 2006)


---


### 12018-10-30

Almost spooky.


### 12018-11-01

sick…

W0 : triple, all entity, character class, equality
Deriv : must return to triple, must backtrack if type mismatch or other violations.
	container :  ( + mirror), new symbols
			we know (). (a,b,c,d) ',' is new. Must be delimiter
			we know (). (a,b) : , is property. THEN (a,b,c) : ',b' and ',c' is operator ERROR !
			+ ambiguity : chose left (parsing from left to right)
	parameters : entity (container) : container affects previous
	operator : new symbol affects next (as param) or old parameterized symbol affects next


Include 
"path"= ? ;

? is evaluator.


TODO : Find the rules fromp world 1 2 3 

uniqueness = unicity ?

*(statement) descope:

operators : quantifiers, ?, uncertainty, belief, access (private), etc


* = ? ;
~ = {};
?x = x;
?!(x) = {x};
?_ ~(=) ~;

*x :: !T;
*T :: Type;
Entity / *T;

? cannot be redifined. ? is assumed. smth = ? means domain.


### 12018-11-03

Mom is trying to help again. Idk if it'll work this time.

I don't feel so good today. Lots of insecurities and bad thoughts.

Native types are consequences of the first axioms and same goes /w most things in WORLD. 


* G0
	* Comments / Space
	* Literals
		* Values
	* Character classes
		* Identity
			* Equality
			* Naming
		* Types
			* Typing
			* Subsumption ?
	* First statement
		* Imports/Include files (acyclical)
		* Equality symbol
		* Solution anonymous quantifier + its symbol
			* Quantifiers
		* EOS
			
	* Triple
		* Statements
			*Subject, Property, Object

* Derivation
	* Reduction to triple (for derivation)
	* Container
		* Parameters
			* Variables
			* Scope
			* +container : Operators / Modifiers


Benchmarks : generate non mirror new symbols and check ambiguity in container inference.

Inference of same instance is in the interpretation thing.

Difference between inference and interpretation : interpretation will affect how knowledge will be stored and inference creates more knowledge.

* Consistence, validité

### 12018-11-04

Language de grammaire

Language de programmation

WORLD


The process of materialization of a notion of a language
(such as an object, a class, a context or a method) as an object inside the language itself
is called reification. -- 

### 12018-11-05

Today is redact + example time.

EXPLAIN

### 12018-11-06

Let's go ° °

I'm trying Quilter again. Let's hope it doesn't mess up…

It certainly is nice tho

Starting next section but I am tired of redacting for now.



### 12018-11-07

Needing a good formalism.


⩕ \doublewedge
Ξ \bauforms


Just E [markiplier meme here]

Lets make regular graphs. The only thing is that edges are also vertices ? nonkjhougihk

### 12018-11-08

I finished the first draft of $W_0$ !


Since we have scope we don't need namespaces like RDF

### 12018-11-09

I need to find a good plan and to rewrite / add the examples.


### 12018-11-11

I searched for a bed yesterday. Now back to find a plan.

### 12018-11-12

Time to rush the plan or something. I won't be able to work loads today…

Not based on RDF Graphs !!! https://www.w3.org/TR/2001/WD-rdf-mt-20010925/

> An RDF graph is a partially labeled directed graph in which nodes are either unlabeled
> - these are also called anonymous or blank nodes - or else labeled with either URIs or
> literals; arcs are labeled with URIs; and distinct labeled nodes have different labels. 

RDF graph with additional unique statement label on edges.

hypergraphe 3-uniforme (E, S) : S, P \subset E : S = { <s,p,o> : s,o \in E, p \in P } 

Language is uniform : only one type and a uniform storage of notions.

structure gives : identity, triples, native reification and uniformity.

axiom of conformity : global statements must BE true. 

Doesn't give first statement, literals and character classes.

Axiom UCD names : character classes + (storing names & conformity ) → literals (not boolean ?)

axiom meta-evaluation : ecapsulate units of knowledge, extract units of knowledge

identity + ????? → first statement

containers from smth…

### 12018-11-13

That was quite a nice thing I found there.

> Alternately, edges can be allowed to point at other edges, irrespective of the
> requirement that the edges be ordered as directed, acyclic graphs. This allows graphs 
> with edge-loops, which need not contain vertices at all. For example, consider the 
> generalized hypergraph consisting of two edges {\displaystyle e_{1}} e_{1} and 
> {\displaystyle e_{2}} e_{2}, and zero vertices, so that 
> {\displaystyle e_{1}=\{e_{2}\}} e_1 = \{e_2\} and 
> {\displaystyle e_{2}=\{e_{1}\}} e_2 = \{e_1\}. 
> As this loop is infinitely recursive, sets that are the edges violate the 
> axiom of foundation. In particular, there is no transitive closure of set membership
> for such hypergraphs. Although such structures may seem strange at first, they can be
> readily understood by noting that the equivalent generalization of their Levi graph is
> no longer bipartite, but is rather just some general directed graph.
-- [Wikipedia](https://en.wikipedia.org/wiki/Hypergraph#Generalizations)


### 12018-11-14

Continuing…

Couldn't reference Axioms… I need to meddle with cleverref

### 12018-11-15

Ok so, ZFC is 3 theories ontop of one another : Z+F+C.

Z is the most basic one. It actually got the first axiom as extensionality : ∀A ∀B [ ∀x (x ∈ A ⇔ x ∈ B) ⇒ A = B ].

So the equality is literally the first thing in Maths. (Second only to quantifiers).

I need to reinstall everyting for axioms…

### 12018-11-16

End of reinstall + stuff about axioms and crossreferences.

All set. (almost, theres some crossref that are broken)


### 12018-11-17

Need to find the red string.

WORLD is an ontological framework.

### 12018-11-18

For delim :

 Ps = Punctuation, open
 Pe = Punctuation, close
 Pi = Punctuation, initial quote (may behave like Ps or Pe depending on usage)
 Pf = Punctuation, final quote (may behave like Ps or Pe depending on usage)
 Po = Punctuation, other

Ps, (Pi?), left_, opening_
Pe, (Pf?), right_, closing_
Po, separator, punctuation, stop


### 12018-11-19

Let's work a little. Plz brain work now…

Some more UTF-8 justification

0061;LATIN SMALL LETTER A;Ll;0;L;;;;;N;;;0041;;0041
0041;LATIN CITAL LETTER A;Lu;0;L;;;;;N;;;;0061;

### 12018-11-23

I had a lot of work for teaching and got my wallet stolen along with hospital time. Excuses excuses excuses



### 12018-11-24

I think I got a good justification of all of what I'm trying to sell. Let's just make sure there's two parts in the document because we need to separate WORLD and Color.

I need to count the number of planners in the IPC that uses domain convertion tools.

I need criterias : 

* Use total conversion to an intermediate language
* Use problem conversion to another paradigm
* Use preprocessing to simplify the domain before execution


### 12018-11-25

Lets continue the planning part while discreetly continuing WORLD.

### 12018-11-26

Today I need to

+ phone to ergo
+ email el mehem
+ get my google in order
+ phone to smstp
+ see the social assistant


Astré mission handicap
gourdine sandrine 0472431477
harker
0427465757 aménagement d'études
fiche de liaison handicap


### 12018-11-27

Well I done a bit of admin. Now to the work.

I forgot about the scope. I need to integrate it in the framework seamlessly.

Or not ? That's quite bothersome.

### 12018-11-28

Ok so using hebrew letters was a bad idea. I'm chosing c for scope…

I need more symbols in my table like \mathcal{E,D} and the power set and everything actually.

### 12018-11-29

I needed some rest.

### 12018-11-30

Getting to work on the PDDL problem…

I got accepted but short. 

### 12018-12-03

I took the weekend off. 

### 12018-12-05

Got my inscription on its way.

### 12018-12-06

Boss wants me there but that means that I will probably be unable to work much.

### 12018-12-07

I just got a new screen and keyboard. That's quite fancy.

### 12018-12-10

That was a bad weekend. Let's try to work a little at least.

I did good but I need to refine the taxonomy of planning.

Planners will output a plan whatever the case (except ofc if there's no answer). 

I need to make a generalization of this search.

If I needed a taxonomy but only on a restricted set of paradigm :

Recovered from earlier : 

A*( Graph g, init I, goal G,  heuristic h)

FF : A*( (S,A) , s_0 , s^* , h_FF)
PSP : last(A*( (P, Refinements), π_ø, π : Flaws(π)=ø ? , h_VHPOP?))
HTN : A*( (O, ?), Ω, { a : methods(a)=ø }, h?)


I need to find all descriptions for all algo

### 12018-12-11

I need to do heart today…

The process of registration for the 5th year is killing me. I was denied in first instance of the process.
Now it seems like everyone is wondering what to do with me.

Good thing tho, a new Infected Mushroom title !

I have a kinda hard deadline for the thesis… Gotta go fast.

### 12018-12-12

Today is the last day to submit with correct price for the inscription.

NEW IM Album !!!!

### 12018-12-13

Gotta register as a speaker but I need to validate.

Starting thesis redaction (plan only)

### 12018-12-15

I took a day off.

I need to redo my plan to begin with.

Language of description: describe a situation

Generic system of knowledge description

In color : speak of each planning paradigm (acording to taxonomy if found.)

### 12018-12-16

Starting the redaction of the thesis from Chapter 1.

### 12018-12-17

Trying to fix formating and enabling two columns in some parts of the thesis.

ICAART doesn't like my formating…

### 12018-12-18

Searching for more info on how to math. I found again the thing on wikipedia about edge-loops.

### 12018-12-19

The guy that made the changes on Wikipedia responded to me with a bucket load of info !!!

I am reading all of this since it is so close to what I did. 
I'm a bit sad that my stuff doesn't seem so unique anymore but glad that at least some people think like me.

Fixing GitLab and stuff.

### 12018-12-20

Reading all sheaf docs. More than 40 pages of highly complex maths to understand.

### 12018-12-21

ICAART is really bad. Reformatting the author and fixing their poorly written format.

Getting back to read.

So sheaves are interesting but apparently I may not need them ?

### 12018-12-22

Studying AtomSpace shown me that they don't really use sheaves.

sgnmxps

Atomspace is a tree structure. It is meant to be universal but super duper slow.

### 12018-12-23

Getting back to redact about sheaves and stuff.

Finished until BNF.

### 12018-12-24

!["Almost Christmas" means it wasn't Christmas !](https://pbs.twimg.com/media/DRvqoMxVwAAySp5.jpg)

Finished all the first part. It will need adjustments later.

### 12018-12-25

![It Christimas, Merr Chirstmas](https://i.kym-cdn.com/photos/images/newsfeed/000/875/649/1a4.png)

### 12018-12-26

I am transferring WORLD into the document.

### 12018-12-27

Light corrections and continuation.
I am wanting to get this chapter complete by the end of the week at worse and tomorrow at best.

The remaining week will be used for corrections, rewriting, formatting and diagrams. Diagrams last since I will use my screen at work for that.

### 12018-12-28

Apparently if it doesn't follow the sheaf axioms it is not a sheaf so I created something entirely new. Also the seeds are to make localized algorithms easier since the structure has a different granularity. I will also rename WORLD even if that means some work.

Now I am continuing on the last part of the chapter hopping to mostly finish today to start the corrections this weekend.

*s(e) : (e scope s);*(e) : (e scope (e));

Screw it I'm removing the scope operator.



