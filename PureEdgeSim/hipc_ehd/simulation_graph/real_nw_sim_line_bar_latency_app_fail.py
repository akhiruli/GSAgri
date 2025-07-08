import matplotlib.pyplot as plt
import numpy as np

# Data
x_axis = [10,20,30,40,50,60,70,80,90,100]

latency_real_nw = [249.56, 263.412, 259.83, 270.34, 274.193, 277.26, 269.19, 280.85, 287.668, 307.434] #273.9737
latency_sim = [236.669, 253.37, 255.52, 265.94, 268.1, 274.78, 276.77, 267.95, 276.06, 278.64] #265.3799

#3.24% higher in real network

app_fail_real_nw = [1, 4, 5, 6, 7, 6, 8, 9, 11, 15] #7.2
app_fail_sim = [1, 4, 6, 6, 6, 5, 9, 10, 9, 9] #6.5

#10.76% higher

# Create figure and axis
#fig, ax1 = plt.subplots(figsize=(9, 5))
fig, ax1 = plt.subplots()

# Colors & Markers
colors = ['b', 'orange']
markers = ['x', 'D']
labels = ['Real-Nw-Trace', 'Nw-Sim-Param']

# Plot Latency as Line Graph
ax1.plot(x_axis, latency_real_nw, marker='x', linestyle='-', label='Real-Nw-Trace', linewidth=1, color='b')
ax1.plot(x_axis, latency_sim, marker='D', linestyle='-', label='Nw-Sim-Param', linewidth=1, color='orange')

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
ax2.bar(x_ticks - bar_width * 1.5, app_fail_real_nw, width=bar_width, color='b', alpha=0.6, label='Real-Nw-Trace')
ax2.bar(x_ticks - bar_width * 0.5, app_fail_sim, width=bar_width, color='orange', alpha=0.6, label='Nw-Sim-Param')

ax2.set_ylabel("No. of applications failure", fontsize=14)
ax2.tick_params(axis="y", labelcolor="black")

# Legends (Combining for both graphs)
ax1.legend(loc="upper left", fontsize=10, borderaxespad=0, framealpha=0.3)
ax2.legend(loc="upper right", fontsize=10, borderaxespad=0, framealpha=0.3, bbox_to_anchor=(0.7, 1))

# Grid & Show
ax1.grid(True, linestyle="--", alpha=0.6)
plt.savefig('real_nw_sim_line_bar_latency_app_fail.png')
plt.show()
