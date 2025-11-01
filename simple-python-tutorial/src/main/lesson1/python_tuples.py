
# https://www.w3schools.com/python/python_tuples.asp
# Tuple items are ordered, unchangeable (immutable), and allow duplicate values.

# Creating a tuple
my_tuple = (1, 2, 3, "Hello", True)

# Accessing elements
print(my_tuple[0])  # Output: 1
print(my_tuple[3])  # Output: Hello

# Tuples are immutable – the following would cause an error
# my_tuple[1] = 10

# You can loop through a tuple
for item in my_tuple:
    print(item)

# The tuple() Constructor
thistuple = tuple(("apple", "banana", "cherry")) # note the double round-brackets
print(thistuple)

# ////////////////////////////////
# chatgpt

# Tuple (immutable, ordered)
fruits_tuple = ('apple', 'banana', 'cherry')
print(type(fruits_tuple))  # <class 'tuple'>

# List (mutable, ordered)
fruits_list = ['apple', 'banana', 'cherry']
print(type(fruits_list))  # <class 'list'>

# Set (mutable, unordered, unique)
fruits_set = {'apple', 'banana', 'cherry'}
print(type(fruits_set))  # <class 'set'>

# Add Items
# Convert the tuple into a list, add "orange", and convert it back into a tuple:
thistuple = ("apple", "banana", "cherry")
y = list(thistuple)
y.append("orange")
thistuple = tuple(y)

# Python - Unpack Tuples
# But, in Python, we are also allowed to extract the values back into variables. This is called "unpacking":
fruits = ("apple", "banana", "cherry")
(green, yellow, red) = fruits
print(green)
print(yellow)
print(red)

# Using Asterisk*
# If the number of variables is less than the number of values, 
# you can add an * to the variable name and the values will be 
# assigned to the variable as a list:
fruits = ("apple", "banana", "cherry", "strawberry", "raspberry")
(green, yellow, *red) = fruits
print(green)
print(yellow)
print(red)
