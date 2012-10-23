/*
 *  Copyright (c) 2012 Malhar, Inc.
 *  All Rights Reserved.
 */
package com.malhartech.stream;

import com.malhartech.api.Sink;
import com.malhartech.dag.*;
import java.util.HashMap;

/**
 *
 * @author chetan
 */
public class MuxStream implements Stream<Object>
{
  private HashMap<String, Sink> outputs;
  @SuppressWarnings("VolatileArrayField")
  private volatile Sink[] sinks = NO_SINKS;
  private long count;

  /**
   *
   * @param config
   */
  @Override
  public void setup(StreamConfiguration config)
  {
    outputs = new HashMap<String, Sink>();
  }

  /**
   *
   */
  @Override
  public void teardown()
  {
    outputs.clear();
    outputs = null;
  }

  /**
   *
   * @param context
   */
  @Override
  @SuppressWarnings("SillyAssignment")
  public void activated(StreamContext context)
  {
    sinks = new Sink[outputs.size()];

    int i = 0;
    for (final Sink s: outputs.values()) {
      sinks[i++] = s;
    }
    sinks = sinks;
  }

  /**
   *
   */
  @Override
  public void deactivated()
  {
    sinks = NO_SINKS;
  }

  /**
   *
   * @param id
   * @param sink
   * @return Sink
   */
  @Override
  public Sink setSink(String id, Sink sink)
  {
    if (sink == null) {
      sink = outputs.remove(id);
      if (outputs.isEmpty()) {
        sinks = NO_SINKS;
      }
    }
    else {
      sink = outputs.put(id, sink);
      if (sinks != NO_SINKS) {
        activated(null);
      }
    }

    return sink;
  }

  /**
   *
   * @param payload
   */
  @Override
  public void process(Object payload)
  {
    count++;
    for (int i = sinks.length; i-- > 0;) {
      sinks[i].process(payload);
    }
  }

  @Override
  public boolean isMultiSinkCapable()
  {
    return true;
  }

  @Override
  public long getProcessedCount()
  {
    return count;
  }
}
