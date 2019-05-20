/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package birdFlockBenchmarks;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.RunnerException;

import javafx.scene.image.Image;
@Measurement(iterations =10)
@Warmup(iterations = 5)
@Fork(value=1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)

public class MyBenchmark {
	public Image testImage;

	@Setup(Level.Invocation)
	public  void setup() {
		Image testImage = new Image((new File("./src/main/imageFolder/bird 5.png").toURI().toString()));
    	MainController.blackAndWhite(testImage);
    	MainController.makeBirdSet(testImage);
    	MainController.checkForAdjacentBlackPixels2(MainController.blackAndWhite(testImage));
    	MainController.addBirdBoundariesAndLabel(testImage);
    	
		
	}
	
    @Benchmark
    public void testMethod() {
        // This is a demo/sample template for building your JMH benchmarks. Edit as needed.
        // Put your benchmark code here.
    }
    
    @Benchmark
    public void testBlackAndWhiteBM() {
    	MainController.blackAndWhite(testImage);
    }
    
    @Benchmark
    public void testMakeBirdSetBM() {
    	MainController.makeBirdSet(testImage);
    }
    
    @Benchmark
    public void testCheckingForAdjacentPixelsBM() {
    	MainController.checkForAdjacentBlackPixels2(MainController.blackAndWhite(testImage));
    	
    }
    
    @Benchmark
    public void testAddingBirdBoundariesAndLabelsBM() {
    	MainController.addBirdBoundariesAndLabel(testImage);
    }
    
    public static void main(String[] args) throws
    						RunnerException, IOException {
    	Main.main(args);
    }

}
