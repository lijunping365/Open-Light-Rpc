package com.saucesubfresh.rpc.core.utils.json;

/**
 * json异常
 *
 * @author lijunping
 */
public class JsonException extends RuntimeException {
  private static final long serialVersionUID = 4335929668377204215L;

  public JsonException(Throwable cause) {
    super(cause);
  }

  @Override
  public Throwable fillInStackTrace() {
    return this;
  }
}
