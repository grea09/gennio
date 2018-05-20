"lite.w";

water :: Liquid;
cup :: Container;
spoon :: Ustensil;
sugar :: Additive;
(tea, coffee) :: Drink;

//(taken(!), hot(_), ! in !) :: Fluent;

take(item) pre (taken(~), ?(item));
take(item) eff (taken(item));

heat(thing) pre (~(hot(thing)), taken(thing));
heat(thing) eff (hot(thing));

pour(stuff,into) pre (stuff ~(in) into, taken(stuff));
pour(stuff,into) eff (stuff in into);

put(utensil) pre (~(placed(utensil)), taken(utensil));
put(utensil) eff (placed(utensil), ~(taken(utensil)));

infuse :: Action;
infuse(extract,liquid,container) method (
	init(infuse(extract,liquid,container)) -> take(extract),
	init(infuse(extract,liquid,container)) -> take(liquid),
	take(liquid) -> heat(liquid),
	heat(liquid) -> pour(liquid, container),
	take(extract) -> pour(extract, container),
	pour(liquid, container) -> goal(infuse(extract,liquid,container)),
	pour(extract, container) -> goal(infuse(extract,liquid,container)),
	heat(liquid) -> goal(infuse(extract,liquid,container))
);

make :: Action;
make(drink) method (
	init(make(drink)) -> take(spoon),
	take(spoon) -> put(spoon),
	init(make(drink)) -> infuse(drink,water,cup),
	infuse(drink,water,cup) -> take(cup),
	take(cup) -> put(cup),
	put(spoon) -> goal(make(drink)),
	infuse(drink,water,cup) -> goal(make(drink)),
	put(cup) -> goal(make(drink))
);


init eff (hot(~), taken(~), placed(~), ~ in ~);
goal pre (hot(water), tea in cup, water in cup, placed(spoon), placed(cup));
