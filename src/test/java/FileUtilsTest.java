import com.dxy.library.util.common.FileUtils;
import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * @author duanxinyuan
 * 2018/10/29 14:30
 */
public class FileUtilsTest {

    @Test
    public void testDelete() {
        FileUtils.delete("/data/logs/test2");
    }

    @Test
    public void createFile() {
        FileUtils.createFile("/data/logs/test123456.txt");
    }

    @Test
    public void getFiles() {
        FileUtils.getFiles("/data/logs/");
    }

    @Test
    public void readLines() {
        FileUtils.readLines("/data/logs/test123456.txt");
    }

    @Test
    public void testSplit() {
        FileUtils.DataIterator iterator = FileUtils.splitTextFile(new File("D:\\data\\logs\\test\\main.log"), 5);
        while (iterator.hasNext()) {
            byte[] data = iterator.next();
            System.out.println(new String(data, StandardCharsets.UTF_8));
        }
    }

}
