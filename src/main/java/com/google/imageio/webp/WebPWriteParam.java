package com.google.imageio.webp;

import javax.imageio.ImageWriteParam;
import java.util.Locale;

/**
 *
 */
public class WebPWriteParam extends ImageWriteParam {
  static {
    WebP.loadNativeLibrary();
  }

  long pointer;

  public WebPWriteParam( Locale aLocale ) {
    super( aLocale );
    pointer = createConfig();
    if ( pointer == 0 ) {
      throw new OutOfMemoryError();
    }
    canWriteCompressed = true;
    compressionTypes = new String[]{
            "0",
            "1",
            "2",
            "3",
            "4",
            "5",
            "6"
    };
    compressionType = compressionTypes[getMethod()];
    compressionQuality = getQuality( pointer ) / 100f;
    compressionMode = MODE_EXPLICIT;
  }

  @Override
  protected void finalize() throws Throwable {
    super.finalize();
    deleteConfig( pointer );
    pointer = 0L;
  }

  private static native long createConfig();

  private static native void deleteConfig( long aPointer );

  long getPointer() {
    return pointer;
  }

  @Override
  public float getCompressionQuality() {
    return super.getCompressionQuality();
  }

  @Override
  public void setCompressionQuality( float quality ) {
    super.setCompressionQuality( quality );
    setQuality( pointer, quality * 100f );
  }

  public int getTargetSize() {
    return getTargetSize( pointer );
  }

  public void setTargetSize( int aTargetSize ) {
    setTargetSize( pointer, aTargetSize );
  }

  public float getTargetPSNR() {
    return getTargetPSNR( pointer );
  }

  public void setTargetPSNR( float aTargetPSNR ) {
    setTargetPSNR( pointer, aTargetPSNR );
  }

  public int getMethod() {
    return getMethod( pointer );
  }

  public void setMethod( int aMethod ) {
    setMethod( pointer, aMethod );
  }

  public int getSegments() {
    return getSegments( pointer );
  }

  public void setSegments( int aSegments ) {
    setSegments( pointer, aSegments );
  }

  public int getSnsStrength() {
    return getSnsStrength( pointer );
  }

  public void setSnsStrength( int aSnsStrength ) {
    setSnsStrength( pointer, aSnsStrength );
  }

  public int getFilterStrength() {
    return getFilterStrength( pointer );
  }

  public void setFilterStrength( int aFilterStrength ) {
    setFilterStrength( pointer, aFilterStrength );
  }

  public int getFilterSharpness() {
    return getFilterSharpness( pointer );
  }

  public void setFilterSharpness( int aFilterSharpness ) {
    setFilterSharpness( pointer, aFilterSharpness );
  }

  public int getFilterType() {
    return getFilterType( pointer );
  }

  public void setFilterType( int aFilterType ) {
    setFilterType( pointer, aFilterType );
  }

  public int getAutofilter() {
    return getAutofilter( pointer );
  }

  public void setAutofilter( int aAutofilter ) {
    setAutofilter( pointer, aAutofilter );
  }

  public int getPass() {
    return getPass( pointer );
  }

  public void setPass( int aPass ) {
    setPass( pointer, aPass );
  }

  public int getShowCompressed() {
    return getShowCompressed( pointer );
  }

  public void setShowCompressed( int aShowCompressed ) {
    setShowCompressed( pointer, aShowCompressed );
  }

  public int getPreprocessing() {
    return getPreprocessing( pointer );
  }

  public void setPreprocessing( int aPreprocessing ) {
    setPreprocessing( pointer, aPreprocessing );
  }

  public int getPartitions() {
    return getPartitions( pointer );
  }

  public void setPartitions( int aPartitions ) {
    setPartitions( pointer, aPartitions );
  }

  public int getPartitionLimit() {
    return getPartitionLimit( pointer );
  }

  public void setPartitionLimit( int aPartitionLimit ) {
    setPartitionLimit( pointer, aPartitionLimit );
  }

  public int getAlphaCompression() {
    return getAlphaCompression( pointer );
  }

  public void setAlphaCompression( int aAlphaCompression ) {
    setAlphaCompression( pointer, aAlphaCompression );
  }

  public int getAlphaFiltering() {
    return getAlphaFiltering( pointer );
  }

  public void setAlphaFiltering( int aAlphaFiltering ) {
    setAlphaFiltering( pointer, aAlphaFiltering );
  }

  public int getAlphaQuality() {
    return getAlphaQuality( pointer );
  }

  public void setAlphaQuality( int aAlphaQuality ) {
    setAlphaQuality( pointer, aAlphaQuality );
  }

  private static native float getQuality( long aPointer );

  private static native void setQuality( long aPointer, float aQuality );

  private static native int getTargetSize( long aPointer );

  private static native void setTargetSize( long aPointer, int aTargetSize );

  private static native float getTargetPSNR( long aPointer );

  private static native void setTargetPSNR( long aPointer, float aTargetPSNR );

  private static native int getMethod( long aPointer );

  private static native void setMethod( long aPointer, int aMethod );

  private static native int getSegments( long aPointer );

  private static native void setSegments( long aPointer, int aSegments );

  private static native int getSnsStrength( long aPointer );

  private static native void setSnsStrength( long aPointer, int aSnsStrength );

  private static native int getFilterStrength( long aPointer );

  private static native void setFilterStrength( long aPointer, int aFilterStrength );

  private static native int getFilterSharpness( long aPointer );

  private static native void setFilterSharpness( long aPointer, int aFilterSharpness );

  private static native int getFilterType( long aPointer );

  private static native void setFilterType( long aPointer, int aFilterType );

  private static native int getAutofilter( long aPointer );

  private static native void setAutofilter( long aPointer, int aAutofilter );

  private static native int getPass( long aPointer );

  private static native void setPass( long aPointer, int aPass );

  private static native int getShowCompressed( long aPointer );

  private static native void setShowCompressed( long aPointer, int aShowCompressed );

  private static native int getPreprocessing( long aPointer );

  private static native void setPreprocessing( long aPointer, int aPreprocessing );

  private static native int getPartitions( long aPointer );

  private static native void setPartitions( long aPointer, int aPartitions );

  private static native int getPartitionLimit( long aPointer );

  private static native void setPartitionLimit( long aPointer, int aPartitionLimit );

  private static native int getAlphaCompression( long aPointer );

  private static native void setAlphaCompression( long aPointer, int aAlphaCompression );

  private static native int getAlphaFiltering( long aPointer );

  private static native void setAlphaFiltering( long aPointer, int aAlphaFiltering );

  private static native int getAlphaQuality( long aPointer );

  private static native void setAlphaQuality( long aPointer, int aAlphaQuality );
}