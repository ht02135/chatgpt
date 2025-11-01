
# https://www.w3schools.com/python/python_dictionaries.asp

# Python Dictionaries
# Dictionary items are ordered, changeable, and do not allow duplicates.
thisdict =    {
  "brand": "Ford",
  "model": "Mustang",
  "year": 1964
}
print(thisdict)

# The dict() Constructor
thisdict = dict(name = "John", age = 36, country = "Norway")
print(thisdict)

# Accessing Items
thisdict =    {
  "brand": "Ford",
  "model": "Mustang",
  "year": 1964
}
x = thisdict["model"]
print(x)

# Get Keys
print(thisdict.keys())

# Get Values
print(thisdict.values())

# Get Items
# The items() method will return each item in a dictionary, as tuples in a list.
print(thisdict.items())

# Nested Dictionaries
myfamily = {
  "child1" : {
    "name" : "Emil",
    "year" : 2004
  },
  "child2" : {
    "name" : "Tobias",
    "year" : 2007
  },
  "child3" : {
    "name" : "Linus",
    "year" : 2011
  }
} 
print(myfamily["child2"]["name"]) 


