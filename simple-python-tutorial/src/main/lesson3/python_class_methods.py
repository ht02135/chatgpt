
# https://www.w3schools.com/python/python_class_methods.asp

# Class Methods
class Person:
  def __init__(self, name):
    self.name = name

  def greet(self):
    print("Hello, my name is " + self.name)

p1 = Person("Emil")
p1.greet() 

# Methods with Parameters
class Calculator:
  def add(self, a, b):
    return a + b

  def multiply(self, a, b):
    return a * b

calc = Calculator()
print(calc.add(5, 3))
print(calc.multiply(4, 7)) 

# Methods Modifying Properties
class Person2:
  def __init__(self, name, age):
    self.name = name
    self.age = age

  def celebrate_birthday(self):
    self.age += 1
    print(f"Happy birthday! You are now {self.age}")

p1 = Person2("Linus", 25)
p1.celebrate_birthday()
p1.celebrate_birthday() 

# The __str__() Method
class Person3:
  def __init__(self, name, age):
    self.name = name
    self.age = age

  def __str__(self):
    return f"{self.name}, ({self.age})"

p1 = Person3("Tobias", 36)
print(p1) 


