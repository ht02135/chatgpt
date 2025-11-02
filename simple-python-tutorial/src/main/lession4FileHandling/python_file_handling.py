
# https://www.w3schools.com/python/python_file_handling.asp

# The open() function takes two parameters; filename, and mode.
# "r" - Read - Default value. Opens a file for reading, error if the file does not exist
# "a" - Append - Opens a file for appending, creates the file if it does not exist
# "w" - Write - Opens a file for writing, creates the file if it does not exist
# "x" - Create - Creates the specified file, returns an error if the file exists

# "t" - Text - Default value. Text mode
# "b" - Binary - Binary mode (e.g. images)

# ////////////////////////////////////

import os

# Define working directory and file paths
BASE_DIR = r"C:\Temp"
TEXT_FILE = os.path.join(BASE_DIR, "test.txt")
NEW_FILE = os.path.join(BASE_DIR, "newfile.txt")
BINARY_FILE = os.path.join(BASE_DIR, "binaryfile.bin")

# Ensure directory exists
os.makedirs(BASE_DIR, exist_ok=True)

def write_text_file():
    print("=== write_text_file() ===")
    with open(TEXT_FILE, "w") as f:
        f.write("Hello from write mode!\n")
        f.write("This file is stored in C:\\Temp.\n")
    print(f"{TEXT_FILE} written successfully.\n")

def append_text_file():
    print("=== append_text_file() ===")
    with open(TEXT_FILE, "a") as f:
        f.write("This line was appended.\n")
    print(f"Appended new line to {TEXT_FILE}.\n")

def read_text_file():
    print("=== read_text_file() ===")
    with open(TEXT_FILE, "r") as f:
        content = f.read()
    print(f"Contents of {TEXT_FILE}:\n{content}\n")

def create_new_file():
    print("=== create_new_file() ===")
    try:
        with open(NEW_FILE, "x") as f:
            f.write("This file was created with 'x' mode.\n")
        print(f"{NEW_FILE} created successfully.\n")
    except FileExistsError:
        print(f"{NEW_FILE} already exists, skipping creation.\n")

def write_binary_file():
    print("=== write_binary_file() ===")
    data = bytes([72, 101, 108, 108, 111])  # "Hello"
    with open(BINARY_FILE, "wb") as f:
        f.write(data)
    print(f"{BINARY_FILE} written successfully in binary mode.\n")

def read_binary_file():
    print("=== read_binary_file() ===")
    with open(BINARY_FILE, "rb") as f:
        data = f.read()
    print(f"Raw binary data: {data}")
    print(f"Decoded as text: {data.decode('utf-8')}\n")

if __name__ == "__main__":
    print("Running file operations in C:\\Temp ...\n")
    write_text_file()
    append_text_file()
    read_text_file()
    create_new_file()
    write_binary_file()
    read_binary_file()
    print("All operations completed successfully.")

# Write to an Existing File
# Append new content to test.txt
with open(TEXT_FILE, "a") as f:
    f.write("Now the file has more content!\n")

# Open and read the file after appending
with open(TEXT_FILE, "r") as f:
    content = f.read()

print("Contents of test.txt after appending:")
print(content)

# Overwrite Existing Content
with open(TEXT_FILE, "w") as f:
  f.write("Woops! I have deleted the content!")

# open and read the file after the overwriting:
with open(TEXT_FILE) as f:
  print(f.read()) 

# Delete a File
if os.path.exists(BINARY_FILE):
  os.remove(BINARY_FILE)
else:
  print("The file does not exist") 











 