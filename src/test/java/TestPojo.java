import java.io.Serializable;

/**
 * @author duanxinyuan
 * 2019/4/15 20:05
 */
public class TestPojo extends AbstractTestPojo implements Serializable {

    private static final long serialVersionUID = -5596484989974394879L;

    private String name;

    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "id: " + getId() + ", name: " + getName() + ", age: " + getAge() + ", address: " + getAddress();
    }
}
