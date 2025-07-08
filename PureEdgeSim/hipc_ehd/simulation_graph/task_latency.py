import matplotlib.pyplot as plt

x_axis = [0, 264, 481, 695, 903, 1093, 1314, 1550, 1746, 1967, 2194]
x_axis = [0, 481, 903, 1314, 1746, 2194, 2629, 2973, 3387, 3855, 4294]
y_axis_GSAgri = [0, 38.522, 32.096, 37.743, 51.887, 64.735, 79.668, 84.380, 91.496, 98.921, 105.396]
y_axis_SCOPE = [0, 62.19, 73.160, 77.735, 78.681, 93.040, 99.75, 110.526, 131.202, 133.874, 147.23]
y_axis_RG = [0, 67.347, 49.68, 76.311, 80.726, 107.073, 103.673, 138.815, 150.602, 184.483, 208.057]
y_axis_OCO = [0, 41.496, 50.901, 49.730, 68.243, 77.46, 93.612, 106.972, 121.58, 133.723, 148.073]

plt.plot(x_axis, y_axis_GSAgri, marker='x', label='GSAgri', linewidth=1.3, color='green')
plt.plot(x_axis, y_axis_SCOPE, marker='o', label='SCOPE', linewidth=1.3, color='blue')
plt.plot(x_axis, y_axis_RG, marker='v', label='RG', linewidth=1.3, color='orange')
plt.plot(x_axis, y_axis_OCO, marker='D', label='OCO', linewidth=1.3, color='m')

#plt.title('Average Latency', fontsize = 15)

plt.ylabel('Average Latency of a Task(In secs)', fontsize = 15)
plt.xlabel('No. of Tasks', fontsize = 15)
plt.legend(loc='best', prop={'size': 15})
plt.savefig('task_latency.png', bbox_inches='tight')
plt.show()
