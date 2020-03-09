import com.dxy.library.util.common.NumberUtils;
import org.junit.Test;

/**
 * @author duanxinyuan
 * 2019/4/24 20:00
 */
public class NumberUtilsTest {

    @Test
    public void test() {
        System.out.println(NumberUtils.add(0.1, 2));
        System.out.println(NumberUtils.add(0.1, 2, 0.2));
        System.out.println(NumberUtils.addForScale(0.1, 2, 0.2));
        System.out.println(NumberUtils.addNoRound(0.1, 2, 0.2));
        System.out.println();

        System.out.println(NumberUtils.subtract(0.1, 2));
        System.out.println(NumberUtils.subtract(0.1, 2, 0.2));
        System.out.println(NumberUtils.subtractForScale(0.1, 2, 0.2));
        System.out.println(NumberUtils.subtractNoRound(0.1, 2, 0.2));
        System.out.println();


        System.out.println(NumberUtils.multiply(0.1, 2));
        System.out.println(NumberUtils.multiply(0.1, 2, 0.2));
        System.out.println(NumberUtils.multiplyForScale(0.1, 2, 0.2));
        System.out.println(NumberUtils.multiplyNoRound(0.1, 2, 0.2));
        System.out.println();


        System.out.println(NumberUtils.divide(0.1, 2));
        System.out.println(NumberUtils.divide(0.1, 2, 0.2));
        System.out.println(NumberUtils.divideForScale(0.1, 2, 0.2));
        System.out.println(NumberUtils.divideForScale(5, 4, 14));
    }

}
