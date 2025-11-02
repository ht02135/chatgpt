
# https://www.w3schools.com/python/python_pip.asp

# PIP is a package manager for Python packages, or modules if you like.

# Check PIP version:
# C:\Users\ht021\AppData\Local\Programs\Python\Python314\Scripts>pip --version
# pip 25.2 from C:\Users\ht021\AppData\Local\Programs\Python\Python314\Lib\site-packages\pip (python 3.14)

# Download a package named "camelcase":
# pip install camelcase

# Import and use "camelcase":
import camelcase

c = camelcase.CamelCase()
txt = "hello world"
print(c.hump(txt)) 

# List Packages
# pip list

# C:\Users\ht021\AppData\Local\Programs\Python\Python314\Scripts>pip list
# Package   Version
# --------- -------
# camelcase 0.2
# pip       25.2


