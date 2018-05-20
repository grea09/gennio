---
title: "RICO: Intent recognition using abstract inverted planning"
author: 
 - Paper \#42
# - Antoine Gréa
# - Laetitia Matignon
# - Samir Aknine
#institute: first.lastname@liris.cnrs.fr, LIRIS UMR5205, University of Lyon, 69622 Villeurbanne, France
tags: [Activity and Plan Recognition, Inverted planning]
abstract: Stuff…

bibliography: bibliography/rico.bib
style: ieee
---

# Introduction {-}



# Related Works


# Definitions
In order to make the notations used in the paper more understandable we gathered them in @tbl:symbols.

**Symbol**                            **Description**
----------                            ---------------
$\mathcal{D}, \mathcal{P}$            Planning domain and problem.
$\mathit{pre}(a)$, $\mathit{eff}(a)$  Preconditions and effects of the action $a$.
$\mathit{methods}(a)$                 Methods of the action $a$.
$var : exp$                           The colon is a separator to be read as "such that".
$[exp]$                               Iverson's brackets: $0$ if $exp=false$, $1$ otherwise.

: The symbol $\pm$ demonstrates when the notation has signed variants. {#tbl:symbols}

## Planning

The domain specifies the allowed operators that can be used to plan and all the fluents they use as preconditions and effects.

::: {.definition name="Domain"} :::
A domain is a tuple $\mathcal{D} = \langle C_\mathcal{D} , R, A \rangle$ where:

* $C_\mathcal{D}$ is the set of **domain constants**.
* $R$ is the set of **relations** (also called *properties*) of the domain. These relations are similar to quantified predicates in first order logic.
* $A$ is the set of *operators* which are fully lifted **actions**.
:::::::::::::::::::::::::::::::::::

**TODO : explain and sumarize**

## Plan Recognition

In intent recognition the aim is to find the plan (or goal) that an agent is willing to do based on observations of their behavior. The following definitions are adapted from automated planning and the work of Soharbi _et al._ [@sohrabi_plan_2016].

::: {.definition name="Observation"} :::
An observation is a tuple $o = \langle f_o, t_o \rangle$ with:

* $f_o$ is the **observed fluent**.
* $t_o$ is the **time of occurence** 

We note $O$ the currently considered sequence of observations.
:::::::::::::::::::::::::::::::::::

::: {.definition name="Plan Recognition Problem"} :::
We define a plan recognition problem as a tuple $\mathcal{R} = \langle \mathcal{D}, I, O, \mathcal{G} \rangle$ whith :

* $\mathcal{D}$ being the associated *planning domain*,
* $I$ being the **initial state** of the world,
* $O$ being the **sequence of observed events** and
* $\mathcal{G}$ being the set of **possible goals** the agents can pursue.
:::::::::::::::::::::::::::::::::::

## Probabilities and approximations

We define a probability distribution over dated states of the world. That distribution is in part given and fixed and the rest needs computation. **TODO : that's super bad…**

Here is the list of given prior probabilities and asumptions :

* $P(O)=\prod_{o\in O} P(o)$
* $P(\mathcal{G}) = \sum_{G\in \mathcal{G}}P(G) = 1$ since we assume that the agent must be pursuing one of the goals.
* $P(G|\pi) = 1$ for a plan $\pi$ appliable in $I$ that achieves $G$.

From dirrect application of Bayes theorem and the previous assomptions, we have :

$$ P(\pi|O) = \frac{P(O|\pi) P(\pi)}{P(O)} = \frac{P(O|\pi) P(\pi|G) P(G)}{P(O)}$$ {#eq:plan-obs}

$$ P(G|O) = \frac{P(O|G)P(G)}{P(O)}$$ {#eq:goal-obs}

From the total probability formula :

$$P(O|G) = \sum_{\pi \in \Pi_G} P(O|\pi) P(\pi|G)$$ {#eq:obs-goal}



# References {-}

\setlength{\parindent}{-0.2in}
\setlength{\leftskip}{0.2in}
\setlength{\parskip}{8pt}
\noindent\noindent\noindent\noindent
