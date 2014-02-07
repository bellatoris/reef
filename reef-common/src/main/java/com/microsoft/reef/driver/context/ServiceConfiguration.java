/**
 * Copyright (C) 2013 Microsoft Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.microsoft.reef.driver.context;

import com.microsoft.reef.activity.events.ActivityStart;
import com.microsoft.reef.activity.events.ActivityStop;
import com.microsoft.reef.annotations.audience.Private;
import com.microsoft.reef.driver.activity.ActivityConfigurationOptions;
import com.microsoft.reef.evaluator.context.events.ContextStart;
import com.microsoft.reef.evaluator.context.events.ContextStop;
import com.microsoft.reef.util.ObjectInstantiationLogger;
import com.microsoft.tang.annotations.Name;
import com.microsoft.tang.annotations.NamedParameter;
import com.microsoft.tang.formats.ConfigurationModule;
import com.microsoft.tang.formats.ConfigurationModuleBuilder;
import com.microsoft.tang.formats.OptionalImpl;
import com.microsoft.tang.formats.OptionalParameter;
import com.microsoft.wake.EventHandler;

import java.util.Set;

/**
 * Configuration module for services. The configuration created here can be passed alongside a ContextConfiguration
 * to form a context. Different from bindings made in the ContextConfiguration, those made here will be passed along
 * to child context.
 */
public class ServiceConfiguration extends ConfigurationModuleBuilder {

  /**
   * A set of services to instantiate. All classes given here will be instantiated in the context, and their references
   * will be made available to child context and activities.
   */
  public static final OptionalParameter<Object> SERVICES = new OptionalParameter<>();

  /**
   * Event handler for context start. Defaults to logging if not bound.
   */
  public static final OptionalImpl<EventHandler<ContextStart>> ON_CONTEXT_STARTED = new OptionalImpl<>();

  /**
   * Event handler for context stop. Defaults to logging if not bound.
   */
  public static final OptionalImpl<EventHandler<ContextStop>> ON_CONTEXT_STOP = new OptionalImpl<>();
  
  /**
   * Event handlers to be informed right before an Activity enters its call() method.
   */
  public static final OptionalImpl<EventHandler<ActivityStart>> ON_ACTIVITY_STARTED = new OptionalImpl<>();

  /**
   * Event handlers to be informed right after an Activity exits its call() method.
   */
  public static final OptionalImpl<EventHandler<ActivityStop>> ON_ACTIVITY_STOP = new OptionalImpl<>();

  /**
   * ConfigurationModule for services.
   */
  public static final ConfigurationModule CONF = new ServiceConfiguration()
      .bindSetEntry(Services.class, SERVICES)
      .bindSetEntry(ContextConfigurationOptions.StartHandlers.class, ON_CONTEXT_STARTED)
      .bindSetEntry(ContextConfigurationOptions.StopHandlers.class, ON_CONTEXT_STOP)
      .bindSetEntry(ActivityConfigurationOptions.StartHandlers.class, ON_ACTIVITY_STARTED)
      .bindSetEntry(ActivityConfigurationOptions.StopHandlers.class, ON_ACTIVITY_STOP)
      .build();

  @NamedParameter(doc = "A set of classes to be instantiated and shared as singletons within this context and all child context",
      default_classes = ObjectInstantiationLogger.class)
  @Private
  public static class Services implements Name<Set<Object>> {
  }

}
