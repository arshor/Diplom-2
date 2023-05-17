import org.apache.commons.lang3.RandomStringUtils;

public class UserGenerator {

    public static User getRandom() {
        final String email = RandomStringUtils.randomAlphabetic(10).toLowerCase() + "@yandex.ru";
        final String password = RandomStringUtils.randomAlphabetic(10);
        final String name = RandomStringUtils.randomAlphabetic(10);
        return new User(email, password, name);
    }

    public static User getConst() {
        return new User("Andrey7286@yandex.ru", "asdFGH_86", "Andrey");
    }
}
