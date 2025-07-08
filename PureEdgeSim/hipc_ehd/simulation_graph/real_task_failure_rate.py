import matplotlib.pyplot as plt

#x_axis = [0, 70, 140, 210, 280, 350, 420, 490, 560, 630, 700]
x_axis = [0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100]

y_axis_GSAgri = [0, 0, 4.28, 3.33, 3.21, 2.57, 2.61, 2.85, 3.03, 3.49, 3.14]
y_axis_SCOPE = [0, 4.28, 6.42, 8.09, 7.85, 6.28, 6.19, 8.67, 8.57, 9.2, 9.71]
y_axis_RG = [0, 4.285, 3.57, 4.28, 6.42, 5.42, 6.66, 7.95, 6.96, 6.5, 7.42]
y_axis_OCO = [0, 4.28, 6.42, 5.714, 6.42, 7.42, 7.85, 7.95, 8.21, 8.25, 8.142]

plt.plot(x_axis, y_axis_GSAgri, marker='x', label='GSAgri', linewidth=1.3, color='green')
plt.plot(x_axis, y_axis_SCOPE, marker='o', label='SCOPE', linewidth=1.3, color='blue')
plt.plot(x_axis, y_axis_RG, marker='v', label='RG', linewidth=1.3, color='orange')
plt.plot(x_axis, y_axis_OCO, marker='D', label='OCO', linewidth=1.3, color='m')

#plt.title('Task Failure Rate', fontsize = 15)

plt.ylabel('Task Failure rate (%)', fontsize = 16)
plt.xlabel('No. of Applications', fontsize = 16)
plt.legend(loc='best', prop={'size': 15})
plt.savefig('real_task_failure_rate.png', bbox_inches='tight')
plt.show()
