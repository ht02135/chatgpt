
# https://www.w3schools.com/python/python_inheritance.asp

# Create a Parent Class
class Person:
  def __init__(self, fname, lname):
    self.firstname = fname
    self.lastname = lname

  def printname(self):
    print(self.firstname, self.lastname)

#Use the Person class to create an object, and then execute the printname method:
x = Person("John", "Doe")
x.printname() 

# Create a Child Class
class Student(Person):
  def __init__(self, fname, lname):
    Person.__init__(self, fname, lname) 
    
x = Student("John", "Doe")
print(x)
    
# Use the super() Function
# Python also has a super() function that will make the child class inherit all the methods and properties from its parent:
class Student2(Person):
  def __init__(self, fname, lname):
    super().__init__(fname, lname) 
    
x2 = Student2("John2", "Doe2")
print(x2)

# Add a property called graduationyear to the Student class:
# Add a method called welcome to the Student class:
class Student3(Person):
  def __init__(self, fname, lname, year):
    super().__init__(fname, lname)
    self.graduationyear = year

  def welcome(self):
    print("Welcome", self.firstname, self.lastname, "to the class of", self.graduationyear) 

x3 = Student3("John3", "Doe3", 2222)
x3.welcome()









