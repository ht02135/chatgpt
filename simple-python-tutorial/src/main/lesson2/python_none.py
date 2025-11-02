
# https://www.w3schools.com/python/python_none.asp

# Comparing to None
result = None
if result is None:
  print("No result yet")
else:
  print("Result is ready")

result = None
if result is not None:
  print("Result is ready")
else:
  print("No result yet")

# A function without a return statement returns None:
def myfunc():
  x = 5

x = myfunc()
print(x)




 