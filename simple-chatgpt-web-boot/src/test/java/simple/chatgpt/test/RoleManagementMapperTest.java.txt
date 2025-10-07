package simple.chatgpt.test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import simple.chatgpt.util.ParamWrapper;

public class RoleManagementMapperTest {

    private static SqlSessionFactory sqlSessionFactory;
    private static String mapperNamespace = "simple.chatgpt.mapper.management.security.RoleManagementMapper";

    @BeforeAll
    public static void setUp() throws Exception {
        // Load MyBatis configuration XML (make sure path is correct)
        String resource = "mybatis-config.xml"; // put this in src/test/resources
        try (InputStream inputStream = RoleManagementMapperTest.class.getClassLoader().getResourceAsStream(resource)) {
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        }
        assertNotNull(sqlSessionFactory, "SqlSessionFactory should not be null");
    }

    @Test
    public void testFindRoleMethods() {
        try (SqlSession session = sqlSessionFactory.openSession()) {

            // ---------------- Test findRoleByName ----------------
            Map<String, Object> params = new HashMap<>();
            Map<String, Object> wrapped = ParamWrapper.wrap("roleName", "ADMIN_ROLE"); // wrap with ParamWrapper

            Object result = session.selectOne(mapperNamespace + ".findRoleByName", wrapped);
            System.out.println("findRoleByName result = " + result);

            // ---------------- Test findRoleById ----------------
            params.clear();
            params.put("roleId", 1L);
            wrapped = ParamWrapper.wrap("roleId", 1L);

            Object resultById = session.selectOne(mapperNamespace + ".findRoleById", wrapped);
            System.out.println("findRoleById result = " + resultById);
        }
    }
}
