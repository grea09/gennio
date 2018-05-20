/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.rico;

import io.genn.color.planning.problem.Solution;
import io.genn.color.world.WorldControl;
import io.genn.rico.observer.Observer;
import io.genn.rico.recognition.Recognizer;
import io.genn.world.World;
import java.util.concurrent.FutureTask;
import me.grea.antoine.utils.log.Log;

/**
 *
 * @author antoine
 */
public class Main {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		
		try {
			Log.i("Opening the world...");
			World world = new World("data/kitchen_tea.w");
			Log.i("Compiling...");
			Log.ENABLED = false;
			world.compile(false);
			Log.ENABLED = true;
			Log.i("Parsing...");
			WorldControl control = new WorldControl(world.flow);
			
			Observer observer = new Observer(world);
			Recognizer reco = new Recognizer(control, observer);
			FutureTask<Solution> futureTask = new FutureTask<>(reco);
			Thread recoThread = new Thread(futureTask);
			recoThread.start();
			Thread.currentThread().sleep(50);
			recoThread.interrupt();
			recoThread.join();
			Log.i(reco.solution.plan());
			
			
		} catch (Exception ex) {
			Log.f(ex);
		}
	}
	
}
