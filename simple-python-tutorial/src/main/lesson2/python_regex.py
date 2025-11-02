
# https://www.w3schools.com/python/python_regex.asp

# RegEx in Python
import re

txt = "The rain in Spain"
x = re.search("^The.*Spain$", txt) 
print(x)

# The findall() Function
# The findall() function returns a list containing all matches.
txt = "The rain in Spain"
x = re.findall("ai", txt)
print(x) 

# The search() Function
# The search() function searches the string for a match, and 
# returns a Match object if there is a match.

# Search for the first white-space character in the string:
txt = "The rain in Spain"
x = re.search("\s", txt)
print("The first white-space character is located in position:", x.start()) 

# The split() Function
# The split() function returns a list where the string has been split 
# at each match:

# tokenizer
txt = "The rain in Spain"
x = re.split("\s", txt)
print(x) 

# The sub() Function
# The sub() function replaces the matches with the text of your choice:

# replace substring
txt = "The rain in Spain"
x = re.sub("\s", "9", txt)
print(x) 



