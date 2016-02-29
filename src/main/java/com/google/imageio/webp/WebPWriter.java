package com.google.imageio.webp;

import javax.imageio.IIOImage;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.RenderedImage;
import java.io.IOException;

/**
 *
 */
class WebPWriter extends ImageWriter {
  static {
    System.loadLibrary( "webp_jni" );
  }

  WebPWriter( ImageWriterSpi originatingProvider ) {
    super( originatingProvider );
  }

  @Override
  public ImageWriteParam getDefaultWriteParam() {
    return new WebPWriteParam( getLocale() );
  }

  @Override
  public IIOMetadata convertImageMetadata( IIOMetadata inData, ImageTypeSpecifier imageType, ImageWriteParam param ) {
    return null;
  }

  @Override
  public IIOMetadata convertStreamMetadata( IIOMetadata inData, ImageWriteParam param ) {
    return null;
  }

  @Override
  public IIOMetadata getDefaultImageMetadata( ImageTypeSpecifier imageType, ImageWriteParam param ) {
    return null;
  }

  @Override
  public IIOMetadata getDefaultStreamMetadata( ImageWriteParam param ) {
    return null;
  }

  @Override
  public void write( IIOMetadata streamMetadata, IIOImage image, ImageWriteParam param ) throws IOException {
    if ( param == null ) {
      param = getDefaultWriteParam();
    }

    WebPWriteParam writeParam = (WebPWriteParam) param;

    ImageOutputStream output = ( ImageOutputStream ) getOutput();
    RenderedImage ri = image.getRenderedImage();

    byte[] encodedData = WebP.encode(writeParam, ri);
    output.write( encodedData );
  }
}