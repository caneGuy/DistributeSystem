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

/**
 * Base class for failure detector.
 */
public class AbstractFailureDetector {

  /**
   * Process received heartbeat message and do failure detect.
   * Child implementation should add listener to use detected result.
   * @param message
   * @return weather reporter process failure or not
   */
  protected void detectFailure(BaseHeartbeatProtocol message) throws Exception{
    throw new Exception("Need to be implemented!");
  }
}
