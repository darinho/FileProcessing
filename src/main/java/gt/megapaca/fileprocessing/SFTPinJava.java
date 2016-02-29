package gt.megapaca.fileprocessing;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author MIGUEL
 */
/**
 *
 */
import java.io.File;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author kodehelp
 * 
*/
public class SFTPinJava {

    private static void mkdirs(ChannelSftp ch, String path) throws SftpException {
        String[] folders = path.split("/");
        for (String folder : folders) {
            if (folder.length() > 0) {
                try {
                    ch.cd(folder);
                } catch (SftpException e) {
                    ch.mkdir(folder);
                    ch.cd(folder);
                }
            }
        }
    }

    /**
     *
     */
    public SFTPinJava() {
// TODO Auto-generated constructor stub
    }

    public static void saveFileToSftp(List<File> files, Boolean deleteSource, String SFTPHOST, int SFTPPORT, String SFTPUSER, String SFTPPASS, String SFTPWORKINGDIR, String PARENT_PATH) {
        /*String SFTPHOST = "megapaca.com";
         int SFTPPORT = 1157;
         String SFTPUSER = "megapaca";
         String SFTPPASS = "Mega2014.mp";
         String SFTPWORKINGDIR = "/home/megapaca/public_html/e/images/source/miguel/";*/

        Session session = null;
        Channel channel = null;
        ChannelSftp channelSftp = null;

        try {
            System.out.println("***** Enter to sftp method *****");
            JSch jsch = new JSch();
            session = jsch.getSession(SFTPUSER, SFTPHOST, SFTPPORT);
            session.setPassword(SFTPPASS);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            channel = session.openChannel("sftp");
            channel.connect();
            System.out.println("Sftp connected.");
            channelSftp = (ChannelSftp) channel;

            //posiciona directorio de trabajo
            channelSftp.cd(SFTPWORKINGDIR);
            //crea\posiciona directorios parent_path
            mkdirs(channelSftp, PARENT_PATH);

            System.out.println("***** Starting uploading *****");

            for (File file : files) {
                //channelSftp.put(new FileInputStream(file), file.getName(), ChannelSftp.OVERWRITE);
                FileInputStream fileInput = null;
                try {
                    fileInput = new FileInputStream(file);
                    channelSftp.put(fileInput, file.getName(), ChannelSftp.OVERWRITE);
                } catch (SftpException ex) {
                    if (!ex.getMessage().equalsIgnoreCase("Permission denied")) {
                        throw ex;
                    }
                } finally {
                    if (fileInput != null) {
                        try {
                            fileInput.close();
                        } catch (IOException ex) {
                            Logger.getLogger(SFTPinJava.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                System.out.println(file.getName() + " was uploaded correctly.");
                if (deleteSource) {
                    if (file.delete()) {
                        System.out.println("The file '" + file.getAbsolutePath() + "' has been successfully deleted");
                    } else {
                        System.out.println("*** The file '" + file.getAbsolutePath() + "' has NOT been successfully deleted");
                    }
                }
            }

            System.out.println("***** Files uploaded correctly *****");

            channelSftp.exit();
            channel.disconnect();
            session.disconnect();

            System.out.println("Sftp disconnected.");

        } catch (JSchException | SftpException | FileNotFoundException ex) {
            Logger.getLogger(SFTPinJava.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
