
# https://www.w3schools.com/python/python_class_properties.asp

# Class Properties vs Object Properties
class Person:
  species = "Human" # Class property

  def __init__(self, name):
    self.name = name # Instance property

p1 = Person("Emil")
p2 = Person("Tobias")

print(p1.name)
print(p2.name)
print(p1.species)
print(p2.species) 

# Add New Properties
# hung : may be i old school. you will never see my pull this move
class Person2:
  def __init__(self, name):
    self.name = name

p3 = Person2("Tobias")

p3.age = 25         #  add new property
p3.city = "Oslo"    #  add new property

print(p3.name)
print(p3.age)
print(p3.city) 