import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import threads.ThreadToDownloadImageToDropbox;
import threads.ThreadToDownloadImageToFolder;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.users.FullAccount;


public class Screenshot
{
    public static void uploadToDropbox(String ACCESS_TOKEN)
    {
        try
        {
            checkingAccessToken(ACCESS_TOKEN);

            DbxClientV2 client = getAccessToYourAccountThroughAccessToken(ACCESS_TOKEN);

            takeScreenshots(client);
        } catch (com.dropbox.core.DbxException e) { System.err.println("The access token is invalid."); }
    }


    public static void uploadToFolder(String pathToFolder)
    {
        try
        {
            checkingFolderPath(pathToFolder);
            takeScreenshots(pathToFolder);
        } catch (FileNotFoundException e) { System.err.println("The specified directory path does not exist."); }
    }


    /*
        You can get access token by creating an application follow the link: www.dropbox.com/developers/apps
        You can also read information about this by follow in link: dropbox.tech/developers/generate-an-access-token-for-your-own-account
    */
    private static DbxClientV2 getAccessToYourAccountThroughAccessToken(String ACCESS_TOKEN)
    {
        DbxRequestConfig config = DbxRequestConfig.newBuilder("").build();

        return new DbxClientV2(config, ACCESS_TOKEN);
    }


    /* Take a screenshot every 10 seconds and send it to dropbox. */
    private static void takeScreenshots(DbxClientV2 client)
    {
        while(true)
        {
            BufferedImage currentImage = createAndGetScreenCapture();
            ByteArrayOutputStream bufferOfBytes = getByteArrayFromImage(currentImage);

            new ThreadToDownloadImageToDropbox(client, bufferOfBytes).start();
            sleep();
        }
    }


    private static void takeScreenshots(String pathToFolder)
    {
        while(true)
        {
            BufferedImage currentImage = createAndGetScreenCapture();

            new ThreadToDownloadImageToFolder(pathToFolder, currentImage).start();
            sleep();
        }
    }


    private static ByteArrayOutputStream getByteArrayFromImage(BufferedImage currentImage)
    {
        ByteArrayOutputStream bufferOfBytes = null;

        try
        {
            bufferOfBytes = new ByteArrayOutputStream();
            ImageIO.write(currentImage, "png", bufferOfBytes);
        } catch (IOException e) { e.printStackTrace(); }

        return bufferOfBytes;
    }


    private static BufferedImage createAndGetScreenCapture()
    {
        BufferedImage currentImage = null;

        try
        {
            Robot robot = new Robot();
            Rectangle area = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            currentImage = robot.createScreenCapture(area);
        } catch (AWTException e) { e.printStackTrace(); }

        return currentImage;
    }


    private static void sleep()
    {
        try
        {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) { e.printStackTrace(); }
    }


    private static void checkingFolderPath(String pathToFolder) throws FileNotFoundException
    {
        File folder = new File(pathToFolder);

        if (!folder.isDirectory())
        {
            throw new FileNotFoundException();
        }
    }


    private static void checkingAccessToken(String ACCESS_TOKEN) throws com.dropbox.core.DbxException
    {
        new DbxClientV2(DbxRequestConfig.newBuilder("").build(), ACCESS_TOKEN).users().getCurrentAccount();
    }


    /* If images are not uploaded to the folder, you can check the name of the connected account. */
    public static String getNameAccountThroughAccessToken(String ACCESS_TOKEN)
    {
        DbxClientV2 client = getAccessToYourAccountThroughAccessToken(ACCESS_TOKEN);
        String nameOfCurrentAccount = null;

        try
        {
            FullAccount account = client.users().getCurrentAccount();
            nameOfCurrentAccount = account.getName().getDisplayName() + " " + account.getEmail();

        } catch (DbxException e) { System.err.println("The access token is invalid."); }

        return nameOfCurrentAccount;
    }
}
