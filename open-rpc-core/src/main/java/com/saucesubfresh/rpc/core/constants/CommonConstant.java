/*
 * Copyright © 2022 Open-Light-Rpc Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.saucesubfresh.rpc.core.constants;

/**
 * @author lijunping
 */
public interface CommonConstant {
  /**
   * ip:port pattern
   */
  String ADDRESS_PATTERN = "%s:%d";

  /**
   * netty的分隔符
   */
  String DELIMITER = "$(* *)$";

  /**
   * 单次包最大4M
   */
  int MAX_LENGTH = 4 * 1024 * 1024;


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
