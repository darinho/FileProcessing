
import gt.megapaca.imagecompressor.ImageUtils;
import gt.megapaca.fileprocessing.SFTPinJava;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.imageio.ImageIO;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author MIGUEL
 */
public class CompressImage {

    public static final float COMPRESSOR_FACTOR = 0.7F;
    public static final float WATERMARK_RELATIVESIZE = 0.1F;
    public static final float WATERMARK_TRANSPARENCY = 0.7F;

    public static void main(String[] args) throws IOException {

        try {
            System.out.println(new Date());

            String imageFileUrl = "C:\\pruebas\\triste.gif";
            String compressedImageFileUrl = "C:\\pruebas\\P_R_U_E_B_A";
            //String compressedImageFileUrl = "00A000000";
            String imageWaterMarkFileUrl = "";

            File file = new File(imageFileUrl);
            BufferedImage bufferedImageOri = ImageIO.read(file);
            System.out.println("have loade buffered.");

            BufferedImage bufferedImage;
            List<File> files = new ArrayList<>();

            //generate a original corrected image
            bufferedImage = bufferedImageOri;//ImageUtils.advancedRescaleImage(bufferedImageOri, 0, 0, ImageUtils.SHARPING, ImageUtils.SCALING);
            ImageUtils.saveCompressedImage(bufferedImage, compressedImageFileUrl + "_o.jpg", ImageUtils.IMAGE_JPEG, 0.6F);
            files.add(new File(compressedImageFileUrl + "_o.jpg"));

            System.out.println("Creada primer imagen");

            //add waterMark to original
            BufferedImage bufferedImageWaterMark = ImageIO.read(new File(imageWaterMarkFileUrl));
            bufferedImageOri = ImageUtils.addImageWatermark(bufferedImageOri, bufferedImageWaterMark, WATERMARK_RELATIVESIZE, ImageUtils.WATERMARK_POSITION_BOOTOMRIGHT, ImageUtils.IMAGE_PNG, WATERMARK_TRANSPARENCY);

            //generate sized images
            bufferedImage = ImageUtils.advancedRescaleImage(bufferedImageOri, 1024, 768, ImageUtils.SHARPING, ImageUtils.SCALING);
            ImageUtils.saveCompressedImage(bufferedImage, compressedImageFileUrl + "_l.jpg", ImageUtils.IMAGE_JPEG, 0.65F);
            files.add(new File(compressedImageFileUrl + "_l.jpg"));

            bufferedImage = ImageUtils.advancedRescaleImage(bufferedImageOri, 720, 480, ImageUtils.SHARPING, ImageUtils.SCALING);
            ImageUtils.saveCompressedImage(bufferedImage, compressedImageFileUrl + "_m.jpg", ImageUtils.IMAGE_JPEG, 0.70F);
            files.add(new File(compressedImageFileUrl + "_m.jpg"));

            bufferedImage = ImageUtils.advancedRescaleImage(bufferedImageOri, 460, 368, ImageUtils.SHARPING, ImageUtils.SCALING);
            ImageUtils.saveCompressedImage(bufferedImage, compressedImageFileUrl + "_s.jpg", ImageUtils.IMAGE_JPEG, 0.75F);
            files.add(new File(compressedImageFileUrl + "_s.jpg"));

            bufferedImage = ImageUtils.advancedRescaleImage(bufferedImageOri, 192, 153, ImageUtils.SHARPING, ImageUtils.SCALING);
            ImageUtils.saveCompressedImage(bufferedImage, compressedImageFileUrl + "_t.jpg", ImageUtils.IMAGE_JPEG, 0.70f);
            files.add(new File(compressedImageFileUrl + "_t.jpg"));

            bufferedImage = ImageUtils.advancedRescaleImage(bufferedImageOri, 50, 34, ImageUtils.SHARPING, ImageUtils.SCALING);
            ImageUtils.saveCompressedImage(bufferedImage, compressedImageFileUrl + "_i.jpg", ImageUtils.IMAGE_JPEG, 0.9F);
            files.add(new File(compressedImageFileUrl + "_i.jpg"));

            System.out.println(files.size() + " files to upload.");

            //************
            String SFTPHOST = "megapaca.com";
            int SFTPPORT = 1157;
            String SFTPUSER = "megapaca";
            String SFTPPASS = "Mega2014.mp";
            String SFTPWORKINGDIR = "/home/megapaca/public_html/e/images/source/";
            String PARENT_PATH = "miguel/";
            SFTPinJava.saveFileToSftp(files, false, SFTPHOST, SFTPPORT, SFTPUSER, SFTPPASS, SFTPWORKINGDIR, PARENT_PATH);
            //************

            System.out.println(new Date());

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

}
