import matplotlib.pyplot as plt

x_axis = [0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100]
y_axis_GSAgri = [0, 249.56, 263.412, 259.83, 270.34, 274.193, 277.26, 269.19, 280.85, 277.668, 277.434]
y_axis_SCOPE = [0, 229.72, 249.46, 265.69, 276.44, 289.81, 301.05, 330.98, 360.63, 406.56, 434.53]
y_axis_OCO = [0, 223.06, 245.64, 259.41, 274.65, 291.50, 322.81, 362.46, 369.73, 422.11, 476.02]
y_axis_RG = [0, 227.57, 243.61, 266.79, 330.32, 330.8, 373.6, 438.4, 388.68, 491.38, 368.56]

plt.plot(x_axis, y_axis_GSAgri, marker='x', label='GSAgri', linewidth=1.3, color='green')
plt.plot(x_axis, y_axis_SCOPE, marker='o', label='SCOPE', linewidth=1.3, color='blue')
plt.plot(x_axis, y_axis_RG, marker='v', label='RG', linewidth=1.3, color='orange')
plt.plot(x_axis, y_axis_OCO, marker='D', label='OCO', linewidth=1.3, color='m')

#plt.title('Average Latency', fontsize = 15)

plt.ylabel('Avg. latency of an application (Secs)', fontsize = 15)
plt.xlabel('No. of applications', fontsize = 15)
plt.legend(loc='best', prop={'size': 15})
plt.savefig('nw_real_app_latency.png', bbox_inches='tight')
plt.show()
