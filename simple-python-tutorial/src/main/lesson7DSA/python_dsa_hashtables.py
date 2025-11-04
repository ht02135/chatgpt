
# https://www.w3schools.com/python/python_dsa_hashtables.asp

# We will build the Hash Table in 5 steps:
# Create an empty list (it can also be a dictionary or a set).
# Create a hash function.
# Inserting an element using a hash function.
# Looking up an element using a hash function.
# Handling collisions.

# Hash Table Class with Logging
import logging

# setup logger
logger = logging.getLogger("HashTableExample")
logging.basicConfig(level=logging.DEBUG, format="%(asctime)s - %(levelname)s - %(message)s")


class HashTable:
    def __init__(self, size=10):
        logger.debug("__init__ called")
        logger.debug("__init__ size=%s", size)
        self.size = size
        self.table = [None] * size
        logger.debug("__init__ table initialized=%s", self.table)

    def _hash(self, key):
        logger.debug("_hash called")
        logger.debug("_hash key=%s", key)
        index = hash(key) % self.size
        logger.debug("_hash index=%s", index)
        return index

    def insert(self, key, value):
        logger.debug("insert called")
        logger.debug("insert key=%s", key)
        logger.debug("insert value=%s", value)
        index = self._hash(key)
        logger.debug("insert index=%s", index)

        if self.table[index] is None:
            self.table[index] = []
            logger.debug("insert created bucket at index=%s", index)
        # Check if key exists; update if found
        for pair in self.table[index]:
            if pair[0] == key:
                pair[1] = value
                logger.debug("insert updated existing key=%s with value=%s", key, value)
                return
        self.table[index].append([key, value])
        logger.debug("insert appended new pair=%s", [key, value])

    def get(self, key):
        logger.debug("get called")
        logger.debug("get key=%s", key)
        index = self._hash(key)
        logger.debug("get index=%s", index)
        if self.table[index] is not None:
            for pair in self.table[index]:
                if pair[0] == key:
                    logger.debug("get found value=%s", pair[1])
                    return pair[1]
        logger.debug("get key not found=%s", key)
        return None

    def remove(self, key):
        logger.debug("remove called")
        logger.debug("remove key=%s", key)
        index = self._hash(key)
        logger.debug("remove index=%s", index)
        if self.table[index] is None:
            logger.debug("remove key not found index empty=%s", index)
            return False
        for i, pair in enumerate(self.table[index]):
            if pair[0] == key:
                logger.debug("remove found key=%s", key)
                del self.table[index][i]
                logger.debug("remove deleted pair=%s", pair)
                return True
        logger.debug("remove key not found=%s", key)
        return False

    def display(self):
        logger.debug("display called")
        for i, bucket in enumerate(self.table):
            logger.debug("display index=%s bucket=%s", i, bucket)
        return self.table

# at the bottom of the file (without the if __name__ == "__main__": guard),
# then every time you import the class elsewhere, it would still create and print that table.
# That’s messy and breaks modularity.

# Example Usage
# When you run a Python file directly, __name__ is automatically set to "__main__".
# “Only run this code when this file is executed directly — not when someone imports it.”
if __name__ == "__main__":
    logger.debug("main called")

    # create hash table with default size
    ht = HashTable(size=5)

    # insert some data
    ht.insert("name", "Hung")
    ht.insert("age", 35)
    ht.insert("city", "New York")

    # retrieve values
    print("name:", ht.get("name"))
    print("city:", ht.get("city"))

    # remove entry
    ht.remove("age")

    # display entire hash table
    print("Hash Table Content:", ht.display())
