
# https://www.w3schools.com/python/python_encapsulation.asp

# Create a private class property named __age:
class Person:
  def __init__(self, name, age):
    self.name = name
    self.__age = age    # private prop

  def get_age(self):    # get method
    return self.__age

p1 = Person("Tobias", 25)
print(p1.get_age()) 

# set
class Person2:
  def __init__(self, name, age):
    self.name = name
    self.__age = age

  def get_age(self):
    return self.__age

  def set_age(self, age):
    if age > 0:
      self.__age = age
    else:
      print("Age must be positive")

p2 = Person2("Tobias", 25)
print(p2.get_age())

p2.set_age(26)
print(p2.get_age()) 

# Python also has a convention for protected properties using a single underscore _ prefix:
class Person3:
  def __init__(self, name, salary):
    self.name = name
    self._salary = salary # Protected property

p3 = Person3("Linus", 50000)
print(p3.name)
# hung : speechless, bc in java this is a no no...
print(p3._salary) # Can access, but shouldn't 

# Private Methods
# You can also make methods private using the double underscore prefix:
class Calculator:
  def __init__(self):
    self.result = 0

  def __validate(self, num):
    if not isinstance(num, (int, float)):
      return False
    return True

  def add(self, num):
    if self.__validate(num):
      self.result += num
    else:
      print("Invalid number")

calc = Calculator()
calc.add(10)
calc.add(5)
print(calc.result)
# calc.__validate(5) # This would cause an error 






