package simple.chatgpt.pojo.management.jwt;

import java.util.List;

public interface JwtUser {

    String getUserName();

    String getPassword();

    List<String> getRoleGroupRefs();

    boolean getLocked();

    boolean getActive();
}
