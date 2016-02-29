package com.google.imageio.webp;

import javax.imageio.ImageReadParam;

/**
 *
 */
public final class WebPReadParam extends ImageReadParam {
  static {
    WebP.loadNativeLibrary();
  }

  long pointer;

  public WebPReadParam() {
    pointer = createDecoderOptions();
    if ( pointer == 0 ) {
      throw new OutOfMemoryError();
    }
  }

  @Override
  protected void finalize() throws Throwable {
    super.finalize();
    deleteDecoderOptions( pointer );
    pointer = 0L;
  }

  public int getCropHeight() {
    return getCropHeight( pointer );
  }

  public void setCropHeight( int aCropHeight ) {
    setCropHeight( pointer, aCropHeight );
  }

  public int getCropLeft() {
    return getCropLeft( pointer );
  }

  public void setCropLeft( int aCropLeft ) {
    setCropLeft( pointer, aCropLeft );
  }

  public int getCropTop() {
    return getCropTop( pointer );
  }

  public void setCropTop( int aCropTop ) {
    setCropTop( pointer, aCropTop );
  }

  public int getCropWidth() {
    return getCropWidth( pointer );
  }

  public void setCropWidth( int aCropWidth ) {
    setCropWidth( pointer, aCropWidth );
  }

  public boolean isForceRotation() {
    return isForceRotation( pointer );
  }

  public void setForceRotation( boolean aForceRotation ) {
    setForceRotation( pointer, aForceRotation );
  }

  public boolean isEnhancement() {
    return !isNoEnhancement( pointer );
  }

  public void setEnhancement( boolean aEnhancement ) {
    setNoEnhancement( pointer, !aEnhancement );
  }

  public boolean isFancyUpsampling() {
    return !isNoFancyUpsampling( pointer );
  }

  public void setFancyUpsampling( boolean aFancyUpsampling ) {
    setNoFancyUpsampling( pointer, !aFancyUpsampling );
  }

  public int getScaledHeight() {
    return getScaledHeight( pointer );
  }

  public void setScaledHeight( int aScaledHeight ) {
    setScaledHeight( pointer, aScaledHeight );
  }

  public int getScaledWidth() {
    return getScaledWidth( pointer );
  }

  public void setScaledWidth( int aScaledWidth ) {
    setScaledWidth( pointer, aScaledWidth );
  }

  public boolean isUseCropping() {
    return isUseCropping( pointer );
  }

  public void setUseCropping( boolean aUseCropping ) {
    setUseCropping( pointer, aUseCropping );
  }

  public boolean isUseScaling() {
    return isUseScaling( pointer );
  }

  public void setUseScaling( boolean aUseScaling ) {
    setUseScaling( pointer, aUseScaling );
  }

  public boolean isUseThreads() {
    return isUseThreads( pointer );
  }

  public void setUseThreads( boolean aUseThreads ) {
    setUseThreads( pointer, aUseThreads );
  }

  private static native long createDecoderOptions();

  private static native void deleteDecoderOptions( long aPointer );

  private static native int getCropHeight( long aPointer );

  private static native void setCropHeight( long aPointer, int aCropHeight );

  private static native int getCropLeft( long aPointer );

  private static native void setCropLeft( long aPointer, int aCropLeft );

  private static native int getCropTop( long aPointer );

  private static native void setCropTop( long aPointer, int aCropTop );

  private static native int getCropWidth( long aPointer );

  private static native void setCropWidth( long aPointer, int aCropWidth );

  private static native boolean isForceRotation( long aPointer );

  private static native void setForceRotation( long aPointer, boolean aForceRotation );

  private static native boolean isNoEnhancement( long aPointer );

  private static native void setNoEnhancement( long aPointer, boolean aNoEnhancement );

  private static native boolean isNoFancyUpsampling( long aPointer );

  private static native void setNoFancyUpsampling( long aPointer, boolean aFancyUpsampling );

  private static native int getScaledHeight( long aPointer );

  private static native void setScaledHeight( long aPointer, int aScaledHeight );

  private static native int getScaledWidth( long aPointer );

  private static native void setScaledWidth( long aPointer, int aScaledWidth );

  private static native boolean isUseCropping( long aPointer );

  private static native void setUseCropping( long aPointer, boolean aUseCropping );

  private static native boolean isUseScaling( long aPointer );

  private static native void setUseScaling( long aPointer, boolean aUseScaling );

  private static native boolean isUseThreads( long aPointer );

  private static native void setUseThreads( long aPointer, boolean aUseThreads );
}