/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gt.megapaca.fileprocessing;

import gt.megapaca.imagecompressor.ImageUtils;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author MIGUEL
 */
public class CompressImages {

    //required
    private File file;
    private String compressedImagePathFileName;
    private Float compressor_Factor = 1F;

    //optional scale size
    private int ResizePixels_LargeSide = 0;
    private int ResizePixels_ShortSide = 0;

    //optional watermark
    private String imageWaterMarkFileUrl = null;
    private float waterMark_RelativeSize = 0.1F;
    private float waterMark_TransparencyFactor = 0.6F;

    //public static final float compressor_Factor = 0.7F;
    //public static final float waterMark_RelativeSize = 0.1F;
    //public static final float waterMark_TransparencyFactor = 0.7F;
    public CompressImages(
            File file,
            String compressedImagePathFileName) {
        //required
        this.file = file;
        this.compressedImagePathFileName = compressedImagePathFileName;
    }

    public CompressImages(
            File file,
            String compressedImagePathFileName,
            Float compressor_Factor) {
        //required
        this.file = file;
        this.compressedImagePathFileName = compressedImagePathFileName;
        this.compressor_Factor = compressor_Factor;
    }

    public CompressImages(
            File file,
            String compressedImagePathFileName,
            Float compressor_Factor,
            int ResizePixels_LargeSide,
            int ResizePixels_ShortSide) {
        //required
        this.file = file;
        this.compressedImagePathFileName = compressedImagePathFileName;
        this.compressor_Factor = compressor_Factor;
        //optional sized
        this.ResizePixels_LargeSide = ResizePixels_LargeSide;
        this.ResizePixels_ShortSide = ResizePixels_ShortSide;
    }

    public CompressImages(
            File file,
            String compressedImagePathFileName,
            Float compressor_Factor,
            String imageWaterMarkFileUrl,
            float waterMark_RelativeSize,
            float waterMark_TransparencyFactor) {
        //required
        this.file = file;
        this.compressedImagePathFileName = compressedImagePathFileName;
        this.compressor_Factor = compressor_Factor;
        //optional watermark
        this.imageWaterMarkFileUrl = imageWaterMarkFileUrl;
        this.waterMark_RelativeSize = waterMark_RelativeSize;
        this.waterMark_TransparencyFactor = waterMark_TransparencyFactor;
    }

    public CompressImages(
            File file,
            String compressedImagePathFileName,
            Float compressor_Factor,
            int ResizePixels_LargeSide,
            int ResizePixels_ShortSide,
            String imageWaterMarkFileUrl,
            float waterMark_RelativeSize,
            float waterMark_TransparencyFactor) {
        //required
        this.file = file;
        this.compressedImagePathFileName = compressedImagePathFileName;
        this.compressor_Factor = compressor_Factor;
        //optional sized
        this.ResizePixels_LargeSide = ResizePixels_LargeSide;
        this.ResizePixels_ShortSide = ResizePixels_ShortSide;
        //optional watermark
        this.imageWaterMarkFileUrl = imageWaterMarkFileUrl;
        this.waterMark_RelativeSize = waterMark_RelativeSize;
        this.waterMark_TransparencyFactor = waterMark_TransparencyFactor;
    }

    public File getCustomSettingsProcessedFiles() throws IOException {

        File fl = null;

        try {
            //System.out.println(new Date());
            String compressedImageFileUrl = compressedImagePathFileName;
            BufferedImage bufferedImageOri = ImageIO.read(file);
            BufferedImage bufferedImage;

            if (this.imageWaterMarkFileUrl != null) {
                //add waterMark to original
                BufferedImage bufferedImageWaterMark = ImageIO.read(new File(imageWaterMarkFileUrl));
                bufferedImageOri = ImageUtils.addImageWatermark(bufferedImageOri, bufferedImageWaterMark, waterMark_RelativeSize, ImageUtils.WATERMARK_POSITION_BOOTOMRIGHT, ImageUtils.IMAGE_PNG, waterMark_TransparencyFactor);
            }

            //generate a original corrected image
            bufferedImage = ImageUtils.advancedRescaleImage(bufferedImageOri, ResizePixels_LargeSide, ResizePixels_ShortSide, ImageUtils.SHARPING, ImageUtils.SCALING);
            ImageUtils.saveCompressedImage(bufferedImage, compressedImageFileUrl, ImageUtils.IMAGE_JPEG, compressor_Factor);
            fl = new File(compressedImageFileUrl);

        } catch (IOException | IllegalStateException ex) {
            Logger.getLogger(CompressImages.class.getName()).log(Level.SEVERE, null, ex);
        }

        return fl;
    }

    public List<File> getFamilyFixedProcessedFiles() throws IOException {

        List<File> files = new ArrayList<>();
        File fl = null;

        try {
            //System.out.println(new Date());

            /*String imageFileUrl = "C:\\pruebas\\IMG_4200.JPG";*/
            String compressedImageFileUrl = compressedImagePathFileName; // "00A000000";
             /*String imageWaterMarkFileUrl = "C:\\MEGAPACA\\DEVELOPMENT\\Projects\\Java\\Tienda en Linea\\_Images\\megapaca-m.fw.png";
             File file = new File(imageFileUrl);*/
            BufferedImage bufferedImageOri = ImageIO.read(file);

            BufferedImage bufferedImage;
            String ext = "";

            //generate a original corrected image
            ext = "o.jpg";
            bufferedImage = ImageUtils.advancedRescaleImage(bufferedImageOri, 0, 0, ImageUtils.SHARPING, ImageUtils.SCALING);
            ImageUtils.saveCompressedImage(bufferedImage, compressedImageFileUrl + ext, ImageUtils.IMAGE_JPEG, 0.6F);
            fl = new File(compressedImageFileUrl + ext);
            files.add(fl);

            //add waterMark to original
            /*BufferedImage bufferedImageWaterMark = ImageIO.read(new File(imageWaterMarkFileUrl));
            bufferedImageOri = ImageUtils.addImageWatermark(bufferedImageOri, bufferedImageWaterMark, waterMark_RelativeSize, ImageUtils.WATERMARK_POSITION_BOOTOMRIGHT, ImageUtils.IMAGE_PNG, waterMark_TransparencyFactor);
*/
            //generate sized images
            ext = "xl.jpg";
            bufferedImage = ImageUtils.advancedRescaleImage(bufferedImageOri, 2048, 1536, ImageUtils.SHARPING, ImageUtils.SCALING);
            ImageUtils.saveCompressedImage(bufferedImage, compressedImageFileUrl + ext, ImageUtils.IMAGE_JPEG, 0.65F);
            fl = new File(compressedImageFileUrl + ext);
            files.add(fl);
            
            ext = "l.jpg";
            bufferedImage = ImageUtils.advancedRescaleImage(bufferedImageOri, 1024, 768, ImageUtils.SHARPING, ImageUtils.SCALING);
            ImageUtils.saveCompressedImage(bufferedImage, compressedImageFileUrl + ext, ImageUtils.IMAGE_JPEG, 0.65F);
            fl = new File(compressedImageFileUrl + ext);
            files.add(fl);

            ext = "m.jpg";
            bufferedImage = ImageUtils.advancedRescaleImage(bufferedImageOri, 720, 480, ImageUtils.SHARPING, ImageUtils.SCALING);
            ImageUtils.saveCompressedImage(bufferedImage, compressedImageFileUrl + ext, ImageUtils.IMAGE_JPEG, 0.70F);
            fl = new File(compressedImageFileUrl + ext);
            files.add(fl);

            ext = "s.jpg";
            bufferedImage = ImageUtils.advancedRescaleImage(bufferedImageOri, 460, 368, ImageUtils.SHARPING, ImageUtils.SCALING);
            ImageUtils.saveCompressedImage(bufferedImage, compressedImageFileUrl + ext, ImageUtils.IMAGE_JPEG, 0.75F);
            fl = new File(compressedImageFileUrl + ext);
            files.add(fl);

            ext = "t.jpg";
            bufferedImage = ImageUtils.advancedRescaleImage(bufferedImageOri, 192, 153, ImageUtils.SHARPING, ImageUtils.SCALING);
            ImageUtils.saveCompressedImage(bufferedImage, compressedImageFileUrl + ext, ImageUtils.IMAGE_JPEG, 0.70f);
            fl = new File(compressedImageFileUrl + ext);
            files.add(fl);

            ext = "i.jpg";
            bufferedImage = ImageUtils.advancedRescaleImage(bufferedImageOri, 50, 34, ImageUtils.SHARPING, ImageUtils.SCALING);
            ImageUtils.saveCompressedImage(bufferedImage, compressedImageFileUrl + ext, ImageUtils.IMAGE_JPEG, 0.9F);
            fl = new File(compressedImageFileUrl + ext);
            files.add(fl);

            //System.out.println(new Date());

        } catch (IOException | IllegalStateException ex) {
            Logger.getLogger(CompressImages.class.getName()).log(Level.SEVERE, null, ex);
        }

        return files;
    }

}
