
# https://www.w3schools.com/python/python_while_loops.asp
# Python has two primitive loop commands:
#    while loops
#    for loops

# The break Statement
# With the break statement we can stop the loop even if the while condition is true:
i = 1
while i < 6:
  print(i)
  if i == 3:
    break
  i += 1 
print(i) # this print 3

# The continue Statement
# With the continue statement we can stop the current iteration, and continue with the next:
i = 0
while i < 6:
  i += 1
  if i == 3:
    continue
  print(i) # skip print 3
  
i = 0
while i < 6:
  i += 1
  if i == 3:
    continue
print(i) # this loop thru i and print 1=6


