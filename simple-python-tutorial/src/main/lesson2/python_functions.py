
# https://www.w3schools.com/python/python_functions.asp

# Creating a Function
def my_function():
  print("Hello from a function")

my_function()

# Why Use Functions?
def fahrenheit_to_celsius(fahrenheit):
  return (fahrenheit - 32) * 5 / 9

print(fahrenheit_to_celsius(77))
print(fahrenheit_to_celsius(95))
print(fahrenheit_to_celsius(50)) 

# The pass Statement
# Function definitions cannot be empty. If you need to create a 
# function placeholder without any code, use the pass statement:
def my_function2():
  pass

# Arguments
def my_function3(fname):
  print(fname + " Refsnes")

my_function3("Emil")
my_function3("Tobias")
my_function3("Linus")

# Default Parameter Values
def my_function4(name = "friend"):
  print("Hello", name)

my_function4("Emil")
my_function4("Tobias")
my_function4()
my_function4("Linus") 

# Keyword Arguments
# this is really a pointless feature.  why would you ever want to use
# this pointless feature anyway is beyond me...
def my_function5(animal, name):
  print("I have a", animal)
  print("My", animal + "'s name is", name)

my_function5(animal = "dog", name = "Buddy") 

# Arbitrary Arguments - *args
# If you do not know how many arguments will be passed into your function, 
# add a * before the parameter name.
def my_function6(*kids):
  print("The youngest child is " + kids[2])
  
my_function6("Emil", "Tobias", "Linus") 

def my_function7(*args):
  print("Type:", type(args))
  print("First argument:", args[0])
  print("Second argument:", args[1])
  print("All arguments:", args)

my_function7("Emil", "Tobias", "Linus") 

# Using *args with Regular Arguments
def my_function8(greeting, *names):
  for name in names:
    print(greeting, name)

my_function8("Hello", "Emil", "Tobias", "Linus") 

# Function Inside Function
def myfunc():
  x = 300
  def myinnerfunc():
    print(x)
  myinnerfunc()

myfunc() 

# The LEGB Rule
x = "global"

def outer():
  x = "enclosing"
  def inner():
    x = "local"
    print("Inner:", x)
  inner()
  print("Outer:", x)

outer()
print("Global:", x) 

# Basic Decorator
# By placing @changecase directly above the function definition, 
# the function myfunction is being "decorated" with the changecase function.
def changecase(func):
  def myinner():
    return func().upper()
  return myinner

@changecase
def myfunction():
  return "Hello Sally"

print(myfunction())

# Arguments in the Decorated Function
def changecase2(func):
  def myinner2(x):
    return func(x).upper()
  return myinner2

@changecase2
def myfunction2(nam):
  return "Hello " + nam

print(myfunction2("John"))

# Lambda Functions
# lambda arguments : expression
x = lambda a : a + 10
print(x(5)) 

# Summarize argument a, b, and c and return the result:
x = lambda a, b, c : a + b + c
print(x(5, 6, 2)) 

# The power of lambda is better shown when you use them as an anonymous function inside another function.
def myfunc3(n):
  return lambda a : a * n

mydoubler = myfunc3(2)
print(mydoubler(11))

# Recursion
# A base case - A condition that stops the recursion
# A recursive case - The function calling itself with a modified argument
def factorial(n):
  # Base case
  if n == 0 or n == 1:
    return 1
  # Recursive case
  else:
    return n * factorial(n - 1)

print(factorial(5)) 

# Fibonacci Sequence
def fibonacci(n):
  if n <= 1:
    return n
  else:
    return fibonacci(n - 1) + fibonacci(n - 2)

print(fibonacci(7)) 

# Recursion with Lists
def find_max(numbers):
  if len(numbers) == 1:
    return numbers[0]
  else:
    max_of_rest = find_max(numbers[1:])
    return numbers[0] if numbers[0] > max_of_rest else max_of_rest

my_list = [3, 7, 2, 9, 1]
print(find_max(my_list)) 






