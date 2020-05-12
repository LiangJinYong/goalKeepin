package goalKeepin.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

public class FileUploadUtils {

	private FileUploadUtils() {}
	
	public static String processFileUpload(MultipartFile file, String basePath, String country) {
		String fileName = file.getOriginalFilename();
		String suffixName = fileName.substring(fileName.lastIndexOf("."));
        
		// generate file name
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/");
        String datePath = sdf.format(new Date());
        
        File imageDir = new File(basePath + datePath);
        if (!imageDir.exists()) {
			imageDir.mkdirs();
		}
        
        sdf = new SimpleDateFormat("HHmmss");
        StringBuilder tempName = new StringBuilder();
        tempName.append(sdf.format(new Date()));
        
        if (country != null) {
        	tempName.append("_" + country);
		}
        
        tempName.append(suffixName);
        String newFileName = tempName.toString();

        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(imageDir.getAbsolutePath() + "/" + newFileName);
            Files.write(path, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return datePath + newFileName;
	}
}
