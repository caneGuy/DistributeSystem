/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cane.distribute.heartbeat;

import cane.distribute.heartbeat.protocol.BaseHeartbeatProtocol;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @see <a href="http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.80.7427&rep=rep1&type=pdf">
 *   AccurateFailureDetector</a>
 */
public class AccurateFailureDetector extends AbstractFailureDetector {

  private ConcurrentHashMap<String, ArrivalWindow> endpointToSamples =
          new ConcurrentHashMap<String, ArrivalWindow>();

  private int phiSuspectThreshold = 8;

  @Override
  protected void detectFailure(BaseHeartbeatProtocol message) throws Exception {
    interpret(message.getEndpoint());
  }

  public void interpret(String endpoint) {
    ArrivalWindow windowForThisEp = endpointToSamples.get(endpoint);
    if (windowForThisEp == null) {
      windowForThisEp = new ArrivalWindow();
    }
    long now = System.currentTimeMillis();
    double phi = windowForThisEp.phi(now);
    if (phi > phiSuspectThreshold) {
      // add listener here
      // example:listener.suspect(ep)
    }
  }

  private class ArrivalWindow {

    private LinkedList<Long> arrivalIntervals = new LinkedList<Long>();

    private int maxSize = 10; // need to be configurable

    private double lastMean = 0; // used to avoid overflow

    protected double phi(long tnow) {
      arrivalIntervals.add(tnow);
      int size = arrivalIntervals.size();

      double t = arrivalIntervals.get(size - 1) - arrivalIntervals.get(0);
      double probability = p(t);
      double log = (-1) * Math.log10( probability );

      if (size > maxSize) {
        arrivalIntervals.remove();
      }
      return log;
    }

    private double p(double t) {
      double mean = mean();
      double exponent = (-1)*(t)/mean;
      return 1 - (1 - Math.pow(Math.E, exponent));
    }

    private double mean() {
      int size = arrivalIntervals.size();
      if (size <= maxSize) {
        lastMean += arrivalIntervals.get(size - 1) / size;
      } else {
        lastMean += (arrivalIntervals.get(size - 1) - arrivalIntervals.get(0)) / maxSize;
      }
      return lastMean;
    }
  }
}
