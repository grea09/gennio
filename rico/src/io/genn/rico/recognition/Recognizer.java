/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.rico.recognition;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import io.genn.color.heart.Heart;
import io.genn.color.planning.problem.Problem;
import io.genn.color.planning.problem.Solution;
import io.genn.color.world.WorldControl;
import io.genn.rico.observer.Observation;
import io.genn.rico.observer.Observer;
import io.genn.world.CompilationException;
import io.genn.world.data.Entity;
import io.genn.world.lang.Types;
import java.util.List;
import java.util.concurrent.Callable;
import static me.grea.antoine.utils.collection.Collections.list;
import me.grea.antoine.utils.log.Log;

/**
 *
 * @author antoine
 */
public class Recognizer implements Callable<Solution> {

	private final WorldControl control;
	private final Problem problem;
	private final Observer observer;
	private int currentObservation;
	public Solution solution;
	private Thread callingThread;

	public synchronized void handleObservations() throws InterruptedException {
		if (callingThread != null || !callingThread.isAlive()) {
			return;
		}
		while (!Thread.interrupted()) {
			observer.observations.wait();
			callingThread.interrupt();
			//TODO ?
		}
	}

	public Recognizer(WorldControl control, Observer observer) throws CompilationException {
		this.observer = observer;
		this.control = control;
		this.problem = control.problem();
		this.solution = problem.solution;
		currentObservation = observer.observations.size() - 1;
	}

	@Override
	public synchronized Solution call() {
		this.callingThread = Thread.currentThread();
		Heart planner = new Heart(problem);
		solution = problem.solution;
		try {
			planner.solve();
		} catch (InterruptedException ex) {
//			Log.i(ex);
		}
		Log.d("FINIH");
		solution.notify();
		return solution;
	}
}
