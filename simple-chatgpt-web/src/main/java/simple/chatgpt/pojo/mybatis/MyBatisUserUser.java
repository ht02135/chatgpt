package simple.chatgpt.pojo.mybatis;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import simple.chatgpt.validator.mybatis.email.ValidUserEmail;
import simple.chatgpt.validator.mybatis.user.ValidUser;

/*
<hibernate.version>5.6.9.Final</hibernate.version> in pom.xml
Older Hibernate Validator (≤ 6.x) uses javax.validation.*
New Hibernate Validator (≥ 7.x / 8.x) uses jakarta.validation.*
*/

/*
you don’t really need a ConstraintValidator if your goal 
is just to trigger validation via AOP.
///////////////////
///When the Aspect calls validator.validate(user):
1>It sees name has @NotBlank → checks the value.
2>It sees email has @UserEmail → runs that validator.
3>Any violations are returned as ConstraintViolation objects.
So all field-level annotations are triggered automatically.
*/
@ValidUser
public class MyBatisUserUser {
    private int id;
    private String name;
    
	@NotBlank(message = "email must not be blank")
	@ValidUserEmail
    private String email;
	
	@NotBlank(message = "firstName must not be blank")
    private String firstName;
	
	@NotBlank(message = "lastName must not be blank")
    private String lastName;
	
    @NotNull(message = "password must be between 4 to 20 characters")
	@Size(min = 4, max = 20)
	private String password;
    
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postCode;
    private String country;

    public MyBatisUserUser() {}

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getAddressLine1() { return addressLine1; }
    public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }

    public String getAddressLine2() { return addressLine2; }
    public void setAddressLine2(String addressLine2) { this.addressLine2 = addressLine2; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getPostCode() { return postCode; }
    public void setPostCode(String postCode) { this.postCode = postCode; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    @Override
    public String toString() {
        return "MyBatisUserUser{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", password='" + password + '\'' +
                ", addressLine1='" + addressLine1 + '\'' +
                ", addressLine2='" + addressLine2 + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", postCode='" + postCode + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}