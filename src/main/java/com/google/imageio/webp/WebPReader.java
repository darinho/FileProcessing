package com.google.imageio.webp;

import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;

/**
 *
 */
class WebPReader extends ImageReader {

  static {
    System.loadLibrary( "webp_jni" );
  }

  private byte[] fData;
  private int fWidth;
  private int fHeight;

  WebPReader( ImageReaderSpi originatingProvider ) {
    super( originatingProvider );
  }

  @Override
  public void setInput( Object input, boolean seekForwardOnly, boolean ignoreMetadata ) {
    super.setInput( input, seekForwardOnly, ignoreMetadata );
    fData = null;
    fWidth = -1;
    fHeight = -1;
  }

  @Override
  public int getNumImages( boolean allowSearch ) throws IOException {
    return 1;
  }

  private void readHeader() throws IOException {
    if ( fWidth != -1 && fHeight != -1 ) {
      return;
    }

    readData();
    int[] info = WebP.getInfo( fData, 0, fData.length );
    fWidth = info[ 0 ];
    fHeight = info[ 1 ];
  }

  private void readData() throws IOException {
    if ( fData != null ) {
      return;
    }

    ImageInputStream input = ( ImageInputStream ) getInput();
    long length = input.length();
    if ( length > Integer.MAX_VALUE ) {
      throw new IOException( "Cannot read image of size " + length );
    }

    if ( input.getStreamPosition() != 0L ) {
      if ( isSeekForwardOnly() ) {
        throw new IOException();
      }
      else {
        input.seek( 0 );
      }
    }

    byte[] data;
    if ( length > 0 ) {
      data = new byte[ ( int ) length ];
      input.readFully( data );
    }
    else {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      byte[] buffer = new byte[ 4096 ];
      int bytesRead;
      while ( ( bytesRead = input.read( buffer ) ) != -1 ) {
        out.write( buffer, 0, bytesRead );
      }
      out.close();
      data = out.toByteArray();
    }
    fData = data;
  }

  private void checkIndex( int imageIndex ) {
    if ( imageIndex != 0 ) {
      throw new IndexOutOfBoundsException( "Invalid image index: " + imageIndex );
    }
  }

  @Override
  public int getWidth( int imageIndex ) throws IOException {
    checkIndex( imageIndex );
    readHeader();
    return fWidth;
  }

  @Override
  public int getHeight( int imageIndex ) throws IOException {
    checkIndex( imageIndex );
    readHeader();
    return fHeight;
  }

  @Override
  public IIOMetadata getStreamMetadata() throws IOException {
    return null;
  }

  @Override
  public IIOMetadata getImageMetadata( int imageIndex ) throws IOException {
    return null;
  }

  @Override
  public Iterator<ImageTypeSpecifier> getImageTypes( int imageIndex ) throws IOException {
    return Collections.singletonList(
        ImageTypeSpecifier.createFromBufferedImageType( BufferedImage.TYPE_INT_ARGB )
    ).iterator();
  }

  @Override
  public ImageReadParam getDefaultReadParam() {
    return new WebPReadParam();
  }

  @Override
  public BufferedImage read( int imageIndex, ImageReadParam param ) throws IOException {
    checkIndex( imageIndex );
    readData();
    readHeader();
    WebPReadParam options = param != null ? (WebPReadParam) param : new WebPReadParam();
    return WebP.decode( options, fData, 0, fData.length );
  }
}