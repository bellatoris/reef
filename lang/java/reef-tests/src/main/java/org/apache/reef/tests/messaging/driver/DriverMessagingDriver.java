/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.reef.tests.messaging.driver;

import org.apache.reef.driver.client.JobMessageObserver;
import org.apache.reef.driver.evaluator.AllocatedEvaluator;
import org.apache.reef.tang.annotations.Unit;
import org.apache.reef.wake.EventHandler;
import org.apache.reef.wake.time.Clock;
import org.apache.reef.wake.time.event.Alarm;
import org.apache.reef.wake.time.event.StartTime;

import javax.inject.Inject;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Dummy implementation of a driver.
 */
@Unit
final class DriverMessagingDriver {

  private static final Logger LOG = Logger.getLogger(DriverMessagingDriver.class.getName());

  private static final int DELAY = 2000; // 2 sec.

  private final Clock clock;
  private final JobMessageObserver client;

  @Inject
  DriverMessagingDriver(final Clock clock, final JobMessageObserver client) {
    this.clock = clock;
    this.client = client;
  }

  final class StartHandler implements EventHandler<StartTime> {
    @Override
    public void onNext(final StartTime startTime) {
      // Schedule an alarm to not go idle immediately
      clock.scheduleAlarm(DELAY, new EventHandler<Alarm>() {
        @Override
        public void onNext(final Alarm alarm) {
        }
      });
    }
  }

  /**
   * Sends the message back to the client and schedules an alarm in 500ms
   * such that the Driver does not immediately go idle.
   */
  final class ClientMessageHandler implements EventHandler<byte[]> {
    @Override
    public void onNext(final byte[] message) {
      LOG.log(Level.INFO, "Message received: {0}", String.valueOf(message));
      client.sendMessageToClient(message);
      clock.scheduleAlarm(DELAY, new EventHandler<Alarm>() {
        @Override
        public void onNext(final Alarm alarm) {
        }
      });
    }
  }

  final class AllocatedEvaluatorHandler implements EventHandler<AllocatedEvaluator> {
    @Override
    public void onNext(final AllocatedEvaluator eval) {
      throw new RuntimeException("This should never be called");
    }
  }
}
