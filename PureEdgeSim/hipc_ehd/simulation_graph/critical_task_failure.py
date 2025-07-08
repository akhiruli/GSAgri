import matplotlib.pyplot as plt

x_axis = [0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100]
y_axis_GSAgri = [0, 9, 42, 76, 75, 79, 68, 85, 97, 101, 103]
y_axis_SCOPE = [0, 8, 48, 88, 86, 78, 74, 98, 111, 112, 116]
y_axis_OCO = [0, 8, 41, 67, 58, 81, 88, 112, 124, 121, 131]
y_axis_RG = [0, 11, 50, 94, 84, 78, 90, 107, 124, 125, 124]

plt.plot(x_axis, y_axis_GSAgri, marker='x', label='GSAgri', linewidth=1.3, color='green')
plt.plot(x_axis, y_axis_SCOPE, marker='o', label='SCOPE', linewidth=1.3, color='blue')
plt.plot(x_axis, y_axis_RG, marker='v', label='RG', linewidth=1.3, color='orange')
plt.plot(x_axis, y_axis_OCO, marker='D', label='OCO', linewidth=1.3, color='m')
#plt.title('Critical Task Failure', fontsize = 15)

plt.ylabel('Number of Critical Task Failure', fontsize = 15)
plt.xlabel('No. of Applications', fontsize = 15)
plt.legend(loc='best', prop={'size': 15})
plt.savefig('critical_task_failure.png', bbox_inches='tight')
plt.show()
