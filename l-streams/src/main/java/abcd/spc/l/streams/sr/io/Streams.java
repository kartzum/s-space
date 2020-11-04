package abcd.spc.l.streams.sr.io;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Streams {
    public static void main(String[] args) {
        outputStream();
    }

    static void outputStream() {
        File f = null;
        try {
            f = File.createTempFile("prefix", null, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String fileName = f.getName();

        try (OutputStream writer = new FileOutputStream(fileName)) {
            writer.write("data".getBytes(StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuilder buffer = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)))) {
            String responseLine;
            while ((responseLine = reader.readLine()) != null) {
                buffer.append(responseLine);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(fileName);
        System.out.println(buffer.toString());
    }
}
