import matplotlib.pyplot as plt
import numpy as np

# Data
x_axis = [10,20,30,40,50,60,70,80,90,100]

low_latency = [233.141, 257.36, 266.785, 282.40, 277.627, 277.3488,276.334, 276.962, 280.775, 283.044] #271.17768
medium_latency =  [236.669, 253.37, 255.52, 265.94, 268.1, 274.78, 276.77, 267.95, 276.06, 278.64] #265.3799
high_latency = [234.225, 245.1214, 247.259, 261.286, 267.663, 272.146, 274.859, 269.265, 277.21, 275.993] #262.50274

low_energy = [3.8278, 7.9573, 10.4962, 15.44, 17.5864, 21.0574, 21.0855, 25.2139, 33.7834, 37.631] #19.40789
medium_energy = [5.69, 10.98, 15.02, 21.05, 23.23, 28.99, 32.55, 38.39, 44.09, 50.50]  #27.049
high_energy = [6.784, 13.71, 17.2276, 22.3378, 26.6793, 32.7691, 37.5592, 42.478, 48.763, 55.885] #30.4193:

# Create figure and axis
#fig, ax1 = plt.subplots(figsize=(9, 5))
fig, ax1 = plt.subplots()

# Colors & Markers
colors = ['b', 'orange', 'green']
markers = ['x', 'o', 'D']
labels = ['low', 'medium', 'high']

# Plot Latency as Line Graph
ax1.plot(x_axis, low_latency, marker='x', linestyle='-', label='low', linewidth=1, color='b')
ax1.plot(x_axis, medium_latency, marker='o', linestyle='-', label='medium', linewidth=1, color='green')
ax1.plot(x_axis, high_latency, marker='D', linestyle='-', label='high', linewidth=1, color='orange')

# Labels & Title
ax1.set_xlabel("No. of applications", fontsize=14)
ax1.set_ylabel("Avg. application latency (In seconds)", fontsize=14)
ax1.tick_params(axis="y", labelcolor="black")

# Create Secondary Y-Axis for Energy (Bar Graph)
ax2 = ax1.twinx()

# Width for bar offset
bar_width = 2.5  # Adjust for spacing
x_ticks = np.array(x_axis)  # Convert to NumPy array for manipulation

# Plot Energy as Bar Chart
ax2.bar(x_ticks - bar_width * 1.5, low_energy, width=bar_width, color='b', alpha=0.6, label='low')
ax2.bar(x_ticks - bar_width * 0.5, medium_energy, width=bar_width, color='green', alpha=0.6, label='medium')
ax2.bar(x_ticks + bar_width * 0.5, high_energy, width=bar_width, color='orange', alpha=0.6, label='high')

ax2.set_ylabel("UD Energy Consumption (Wh)", fontsize=14)
ax2.tick_params(axis="y", labelcolor="black")

# Legends (Combining for both graphs)
ax1.legend(loc="upper left", fontsize=10, borderaxespad=0, framealpha=0.3)
ax2.legend(loc="upper right", fontsize=10, borderaxespad=0, framealpha=0.3, bbox_to_anchor=(0.7, 1))

# Grid & Show
ax1.grid(True, linestyle="--", alpha=0.6)
plt.savefig('low_medium_high_line_bar_latency_energy.png')
plt.show()
