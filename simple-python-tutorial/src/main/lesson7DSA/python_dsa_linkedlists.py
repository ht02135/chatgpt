
# https://www.w3schools.com/python/python_dsa_linkedlists.asp

# There are three basic forms of linked lists:
# Singly linked lists
# Doubly linked lists
# Circular linked lists

import logging

# setup logger
logger = logging.getLogger("LinkedListExample")
logging.basicConfig(level=logging.DEBUG, format="%(asctime)s - %(levelname)s - %(message)s")

# 1. Singly Linked List (with logging)
class Node:
    def __init__(self, data):
        logger.debug("Node.__init__ data=%s", data)
        self.data = data
        self.next = None


class SinglyLinkedList:
    def __init__(self):
        logger.debug("SinglyLinkedList.__init__ called")
        self.head = None

    def append(self, data):
        logger.debug("append called data=%s", data)
        new_node = Node(data)
        if not self.head:
            self.head = new_node
            logger.debug("append head set new_node=%s", new_node)
            return
        current = self.head
        while current.next:
            current = current.next
        current.next = new_node
        logger.debug("append linked new_node=%s", new_node)

    def display(self):
        logger.debug("display called")
        elements = []
        current = self.head
        while current:
            elements.append(current.data)
            current = current.next
        logger.debug("display elements=%s", elements)
        return elements

# 2. Doubly Linked List (with logging)
class DNode:
    def __init__(self, data):
        logger.debug("DNode.__init__ data=%s", data)
        self.data = data
        self.next = None
        self.prev = None


class DoublyLinkedList:
    def __init__(self):
        logger.debug("DoublyLinkedList.__init__ called")
        self.head = None

    def append(self, data):
        logger.debug("append called data=%s", data)
        new_node = DNode(data)
        if not self.head:
            self.head = new_node
            logger.debug("append head set new_node=%s", new_node)
            return
        current = self.head
        while current.next:
            current = current.next
        current.next = new_node
        new_node.prev = current
        logger.debug("append linked prev=%s new_node=%s", current, new_node)

    def display_forward(self):
        logger.debug("display_forward called")
        elements = []
        current = self.head
        while current:
            elements.append(current.data)
            current = current.next
        logger.debug("display_forward elements=%s", elements)
        return elements

    def display_backward(self):
        logger.debug("display_backward called")
        elements = []
        current = self.head
        if not current:
            logger.debug("display_backward empty list")
            return elements
        while current.next:
            current = current.next
        while current:
            elements.append(current.data)
            current = current.prev
        logger.debug("display_backward elements=%s", elements)
        return elements

# at the bottom of the file (without the if __name__ == "__main__": guard),
# then every time you import the class elsewhere, it would still create and print that table.
# That’s messy and breaks modularity.

# Example Usage
# When you run a Python file directly, __name__ is automatically set to "__main__".
# “Only run this code when this file is executed directly — not when someone imports it.”
if __name__ == "__main__":
    logger.debug("main called")

    # --- Singly Linked List Example ---
    s_list = SinglyLinkedList()
    s_list.append("A")
    s_list.append("B")
    s_list.append("C")
    print("Singly Linked List:", s_list.display())

    # --- Doubly Linked List Example ---
    d_list = DoublyLinkedList()
    d_list.append(1)
    d_list.append(2)
    d_list.append(3)
    print("Doubly Linked List Forward:", d_list.display_forward())
    print("Doubly Linked List Backward:", d_list.display_backward())




