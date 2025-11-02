
# https://www.w3schools.com/python/python_classes.asp
# https://www.w3schools.com/python/python_class_self.asp

class MyClass:
  x = 5
  
p1 = MyClass()
p2 = MyClass()
p3 = MyClass()

print(p1.x)
print(p2.x)
print(p3.x) 

# The pass Statement
# class definitions cannot be empty, but if you for some reason 
# have a class definition with no content, put in the pass statement 
# to avoid getting an error.
class Person0:
  pass

# The __init__() Method
# All classes have a built-in method called __init__(), which is 
# always executed when the class is being initiated.
class Person:
  def __init__(self, name, age):
    self.name = name
    self.age = age

p1 = Person("Emil", 36)

print(p1.name)
print(p1.age) 

# Default Values in __init__()
class Person2:
  def __init__(self, name, age=18):
    self.name = name
    self.age = age

p1 = Person2("Emil")
p2 = Person2("Tobias", 25)

print(p1.name, p1.age)
print(p2.name, p2.age) 

# The self Parameter
# The self parameter is a reference to the current instance of the class.
class Person3:
  def __init__(self, name, age):
    self.name = name
    self.age = age

  def greet3(self):
    print("Hello, my name is " + self.name)

p1 = Person3("Emil", 25)
p1.greet3() 

# Calling Methods with self
# hung : personally requiring self in param leaves me the bad taste
# why not just make self accessible in method without specify in param?
class Person4:
  def __init__(self, name):
    self.name = name

  def greet(self):
    return "Hello, " + self.name

  def welcome(self):
    message = self.greet()
    print(message + "! Welcome to our website.")

p1 = Person4("Tobias")
p1.welcome() 










