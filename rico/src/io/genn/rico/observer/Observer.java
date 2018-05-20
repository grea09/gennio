/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.rico.observer;

import com.google.common.eventbus.EventBus;
import io.genn.world.CompilationException;
import io.genn.world.World;
import io.genn.world.data.Entity;
import io.genn.world.lang.Types;
import java.util.List;
import static me.grea.antoine.utils.collection.Collections.list;
import me.grea.antoine.utils.log.Log;

/**
 *
 * @author antoine
 */
public class Observer implements Runnable{
	private final World world;
	public final List<Observation> observations;

	public Observer(World world) {
		this.world = world;
		this.observations = list();
	}

	@Override
	public void run(){
		while(!Thread.interrupted()){
			try {
				Entity tea = world.flow.create("tea");
				Entity _tea_ = world.flow.create(Types.GROUP, list(tea));
				Entity taken = world.flow.create("taken", _tea_);
				world.flow.store.global();
				Thread.sleep(20);
				observations.add(new Observation(taken, 1.0f, System.currentTimeMillis()));
				observations.notify();
				Thread.sleep(1000);
			} catch (InterruptedException | CompilationException ex) {
				Log.f(ex);
			}
		}
	}
}
