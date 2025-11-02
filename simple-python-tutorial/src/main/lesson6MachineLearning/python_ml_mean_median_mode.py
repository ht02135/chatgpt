
# https://www.w3schools.com/python/python_ml_getting_started.asp
# https://www.w3schools.com/python/python_ml_mean_median_mode.asp

# Mean - The average value
# Median - The mid point value
# Mode - The most common value

import numpy

speed = [99,86,87,88,111,86,103,87,94,78,77,85,86]
x = numpy.mean(speed)
y = numpy.median(speed)
print(f"mean = {x}")                            # acceptable
print("mean = {}".format(x))                    # acceptable
print("mean =", x)                              # shitty

print(f"mean = {x}, median = {y}")              # acceptable
print("mean = {}, median = {}".format(x, y))    # acceptable
print("mean =", x, ", median =", y)             # shitty



