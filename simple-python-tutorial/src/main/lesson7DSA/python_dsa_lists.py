
# https://www.w3schools.com/python/python_dsa_lists.asp

# Python Lists and Arrays
# Lists are ordered, mutable, and can contain elements of different types.
# A list is a built-in data structure in Python, used to store multiple elements.

# Empty list
x = []

# List with initial values
y = [1, 2, 3, 4, 5]

# List with mixed types
z = [1, "hello", 3.14, True] 

# Create an algorithm to find the lowest value in a list:
my_array = [7, 12, 9, 4, 11, 8]
minVal = my_array[0]

for i in my_array:
  if i < minVal:
    minVal = i

print('Lowest value:', minVal)

