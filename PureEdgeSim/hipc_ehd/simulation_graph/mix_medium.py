import matplotlib.pyplot as plt

#x_axis = [0, 15, 30, 45, 60, 75, 90, 105, 120, 135, 150]
x_axis = [0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100]

#latency:
y_axis_latency_medium_GSAgri = [0, 262.0, 246.93, 257.24, 264.12, 279.06, 271.205, 265.938, 273.83, 279.059, 286.61]
y_axis_latency_medium_SCOPE = [0, 236.49, 231.96, 265.156, 279.98, 291.59, 304.36, 323.32, 361.703, 391.66, 438.933]
y_axis_latency_medium_OCO = [0, 229.33, 235.49, 252.68, 271.40, 282.22, 321.82, 319.89, 388.29, 428.02, 465.59]
y_axis_latency_medium_RG = [0, 238.24, 247.75, 254.64, 315.82, 337.43, 272.45, 307.88, 389.35, 589.34, 559.59]

#Energy
y_axis_energy_medium_GSAgri = [0, 5.6, 11.44, 14.22, 20.40, 23.91, 27.903, 33.058, 38.87, 45.47, 50.28]
y_axis_energy_medium_SCOPE = [0, 17.23, 35.79, 40.98, 63.175, 74.125, 81.825, 93.015, 107.125, 131.085, 145.855]
y_axis_energy_medium_OCO = [0, 16.44, 34.855, 37.755, 64.51, 69.72, 78.155, 90.165, 101.09, 119.945, 138.175]
y_axis_energy_medium_RG = [0, 13.93, 25.545, 31.91, 49.24, 55.31, 67.02, 77.195, 82.93, 104.405, 118.96]

#Failure:
y_axis_failure_medium_GSAgri = [0, 1, 4, 6, 6, 6, 5, 9, 9, 10, 11]
y_axis_failure_medium_SCOPE = [0, 2, 4, 8, 11, 18, 20, 27, 32, 36, 42]
y_axis_failure_medium_OCO = [0, 1, 5, 6, 11, 17, 21, 28, 33, 36, 44]
y_axis_failure_medium_RG = [0, 1, 4, 11, 19, 23, 33, 38, 41, 47, 53]

fig, (ax1, ax2, ax3) = plt.subplots(3, 1, figsize=(8, 10), sharex=True) #horizontal
#fig, (ax1, ax2, ax3) = plt.subplots(1, 3, figsize=(10, 5))

ax1.plot(x_axis, y_axis_latency_medium_GSAgri, marker='x', label='GSAgri', linewidth=1, color='green')
ax1.plot(x_axis, y_axis_latency_medium_SCOPE, marker='o', label='SCOPE', linewidth=1, color='blue')
ax1.plot(x_axis, y_axis_latency_medium_OCO, marker='D', label='OCO', linewidth=1, color='m')
ax1.plot(x_axis, y_axis_latency_medium_RG, marker='v', label='RG', linewidth=1, color='orange')
ax1.set_ylabel('Avg latency (Secs)', fontsize = 13)

ax2.plot(x_axis, y_axis_energy_medium_GSAgri, marker='x', label='GSAgri', linewidth=1, color='green')
ax2.plot(x_axis, y_axis_energy_medium_SCOPE, marker='o', label='SCOPE', linewidth=1, color='blue')
ax2.plot(x_axis, y_axis_energy_medium_OCO, marker='D', label='OCO', linewidth=1, color='m')
ax2.plot(x_axis, y_axis_energy_medium_RG, marker='v', label='RG', linewidth=1, color='orange')
ax2.set_ylabel('Energy consumption (Wh)', fontsize = 13)
#ax2.set_title('Medium Density Application')

ax3.plot(x_axis, y_axis_failure_medium_GSAgri, marker='x', label='GSAgri', linewidth=1, color='green')
ax3.plot(x_axis, y_axis_failure_medium_SCOPE, marker='o', label='SCOPE', linewidth=1, color='blue')
ax3.plot(x_axis, y_axis_failure_medium_OCO, marker='D', label='OCO', linewidth=1, color='m')
ax3.plot(x_axis, y_axis_failure_medium_RG, marker='v', label='RG', linewidth=1, color='orange')
ax3.set_ylabel('Num of App Failure', fontsize = 13)

plt.xlabel('No. of applications', fontsize = 13)
ax1.legend(loc='best', prop={'size': 12})
ax2.legend(loc='best', prop={'size': 12})
ax3.legend(loc='best', prop={'size': 12})

plt.savefig('mix_medium.png', bbox_inches='tight')
plt.show()
