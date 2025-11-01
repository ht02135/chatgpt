
# https://www.w3schools.com/python/python_arrays.asp

cars = ["Ford", "Volvo", "BMW"] 
for x in cars:
  print(x) 

# https://www.w3schools.com/python/python_iterators.asp

# Iterator vs Iterable
# Lists, tuples, dictionaries, and sets are all iterable objects. 
# They are iterable containers which you can get an iterator from.
# All these objects have a iter() method which is used to get an iterator:

mytuple = ("apple", "banana", "cherry")
myit = iter(mytuple)

print(next(myit))
print(next(myit))
print(next(myit))

# Create an Iterator
# To create an object/class as an iterator you have to implement the 
# methods __iter__() and __next__() to your object.

class MyNumbers:
  def __iter__(self):
    self.a = 1
    return self

  def __next__(self):
    x = self.a
    self.a += 1
    return x

myclass = MyNumbers()
myiter = iter(myclass)

print(next(myiter))
print(next(myiter))
print(next(myiter))
print(next(myiter))
print(next(myiter)) 






