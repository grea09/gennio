/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.rico.observer;

import io.genn.world.data.Entity;

/**
 *
 * @author antoine
 */
public class Observation {
	Entity fluent;
	float confidence;
	long occurence;

	public Observation(Entity fluent, float confidence, long occurence) {
		this.fluent = fluent;
		this.confidence = confidence;
		this.occurence = occurence;
	}

	@Override
	public String toString() {
		return "Observation{" + "fluent=" + fluent + ", confidence=" + confidence +
				", occurence=" + occurence + '}';
	}
	
	
	
}
