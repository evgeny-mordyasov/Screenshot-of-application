package threads;

import com.dropbox.core.v2.DbxClientV2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ThreadToDownloadImageToDropbox extends Thread
{
    private DbxClientV2 client;
    private ByteArrayOutputStream bufferOfBytes;


    public ThreadToDownloadImageToDropbox(DbxClientV2 client, ByteArrayOutputStream bufferOfBytes)
    {
        this.client = client;
        this.bufferOfBytes = bufferOfBytes;
    }


    @Override
    public void run()
    {
        uploadImage(client, bufferOfBytes);
    }


    private void uploadImage(DbxClientV2 client, ByteArrayOutputStream bufferOfBytes)
    {
        try (InputStream in = new ByteArrayInputStream(bufferOfBytes.toByteArray()))
        {
            client.files().uploadBuilder(getPathToDropbox()).uploadAndFinish(in);
        } catch (IOException e ) { e.printStackTrace(); }
        catch (com.dropbox.core.DbxException e) { e.printStackTrace(); }
    }


    private String getPathToDropbox()
    {
        return new String("/" + new SimpleDateFormat("dd MM yyyy H:mm:ss").format(new Date()) + ".png");
    }
}
