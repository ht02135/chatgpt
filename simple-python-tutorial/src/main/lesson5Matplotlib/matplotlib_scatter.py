
# https://www.w3schools.com/python/matplotlib_scatter.asp

# Creating Scatter Plots
import matplotlib.pyplot as plt
import numpy as np
import os

# ensure output folder exists
output_dir = r"C:\temp"
os.makedirs(output_dir, exist_ok=True)

# --- First Scatter Plot ---
x = np.array([5,7,8,7,2,17,2,9,4,11,12,9,6])
y = np.array([99,86,87,88,111,86,103,87,94,78,77,85,86])

plt.scatter(x, y)
# plt.show() # show it 
plt.savefig(os.path.join(output_dir, "scatter_plot1.png"))
plt.close()

# Compare Plots
# day one, the age and speed of 13 cars:
x2 = np.array([5,7,8,7,2,17,2,9,4,11,12,9,6])
y2 = np.array([99,86,87,88,111,86,103,87,94,78,77,85,86])
plt.scatter(x2, y2)
# plt.show() # show it 
plt.scatter(x2, y2, color='hotpink')

# day two, the age and speed of 15 cars:
x2 = np.array([2,2,8,1,15,8,12,9,7,3,11,4,7,14,12])
y2 = np.array([100,105,84,105,90,99,90,95,94,100,79,112,91,80,85])
plt.scatter(x2, y2)
plt.scatter(x2, y2, color='#88c999')

plt.savefig(os.path.join(output_dir, "scatter_compare.png"))
plt.close()

# You can change the size of the dots with the s argument.
x3 = np.array([5,7,8,7,2,17,2,9,4,11,12,9,6])
y3 = np.array([99,86,87,88,111,86,103,87,94,78,77,85,86])
sizes = np.array([20,50,100,200,500,1000,60,90,10,300,600,800,75])

plt.scatter(x3, y3, s=sizes)
# plt.show() # show it 
plt.savefig(os.path.join(output_dir, "scatter_sizes.png"))
plt.close()

print("✅ Plots saved to C:\\temp")
