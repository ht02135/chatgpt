
# https://www.w3schools.com/python/python_ml_normal_data_distribution.asp

# Standard deviation measures how spread out your data is around the average (mean).
# Small standard deviation:
# Most values are close to the mean → data is tightly grouped.
# Large standard deviation:
# Values are spread out far from the mean → data is more variable.

# Example: Comparing small vs large standard deviation in car fuel efficiency
import numpy
import matplotlib.pyplot as plt
import os

# --- Variables ---
mean_mpg = 30.0            # average fuel efficiency (MPG)
std_dev_small = 1.0        # small standard deviation: most cars very similar
std_dev_large = 5.0        # large standard deviation: cars vary a lot
sample_size = 100000       # number of simulated cars
num_bins = 100             # number of bars (bins) in histogram

# --- Output setup ---
output_dir = r"C:\temp"
os.makedirs(output_dir, exist_ok=True)
output_file = os.path.join(output_dir, "mpg_stddev_comparison.png")

# --- Generate data ---
mpg_small_std = numpy.random.normal(mean_mpg, std_dev_small, sample_size)
mpg_large_std = numpy.random.normal(mean_mpg, std_dev_large, sample_size)

# --- Plot histograms ---
plt.hist(mpg_small_std, num_bins, alpha=0.7, label=f"Small Std Dev ({std_dev_small})")
plt.hist(mpg_large_std, num_bins, alpha=0.7, label=f"Large Std Dev ({std_dev_large})")

plt.title("Comparison: Small vs Large Standard Deviation in Car MPG")
plt.xlabel("Miles per Gallon (MPG)")
plt.ylabel("Number of Cars")
plt.legend()

# --- Save plot ---
plt.savefig(output_file)
plt.close()

print(f"✅ Comparison histogram saved to {output_file}")
