import matplotlib.pyplot as plt

#x_axis = [0, 70, 140, 210, 280, 350, 420, 490, 560, 630, 700]
x_axis = [0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100]
y_axis_GSAgri = [0, 0.334, 0.359, .352, 0.349, 0.366, 0.362, 0.332, 0.357, 0.361, 0.368]
y_axis_SCOPE = [0, .61, 0.64, .705, 0.74, 0.861, .923, 1.168, 1.188, 1.205, 1.264]
y_axis_RG = [0, 0.568, .603, .62, .75, 0.823, .905, 1.092, 1.112, 1.138, 1.18]
y_axis_OCO = [0, 0.422, 0.389, 0.440, 0.433, 0.430, 0.4199, 0.427, 0.436, 0.447, 0.453]

plt.plot(x_axis, y_axis_GSAgri, marker='x', label='GSAgri', linewidth=1.3, color='green')
plt.plot(x_axis, y_axis_SCOPE, marker='o', label='SCOPE', linewidth=1.3, color='blue')
plt.plot(x_axis, y_axis_RG, marker='v', label='RG', linewidth=1.3, color='orange')
plt.plot(x_axis, y_axis_OCO, marker='D', label='OCO', linewidth=1.3, color='m')

#plt.title('Average Latency', fontsize = 15)

plt.ylabel('Average Latency of a Task(In secs)', fontsize = 15)
plt.xlabel('No. of Applications', fontsize = 15)
plt.legend(loc='best', prop={'size': 15})
plt.savefig('real_task_latency.png', bbox_inches='tight')
plt.show()
