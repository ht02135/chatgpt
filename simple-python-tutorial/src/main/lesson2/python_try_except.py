
# https://www.w3schools.com/python/python_try_except.asp

# finally
try:
  f = open("demofile.txt")
  try:
    f.write("Lorum Ipsum")
  except:
    print("Something went wrong when writing to the file")
  finally:
    f.close()
except:
  print("Something went wrong when opening the file") 

# Raise an exception
def check_positive(x):
    if x < 0:
        raise Exception("Sorry, no numbers below zero")
    return x

x = -1
try:
  print(check_positive(x))
except:
  print("Something went wrong when check_positive") 

# Raise a TypeError if x is not an integer:
def process_number(x):
    if not isinstance(x, int):
        raise TypeError("Only integers are allowed")
    print(f"Processing number: {x}")
  
try:
  # Output: Processing number: 10
  process_number(10)
  
  # Raises: TypeError: Only integers are allowed
  process_number("hello")
except:
  print("Something went wrong when process_number")
  
 
    
    
    