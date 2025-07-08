import matplotlib.pyplot as plt

#x_axis = [0, 15, 30, 45, 60, 75, 90, 105, 120, 135, 150]
x_axis = [0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100]

#latency:
y_axis_latency_small_GSAgri = [0, 138.25, 154.002, 168.815, 172.672, 167.703, 175.471, 172.631, 176.23, 180.504, 182.004]
y_axis_latency_small_SCOPE = [0, 126.092, 144.76, 164.448, 178.83, 180.87, 189.24, 195.68, 208.33, 215.138, 224.41]
y_axis_latency_small_OCO = [0, 119.53, 136.37, 161.24, 176.34, 181.18, 188.78, 192.29, 191.702, 211.48, 216.607]
y_axis_latency_small_RG = [0, 138.06, 145.81, 172.65, 175.95, 181.65, 204.16, 211.33, 241.23, 257.38, 265.46]

#Energy
y_axis_energy_small_GSAgri = [0, 5.75, 10.19, 16.437, 19.92, 21.559, 27.908, 32.866, 38.85, 47.017, 48.03]
y_axis_energy_small_SCOPE = [0, 7.94, 15.24, 22.4, 26.17, 30.52, 38.575, 45.265, 53.675, 67.38, 67.215]
y_axis_energy_small_OCO = [0, 6.025, 15.42, 22.38, 25.135, 28.715, 35.725, 42.22, 52.275, 64.51, 62.445]
y_axis_energy_small_RG = [0, 7.07, 16.44, 25.07, 28.36, 33.72, 38.36, 49.41, 56.85, 79.27, 66.13]

#Failure:
y_axis_failure_small_GSAgri = [0, 2, 2, 4, 4, 5, 5, 5, 6, 7, 8]
y_axis_failure_small_SCOPE = [0, 2, 2, 4, 4, 5, 9, 9, 19, 22, 21]
y_axis_failure_small_OCO = [0, 2, 2, 4, 4, 5, 6, 9, 12, 18, 27]
y_axis_failure_small_RG = [0, 2, 2, 4, 5, 10, 12, 17, 29, 31, 36]

fig, (ax1, ax2, ax3) = plt.subplots(3, 1, figsize=(8, 10), sharex=True) #horizontal
#fig, (ax1, ax2, ax3) = plt.subplots(1, 3, figsize=(10, 5))

ax1.plot(x_axis, y_axis_latency_small_GSAgri, marker='x', label='GSAgri', linewidth=1, color='green')
ax1.plot(x_axis, y_axis_latency_small_SCOPE, marker='o', label='SCOPE', linewidth=1, color='blue')
ax1.plot(x_axis, y_axis_latency_small_OCO, marker='D', label='OCO', linewidth=1, color='m')
ax1.plot(x_axis, y_axis_latency_small_RG, marker='v', label='RG', linewidth=1, color='orange')
ax1.set_ylabel('Avg latency (Secs)', fontsize = 13)

ax2.plot(x_axis, y_axis_energy_small_GSAgri, marker='x', label='GSAgri', linewidth=1, color='green')
ax2.plot(x_axis, y_axis_energy_small_SCOPE, marker='o', label='SCOPE', linewidth=1, color='blue')
ax2.plot(x_axis, y_axis_energy_small_OCO, marker='D', label='OCO', linewidth=1, color='m')
ax2.plot(x_axis, y_axis_energy_small_RG, marker='v', label='RG', linewidth=1, color='orange')
ax2.set_ylabel('Energy consumption (Wh)', fontsize = 13)
#ax2.set_title('Medium Density Application')

ax3.plot(x_axis, y_axis_failure_small_GSAgri, marker='x', label='GSAgri', linewidth=1, color='green')
ax3.plot(x_axis, y_axis_failure_small_SCOPE, marker='o', label='SCOPE', linewidth=1, color='blue')
ax3.plot(x_axis, y_axis_failure_small_OCO, marker='D', label='OCO', linewidth=1, color='m')
ax3.plot(x_axis, y_axis_failure_small_RG, marker='v', label='RG', linewidth=1, color='orange')
ax3.set_ylabel('Num of App Failure', fontsize = 13)

plt.xlabel('No. of applications', fontsize = 13)
ax1.legend(loc='best', prop={'size': 12})
ax2.legend(loc='best', prop={'size': 12})
ax3.legend(loc='best', prop={'size': 12})

plt.savefig('mix_small.png', bbox_inches='tight')
plt.show()
