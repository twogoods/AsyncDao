package com.tg.async.mysql;

import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import scala.concurrent.ExecutionContext;

import java.util.Objects;

/**
 * Execution environment for Scala Future. The submitted {@link Runnable} are executed in the Vert.x Event Loop.
 */
public class VertxEventLoopExecutionContext implements ExecutionContext {

  public static ExecutionContext create(Vertx vertx) {
    return new VertxEventLoopExecutionContext(vertx);
  }

  private final Context context;

  private VertxEventLoopExecutionContext(Vertx vertx) {
    Objects.requireNonNull(vertx);
    Context ctx = Vertx.currentContext();
    if (ctx == null) {
      ctx = vertx.getOrCreateContext();
    }
    this.context = ctx;
  }

  @Override
  public void execute(Runnable runnable) {
    if (context == Vertx.currentContext()) {
      try {
        runnable.run();
      } catch (Throwable e) {
        reportFailure(e);
      }
    } else {
      context.runOnContext(v -> {
        try {
          runnable.run();
        } catch (Throwable e) {
          reportFailure(e);
        }
      });
    }
  }

  @Override
  public void reportFailure(Throwable cause) {
    Handler<Throwable> exceptionHandler = context.exceptionHandler();
    if (exceptionHandler != null) {
      exceptionHandler.handle(cause);
    }
  }

  @Override
  public ExecutionContext prepare() {
    // No preparation required.
    return this;
  }
}
