import matplotlib.pyplot as plt

#x_axis = [0, 15, 30, 45, 60, 75, 90, 105, 120, 135, 150]
x_axis = [0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100]

#latency:
y_axis_latency_large_GSAgri = [0, 289.0, 325.89, 322.17, 315.0, 324.75, 334.22, 342.73, 345.42, 362.27, 382.0]
y_axis_latency_large_SCOPE = [0, 256.79, 282.04, 332.58, 365.20, 382.75, 448.71, 517.79, 567.621, 605.037, 700.321]
y_axis_latency_large_OCO = [0, 261.5, 296.18, 341.37, 372.57, 379.30, 472.11, 497.07, 584.1, 622.56, 623.47]
y_axis_latency_large_RG = [0, 262.81, 292.28, 416.57, 445.43, 492.86, 502.04, 523.45, 623.34, 659.76, 667.41]

#Energy
y_axis_energy_large_GSAgri = [0, 5.94, 10.25, 15.93, 19.61, 26.92, 28.41, 34.05, 36.92, 46.26, 49.99]
y_axis_energy_large_SCOPE = [0, 27.145, 47.375, 68.355, 83.825, 105.19, 120.7, 145.195, 153.64, 187.605, 194.485]
y_axis_energy_large_OCO = [0, 28.57, 49.05, 67.12, 85.2, 110.855, 115.525, 145.36, 158.63, 189.48, 194.58]
y_axis_energy_large_RG = [0, 22.18, 36.62, 54.52, 71.6145, 89.0655, 98.025, 121.5605, 134.506, 154.135, 162.165]

#Failure:
y_axis_failure_large_GSAgri = [0, 2, 2, 2, 2, 3, 3, 4, 7, 11, 14]
y_axis_failure_large_SCOPE = [0, 6, 4, 13, 21, 25, 34, 40, 46, 55, 64]
y_axis_failure_large_OCO = [0, 6, 6, 13, 18, 26, 39, 38, 45, 52, 59]
y_axis_failure_large_RG = [0, 5, 6, 23, 38, 45, 56, 64, 68, 70, 76]

fig, (ax1, ax2, ax3) = plt.subplots(3, 1, figsize=(8, 10), sharex=True) #horizontal
#fig, (ax1, ax2, ax3) = plt.subplots(1, 3, figsize=(10, 5))

ax1.plot(x_axis, y_axis_latency_large_GSAgri, marker='x', label='GSAgri', linewidth=1, color='green')
ax1.plot(x_axis, y_axis_latency_large_SCOPE, marker='o', label='SCOPE', linewidth=1, color='blue')
ax1.plot(x_axis, y_axis_latency_large_OCO, marker='D', label='OCO', linewidth=1, color='m')
ax1.plot(x_axis, y_axis_latency_large_RG, marker='v', label='RG', linewidth=1, color='orange')
ax1.set_ylabel('Avg latency (Secs)', fontsize = 13)

ax2.plot(x_axis, y_axis_energy_large_GSAgri, marker='x', label='GSAgri', linewidth=1, color='green')
ax2.plot(x_axis, y_axis_energy_large_SCOPE, marker='o', label='SCOPE', linewidth=1, color='blue')
ax2.plot(x_axis, y_axis_energy_large_OCO, marker='D', label='OCO', linewidth=1, color='m')
ax2.plot(x_axis, y_axis_energy_large_RG, marker='v', label='RG', linewidth=1, color='orange')
ax2.set_ylabel('Energy consumption (Wh)', fontsize = 13)
#ax2.set_title('Medium Density Application')

ax3.plot(x_axis, y_axis_failure_large_GSAgri, marker='x', label='GSAgri', linewidth=1, color='green')
ax3.plot(x_axis, y_axis_failure_large_SCOPE, marker='o', label='SCOPE', linewidth=1, color='blue')
ax3.plot(x_axis, y_axis_failure_large_OCO, marker='D', label='OCO', linewidth=1, color='m')
ax3.plot(x_axis, y_axis_failure_large_RG, marker='v', label='RG', linewidth=1, color='orange')
ax3.set_ylabel('Num of App Failure', fontsize = 13)

plt.xlabel('No. of applications', fontsize = 13)
ax1.legend(loc='best', prop={'size': 12})
ax2.legend(loc='best', prop={'size': 12})
ax3.legend(loc='best', prop={'size': 12})

plt.savefig('mix_large.png', bbox_inches='tight')
plt.show()
