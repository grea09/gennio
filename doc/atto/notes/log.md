---
title: Atto Logs
author: Antoine GRÉA
---

> Project

        \|/\
        -o--\
        /|\  \tto


* Follow up to duck for the last sprint.
* Dates in ISO-HE (Human Era).
* Sorted by date from most recent to most ancient.
* Written in 21^st^ Century English.


# Phase 3

## 12019-11-01

Speak about model theory
Cite sections and chapters that uses stuff
Add references to previous tools when needed.
Check if enough explanation


## 12019-10-30

Schema of theory dependancies.

Well, I won't lose a friend to work so here I go.
Too bad I was feeling good for once.


## 12019-10-29

Lots of bad. Suicidal thoughts back again.

Needs organization.

Mindmap !!!!



## 12019-10-27

Bad rush.

Schema of abstraction, structure, circularity and reification.



## 12019-10-20

f1 x f2 = x → f1(x) x f2(x)
null x f = f


## 12019-10-18

Equation in FOL + defs of comb and sup


## 12019-10-17

\to → \to
: → V \to
\none → \none
= → \none (variable) \to
= → \bowtie \to ? (currying ?)

…

Association & Identity (affectation) in axioms

## 12019-10-12

Dans l'antiquité, la philosophie, les mathématiques et la logique étaient considéré comme une seule discipline. Depuis Aristote nous avons réalisé que le monde n'es pas que noir et blanc mais rempli de nuance et de couleur. L'inspiration de cette thèse viens d'un des philosophe et scientifique les plus influent de son époque: Alfred Korzibsky. Celui-ci a fondé une discipline qu'il a nommé *sémantique générale* qui consiste à traiter les problèmes de représentation des connaissance chez les humains. Korzibsky trouve alors que la connaissance complète de la réalité étant inaccessible, il nous faut abstraire. Cette abstraction n'a alors de ressemblance avec la réalité que de part sa structure. On retrouve alors dans ces travaux précurseurs, des notions similaires à celle des languages de description morderne.

C'est à partir de cette inspiration que le présent document est construit. On démarre alors, hors des sentiers batut et au déla de l'informatique par une brève excursion dans le monde des formalisme mathématique et logique. Ceci permet de formaliser un language qui permet de se décrire lui-même par structure et qui évolue avec son usage. Le reste des travaux illustre les applications possibles à travers des dommaines spécifique comme la plannification automatique et la reconnaissance d'intention.



## 12019-10-11

Context: Theory of mind

Intent recognition in AI

Why ? Interest ? Problem ?


## 12019-10-09

Explain what a fondation of mathematics is and how its no big deal.

## 12019-10-06

Getting filters fixed


## 12019-10-05

Correction

http://grea.me/apps/polls/poll/OKhXcLwjfScEwIA9

## 12019-10-03

Submitting the draft


## 12019-09-29

Chap 3


## 12019-09-28

Unlogged days + bad times.

Correction up to chap 2

## 12019-09-22

Glossaries: entry in yaml
(+abrev) or @gls:abrev ?

(+^pop) = POP[^Partial Order Planning]
(+pop) = POP

# Phase 2

## 12019-09-19

Deadline is now.
I'm screwed.

I deserve this.


## 12019-09-16

Unlogged days.

Getting to recover Rico.

Recovered: 

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

---

For partial ordered plans. Sohrabi said that the less good plans have a negligeable impact on the probability and can be ignored. Compute the best score considering order constraints.

Each steps that can be done at the same time are "merged" into a pool of infered things.

P(G|O) = SUM_pi [P (O|pi)P (pi|G)] P (G)

P(O|pi) = P(pi|O) * P(O)

	P(pi|O) = (a observed -  b (Missed + noisy) fluents) * number of order merges *
		P(pi) // function of abstraction


P(pi|G) = cost of plan normalised for all possible plans * P(pi) // function of abstraction

Comparison to POP with complete heuristic and only one plan.

(POP with diverse results doesn't exists ?)

---

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

---

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



## 12019-09-12

**Missing days : 3**

…

## 12019-09-09

**Missing days : 3**

## 12019-09-06

**Missing day : 1**

Getting back at redaction.

## 12019-09-04

Roy uses frequencies with a learning period. I need to make sure I got the right way to represent the tables.

## 12019-09-03

Stayed home.

## 12019-09-02

Unlogged days.

Doing figures.

The planning one:
\plan_2[
          actions -> pre eff -> action...
]

right side: sohrabi


## 12019-08-30

Skipping and straight to chap 6.


## 12019-08-29

Trying...


## 12019-08-28

**Missing day: 1**

I don't feel good at all.

number of cycles = |\{ c : c \in \chi_{g_O}^n(a) \land a \in c \land n>1 \}|

autocycle = [ (a \rightarrow a) \in \chi_g_O ]

h(a) = \lBrace f(µ££µ£µµ£

## 12019-08-26

**Missing day: 1**

Unlogged two days

I can't make it
…


## 12019-08-23

Making all the necessary explanation to rewrite formulas.


## 12019-08-22

**Missing day: 1**

Getting new symbols to work.



## 12019-08-20

**Missing days: 2**

Just got back from some dark times. I managed to get symbols to display. Now I just need to make them closer and define a macro.


## 12019-08-17

$\leftblackspoon$

$\leftrightspoon$

$\bullet^{\uprightcurvearrow}_{\downrightcurvedarrow}$

$\bullet^{\rightdowncurvedarrow}_{\rightupcurvedarrow}$

## 12019-08-16

**Missing day: 1**

Fixing formulaes again.

\multimap

\multimapdotboth
\multimapdot

bullet + STRIKETHROUGH




## 12019-08-14

Worked without logs but slow.

@alg7

## 12019-08-11

Messed up my sleep…


## 12019-08-10

Worked without logs for small tasks

I feel better

## 12019-08-07

Still woozy…


## 12019-08-06

Doing schemas


## 12019-08-05

**Missing days: 6**

I'm sorry.


## 12019-07-30

Getting outside to work on my tablet.


## 12019-07-28

Trying


## 12019-07-27

Server up again. Did work on tablet.


## 12019-07-26

Server down and no connection with anything at home.

I'm worried.


## 12019-07-25

**Missing days: 6**

…

I don't feel too good



## 12019-07-19

Getting to chap 5

## 12019-07-18

**Missing days: 3**

Bleh

Doing gitlab first after loosing chap2

## 12019-07-15

**Missing day: 1**

Getting to fix everything
Chapter 4 finished.

## 12019-07-13

**Missing day: 1**

Did some more formalism

Issues with compilation

## 12019-07-11

Getting to the general equation.


## 12019-07-10

**Missing days: 2**

Doing all design in chap 4


## 12019-07-08

Fixing fonts

Lots of rdv

## 12019-07-07

**Missing day: 1**

Finishing chap 4

Issues with fonts…


## 12019-07-05

Half of chap 4 done.


## 12019-07-04

Start of chap 4
got some issues with notation and types.

## 12019-07-03

End of example.

## 12019-07-02

Example done but needs listings.


## 12019-07-01

**Missing day: 1**

Getting to the example.


## 12019-06-29

Finished chapter 2 and good progress on 3


## 12019-06-28

**Missing days: 2**

Finished the first chapter…


## 12019-06-26

**Missing days: 21**

I… Really don't know

I should just not care and do it.




## 12019-06-05

Small touches


## 12019-06-04

**Missing day: 1**

Did some stuff and almost finished chapter 1.

commutative, associative

> c   
^ c a 
X c a 
V     
o   a 
ø   a 
= c   

Semiring then (F, X, o)


## 12019-06-02

Association today

Association recursively uncurry the function application on the left of the specification operator



## 12019-06-01

**Missing day: 1**

Not feeling well.
…


V = x, ? → ⦃D(x):&o?⦄
Like currying but for sets. like map in python and stuff.

Allpies a function on all elements of a set.


## 12019-05-30

All wrong with my thesis:

* Russel's paradox is obviously here. There might be a way but its not addressed.
* My "fondation" is too close to category theory. It seems like a rip off. And if not of that of lambda calculus and things. It's just a bad mashup of stuff I vaguely found on Wikipedia.
* The "operators" I found are probably existing and misnommed. I bet on that being in a textbook or even on Wikipedia in places I was too lazy to read.
* WORLD or SELF is just a small parser with a dummy hack to make it "dynamic". It barely evolve or infer anything ang is just a fancier way to write a for loop.
* Color is probably way off in the applicability of it. Probabilistic planning is misrepresented and I clearly didn't understood any of it. Also I am betting that the satus quo is actually the most solid framework we'll have considering all the smarter people working on it. It is also flawed and internally inconsistent. It is barely theoritical and probably inefficient AF
* I can't say that any of my planners are a success. The first is obviously badly coded and just a fraud. The rest is just misnommed notions found elsewhere or something. I don't deserve those publications. Heart is slightly better but is fundamentally flawed as the formalism is off and the domain has only one solution since actions cannot be repeated. That's its weakspot clearly. If I allow repeatability, I can't prevent cycles and the stuff gets haywire. And also my "proofs" all fall with that.
* For recognition it's a 100% screwup on my part. It's just a smoking pile of failure and regrets. I simply did my thesis in the wrong way through and through.
* And to top it off I am playing the depression card for years and I am too lazy to simply get to work and face my responssabilities. The redaction is lazy and weak and incoherent. It is unecessarily complex and misleading. Some of it are just copy and paste from previous jobs without even reading it...

I am a real fraud and I won't deserve any of what good will happen next as I cheated my way there.


## 12019-05-29

**Missing day: 1**

bleh

!!!! Currying = valuation = meta ????!!???

Also when currying the definition of $\gtrdot$ we obtains an interesting function.

::: {.definition #def:valuation name="Valuation"}
The valuation function turns any formula of function into its simplest value. We note the valuation of a function $[[x]]$ and define it as $x \to (\gtrdot \to x)$.^[$[[]] = x \to (\gtrdot \to x)$].
:::

If we compute its *inverse* (see @later) it becomes the *lifting function* (notted $]][[$) that turns any function into a variable. The currying of the definition of $\gtrdot$ can be noted as $]][[\gtrdot]][[$.


I need to redo most of it again …

[[ \ldbrack ]] \rdbrack



## 12019-05-27

Introduction finished, doing examples and dealing with margin figures.

## 12019-05-26

**Missing day: 1**

Trying to finish most of introduction.


## 12019-05-24

**Missing days: 8**

Doing the introduction.

## 12019-05-16

**Missing days: 4**

I'm getting a "class" with a group of students soon. I'll try to resume my work after that. I need to finish the first chapter soon so I can catch up with the planned advancement.

$\chi = v \to \{ e: v \in \chi(e) \}$
$\chi = e \to \{ v: e \in \chi(v) \}$

Barelly got anything done. But I did work.


## 12019-05-12

* Phase 1: complete
* Env: Ready
* Mind: Stable
* Pancake: baked

> 2 week sprint engaged.

Starting…

Member function $\in = x, S \to ?????$

$S = \gtrdot \to x$ with $x$ being a member of $S$


# Phase 1

[See](../../duck.md)


