package com.simpulatorC.simulator;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileStream {

    // Method, which read file from the file stream.
    public String ReadFile(FileInputStream fileInputStream)
    {
       try
       {
           BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
           String text;
           StringBuilder stringBuilder = new StringBuilder();
           while ((text = bufferedReader.readLine()) != null)
               stringBuilder.append(text);
           return stringBuilder.toString();
       } catch (IOException e) { e.printStackTrace(); return null;}
       finally
       {
         if (fileInputStream != null)
         {
             try { fileInputStream.close(); }
             catch (IOException e) { e.printStackTrace(); }
         }
       }
    }

    // Method, which save text file in Internal Storage.
    public void SaveFile(String text, FileOutputStream fileOutputStream)
    {
        try { fileOutputStream.write(text.getBytes()); }
        catch (IOException e) { e.printStackTrace(); }
        finally
        {
            if (fileOutputStream != null)
            {
                try { fileOutputStream.close(); }
                catch (IOException e) { e.printStackTrace(); }
            }
        }
    }
}
