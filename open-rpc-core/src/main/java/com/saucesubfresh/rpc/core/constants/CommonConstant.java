package com.saucesubfresh.rpc.core.constants;

/**
 * @author 李俊平
 */
public interface CommonConstant {
  /**
   * ip:port pattern
   */
  String ADDRESS_PATTERN = "%s:%d";


  public static final class Symbol {
    private Symbol() {
    }

    /**
     * The constant COMMA.
     */
    public static final String COMMA = ",";
    public static final String SPOT = ".";
    /**
     * The constant UNDER_LINE.
     */
    public final static String UNDER_LINE = "_";
    /**
     * The constant PER_CENT.
     */
    public final static String PER_CENT = "%";
    /**
     * The constant AT.
     */
    public final static String AT = "@";
    /**
     * The constant PIPE.
     */
    public final static String PIPE = "||";
    public final static String SHORT_LINE = "-";
    public final static String SPACE = " ";
    public final static String DOUBLE_COLON = "::";
    public static final String SLASH = "/";
    public static final String MH = ":";

  }


}
