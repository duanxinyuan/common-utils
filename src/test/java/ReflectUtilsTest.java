import com.google.common.collect.Lists;
import com.dxy.library.util.common.ReflectUtils;
import lombok.Data;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author duanxinyuan
 * 2019/2/21 19:52
 */
public class ReflectUtilsTest {


    @Test
    public void test() {
        TestPojo testPojo = new TestPojo();
        ReflectUtils.invokeSet(testPojo, "name", "A");
        String name = (String) ReflectUtils.invokeGet(testPojo, "name");
        System.out.println(name);

        List<Field> fields = ReflectUtils.getFields(TestPojo.class);
        System.out.println(fields);

        Object value = ReflectUtils.getFieldValue(testPojo, "name");
        System.out.println(value);
    }

    @Test
    public void union() {
        TestPojo source = new TestPojo();
        source.setAge(10);
        source.setName("test1");

        TestPojo target = new TestPojo();
        target.setId("56a4d6a");
        target.setAddress("56a4d6a");
        target.setName("test2");

        System.out.println(ReflectUtils.union(source, target));
    }

    @Test
    public void copy() {
        TestPojo source = new TestPojo();
        source.setAge(10);
        source.setName("test1");

        TestPojo target = new TestPojo();
        target.setId("56a4d6a");
        target.setAddress("56a4d6a");
        target.setName("test2");

        System.out.println(ReflectUtils.copy(source, target));

        List<TestPojo> testPojos = Lists.newArrayList();
        for (int i = 0; i < 3; i++) {
            testPojos.add(source);
        }
        System.out.println(ReflectUtils.copy(testPojos, TestPojo.class));
    }

    @Test
    public void testCopyByCls() {
        Map<String, String> mValue = new HashMap<>();
        mValue.put("key", "K");
        mValue.put("value", "V");
        Item objValue = ReflectUtils.copy(mValue, Item.class);
        Assert.assertEquals("K", objValue.getKey());
        Assert.assertEquals("V", objValue.getValue());
    }

    @Test
    public void testCopyFromMap() {
        Map<String, String> mValue = new HashMap<>();
        mValue.put("key", "K");
        mValue.put("value", "V");
        Item objValue = new Item();
        ReflectUtils.copy(mValue, objValue);
        Assert.assertEquals("K", objValue.getKey());
        Assert.assertEquals("V", objValue.getValue());
    }

    @Test
    public void testCopyToMap() {
        Item objValue = new Item();
        objValue.setKey("K");
        objValue.setValue("V");
        Map<String, String> mValue = new HashMap<>();
        ReflectUtils.copy(objValue, mValue);
        Assert.assertEquals("K", mValue.get("key"));
        Assert.assertEquals("V", mValue.get("value"));
    }

    @Test
    public void testCopyLoop() {
        Item source = new Item();
        source.setKey("K");
        source.setValue("V");
        Map<String, String> mValue = new HashMap<>();
        ReflectUtils.copy(source, mValue);

        Item target = ReflectUtils.copy(mValue, Item.class);
        Assert.assertEquals(source, target);
    }

    @Data
    private static class Item {
        private static final int STATIC = 123;
        private String key;
        private String value;
    }

}
