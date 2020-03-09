import com.dxy.library.util.common.IOUtils;
import org.junit.Test;

import java.io.InputStream;
import java.time.Clock;

/**
 * @author duanxinyuan
 * 2019/4/12 18:34
 */
public class IOUtilsTest {

    @Test
    public void test() {
        InputStream inputStream = IOUtilsTest.class.getResourceAsStream("testio.txt");
        String path = IOUtilsTest.class.getResource("testio.txt").getPath();
        System.out.println(path);
        System.out.println(Clock.systemUTC().millis());
        IOUtils.readAsync(inputStream, 3, System.out::println);
        System.out.println(Clock.systemUTC().millis());
        System.out.println("success");
    }

}
