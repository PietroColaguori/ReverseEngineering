import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.CRC32;
import java.io.FileOutputStream;

public class GetFlag {
    
    // Reconstructs the secret key used to decrypt the content of iv.png
    private static String getSecretKey() {

        // Strings observed from resources.arsc:/res/values/strings.xml (using JADX)
        String url = "https://flare-on.com/evilc2server/report_token/report_token.php?token=";
        String word = "wednesday";
        
        StringBuilder sb = new StringBuilder();
        sb.append(url.substring(4, 10));  // sb = "s://fl"
        sb.append(word.substring(2, 5));  // sb = "s://fldne"
        
        // Gets the CRC32 checksum the sb bytes
        byte[] bytes = sb.toString().getBytes();
        CRC32 crc32 = new CRC32();
        crc32.update(bytes);
        long crc32Checksum = crc32.getValue();
        
        // Concatenates the CRC32 checksum two times and returns the slice in [0, 16]
        StringBuilder sb3 = new StringBuilder();
        sb3.append(crc32Checksum);
        sb3.append(crc32Checksum);
        return sb3.toString().substring(0, 16);
    }

    // Performs decryption with the discovered algo specs
    private static byte[] decryptAES(byte[] encryptedData, String secretKey) throws Exception {
        
        // Algo specs obtained statically from resources.arsc:/res/values/strings.xml
        String ivString = "abcdefghijklmnop"; 
        byte[] ivBytes = ivString.getBytes();
        
        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        
        return cipher.doFinal(encryptedData);
    }

    public static void main(String[] args) {
        try {
            String secretKey = getSecretKey();
            System.out.println("Secret Key: " + secretKey);
            
            // Note: iv.png must be in the same directory as the GetFlag.java file
            byte[] encryptedData = Files.readAllBytes(Paths.get("iv.png"));
            
            byte[] decryptedData = decryptAES(encryptedData, secretKey);
            
            // The decrypted bytes are written in a new PNG file
            try (FileOutputStream fos = new FileOutputStream("playerscore.png")) {
                fos.write(decryptedData);
            }
            
            System.out.println("Decryption complete! Saved as playerscore.png");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
