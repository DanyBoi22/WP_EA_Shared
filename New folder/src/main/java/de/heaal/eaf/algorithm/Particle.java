/*
 * Evolutionary Algorithms Framework
 *
 * Copyright (c) 2023 Christian Lins <christian.lins@haw-hamburg.de>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.heaal.eaf.algorithm;

import de.heaal.eaf.base.VecN;
import de.heaal.eaf.base.GenericIndividual;

/**
 * A Particle individual with velocity and position.
 * 
 * @author Christian Lins <christian.lins@haw-hamburg.de>
 */
public class Particle extends GenericIndividual {
    
    private final VecN velocity;
    private VecN tempPosition;
    
    public Particle(VecN position) {
        super(position); // initializes the genome
        velocity = new VecN(position.len());
        tempPosition = position.copy();
    }
    
    private Particle(VecN position, VecN tempPosition, VecN velocity) {
        super(position);
        this.tempPosition = tempPosition;
        this.velocity = velocity;
    }
    
    @Override
    public Particle copy() {
        return new Particle(genome.copy(), tempPosition.copy(), velocity.copy());
    }
    
    /**
     * Returns the velocity vector of this Particle.
     * @return 
     */
    public VecN getVelocity() {
        return this.velocity;
    }
    
    /**
     * Returns the best position of this Particle (the genome).
     * @return 
     */
    public VecN getPosition() {
        return this.genome;
    }
    
    public void setPosition(VecN pos) {
        this.genome = pos;
    }
    
    public VecN getTempPosition() {
        return this.tempPosition;
    }
    
    public void setTempPosition(VecN pos) {
        this.tempPosition = pos;
    }
    
    public float distance(Particle other) {
        if (genome.len() != other.genome.len()) {
            return Float.POSITIVE_INFINITY;
        }
        
        float sum = 0;
        for (int i = 0; i < genome.len(); i++) {
            float diff = genome.array()[i] - other.genome.array()[i];
            sum += diff * diff;
        }
        return (float)Math.sqrt(sum);
    }
}
