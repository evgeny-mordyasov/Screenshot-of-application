package threads;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ThreadToDownloadImageToFolder extends Thread
{
    private final String PATH_TO_FOLDER;
    private BufferedImage currentImage;


    public ThreadToDownloadImageToFolder(String pathToFolder, BufferedImage currentImage)
    {
        this.PATH_TO_FOLDER = pathToFolder;
        this.currentImage = currentImage;
    }


    @Override
    public void run()
    {
        uploadImage(currentImage);
    }


    private void uploadImage(BufferedImage currentImage)
    {
        try
        {
            ImageIO.write(currentImage, "png", getPathToFolder());
        } catch (IOException e) {}
    }


    private FileOutputStream getPathToFolder() throws FileNotFoundException
    {
        return new FileOutputStream(PATH_TO_FOLDER + new SimpleDateFormat("dd_MM_yyyy_H_mm_ss").format(new Date()) + ".png");
    }
}
