package com.google.imageio.webp;

/**
 *
 */
enum VP8StatusCode {
  VP8_STATUS_OK,
  VP8_STATUS_OUT_OF_MEMORY,
  VP8_STATUS_INVALID_PARAM,
  VP8_STATUS_BITSTREAM_ERROR,
  VP8_STATUS_UNSUPPORTED_FEATURE,
  VP8_STATUS_SUSPENDED,
  VP8_STATUS_USER_ABORT,
  VP8_STATUS_NOT_ENOUGH_DATA,;

  private static VP8StatusCode[] VALUES = values();

  public static VP8StatusCode getStatusCode( int value ) {
    if ( value >= 0 && value < VALUES.length ) {
      return VALUES[ value ];
    }
    else {
      return null;
    }
  }
}