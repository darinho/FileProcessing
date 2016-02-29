/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.google.imageio.webp;

/**
 *
 * @author MIGUEL
 */
import java.awt.image.*;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Hashtable;

/**
 *
 */
final class WebP {
  private static boolean NATIVE_LIBRARY_LOADED = false;

  static synchronized void loadNativeLibrary() {
    if ( !NATIVE_LIBRARY_LOADED ) {
      NATIVE_LIBRARY_LOADED = true;
      System.loadLibrary( "webp_jni" );
    }
  }

  static {
    loadNativeLibrary();
  }

  private WebP() {
  }

  public static BufferedImage decode( WebPReadParam readParam, byte[] data, int offset, int length ) throws IOException {
    if ( readParam == null ) {
      throw new NullPointerException( "Decoder options may not be null" );
    }

    if ( data == null ) {
      throw new NullPointerException( "Input data may not be null" );
    }

    if ( offset + length > data.length ) {
      throw new IllegalArgumentException( "Offset/length exceeds array size" );
    }

    int[] out = new int[4];
    int[] pixels = decode( readParam.pointer, data, offset, length, out, ByteOrder.nativeOrder().equals( ByteOrder.BIG_ENDIAN ) );
    VP8StatusCode status = VP8StatusCode.getStatusCode( out[0] );
    switch ( status ) {
      case VP8_STATUS_OK:
        break;
      case VP8_STATUS_OUT_OF_MEMORY:
        throw new OutOfMemoryError();
      default:
        throw new IOException( "Decode returned code " + status );
    }

    int width = out[1];
    int height = out[2];
    boolean alpha = out[3] != 0;

    ColorModel colorModel;
    if ( alpha ) {
      colorModel = new DirectColorModel( 32, 0x00ff0000, 0x0000ff00, 0x000000ff, 0xff000000 );
    } else {
      colorModel = new DirectColorModel( 24, 0x00ff0000, 0x0000ff00, 0x000000ff, 0x00000000 );
    }

    SampleModel sampleModel = colorModel.createCompatibleSampleModel( width, height );
    DataBufferInt db = new DataBufferInt( pixels, width * height );
    WritableRaster raster = WritableRaster.createWritableRaster( sampleModel, db, null );

    return new BufferedImage( colorModel, raster, false, new Hashtable<Object, Object>() );
  }

  private static native int[] decode( long decoderOptionsPointer, byte[] data, int offset, int length, int[] flags, boolean bigEndian );

  public static int[] getInfo( byte[] data, int offset, int length ) throws IOException {
    int[] out = new int[2];
    int result = getInfo( data, offset, length, out );
    if (result == 0) {
      throw new IOException( "Invalid WebP data" );
    }

    return out;
  }

  private static native int getInfo( byte[] data, int offset, int length, int[] out );

  public static byte[] encode( WebPWriteParam writeParam, RenderedImage image ) throws IOException {
    if ( writeParam == null ) {
      throw new NullPointerException( "Encoder options may not be null" );
    }

    if ( image == null ) {
      throw new NullPointerException( "Image may not be null" );
    }

    boolean encodeAlpha = hasTranslucency( image );
    if ( encodeAlpha ) {
      byte[] rgbaData = getRGBA( image );
      return encodeRGBA( writeParam.getPointer(), rgbaData, image.getWidth(), image.getHeight(), image.getWidth() * 4 );
    }
    else {
      byte[] rgbData = getRGB( image );
      return encodeRGB( writeParam.getPointer(), rgbData, image.getWidth(), image.getHeight(), image.getWidth() * 3 );
    }
  }

  private static native byte[] encodeRGBA( long config, byte[] rgbaData, int width, int height, int stride );

  private static native byte[] encodeRGB( long config, byte[] rgbaData, int width, int height, int stride );

  private static boolean hasTranslucency( RenderedImage ri ) {
    return ri.getColorModel().hasAlpha();
  }

  private static int getShift( int mask ) {
    int shift = 0;
    while ( ( ( mask >> shift ) & 0x1 ) == 0 ) {
      shift++;
    }
    return shift;
  }

  private static byte[] getRGB( RenderedImage renderedImage ) throws IOException {
    int width = renderedImage.getWidth();
    int height = renderedImage.getHeight();

    ColorModel colorModel = renderedImage.getColorModel();
    if ( colorModel instanceof ComponentColorModel ) {
      ComponentSampleModel sampleModel = ( ComponentSampleModel ) renderedImage.getSampleModel();
      int type = sampleModel.getTransferType();
      if ( type == DataBuffer.TYPE_BYTE ) {
        return extractComponentRGBByte( width, height, sampleModel, ( ( DataBufferByte ) renderedImage.getData().getDataBuffer() ) );
      }
      else if ( type == DataBuffer.TYPE_INT ) {
        return extractComponentRGBInt( width, height, sampleModel, ( ( DataBufferInt ) renderedImage.getData().getDataBuffer() ) );
      }
      else {
        throw new IOException( "Incompatible image: " + renderedImage );
      }
    }
    else if ( colorModel instanceof DirectColorModel ) {
      SinglePixelPackedSampleModel sampleModel = ( SinglePixelPackedSampleModel ) renderedImage.getSampleModel();
      int type = sampleModel.getTransferType();
      if ( type == DataBuffer.TYPE_INT ) {
        return extractDirectRGBInt( width, height, ( DirectColorModel ) colorModel, sampleModel, ( ( DataBufferInt ) renderedImage.getData().getDataBuffer() ) );
      }
      else {
        throw new IOException( "Incompatible image: " + renderedImage );
      }
    }

    return extractGenericRGB( renderedImage, width, height, colorModel );
  }

  private static byte[] extractGenericRGB( RenderedImage renderedImage, int width, int height, ColorModel colorModel ) {
    Object dataElements = null;
    byte[] rgbData = new byte[ width * height * 3 ];
    for ( int b = 0, y = 0; y < height; y++ ) {
      for ( int x = 0; x < width; x++, b += 3 ) {
        dataElements = renderedImage.getData().getDataElements( x, y, dataElements );
        rgbData[ b ] = ( byte ) colorModel.getRed( dataElements );
        rgbData[ b + 1 ] = ( byte ) colorModel.getGreen( dataElements );
        rgbData[ b + 2 ] = ( byte ) colorModel.getBlue( dataElements );
      }
    }
    return rgbData;
  }

  private static byte[] extractDirectRGBInt( int width, int height, DirectColorModel colorModel, SinglePixelPackedSampleModel sampleModel, DataBufferInt dataBuffer ) {
    byte[] out = new byte[ width * height * 3 ];

    int rMask = colorModel.getRedMask();
    int gMask = colorModel.getGreenMask();
    int bMask = colorModel.getBlueMask();
    int rShift = getShift( rMask );
    int gShift = getShift( gMask );
    int bShift = getShift( bMask );
    int[] bank = dataBuffer.getBankData()[ 0 ];
    int scanlineStride = sampleModel.getScanlineStride();
    int scanIx = 0;
    for ( int b = 0, y = 0; y < height; y++ ) {
      int pixIx = scanIx;
      for ( int x = 0; x < width; x++, b += 3 ) {
        int pixel = bank[ pixIx++ ];
        out[ b ] = ( byte ) ( ( pixel & rMask ) >>> rShift );
        out[ b + 1 ] = ( byte ) ( ( pixel & gMask ) >>> gShift );
        out[ b + 2 ] = ( byte ) ( ( pixel & bMask ) >>> bShift );
      }
      scanIx += scanlineStride;
    }
    return out;
  }

  private static byte[] extractComponentRGBInt( int width, int height, ComponentSampleModel sampleModel, DataBufferInt dataBuffer ) {
    byte[] out = new byte[ width * height * 3 ];

    int[] bankIndices = sampleModel.getBankIndices();
    int[] rBank = dataBuffer.getBankData()[ bankIndices[ 0 ] ];
    int[] gBank = dataBuffer.getBankData()[ bankIndices[ 1 ] ];
    int[] bBank = dataBuffer.getBankData()[ bankIndices[ 2 ] ];

    int[] bankOffsets = sampleModel.getBandOffsets();
    int rScanIx = bankOffsets[ 0 ];
    int gScanIx = bankOffsets[ 1 ];
    int bScanIx = bankOffsets[ 2 ];

    int pixelStride = sampleModel.getPixelStride();
    int scanlineStride = sampleModel.getScanlineStride();
    for ( int b = 0, y = 0; y < height; y++ ) {
      int rPixIx = rScanIx;
      int gPixIx = gScanIx;
      int bPixIx = bScanIx;
      for ( int x = 0; x < width; x++, b += 3 ) {
        out[ b ] = ( byte ) rBank[ rPixIx ];
        rPixIx += pixelStride;
        out[ b + 1 ] = ( byte ) gBank[ gPixIx ];
        gPixIx += pixelStride;
        out[ b + 2 ] = ( byte ) bBank[ bPixIx ];
        bPixIx += pixelStride;
      }
      rScanIx += scanlineStride;
      gScanIx += scanlineStride;
      bScanIx += scanlineStride;
    }
    return out;
  }

  private static byte[] extractComponentRGBByte( int width, int height, ComponentSampleModel sampleModel, DataBufferByte dataBuffer ) {
    byte[] out = new byte[ width * height * 3 ];

    int[] bankIndices = sampleModel.getBankIndices();
    byte[] rBank = dataBuffer.getBankData()[ bankIndices[ 0 ] ];
    byte[] gBank = dataBuffer.getBankData()[ bankIndices[ 1 ] ];
    byte[] bBank = dataBuffer.getBankData()[ bankIndices[ 2 ] ];

    int[] bankOffsets = sampleModel.getBandOffsets();
    int rScanIx = bankOffsets[ 0 ];
    int gScanIx = bankOffsets[ 1 ];
    int bScanIx = bankOffsets[ 2 ];

    int pixelStride = sampleModel.getPixelStride();
    int scanlineStride = sampleModel.getScanlineStride();
    for ( int b = 0, y = 0; y < height; y++ ) {
      int rPixIx = rScanIx;
      int gPixIx = gScanIx;
      int bPixIx = bScanIx;
      for ( int x = 0; x < width; x++, b += 3 ) {
        out[ b ] = rBank[ rPixIx ];
        rPixIx += pixelStride;
        out[ b + 1 ] = gBank[ gPixIx ];
        gPixIx += pixelStride;
        out[ b + 2 ] = bBank[ bPixIx ];
        bPixIx += pixelStride;
      }
      rScanIx += scanlineStride;
      gScanIx += scanlineStride;
      bScanIx += scanlineStride;
    }
    return out;
  }

  private static byte[] getRGBA( RenderedImage renderedImage ) throws IOException {
    int width = renderedImage.getWidth();
    int height = renderedImage.getHeight();

    ColorModel colorModel = renderedImage.getColorModel();
    if ( colorModel instanceof ComponentColorModel ) {
      ComponentSampleModel sampleModel = ( ComponentSampleModel ) renderedImage.getSampleModel();
      int type = sampleModel.getTransferType();
      if ( type == DataBuffer.TYPE_BYTE ) {
        return extractComponentRGBAByte( width, height, sampleModel, ( ( DataBufferByte ) renderedImage.getData().getDataBuffer() ) );
      }
      else if ( type == DataBuffer.TYPE_INT ) {
        return extractComponentRGBAInt( width, height, sampleModel, ( ( DataBufferInt ) renderedImage.getData().getDataBuffer() ) );
      }
      else {
        throw new IOException( "Incompatible image: " + renderedImage );
      }
    }
    else if ( colorModel instanceof DirectColorModel ) {
      SinglePixelPackedSampleModel sampleModel = ( SinglePixelPackedSampleModel ) renderedImage.getSampleModel();
      int type = sampleModel.getTransferType();
      if ( type == DataBuffer.TYPE_INT ) {
        return extractDirectRGBAInt( width, height, ( DirectColorModel ) colorModel, sampleModel, ( ( DataBufferInt ) renderedImage.getData().getDataBuffer() ) );
      }
      else {
        throw new IOException( "Incompatible image: " + renderedImage );
      }
    }

    return extractGenericRGBA( renderedImage, width, height, colorModel );
  }

  private static byte[] extractGenericRGBA( RenderedImage renderedImage, int width, int height, ColorModel colorModel ) {
    Object dataElements = null;
    byte[] rgbData = new byte[ width * height * 4 ];
    for ( int b = 0, y = 0; y < height; y++ ) {
      for ( int x = 0; x < width; x++, b += 4 ) {
        dataElements = renderedImage.getData().getDataElements( x, y, dataElements );
        rgbData[ b ] = ( byte ) colorModel.getRed( dataElements );
        rgbData[ b + 1 ] = ( byte ) colorModel.getGreen( dataElements );
        rgbData[ b + 2 ] = ( byte ) colorModel.getBlue( dataElements );
        rgbData[ b + 3 ] = ( byte ) colorModel.getAlpha( dataElements );
      }
    }
    return rgbData;
  }

  private static byte[] extractDirectRGBAInt( int width, int height, DirectColorModel colorModel, SinglePixelPackedSampleModel sampleModel, DataBufferInt dataBuffer ) {
    byte[] out = new byte[ width * height * 4 ];

    int rMask = colorModel.getRedMask();
    int gMask = colorModel.getGreenMask();
    int bMask = colorModel.getBlueMask();
    int aMask = colorModel.getAlphaMask();
    int rShift = getShift( rMask );
    int gShift = getShift( gMask );
    int bShift = getShift( bMask );
    int aShift = getShift( aMask );
    int[] bank = dataBuffer.getBankData()[ 0 ];
    int scanlineStride = sampleModel.getScanlineStride();
    int scanIx = 0;
    for ( int b = 0, y = 0; y < height; y++ ) {
      int pixIx = scanIx;
      for ( int x = 0; x < width; x++, b += 4 ) {
        int pixel = bank[ pixIx++ ];
        out[ b ] = ( byte ) ( ( pixel & rMask ) >>> rShift );
        out[ b + 1 ] = ( byte ) ( ( pixel & gMask ) >>> gShift );
        out[ b + 2 ] = ( byte ) ( ( pixel & bMask ) >>> bShift );
        out[ b + 3 ] = ( byte ) ( ( pixel & aMask ) >>> aShift );
      }
      scanIx += scanlineStride;
    }
    return out;
  }

  private static byte[] extractComponentRGBAInt( int width, int height, ComponentSampleModel sampleModel, DataBufferInt dataBuffer ) {
    byte[] out = new byte[ width * height * 4 ];

    int[] bankIndices = sampleModel.getBankIndices();
    int[] rBank = dataBuffer.getBankData()[ bankIndices[ 0 ] ];
    int[] gBank = dataBuffer.getBankData()[ bankIndices[ 1 ] ];
    int[] bBank = dataBuffer.getBankData()[ bankIndices[ 2 ] ];
    int[] aBank = dataBuffer.getBankData()[ bankIndices[ 3 ] ];

    int[] bankOffsets = sampleModel.getBandOffsets();
    int rScanIx = bankOffsets[ 0 ];
    int gScanIx = bankOffsets[ 1 ];
    int bScanIx = bankOffsets[ 2 ];
    int aScanIx = bankOffsets[ 3 ];

    int pixelStride = sampleModel.getPixelStride();
    int scanlineStride = sampleModel.getScanlineStride();
    for ( int b = 0, y = 0; y < height; y++ ) {
      int rPixIx = rScanIx;
      int gPixIx = gScanIx;
      int bPixIx = bScanIx;
      int aPixIx = aScanIx;
      for ( int x = 0; x < width; x++, b += 4 ) {
        out[ b ] = ( byte ) rBank[ rPixIx ];
        rPixIx += pixelStride;
        out[ b + 1 ] = ( byte ) gBank[ gPixIx ];
        gPixIx += pixelStride;
        out[ b + 2 ] = ( byte ) bBank[ bPixIx ];
        bPixIx += pixelStride;
        out[ b + 3 ] = ( byte ) aBank[ aPixIx ];
        aPixIx += pixelStride;
      }
      rScanIx += scanlineStride;
      gScanIx += scanlineStride;
      bScanIx += scanlineStride;
      aScanIx += scanlineStride;
    }
    return out;
  }

  private static byte[] extractComponentRGBAByte( int width, int height, ComponentSampleModel sampleModel, DataBufferByte dataBuffer ) {
    byte[] out = new byte[ width * height * 4 ];

    int[] bankIndices = sampleModel.getBankIndices();
    byte[] rBank = dataBuffer.getBankData()[ bankIndices[ 0 ] ];
    byte[] gBank = dataBuffer.getBankData()[ bankIndices[ 1 ] ];
    byte[] bBank = dataBuffer.getBankData()[ bankIndices[ 2 ] ];
    byte[] aBank = dataBuffer.getBankData()[ bankIndices[ 3 ] ];

    int[] bankOffsets = sampleModel.getBandOffsets();
    int rScanIx = bankOffsets[ 0 ];
    int gScanIx = bankOffsets[ 1 ];
    int bScanIx = bankOffsets[ 2 ];
    int aScanIx = bankOffsets[ 3 ];

    int pixelStride = sampleModel.getPixelStride();
    int scanlineStride = sampleModel.getScanlineStride();
    for ( int b = 0, y = 0; y < height; y++ ) {
      int rPixIx = rScanIx;
      int gPixIx = gScanIx;
      int bPixIx = bScanIx;
      int aPixIx = aScanIx;
      for ( int x = 0; x < width; x++, b += 4 ) {
        out[ b ] = rBank[ rPixIx ];
        rPixIx += pixelStride;
        out[ b + 1 ] = gBank[ gPixIx ];
        gPixIx += pixelStride;
        out[ b + 2 ] = bBank[ bPixIx ];
        bPixIx += pixelStride;
        out[ b + 3 ] = aBank[ aPixIx ];
        aPixIx += pixelStride;
      }
      rScanIx += scanlineStride;
      gScanIx += scanlineStride;
      bScanIx += scanlineStride;
      aScanIx += scanlineStride;
    }
    return out;
  }
}
